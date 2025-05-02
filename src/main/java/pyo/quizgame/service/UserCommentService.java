package pyo.quizgame.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pyo.quizgame.domain.UserComment;
import pyo.quizgame.domain.UserPost;
import pyo.quizgame.repository.CommentRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCommentService {

    private final CommentRepository commentRepository;

    public List<UserComment> getUserCommentByNickName(String nickName) {
        return commentRepository.findByNickName(nickName);
    }

    public UserComment save(UserComment userComment) {
        return commentRepository.save(userComment);
    }

    public Optional<UserComment> getUserCommentById(Long id) {
        return commentRepository.findById(id);
    }

    public void delete(UserComment userComment) {
        commentRepository.delete(userComment);
    }

    // ✅ 수정: 일반 댓글 추가
    public UserComment addComment(String nickName, String content, UserPost userPost) {
        UserComment comment = UserComment.create(nickName, content, userPost);
        return commentRepository.save(comment);
    }

    // ✅ 수정: 대댓글 추가
    public UserComment addReply(Long parentId, String nickName, String content, UserPost userPost) {
        UserComment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));

        UserComment reply = UserComment.create(nickName, content, userPost);
        parent.addReply(reply);

        return commentRepository.save(reply);
    }
}
