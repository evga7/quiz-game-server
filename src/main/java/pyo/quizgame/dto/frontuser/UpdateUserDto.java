package pyo.quizgame.dto.frontuser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDto {
    private String nickName;
    private Long totalQuizCount;
    private Long solvedQuizCount;
}