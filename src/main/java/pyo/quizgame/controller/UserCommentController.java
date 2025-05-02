package pyo.quizgame.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pyo.quizgame.domain.UserComment;
import pyo.quizgame.domain.UserPost;
import pyo.quizgame.dto.UserCommentDto;
import pyo.quizgame.dto.UserPostDto;
import pyo.quizgame.service.FrontUserService;
import pyo.quizgame.service.UserCommentService;
import pyo.quizgame.service.UserPostService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/front-user/user-post")
public class UserCommentController {
    private final UserCommentService userCommentService;
    private final UserPostService userPostService;
    private final FrontUserService frontUserService;

    // 댓글 작성
    @Operation(summary = "댓글 쓰기")
    @PostMapping("/comment")
    public String writeComment(Long id, String nickName, String content, Model model) {
        UserPost userPost = userPostService.getUserPostByPostId(id).orElseThrow();
        if (StringUtils.isEmpty(content) || content.length() > 45) {
            model.addAttribute("errors", "댓글은 1~45자 이내로 작성해주세요.");
        } else {
            UserComment comment = makeComment(nickName, content, userPost);
            userCommentService.save(comment);
            userPost.addComment(comment);
            userPost.upCommentCount();
            frontUserService.upCommentCount(nickName);
        }
        loadComments(model, userPost);
        return "posts :: #comment-content";
    }

    // 대댓글 작성
    @Operation(summary = "대댓글 쓰기")
    @PostMapping("/comment/{parentId}/reply")
    public String writeReply(@PathVariable Long parentId, Long postId, String nickName, String content, Model model) {
        UserPost userPost = userPostService.getUserPostByPostId(postId).orElseThrow();
        UserComment parentComment = userCommentService.getUserCommentById(parentId).orElseThrow();

        if (StringUtils.isEmpty(content) || content.length() > 45) {
            model.addAttribute("errors", "대댓글은 1~45자 이내로 작성해주세요.");
        } else {
            UserComment reply = makeComment(nickName, content, userPost);
            parentComment.addReply(reply);
            userCommentService.save(reply);
            userPost.upCommentCount();
            frontUserService.upCommentCount(nickName);
        }
        loadComments(model, userPost);
        return "posts :: #comment-content";
    }

    // 댓글 수정
    @Operation(summary = "댓글 수정")
    @PostMapping("/edit-comment")
    public String editComment(Long id, String content, Long postId, Model model) {
        UserPost userPost = userPostService.getUserPostByPostId(postId).orElseThrow();
        UserComment comment = userCommentService.getUserCommentById(id).orElseThrow();

        if (StringUtils.isEmpty(content) || content.length() > 45) {
            model.addAttribute("errors", "댓글은 1~45자 이내로 수정해주세요.");
        } else {
            comment.setContent(content);
            userCommentService.save(comment);
        }
        loadComments(model, userPost);
        return "posts :: #comment-content";
    }

    // 댓글 삭제
    @Operation(summary = "댓글 삭제")
    @PostMapping("/del-comment")
    public String deleteComment(Long id, Long postId, String nickName, Model model) {
        UserPost userPost = userPostService.getUserPostByPostId(postId).orElseThrow();
        UserComment comment = userCommentService.getUserCommentById(id).orElseThrow();
        comment.markAsDeleted();
        userPost.downCommentCount();
        frontUserService.downCommentCount(nickName); // 사용자 댓글 수 감소
        loadComments(model, userPost);
        return "posts :: #comment-content";
    }

    // 공통 메서드: 댓글 트리 재로딩
    private void loadComments(Model model, UserPost userPost) {
        UserPostDto postDto = createUserPostWithCommentDto(userPost);
        model.addAttribute("comments", postDto.getComments());
    }

    // 댓글/대댓글 생성
    private UserComment makeComment(String nickName, String content, UserPost userPost) {
        UserComment comment = new UserComment();
        comment.setNickName(nickName);
        comment.setContent(content);
        comment.setUserPost(userPost);
        return comment;
    }

    // 트리 구조 변환
    private UserPostDto createUserPostWithCommentDto(UserPost userPost) {
        return UserPostDto.builder()
                .id(userPost.getId())
                .comments(buildCommentTree(userPost.getUserComments()))
                .build();
    }


    private List<UserCommentDto> buildCommentTree(List<UserComment> comments) {
        Map<Long, UserCommentDto> commentMap = new HashMap<>();
        List<UserCommentDto> rootComments = new ArrayList<>();

        for (UserComment comment : comments) {
            UserCommentDto dto = new UserCommentDto();
            dto.setId(comment.getId());
            dto.setNickName(comment.getNickName());
            dto.setContent(comment.getContent());
            dto.setIsDeleted(comment.getIsDeleted());
            dto.setReplies(new ArrayList<>());
            dto.setModifiedDate(comment.getModifiedDate());
            commentMap.put(comment.getId(), dto);
        }

        for (UserComment comment : comments) {
            if (comment.getParent() == null) {
                rootComments.add(commentMap.get(comment.getId()));
            } else {
                UserCommentDto parentDto = commentMap.get(comment.getParent().getId());
                if (parentDto != null) {
                    parentDto.getReplies().add(commentMap.get(comment.getId()));
                }
            }
        }

        return rootComments;
    }

}
