package pyo.quizgame.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pyo.quizgame.service.UserBlockService;

@Controller
@RequiredArgsConstructor
public class UserBlockController {

    private final UserBlockService userBlockService;

    @Operation(summary = "유저 차단하기")
    @PostMapping("/front-user/user-block")
    public String blockUser(String nickName, String blockedNickName, RedirectAttributes redirectAttributes) {
        userBlockService.blockUser(nickName, blockedNickName);
        redirectAttributes.addAttribute("nickName", nickName);
        return "redirect:/user-boards";
    }

    @Operation(summary = "유저 차단 해제하기")
    @PostMapping("/front-user/user-un-block")
    public String unblockUser(String nickName, String blockedNickName, Model model) {
        model.addAttribute("blocks", userBlockService.unblockUser(nickName, blockedNickName));
        model.addAttribute("nickName", nickName);
        return "user-block-info :: #blockTable";
    }
}
