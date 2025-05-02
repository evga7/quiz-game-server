package pyo.quizgame.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pyo.quizgame.domain.UserRankInfo;
import pyo.quizgame.service.RankService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RankController {

    private final RankService rankService;

    @Operation(summary = "TOP 100 유저 랭킹 조회")
    @GetMapping("/front-user/user-rank")
    public List<UserRankInfo> getUserRank() {
        return rankService.getTopUserRank();
    }
}
