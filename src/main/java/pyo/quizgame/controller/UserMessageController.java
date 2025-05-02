package pyo.quizgame.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pyo.quizgame.domain.UserMessage;
import pyo.quizgame.dto.UserMessageDto;
import pyo.quizgame.service.UserMessageService;

@Controller
@RequiredArgsConstructor
public class UserMessageController {

    private final UserMessageService userMessageService;

    @Operation(summary = "프론트 유저 의견 등록 API")
    @PostMapping("/front-user/message")
    public @ResponseBody String saveUserMessage(UserMessageDto userMessageDto) {
        userMessageService.saveUserMessage(userMessageDto);
        return "OK";
    }

    @Operation(summary = "유저 의견 목록 조회 페이지")
    @GetMapping("/user-messages")
    public String getAllMessages(Model model,
                                 @PageableDefault(size = 10) Pageable pageable,
                                 @RequestParam(required = false, defaultValue = "") String searchText) {

        Page<UserMessage> userMessages = userMessageService.searchUserMessages(searchText, pageable);

        model.addAttribute("currentPageNumber", userMessages.getNumber() + 1);
        model.addAttribute("startPage", 1);
        model.addAttribute("totalPages", userMessages.getTotalPages());
        model.addAttribute("userMessages", userMessages);
        return "user-messages";
    }
}
