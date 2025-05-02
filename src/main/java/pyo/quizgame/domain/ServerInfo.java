package pyo.quizgame.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ServerInfo {

    @Id
    private Long id;

    private Long totalNumberOfQuiz;
    private Long totalNumberOfUser;

    @OneToOne
    @JoinColumn(name = "top_quiz_player_id")
    private FrontUserInfo topQuizPlayer;

    @OneToOne
    @JoinColumn(name = "bad_quiz_player_id")
    private FrontUserInfo badQuizPlayer;

    @OneToOne
    @JoinColumn(name = "best_quiz_player_id")
    private FrontUserInfo bestQuizPlayer;
}
