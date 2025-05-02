package pyo.quizgame.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @JsonBackReference
    private UserPost userPost;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 명시 추가
    private FrontUserInfo frontUserInfo;

    // 빌더 생성자는 유지
    public UserLike(FrontUserInfo frontUserInfo, UserPost userPost) {
        this.frontUserInfo = frontUserInfo;
        this.userPost = userPost;
    }
}
