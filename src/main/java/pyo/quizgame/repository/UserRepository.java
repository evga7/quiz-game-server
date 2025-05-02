package pyo.quizgame.repository;

import org.springframework.data.repository.CrudRepository;
import pyo.quizgame.domain.UserInfo;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserInfo, Long> {
    Optional<UserInfo> findByUserId(String userId);
}
