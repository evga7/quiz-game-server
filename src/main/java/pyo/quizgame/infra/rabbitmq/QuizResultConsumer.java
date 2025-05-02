package pyo.quizgame.infra.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pyo.quizgame.service.FrontUserService;

@Component
@RequiredArgsConstructor
public class QuizResultConsumer {

    private final FrontUserService frontUserService;


    @RabbitListener(queues = "quiz.result.queue")
    public void consumeQuizResult(QuizResultMessage msg) {
        frontUserService.applyQuizResult(msg);
    }
}
