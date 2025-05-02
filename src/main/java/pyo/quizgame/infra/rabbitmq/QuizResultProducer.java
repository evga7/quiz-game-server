package pyo.quizgame.infra.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizResultProducer {

    private final RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE = "quiz.result.exchange";
    private static final String ROUTING_KEY = "quiz.result.update";

    public void sendQuizResult(QuizResultMessage message) {
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, message);
    }
}
