package pyo.quizgame.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pyo.quizgame.domain.ServerInfo;
import pyo.quizgame.service.ServerInfoService;

@RestController
@RequiredArgsConstructor
public class ServerInfoController {

    private final ServerInfoService serverInfoService;

    private static final String APP_VERSION = "0.0.6-SNAPSHOT";

    @Operation(summary = "서버 정보 1시간마다 업데이트")
    @Scheduled(cron = "0 0 0/1 * * *")
    @GetMapping("/cron-total-number-of-quiz")
    public String cronServerInfo() {
        serverInfoService.updateServerInfo();
        return "OK";
    }

    @Operation(summary = "최신 서버 정보 가져오기")
    @GetMapping("/front-user/server-info")
    public ServerInfo getServerInfo() {
        return serverInfoService.getLatestInfo()
                .orElseGet(ServerInfo::new); // Optional 비어있으면 빈 객체 반환
    }

    @Operation(summary = "버전 체크 API")
    @GetMapping("/front-user/version-check")
    public String versionCheck() {
        return APP_VERSION;
    }
}
