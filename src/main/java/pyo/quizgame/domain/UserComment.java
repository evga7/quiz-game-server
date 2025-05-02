package pyo.quizgame.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Setter
    private String nickName;

    @Setter
    @Column(nullable = false)
    private String content;

    @Setter
    private Long likes = 0L;

    @Setter
    private Boolean isDeleted = false;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @JsonBackReference
    private UserPost userPost;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonBackReference
    private UserComment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UserComment> replies = new ArrayList<>();

    public void addReply(UserComment reply) {
        this.replies.add(reply);
        reply.setParent(this);
    }
    public static UserComment create(String nickName, String content, UserPost post) {
        UserComment comment = new UserComment();
        comment.setNickName(nickName);
        comment.setContent(content);
        comment.setUserPost(post);
        return comment;
    }


    public void markAsDeleted() {
        this.isDeleted = true;
    }
}
