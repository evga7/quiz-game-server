package pyo.quizgame.dto.frontuser;

import lombok.Getter;
import lombok.Setter;
import pyo.quizgame.domain.FrontUserInfo;

@Getter
@Setter
public class SignupDto {
    private String nickName;
    public FrontUserInfo toEntity() {
        return FrontUserInfo.builder()
                .nickName(this.nickName)
                .totalQuizCount(0L)
                .solvedQuizCount(0L)
                .percentageOfAnswer("0.00")
                .numberOfPosts(0L)
                .numberOfComments(0L)
                .numberOfBlockedUser(0L)
                .build();
    }
}