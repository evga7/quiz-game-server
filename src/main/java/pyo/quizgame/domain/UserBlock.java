package pyo.quizgame.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String blockedNickName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @ToString.Exclude
    private FrontUserInfo frontUserInfo;

    public UserBlock(FrontUserInfo frontUserInfo, String blockedNickName) {
        this.frontUserInfo = frontUserInfo;
        this.blockedNickName = blockedNickName;
        frontUserInfo.addBlockUser(this);
    }
    public void setFrontUserInfo(FrontUserInfo frontUserInfo) {
        this.frontUserInfo = frontUserInfo;
    }

}
