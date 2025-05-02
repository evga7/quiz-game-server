package pyo.quizgame.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pyo.quizgame.domain.QuizInfo;
import pyo.quizgame.dto.QuizInfoDto;
import pyo.quizgame.service.QuizService;

import java.util.List;

@RequiredArgsConstructor
@Controller
@Slf4j
public class QuizController {

    private final QuizService quizService;

    @Operation(summary = "퀴즈 추가 페이지로 이동")
    @GetMapping("/add-quiz")
    public String goAddQuiz() {
        return "add-quiz";
    }

    @Operation(summary = "메인 페이지 불러오기")
    @GetMapping("/")
    public String getQuizPage(Model model, @PageableDefault(size = 10) Pageable pageable,
                              @RequestParam(required = false, defaultValue = "") String searchText) {

        Page<QuizInfo> quizInfos = quizService.getQuizPage(searchText, pageable);
        model.addAttribute("currentPageNumber", quizInfos.getNumber() + 1);
        model.addAttribute("startPage", 1);
        model.addAttribute("totalPages", quizInfos.getTotalPages());
        model.addAttribute("quizs", quizInfos);
        return "main";
    }

    @Operation(summary = "퀴즈 상세페이지 이동")
    @GetMapping("/quiz/{id}")
    public String quiz(@PathVariable Long id, Model model) {
        QuizInfoDto quiz = quizService.getQuizDetail(id);
        model.addAttribute("quiz", quiz);
        model.addAttribute("path", quiz.getFilePath());
        return "quiz";
    }

    @Operation(summary = "퀴즈 수정페이지 불러오기")
    @GetMapping("/quiz/edit/{id}")
    public String editQuiz(@PathVariable Long id, Model model) {
        QuizInfoDto quiz = quizService.getQuizDetail(id);
        model.addAttribute("quiz", quiz);
        model.addAttribute("path", quiz.getFilePath());
        return "edit-quiz";
    }

    @Operation(summary = "퀴즈 추가하는 API")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/quiz")
    public String insertQuiz(@ModelAttribute QuizInfoDto quizInfoDto, RedirectAttributes redirectAttributes) {
        quizService.saveQuiz(quizInfoDto);
        redirectAttributes.addAttribute("posted", true);
        return "redirect:/";
    }

    @Operation(summary = "퀴즈 수정 POST")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/quiz/edit")
    public String modifyQuiz(@ModelAttribute QuizInfoDto quizInfoDto, RedirectAttributes redirectAttributes) {
        quizService.updateQuiz(quizInfoDto);
        redirectAttributes.addAttribute("changed", true);
        return "redirect:/";
    }

    @Operation(summary = "퀴즈 삭제 API")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/quiz/{id}")
    public String deleteQuiz(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        quizService.deleteQuiz(id);
        redirectAttributes.addAttribute("deleted", true);
        return "redirect:/";
    }

    @Operation(summary = "퀴즈 번호로 해당퀴즈 가져오는 API")
    @GetMapping("/front-user/quiz/{id}")
    public @ResponseBody QuizInfoDto findQuiz(@PathVariable Long id) {
        return quizService.getQuizDetail(id);
    }

    @Operation(summary = "랜덤 퀴즈 가져와 프론트에 전달하는 API")
    @GetMapping("/front-user/random-quiz")
    public @ResponseBody List<QuizInfoDto> findRandomQuiz(@RequestParam("number") Long number) {
        return quizService.getRandomQuizzesFromRedis(number);
    }
}
