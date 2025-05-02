package pyo.quizgame.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pyo.quizgame.domain.UserPost;

@Getter
@AllArgsConstructor
public class LikeResult {
    private boolean liked;
    private UserPost post;
}
