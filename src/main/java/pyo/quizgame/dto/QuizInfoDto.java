package pyo.quizgame.dto;

import lombok.Builder;
import lombok.Data;
import pyo.quizgame.domain.QuizInfo;

@Data
@Builder
public class QuizInfoDto {
    private Long id;
    private String title;
    private Long answer;
    private String example1;
    private String example2;
    private String example3;
    private String example4;

    private String filePath;
    private String fileOriginalName;
    private String changeFileOriginalName;

    public static QuizInfoDto from(QuizInfo quizInfo) {
        return QuizInfoDto.builder()
                .id(quizInfo.getId())
                .title(quizInfo.getTitle())
                .answer(quizInfo.getAnswer())
                .example1(quizInfo.getExample1())
                .example2(quizInfo.getExample2())
                .example3(quizInfo.getExample3())
                .example4(quizInfo.getExample4())
                .filePath(quizInfo.getS3FileInfo().getFilePath())
                .fileOriginalName(quizInfo.getFileOriginalName())
                .changeFileOriginalName(quizInfo.getChangeFileOriginalName())
                .build();
    }
}
