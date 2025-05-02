package pyo.quizgame.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pyo.quizgame.domain.UserRankInfo;
import pyo.quizgame.dto.frontuser.NicknameCheckDto;
import pyo.quizgame.dto.frontuser.SignupDto;
import pyo.quizgame.dto.frontuser.UpdateUserDto;
import pyo.quizgame.infra.rabbitmq.QuizResultMessage;
import pyo.quizgame.infra.rabbitmq.QuizResultProducer;
import pyo.quizgame.service.FrontUserService;
import pyo.quizgame.service.RankService;
import pyo.quizgame.service.UserCommentService;
import pyo.quizgame.service.UserPostService;

@Controller
@RequiredArgsConstructor
public class FrontUserController {

    private final FrontUserService frontUserService;
    private final RankService rankService;
    private final UserPostService userPostService;
    private final UserCommentService userCommentService;
    private final QuizResultProducer quizResultProducer;

    @Operation(summary = "회원가입 (닉네임 설정)")
    @PostMapping("/front-user")
    @ResponseBody
    public String signup(@ModelAttribute SignupDto signupDto) {
        frontUserService.signup(signupDto.toEntity());
        return "OK";
    }


//    @Operation(summary = "프론트 유저 퀴즈 정보 업데이트(동기) 사용 X")
//    @PostMapping("/front-user/update2")
//    @ResponseBody
//    public String updateUserSync(@RequestBody FrontUserInfo frontUserInfo) {
//        return frontUserService.updateUserSync(frontUserInfo) ? "OK" : "NO";
//    }

    @Operation(summary = "프론트 유저 퀴즈 정보 업데이트(비동기)")
    @PostMapping("/front-user/update")
    @ResponseBody
    public String updateUserAsync(@ModelAttribute UpdateUserDto updateUserDto) {
        if (frontUserService.existsByNickName(updateUserDto.getNickName())) {
            quizResultProducer.sendQuizResult(new QuizResultMessage(
                    updateUserDto.getNickName(),
                    updateUserDto.getTotalQuizCount(),
                    updateUserDto.getSolvedQuizCount()
            ));
            return "OK";
        }
        return "NO";
    }

    @Operation(summary = "닉네임 유효성 검사")
    @PostMapping("/front-user/check")
    @ResponseBody
    public String checkNickName(@ModelAttribute NicknameCheckDto nicknameCheckDto) {
        return frontUserService.validateNickName(nicknameCheckDto.getNickName());
    }

    @Operation(summary = "유저 등수 조회")
    @PostMapping("/front-user/rank")
    @ResponseBody
    public UserRankInfo getPlayerRank(@ModelAttribute NicknameCheckDto nicknameCheckDto) {
        return frontUserService.getUserRankInfo(nicknameCheckDto.getNickName());
    }

    @Operation(summary = "10분마다 랭킹 갱신")
    @GetMapping("/quiz/update-user-rank")
    @ResponseBody
    public String updateUserRank() {
        rankService.updateUserRankData();
        return "OK";
    }

    @Operation(summary = "유저 페이지")
    @GetMapping("/front-user/user-page")
    public String userPage(String nickName, Model model) {
        model.addAttribute("user", frontUserService.getUserByNickName(nickName));
        return "user-page";
    }

    @Operation(summary = "유저 게시글 조회")
    @GetMapping("/front-user/user-post-info")
    public String getUserPostInfo(String nickName, Model model) {
        model.addAttribute("posts", userPostService.getUserPostByNickName(nickName));
        return "user-post-info";
    }

    @Operation(summary = "유저 차단 정보")
    @GetMapping("/front-user/user-block-info")
    public String getUserBlockInfo(String nickName, Model model) {
        model.addAttribute("blocks", frontUserService.getUserBlocks(nickName));
        return "user-block-info";
    }

    @Operation(summary = "유저 댓글 조회")
    @GetMapping("/front-user/user-comment-info")
    public String getUserCommentInfo(String nickName, Model model) {
        model.addAttribute("comments", userCommentService.getUserCommentByNickName(nickName));
        return "user-comment-info";
    }

    @Operation(summary = "개인정보 정책 페이지")
    @GetMapping("/front-user/user-privacy-policy")
    public String getPrivacyPolicy() {
        return "privacy-policy";
    }

    @Operation(summary = "이용약관 페이지")
    @GetMapping("/front-user/user-terms-conditions")
    public String getTermsAndConditions() {
        return "terms-conditions";
    }
}
