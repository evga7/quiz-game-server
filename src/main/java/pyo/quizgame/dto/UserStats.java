package pyo.quizgame.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserStats {
    private final long totalQuizCount;
    private final long solvedQuizCount;
}
