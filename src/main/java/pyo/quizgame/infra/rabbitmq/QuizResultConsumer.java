package pyo.quizgame.infra.rabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pyo.quizgame.service.FrontUserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuizResultConsumer {

    private final FrontUserService frontUserService;


    @RabbitListener(queues = "quiz.result.queue")
    public void consumeQuizResult(QuizResultMessage msg) {
        try {
            frontUserService.updateUserAsync(msg);
        } catch (Exception e) {
            log.error("유저 퀴즈 결과 반영 실패: {}", msg, e);
            throw e; // Spring이 감지 → NACK → 재시도 or DLQ
        }
    }

}
