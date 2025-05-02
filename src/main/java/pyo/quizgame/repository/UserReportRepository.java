package pyo.quizgame.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pyo.quizgame.domain.UserReport;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {

    Page<UserReport> findByReportContentContaining(String content, Pageable pageable);
}
