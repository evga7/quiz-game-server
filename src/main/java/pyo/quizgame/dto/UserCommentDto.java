package pyo.quizgame.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCommentDto {
    private Long id;
    private String nickName;
    private Long parentId;

    @NotEmpty(message = "댓글을 입력해주세요")
    @Size(min = 1, max = 45, message = "댓글은 1~45자 사이로 입력해주세요.")
    private String content;

    private String createdDate;
    private String modifiedDate;
    private Boolean isDeleted;

    // 대댓글 리스트 추가
    @Builder.Default
    private List<UserCommentDto> replies = new ArrayList<>();
}
