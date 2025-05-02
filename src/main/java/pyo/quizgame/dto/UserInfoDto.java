package pyo.quizgame.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoDto {
    private String userId;
    private String password;
    private String auth;
}
