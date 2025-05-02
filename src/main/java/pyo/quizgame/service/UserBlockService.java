package pyo.quizgame.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pyo.quizgame.domain.FrontUserInfo;
import pyo.quizgame.domain.UserBlock;
import pyo.quizgame.repository.UserBlockRepository;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserBlockService {

    private final UserBlockRepository userBlockRepository;
    private final FrontUserService frontUserService;

    @Transactional
    public void blockUser(String nickName, String blockedNickName) {
        FrontUserInfo user = frontUserService.getUserByNickName(nickName);
        Optional<UserBlock> existingBlock = findBlockUser(user, blockedNickName);

        if (existingBlock.isEmpty()) {
            userBlockRepository.save(new UserBlock(user, blockedNickName));
            frontUserService.upBlockCount(nickName);
        }
    }

    @Transactional
    public Set<UserBlock> unblockUser(String nickName, String blockedNickName) {
        FrontUserInfo user = frontUserService.getUserByNickName(nickName);
        Optional<UserBlock> block = findBlockUser(user, blockedNickName);

        block.ifPresent(userBlock -> {
            user.unBlockUser(userBlock);
            userBlockRepository.delete(userBlock);
            frontUserService.downBlockCount(nickName);
        });

        return user.getUserBlocks();
    }

    @Transactional(readOnly = true)
    public Optional<UserBlock> findBlockUser(FrontUserInfo frontUserInfo, String blockedNickName) {
        return userBlockRepository.findByFrontUserInfoAndBlockedNickName(frontUserInfo, blockedNickName);
    }
}
