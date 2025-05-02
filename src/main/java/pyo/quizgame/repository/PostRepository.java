package pyo.quizgame.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pyo.quizgame.domain.UserPost;

import java.util.List;

public interface PostRepository extends JpaRepository<UserPost,Long> {

    
    Page<UserPost> findByTitleContaining(String string, Pageable pageable);
    
    List<UserPost> findByTitleContainingOrderByIdDesc(String string);

    @Transactional
    @Modifying
    @Query(value = "update user_post p set p.view_count = p.view_count + 1 where p.post_id=:id",nativeQuery = true)
    void updatePageView(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query(value = "update user_post p set p.like_count = p.like_count + 1 where p.post_id=:id",nativeQuery = true)
    void addLikes(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query(value = "update user_post p set p.like_count = p.like_count - 1 where p.post_id=:id",nativeQuery = true)
    void unLikes(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query(value = "update user_post p set p.comment_count = p.comment_count + 1 where p.post_id=:id",nativeQuery = true)
    void upCommentCount(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query(value = "update user_post p set p.comment_count = p.comment_count - 1 where p.post_id=:id",nativeQuery = true)
    void downCommentCount(@Param("id") Long id);

    List<UserPost> findByNickName(String string);
}
