package pyo.quizgame.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pyo.quizgame.domain.UserReport;
import pyo.quizgame.repository.UserReportRepository;

@RequiredArgsConstructor
@Service
public class UserReportService {

    private final UserReportRepository userReportRepository;

    public Page<UserReport> getUserReports(String searchText, Pageable pageable) {
        return userReportRepository.findByReportContentContaining(searchText, pageable);
    }

    public void save(UserReport userReport) {
        userReportRepository.save(userReport);
    }
}
