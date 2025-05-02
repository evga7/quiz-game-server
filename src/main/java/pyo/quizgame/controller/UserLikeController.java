package pyo.quizgame.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import pyo.quizgame.dto.LikeResult;
import pyo.quizgame.service.UserLikeService;

@Controller
@RequiredArgsConstructor
public class UserLikeController {

    private final UserLikeService likeService;

    @Operation(summary = "좋아요 혹은 좋아요 취소")
    @PostMapping("/front-user/user-post/like")
    public String toggleLike(Long id, String nickName, Model model) {
        LikeResult result = likeService.toggleLike(nickName, id);

        model.addAttribute("isLiked", result.isLiked());
        model.addAttribute("post", result.getPost());

        return "posts :: #likeTable";
    }


}
