package pyo.quizgame.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pyo.quizgame.domain.FrontUserInfo;
import pyo.quizgame.domain.UserLike;
import pyo.quizgame.domain.UserPost;
import pyo.quizgame.dto.LikeResult;
import pyo.quizgame.repository.PostRepository;
import pyo.quizgame.repository.UserLikeRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserLikeService {

    private final UserLikeRepository likeRepository;
    private final PostRepository postRepository;
    private final FrontUserService frontUserService;
    private final UserPostService userPostService;

    @Transactional
    public LikeResult toggleLike(String nickName, Long postId) {
        FrontUserInfo user = frontUserService.getUserByNickName(nickName);
        UserPost post = userPostService.getUserPostByPostId(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Optional<UserLike> existingLike = likeRepository.findByFrontUserInfoAndUserPost(user, post);

        boolean liked;
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            postRepository.unLikes(post.getId());
            post.subUserLike();
            liked = false;
        } else {
            likeRepository.save(new UserLike(user, post));
            postRepository.addLikes(post.getId());
            post.addUserLike();
            liked = true;
        }

        return new LikeResult(liked, post);
    }



    @Transactional(readOnly = true)
    public Optional<UserLike> isAlreadyLike(FrontUserInfo frontUserInfo, UserPost userPost) {
        return likeRepository.findByFrontUserInfoAndUserPost(frontUserInfo, userPost);
    }

    @Transactional(readOnly = true)
    public List<UserLike> getUserLikeByPostId(UserPost userPost) {
        return likeRepository.findAllByUserPost(userPost);
    }
}
