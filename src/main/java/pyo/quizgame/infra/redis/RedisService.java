package pyo.quizgame.infra.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public static final String QUIZ_ID_SET_KEY = "quiz:id:set";

    public void addQuizId(Long id) {
        try {
            redisTemplate.opsForSet().add(QUIZ_ID_SET_KEY, String.valueOf(id));
            log.debug("퀴즈 추가 ID {} to Redis set: {}", id, QUIZ_ID_SET_KEY);
        } catch (Exception e) {
            log.error("실패 quiz ID {} to Redis: {}", id, e.getMessage(), e);
        }
    }

    public void removeQuizId(Long id) {
        try {
            redisTemplate.opsForSet().remove(QUIZ_ID_SET_KEY, String.valueOf(id));
            log.debug("퀴즈 삭제 ID {} from Redis set: {}", id, QUIZ_ID_SET_KEY);
        } catch (Exception e) {
            log.error("삭제 실패 quiz ID {} from Redis: {}", id, e.getMessage(), e);
        }
    }

    public boolean containsQuizId(Long id) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(QUIZ_ID_SET_KEY, String.valueOf(id)));
    }

    public Long getQuizIdSetCount() {
        return redisTemplate.opsForSet().size(QUIZ_ID_SET_KEY);
    }

    public void clearQuizIdSet() {
        redisTemplate.delete(QUIZ_ID_SET_KEY);
    }

    public void addAllQuizIds(Collection<String> ids) {
        redisTemplate.opsForSet().add(QUIZ_ID_SET_KEY, ids.toArray(new String[0]));
    }

    public Set<String> getRandomQuizIds(long count) {
        return redisTemplate.opsForSet().distinctRandomMembers(QUIZ_ID_SET_KEY, count);
    }
}
