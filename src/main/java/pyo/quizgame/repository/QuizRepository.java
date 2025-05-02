package pyo.quizgame.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pyo.quizgame.domain.QuizInfo;

import java.util.List;

public interface QuizRepository extends JpaRepository<QuizInfo,Long> {

    Page<QuizInfo> findByTitleContaining(String title, Pageable pageable);

    //sql용
    @Query(nativeQuery=true, value="SELECT * FROM quiz_info ORDER BY rand() LIMIT :number")
    List<QuizInfo> findRandomQuiz(@Param("number") Long number);

    @Query("SELECT q FROM QuizInfo q WHERE q.id IN :ids")
    List<QuizInfo> findByIdIn(@Param("ids") List<Long> ids);

    @Query("SELECT q.id FROM QuizInfo q")
    List<Long> findAllIds();

//    //postgresql용
//    @Query(nativeQuery=true, value="SELECT * FROM quiz_info ORDER BY random() LIMIT :number")
//    List<QuizInfo> findRandomQuiz(@Param("number") Long number);
}
