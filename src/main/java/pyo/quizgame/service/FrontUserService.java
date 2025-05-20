package pyo.quizgame.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pyo.quizgame.domain.FrontUserInfo;
import pyo.quizgame.domain.UserBlock;
import pyo.quizgame.domain.UserRankInfo;
import pyo.quizgame.dto.UserStats;
import pyo.quizgame.infra.rabbitmq.QuizResultMessage;
import pyo.quizgame.repository.FrontUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FrontUserService {

    private final FrontUserRepository frontUserRepository;

    @Transactional
    public void signup(FrontUserInfo frontUserInfo) {
        if (!frontUserRepository.existsByNickName(frontUserInfo.getNickName())) {
            frontUserRepository.save(frontUserInfo);
        }
    }

    @Transactional(readOnly = true)
    public List<FrontUserInfo> getRank() {
        return frontUserRepository.getPlayerTop100();
    }

    @Transactional
    public boolean updateUserSync(FrontUserInfo frontUserInfo) {
        Optional<FrontUserInfo> userOpt = frontUserRepository.findByNickName(frontUserInfo.getNickName());
        if (userOpt.isEmpty()) {
            return false;
        }
        frontUserRepository.incrementQuizCount(frontUserInfo.getNickName(),
                frontUserInfo.getTotalQuizCount(),
                frontUserInfo.getSolvedQuizCount());

        updatePercentage(frontUserInfo.getNickName());
        return true;
    }
    @Transactional
    public void updateUserAsync(QuizResultMessage msg) {
        frontUserRepository.incrementQuizCount(
                msg.getNickName(),
                msg.getTotalQuizCount(),
                msg.getSolvedQuizCount()
        );
        updatePercentage(msg.getNickName());

    }


    @Transactional
    public void updatePercentage(String nickName) {
        UserStats stats = frontUserRepository.fetchStats(nickName);
        double percentage = (double) stats.getSolvedQuizCount() / stats.getTotalQuizCount() * 100;
        frontUserRepository.updatePercentage(nickName, String.format("%.2f", percentage));
    }

    @Transactional(readOnly = true)
    public boolean existsByNickName(String nickName) {
        return frontUserRepository.existsByNickName(nickName);
    }

    @Transactional(readOnly = true)
    public FrontUserInfo getUserByNickName(String nickName) {
        return frontUserRepository.findByNickName(nickName).orElseThrow();
    }

    @Transactional(readOnly = true)
    public List<UserBlock> getUserBlocks(String nickName) {
        return frontUserRepository.findByNickName(nickName)
                .map(userInfo -> new ArrayList<>(userInfo.getUserBlocks()))
                .orElseThrow();
    }

    @Transactional(readOnly = true)
    public UserRankInfo getUserRankInfo(String nickName) {
        FrontUserInfo user = frontUserRepository.findByNickName(nickName).orElseThrow();
        Long ranking = frontUserRepository.getPlayerRanking(nickName);

        return UserRankInfo.builder()
                .id(ranking)
                .nickName(user.getNickName())
                .percentageOfAnswer(user.getPercentageOfAnswer())
                .solvedQuizCount(user.getSolvedQuizCount())
                .totalQuizCount(user.getTotalQuizCount())
                .build();
    }

    public String validateNickName(String nickName) {
        if (nickName.isEmpty()) return "empty";
        if (nickName.length() > 8) return "length";
        if (nickName.contains("운영자") || nickName.contains("admin")) return "noNickName";
        if (!nickName.matches("[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝]*")) return "notMatch";
        if (existsByNickName(nickName)) return "exists";
        return "OK";
    }





    @Transactional(readOnly = true)
    public long getUserCount() {
        return frontUserRepository.count();
    }

    @Transactional(readOnly = true)
    public Optional<FrontUserInfo> getTopQuizPlayer() {
        return frontUserRepository.findTop1ByOrderByTotalQuizCountDesc();
    }

    @Transactional(readOnly = true)
    public Optional<FrontUserInfo> getBadPlayer() {
        return frontUserRepository.findBadPlayer();
    }

    @Transactional(readOnly = true)
    public Optional<FrontUserInfo> getBestPlayer() {
        return frontUserRepository.getBestPlayer();
    }

    @Transactional
    public void upBlockCount(String nickName) {
        frontUserRepository.upBlockCount(nickName);
    }

    @Transactional
    public void downBlockCount(String nickName) {
        frontUserRepository.downBlockCount(nickName);
    }
    @Transactional
    public void upCommentCount(String nickName) {
        frontUserRepository.upCommentCount(nickName);
    }

    @Transactional
    public void downCommentCount(String nickName) {
        frontUserRepository.downCommentCount(nickName);
    }
    @Transactional
    public void upPostCount(String nickName) {
        frontUserRepository.upPostCount(nickName);
    }

    @Transactional
    public void downPostCount(String nickName) {
        frontUserRepository.downPostCount(nickName);
    }




}
