package pyo.quizgame.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import pyo.quizgame.domain.QuizInfo;
import pyo.quizgame.domain.S3FileInfo;
import pyo.quizgame.dto.QuizInfoDto;
import pyo.quizgame.infra.redis.RedisService;
import pyo.quizgame.repository.QuizRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final RedisService redisService;
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public Page<QuizInfo> getQuizPage(String searchText, Pageable pageable) {
        return quizRepository.findByTitleContaining(searchText, pageable);
    }

    @Transactional(readOnly = true)
    public QuizInfoDto getQuizDetail(Long id) {
        return quizRepository.findById(id)
                .map(QuizInfoDto::from)
                .orElse(null);
    }

    @Transactional
    public void saveQuiz(QuizInfoDto quizInfoDto) {
        S3FileInfo s3FileInfo = resolveS3FileInfo(quizInfoDto.getFileOriginalName());

        QuizInfo quizInfo = QuizInfo.builder()
                .title(quizInfoDto.getTitle())
                .answer(quizInfoDto.getAnswer())
                .example1(quizInfoDto.getExample1())
                .example2(quizInfoDto.getExample2())
                .example3(quizInfoDto.getExample3())
                .example4(quizInfoDto.getExample4())
                .fileOriginalName(quizInfoDto.getFileOriginalName())
                .changeFileOriginalName(quizInfoDto.getChangeFileOriginalName())
                .s3FileInfo(s3FileInfo)
                .build();

        setDefaultImageIfEmpty(quizInfo);

        if (!"noImage.png".equals(quizInfo.getFileOriginalName())) {
            applyTimestampedFileName(quizInfo);
        }

        quizRepository.save(quizInfo);
        redisService.addQuizId(quizInfo.getId());
    }

    @Transactional
    public void updateQuiz(QuizInfoDto quizInfoDto) {
        QuizInfo quizInfo = quizRepository.findById(quizInfoDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 퀴즈입니다."));

        S3FileInfo s3FileInfo = resolveS3FileInfo(quizInfoDto.getFileOriginalName());

        quizInfo.update(
                quizInfoDto.getTitle(),
                quizInfoDto.getAnswer(),
                quizInfoDto.getExample1(),
                quizInfoDto.getExample2(),
                quizInfoDto.getExample3(),
                quizInfoDto.getExample4(),
                quizInfoDto.getFileOriginalName(),
                quizInfoDto.getChangeFileOriginalName(),
                s3FileInfo
        );

        if (!"noImage.png".equals(quizInfo.getFileOriginalName())) {
            applyTimestampedFileName(quizInfo);
        }

        redisService.addQuizId(quizInfo.getId()); // 수정 후 다시 Redis 동기화
    }

    @Transactional
    public void deleteQuiz(Long id) {
        quizRepository.deleteById(id);
        redisService.removeQuizId(id);
    }

    @Transactional(readOnly = true)
    public List<QuizInfoDto> getRandomQuizzesFromDb(Long number) {
        List<QuizInfo> randomQuizList = quizRepository.findRandomQuiz(number);
        return randomQuizList.stream()
                .map(QuizInfoDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuizInfoDto> getRandomQuizzesFromRedis(Long number) {
        Set<String> randomIds = redisService.getRandomQuizIds(number);

        if (randomIds == null || randomIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> ids = randomIds.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        List<QuizInfo> quizList = quizRepository.findByIdIn(ids);
        return quizList.stream()
                .map(QuizInfoDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getAllQuizCount() {
        return quizRepository.count();
    }

    private void setDefaultImageIfEmpty(QuizInfo quizInfo) {
        if (!StringUtils.hasText(quizInfo.getFileOriginalName())) {
            quizInfo.updateFileOriginalName("noImage.png");
        }
    }

    private void applyTimestampedFileName(QuizInfo quizInfo) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String newFileName = timestamp + "_" + quizInfo.getFileOriginalName();
        quizInfo.updateFileOriginalName(newFileName);
    }
    private S3FileInfo resolveS3FileInfo(String fileOriginalName) {
        if (!StringUtils.hasText(fileOriginalName)) {
            return s3Service.getDefaultS3FileInfo();
        }
        return s3Service.getS3FileInfo(fileOriginalName);
    }
}
