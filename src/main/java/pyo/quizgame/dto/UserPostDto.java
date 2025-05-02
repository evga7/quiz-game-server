package pyo.quizgame.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPostDto {

    private Long id;
    private String nickName;

    @Size(min = 2, max = 25, message = "제목을 2~25자 사이로 입력해주세요.")
    private String title;

    @Size(min = 2, max = 500, message = "내용은 2~500자 사이로 입력해주세요.")
    private String content;

    private String createdDate;
    private String modifiedDate;


    // 추가 필드들
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;

    @Builder.Default
    private List<UserCommentDto> comments = new ArrayList<>();
}