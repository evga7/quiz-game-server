package pyo.quizgame.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRankInfo {

    @Id
    private Long id;

    private String nickName;

    private Long totalQuizCount;

    private Long solvedQuizCount;

    private String percentageOfAnswer;

    @Builder
    public UserRankInfo(Long id, String nickName, Long totalQuizCount, Long solvedQuizCount, String percentageOfAnswer) {
        this.id = id;
        this.nickName = nickName;
        this.totalQuizCount = totalQuizCount;
        this.solvedQuizCount = solvedQuizCount;
        this.percentageOfAnswer = percentageOfAnswer;
    }
}
