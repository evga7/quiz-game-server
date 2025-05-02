package pyo.quizgame.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pyo.quizgame.domain.UserMessage;

public interface MessageRepository extends JpaRepository<UserMessage,Long> {

    Page<UserMessage> findByMessageContaining(String title, Pageable pageable);
}
