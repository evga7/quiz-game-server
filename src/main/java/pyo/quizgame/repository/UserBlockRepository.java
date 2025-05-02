package pyo.quizgame.repository;

import org.springframework.data.repository.CrudRepository;
import pyo.quizgame.domain.FrontUserInfo;
import pyo.quizgame.domain.UserBlock;

import java.util.Optional;

public interface UserBlockRepository extends CrudRepository<UserBlock,Long> {

    Optional<UserBlock> findByFrontUserInfoAndBlockedNickName(FrontUserInfo frontUserInfo,String blockedNickName);
}
