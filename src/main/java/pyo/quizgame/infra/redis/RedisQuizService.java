package pyo.quizgame.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pyo.quizgame.domain.QuizInfo;
import pyo.quizgame.repository.QuizRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisQuizService {

    private final RedisService redisService;
    private final QuizRepository quizRepository;

    @Transactional
    public void syncQuizIdsToRedisIfNeeded() {
        Long redisCount = redisService.getQuizIdSetCount();
        Long dbCount = quizRepository.count();

        if (redisCount == null || !redisCount.equals(dbCount)) {
            redisService.clearQuizIdSet();

            List<QuizInfo> allQuizzes = quizRepository.findAll();
            Set<String> quizIds = allQuizzes.stream()
                    .map(q -> String.valueOf(q.getId()))
                    .collect(Collectors.toSet());

            redisService.addAllQuizIds(quizIds);
        }
    }
}
