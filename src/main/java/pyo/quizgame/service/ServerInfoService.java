package pyo.quizgame.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pyo.quizgame.domain.ServerInfo;
import pyo.quizgame.repository.ServerInfoRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServerInfoService {

    private final ServerInfoRepository serverInfoRepository;
    private final QuizService quizService;
    private final FrontUserService frontUserService;

    @Transactional
    public void updateServerInfo() {
        long totalQuizCount = quizService.getAllQuizCount();
        long totalUserCount = frontUserService.getUserCount();

        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setId(0L); // 고정 ID

        serverInfo.setTotalNumberOfQuiz(totalQuizCount);
        serverInfo.setTotalNumberOfUser(totalUserCount);

        frontUserService.getTopQuizPlayer().ifPresent(serverInfo::setTopQuizPlayer);
        frontUserService.getBadPlayer().ifPresent(serverInfo::setBadQuizPlayer);
        frontUserService.getBestPlayer().ifPresent(serverInfo::setBestQuizPlayer);

        serverInfoRepository.save(serverInfo);
    }

    @Transactional(readOnly = true)
    public Optional<ServerInfo> getLatestInfo() {
        return serverInfoRepository.findById(0L); // 고정 ID로 조회
    }
}
