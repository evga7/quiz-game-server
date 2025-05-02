package pyo.quizgame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pyo.quizgame.domain.FrontUserInfo;
import pyo.quizgame.dto.UserStats;

import java.util.List;
import java.util.Optional;

public interface FrontUserRepository extends JpaRepository<FrontUserInfo,Long> {

    boolean existsByNickName(String nickName);

    Optional<FrontUserInfo> findByNickName(String nickName);
    @Query(nativeQuery = true,value = "select * from front_user_info WHERE total_quiz_count >= 30 " +
            "order by percentage_of_answer desc limit 100")
    List<FrontUserInfo> getPlayerTop100();


    @Query(nativeQuery = true,value = "SELECT COUNT(*) FROM front_user_info " +
            "WHERE percentage_of_answer > ( SELECT percentage_of_answer FROM front_user_info WHERE nick_name =  ?1 )")
    Long getPlayerRanking(String nickName);

    //가장 많이푼사람 1명
    Optional<FrontUserInfo> findTop1ByOrderByTotalQuizCountDesc();


    @Query(nativeQuery = true,value = "select * from front_user_info " +
            "WHERE total_quiz_count > 30 order by percentage_of_answer limit 1")
    Optional<FrontUserInfo> findBadPlayer();


    @Query(nativeQuery = true,value = "select * from front_user_info " +
            "WHERE total_quiz_count > 30 order by percentage_of_answer desc limit 1")
    Optional<FrontUserInfo> getBestPlayer();

    @Transactional
    @Modifying
    @Query(value = "update front_user_info p set p.number_of_posts = p.number_of_posts + 1 where p.nick_name=:nick_name",nativeQuery = true)
    void upPostCount(@Param("nick_name") String nickName);

    @Transactional
    @Modifying
    @Query(value = "update front_user_info p set p.number_of_posts = p.number_of_posts - 1 where p.nick_name=:nick_name",nativeQuery = true)
    void downPostCount(@Param("nick_name") String nickName);

    @Transactional
    @Modifying
    @Query(value = "update front_user_info p set p.number_of_comments = p.number_of_comments + 1 where p.nick_name=:nick_name",nativeQuery = true)
    void upCommentCount(@Param("nick_name") String nickName);

    @Transactional
    @Modifying
    @Query(value = "update front_user_info p set p.number_of_comments = p.number_of_comments - 1 where p.nick_name=:nick_name",nativeQuery = true)
    void downCommentCount(@Param("nick_name") String nickName);

    @Transactional
    @Modifying
    @Query(value = "update front_user_info p set p.number_of_blocked_user = p.number_of_blocked_user + 1 where p.nick_name=:nick_name",nativeQuery = true)
    void upBlockCount(@Param("nick_name") String nickName);

    @Transactional
    @Modifying
    @Query(value = "update front_user_info p set p.number_of_blocked_user = p.number_of_blocked_user - 1 where p.nick_name=:nick_name",nativeQuery = true)
    void downBlockCount(@Param("nick_name") String nickName);

    @Modifying
    @Query("UPDATE FrontUserInfo f SET " +
            "f.totalQuizCount = f.totalQuizCount + :total, " +
            "f.solvedQuizCount = f.solvedQuizCount + :solved " +
            "WHERE f.nickName = :nickName")
    void incrementQuizCount(@Param("nickName") String nickName,
                            @Param("total") long total,
                            @Param("solved") long solved);

    @Query("SELECT new pyo.quizgame.dto.UserStats(" +
            "  f.totalQuizCount, " +
            "  f.solvedQuizCount" +
            ") FROM FrontUserInfo f WHERE f.nickName = :n")
    UserStats fetchStats(@Param("n") String nickName);


    @Modifying
    @Query("UPDATE FrontUserInfo f SET f.percentageOfAnswer = :p " +
            "WHERE f.nickName = :n")
    void updatePercentage(@Param("n") String nickName,
                          @Param("p") String percentage);


}
