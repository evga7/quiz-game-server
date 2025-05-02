package pyo.quizgame.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class UserPost extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @NotNull
    private String title;

    private String nickName;

    private Long likeCount;

    private Long viewCount;

    @NotNull
    private String content;

    private Long commentCount;

    @OneToMany(mappedBy = "userPost", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonManagedReference
    private List<UserComment> userComments = new ArrayList<>();

    public void addComment(UserComment userComment) {
        userComments.add(userComment);
        userComment.setUserPost(this);
    }

    public void deleteComment(UserComment userComment) {
        userComments.remove(userComment);
    }

    public void addUserLike() {
        this.likeCount += 1;
    }

    public void subUserLike() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    public void upViewCount() {
        this.viewCount += 1;
    }

    public void upCommentCount() {
        this.commentCount += 1;
    }

    public void downCommentCount() {
        this.commentCount = Math.max(0, this.commentCount - 1);
    }

    public void updatePost(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
