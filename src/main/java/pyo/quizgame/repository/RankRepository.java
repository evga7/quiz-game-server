package pyo.quizgame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pyo.quizgame.domain.UserRankInfo;

public interface RankRepository extends JpaRepository<UserRankInfo,Long> {


}
