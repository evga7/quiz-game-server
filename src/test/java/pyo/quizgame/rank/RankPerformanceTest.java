package pyo.quizgame.rank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import pyo.quizgame.domain.UserRankInfo;
import pyo.quizgame.repository.RankRepository;
import pyo.quizgame.service.RankService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
public class RankPerformanceTest {

    @Autowired
    private RankService rankService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RankRepository userRankRepository;

    private static final int TEST_REPEAT = 100;

    @Test
    @DisplayName("✅ MySQL vs Redis 랭킹 조회 성능 비교")
    void compareMysqlAndRedisRankLoadPerformance() throws JsonProcessingException {
        List<Double> mysqlTimes = new ArrayList<>();
        List<Double> redisTimes = new ArrayList<>();

        for (int i = 0; i < TEST_REPEAT; i++) {
            // MySQL 성능 측정
            long mysqlStart = System.nanoTime();
            List<UserRankInfo> mysqlResult = userRankRepository.findAll();
            long mysqlEnd = System.nanoTime();
            mysqlTimes.add((mysqlEnd - mysqlStart) / 1_000_000.0);

            // Redis 성능 측정
            long redisStart = System.nanoTime();
            Set<String> redisSet = redisTemplate.opsForZSet().reverseRange("quiz:rank", 0, 99);
            List<UserRankInfo> redisResult = redisSet.stream()
                    .map(json -> {
                        try {
                            return objectMapper.readValue(json, UserRankInfo.class);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
            long redisEnd = System.nanoTime();
            redisTimes.add((redisEnd - redisStart) / 1_000_000.0);
        }

        printStats("MySQL", mysqlTimes);
        printStats("Redis", redisTimes);
    }

    private void printStats(String label, List<Double> times) {
        double avg = times.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double min = times.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
        double max = times.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        double stdDev = calculateStdDev(times, avg);

        System.out.println("===== " + label + " 랭킹 조회 성능 통계 =====");
        System.out.printf("평균 시간: %.2f ms\n", avg);
        System.out.printf("최소 시간: %.2f ms\n", min);
        System.out.printf("최대 시간: %.2f ms\n", max);
        System.out.printf("표준 편차: %.2f ms\n", stdDev);
    }

    private double calculateStdDev(List<Double> values, double mean) {
        double variance = values.stream()
                .mapToDouble(t -> Math.pow(t - mean, 2))
                .average()
                .orElse(0.0);
        return Math.sqrt(variance);
    }
}

