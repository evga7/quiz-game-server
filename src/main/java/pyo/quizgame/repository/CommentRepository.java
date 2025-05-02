package pyo.quizgame.repository;

import org.springframework.data.repository.CrudRepository;
import pyo.quizgame.domain.UserComment;

import java.util.List;

public interface CommentRepository extends CrudRepository<UserComment,Long> {

    List<UserComment> findByNickName(String nickName);

}
