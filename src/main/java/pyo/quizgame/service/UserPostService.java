package pyo.quizgame.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pyo.quizgame.domain.UserPost;
import pyo.quizgame.repository.PostRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserPostService {

    private final PostRepository postRepository;
    public void save(UserPost userPost){
        postRepository.save(userPost);
    }
    public Optional<UserPost> getUserPostByPostId(Long id)
    {
        return postRepository.findById(id);
    }
    public List<UserPost> getUserBoardList(String searchText) {

        return postRepository.findByTitleContainingOrderByIdDesc(searchText);
    }
    public void updateView(Long id)
    {
        postRepository.updatePageView(id);
    }
    public void downCommentCount(Long id){
        postRepository.downCommentCount(id);
    }
    public void upCommentCount(Long id)
    {
        postRepository.upCommentCount(id);
    }
    public List<UserPost> getUserPostByNickName(String nickName)
    {
        return postRepository.findByNickName(nickName);
    }
    public void deletePost(UserPost userPost)
    {
        postRepository.delete(userPost);
    }
}
