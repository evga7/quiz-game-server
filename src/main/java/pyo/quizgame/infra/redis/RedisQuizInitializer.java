package pyo.quizgame.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisQuizInitializer {

    private final RedisQuizService redisQuizService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        redisQuizService.syncQuizIdsToRedisIfNeeded();
    }
}
