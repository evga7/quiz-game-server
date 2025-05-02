package pyo.quizgame.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pyo.quizgame.domain.FrontUserInfo;
import pyo.quizgame.domain.UserRankInfo;
import pyo.quizgame.repository.RankRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankService {

    private final RankRepository rankRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final FrontUserService frontUserService;

    private static final String REDIS_RANK_KEY = "quiz:rank";

    @Transactional
    public void saveAll(List<UserRankInfo> userRankInfos) {
        rankRepository.saveAll(userRankInfos);
    }

    @Transactional(readOnly = true)
    public List<UserRankInfo> getRankFromDb() {
        return rankRepository.findAll();
    }

    @Transactional
    public void updateUserRankData() {
        List<FrontUserInfo> rankList = frontUserService.getRank();
        List<UserRankInfo> userRankInfos = new ArrayList<>();
        long sequence = 0L;

        redisTemplate.delete(REDIS_RANK_KEY);

        for (FrontUserInfo user : rankList) {
            UserRankInfo info = UserRankInfo.builder()
                    .id(++sequence)
                    .nickName(user.getNickName())
                    .percentageOfAnswer(user.getPercentageOfAnswer())
                    .solvedQuizCount(user.getSolvedQuizCount())
                    .totalQuizCount(user.getTotalQuizCount())
                    .build();

            try {
                String json = objectMapper.writeValueAsString(info);
                double score = Double.parseDouble(user.getPercentageOfAnswer());
                redisTemplate.opsForZSet().add(REDIS_RANK_KEY, json, score);
            } catch (JsonProcessingException e) {
                log.error("Redis 저장 실패: {}", user.getNickName(), e);
            }

            userRankInfos.add(info);
        }

        saveAll(userRankInfos);
    }

    @Transactional(readOnly = true)
    public List<UserRankInfo> getTopUserRank() {
        Set<String> redisData = redisTemplate.opsForZSet().reverseRange(REDIS_RANK_KEY, 0, 99);

        if (redisData == null || redisData.isEmpty()) {
            log.warn("🚨 Redis에 랭킹 데이터 없음 → 갱신 시도");
            updateUserRankData();
            redisData = redisTemplate.opsForZSet().reverseRange(REDIS_RANK_KEY, 0, 99);
        }

        if (redisData == null) {
            return new ArrayList<>();
        }

        return redisData.stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, UserRankInfo.class);
                    } catch (JsonProcessingException e) {
                        log.error("랭킹 데이터 파싱 실패", e);
                        return null;
                    }
                })
                .filter(rankInfo -> rankInfo != null)
                .collect(Collectors.toList());
    }
}
