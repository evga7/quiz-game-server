package pyo.quizgame.repository;

import org.springframework.data.repository.CrudRepository;
import pyo.quizgame.domain.FrontUserInfo;
import pyo.quizgame.domain.UserLike;
import pyo.quizgame.domain.UserPost;

import java.util.List;
import java.util.Optional;

public interface UserLikeRepository extends CrudRepository<UserLike,Long> {

    Optional<UserLike> findByFrontUserInfoAndUserPost(FrontUserInfo frontUserInfo, UserPost userPost);

    List<UserLike> findAllByUserPost(UserPost userPost);
}
