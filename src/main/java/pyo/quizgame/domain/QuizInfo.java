package pyo.quizgame.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class QuizInfo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private Long answer;

    private String example1;
    private String example2;
    private String example3;
    private String example4;

    private String fileOriginalName;
    private String changeFileOriginalName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = false)
    private S3FileInfo s3FileInfo;

    @Builder
    public QuizInfo(String title, Long answer, String example1, String example2, String example3, String example4,
                    String fileOriginalName, String changeFileOriginalName, S3FileInfo s3FileInfo) {
        this.title = title;
        this.answer = answer;
        this.example1 = example1;
        this.example2 = example2;
        this.example3 = example3;
        this.example4 = example4;
        this.fileOriginalName = fileOriginalName;
        this.changeFileOriginalName = changeFileOriginalName;
        this.s3FileInfo = s3FileInfo;
    }


    public void update(String title, Long answer, String example1, String example2, String example3, String example4,
                       String fileOriginalName, String changeFileOriginalName, S3FileInfo s3FileInfo) {
        this.title = title;
        this.answer = answer;
        this.example1 = example1;
        this.example2 = example2;
        this.example3 = example3;
        this.example4 = example4;
        this.fileOriginalName = fileOriginalName;
        this.changeFileOriginalName = changeFileOriginalName;
        this.s3FileInfo = s3FileInfo;
    }

    public void updateFileOriginalName(String fileOriginalName) {
        this.fileOriginalName = fileOriginalName;
    }
}
