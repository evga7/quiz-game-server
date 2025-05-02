package pyo.quizgame.validate;

import org.springframework.stereotype.Component;
import pyo.quizgame.dto.UserPostDto;

import java.util.HashMap;
import java.util.Map;

@Component
public class PostValidator {
    public Map<String, String> validateEditPost(UserPostDto userPostDto) {
        Map<String, String> errors = new HashMap<>();
        if (isTitleInvalid(userPostDto.getTitle())) {
            errors.put("titleLength", "제목은 2~25자 로 작성해 주세요.");
        }
        if (isContentInvalid(userPostDto.getContent())) {
            errors.put("contentLength", "내용은 2~500자 로 작성해 주세요.");
        }
        return errors;
    }

    private boolean isTitleInvalid(String title) {
        return title.length() < 2 || title.length() > 25;
    }

    private boolean isContentInvalid(String content) {
        return content.length() < 2 || content.length() > 500;
    }
}
