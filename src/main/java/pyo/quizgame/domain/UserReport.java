package pyo.quizgame.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserReport extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId = 0L;
    private Long commentId = 0L;
    private String reporterNickName;
    private String reportContent;

    @Builder
    public UserReport(String reporterNickName, String reportContent, Long postId, Long commentId) {
        this.reporterNickName = reporterNickName;
        this.reportContent = reportContent;
        this.postId = postId;
        this.commentId = commentId;
    }
}
