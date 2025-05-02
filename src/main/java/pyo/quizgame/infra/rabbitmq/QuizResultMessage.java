package pyo.quizgame.infra.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizResultMessage {
    private String nickName;
    private long totalQuizCount;
    private long solvedQuizCount;
}
