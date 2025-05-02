package pyo.quizgame.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class FrontUserInfo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nickName;

    private Long totalQuizCount = 0L;
    private Long solvedQuizCount = 0L;
    private String percentageOfAnswer = "0.00";
    private Long numberOfPosts = 0L;
    private Long numberOfComments = 0L;
    private Long numberOfBlockedUser = 0L;

    @OneToMany(mappedBy = "frontUserInfo", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    private Set<UserBlock> userBlocks = new HashSet<>();

    public FrontUserInfo(Long id, String nickName) {
        this.id = id;
        this.nickName = nickName;
    }

    public void addBlockUser(UserBlock blockUser) {
        userBlocks.add(blockUser);
        blockUser.setFrontUserInfo(this);
    }

    public void unBlockUser(UserBlock blockUser) {
        userBlocks.remove(blockUser);
        blockUser.setFrontUserInfo(null);
    }
}
