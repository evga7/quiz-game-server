package pyo.quizgame.quiz;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StopWatch;
import pyo.quizgame.domain.QuizInfo;
import pyo.quizgame.repository.QuizRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@SpringBootTest
public class QuizPerformanceTest {
    @Autowired
    QuizRepository quizRepository;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Test
    void compareQuizLoadPerformanceMultipleTimes() {
        int iterations = 10;

        List<Double> dbRandTimes = new ArrayList<>();
        List<Double> redisTimes = new ArrayList<>();
        List<Double> javaRandTimes = new ArrayList<>();

        for (int i = 0; i < iterations; i++) {
            StopWatch stopWatch = new StopWatch();

            // 1. DB RAND()
            stopWatch.start("DB RAND()");
            List<QuizInfo> randQuiz = quizRepository.findRandomQuiz(10000L);
            stopWatch.stop();
            dbRandTimes.add((double) stopWatch.getLastTaskTimeMillis());


            // 2. Java Random + DB IN
            stopWatch.start("Java Random + DB IN");
            List<Long> allQuizIds = quizRepository.findAllIds(); // 전체 ID
            Collections.shuffle(allQuizIds);
            List<Long> randomIdsJava = allQuizIds.stream().limit(10000).toList();
            List<QuizInfo> javaRandomQuiz = quizRepository.findByIdIn(randomIdsJava);
            stopWatch.stop();
            javaRandTimes.add((double) stopWatch.getLastTaskTimeMillis());

            // 3. Redis + DB IN
            stopWatch.start("Redis + DB IN");
            Set<String> randomIds = redisTemplate.opsForSet().distinctRandomMembers("quiz:id:set", 10000);
            List<Long> idsFromRedis = randomIds.stream().map(Long::valueOf).toList();
            List<QuizInfo> redisQuiz = quizRepository.findByIdIn(idsFromRedis);
            stopWatch.stop();
            redisTimes.add((double) stopWatch.getLastTaskTimeMillis());

            System.out.printf("==== %d회차 테스트 결과 ====\n%s\n", i + 1, stopWatch.prettyPrint());
        }

        printStats("DB RAND()", dbRandTimes);
        printStats("Java Random + DB IN", javaRandTimes);
        printStats("Redis + DB IN", redisTimes);
    }

    private void printStats(String title, List<Double> times) {
        double average = times.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double min = times.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = times.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        double stdDev = calculateStandardDeviation(times, average);

        System.out.println("===== " + title + " 성능 통계 =====");
        System.out.printf("평균 시간: %.2f ms\n", average);
        System.out.printf("최소 시간: %.2f ms\n", min);
        System.out.printf("최대 시간: %.2f ms\n", max);
        System.out.printf("표준 편차: %.2f ms\n", stdDev);
        System.out.println();
    }

    private double calculateStandardDeviation(List<Double> times, double average) {
        double variance = times.stream()
                .mapToDouble(time -> Math.pow(time - average, 2))
                .average()
                .orElse(0);
        return Math.sqrt(variance);
    }
}
