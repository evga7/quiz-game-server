package pyo.quizgame.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pyo.quizgame.domain.FrontUserInfo;
import pyo.quizgame.domain.UserBlock;
import pyo.quizgame.domain.UserComment;
import pyo.quizgame.domain.UserPost;
import pyo.quizgame.dto.UserCommentDto;
import pyo.quizgame.dto.UserPostDto;
import pyo.quizgame.service.FrontUserService;
import pyo.quizgame.service.UserPostService;
import pyo.quizgame.validate.PostValidator;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class UserPostController {

    private final UserPostService userPostService;
    private final FrontUserService frontUserService;
    private final PostValidator postValidator;

    @Operation(summary = "유저 게시물 작성")
    @PostMapping("/front-user/user-post")
    public String userPost(@Valid UserPostDto userPostDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors())
            return "add-post";
        String nickName = userPostDto.getNickName();
        UserPost userPost = UserPost.builder()
                .nickName(userPostDto.getNickName())
                .title(userPostDto.getTitle())
                .content(userPostDto.getContent())
                .commentCount(0L)
                .viewCount(0L)
                .viewCount(0L)
                .likeCount(0L)
                .build();
        redirectAttributes.addAttribute("nickName", nickName);
        userPostService.save(userPost);
        frontUserService.upPostCount(userPost.getNickName());
        return "redirect:/user-boards";
    }


    @Operation(summary = "게시물 수정하기")
    @PostMapping("/front-user/user-post/edit-post")
    public String userEditPost(UserPostDto userPostDto, @RequestParam String nickName, Long postId, RedirectAttributes redirectAttributes, Model model) {
        Map<String, String> errors = postValidator.validateEditPost(userPostDto);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("beforeContent", userPostDto.getContent());
            model.addAttribute("beforeTitle", userPostDto.getTitle());
            model.addAttribute("postId", postId);
            redirectAttributes.addAttribute("postId", postId);
            return "edit-post";
        }

        Optional<UserPost> userPostByPostId = userPostService.getUserPostByPostId(postId);
        if (userPostByPostId.isPresent()) {
            UserPost userPost = userPostByPostId.get();
            userPost.updatePost(userPostDto.getTitle(), userPostDto.getContent());
            userPostService.save(userPost);
        }
        redirectAttributes.addAttribute("nickName" , nickName);
        return "redirect:/user-boards";
    }


    @Operation(summary = "유저 게시판 가져오기")
    @GetMapping("/user-boards")
    public String getUserPostPage(Model model, @PageableDefault(size = 10, sort = "id",direction = Sort.Direction.DESC) Pageable pageable,
                                  @RequestParam(required = false, defaultValue = "") String searchText,@RequestParam String nickName) {
        Page<UserPost> userBoards = getUserBoards(searchText, pageable, nickName);
        int startPage = 1;
        int totalPages = userBoards.getTotalPages();
        int currentPageNumber = userBoards.getPageable().getPageNumber()+1;
        model.addAttribute("currentPageNumber",currentPageNumber);
        model.addAttribute("startPage", startPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("userBoards", userBoards);
        return "user-boards";
    }

    @Operation(summary = "게시판 게시글 조회")
    @GetMapping("/front-user/user-post/{postId}")
    public String getUserPost(@PathVariable Long postId, Model model, @RequestParam String nickName) {
        UserPost userPost = userPostService.getUserPostByPostId(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물"));
        UserPostDto userPostDto = createUserPostWithCommentDto(userPost);
        userPostService.updateView(postId);
        userPost.upViewCount();
        model.addAttribute("post", userPost);
        model.addAttribute("comments", userPostDto.getComments());
        return "posts";
    }

    private UserPostDto createUserPostWithCommentDto(UserPost userPost) {
        // 최상위 댓글 리스트
        List<UserComment> allComments = userPost.getUserComments();
        Map<Long, UserCommentDto> commentDtoMap = new LinkedHashMap<>();

        // 모든 댓글 DTO를 생성해 맵에 저장
        for (UserComment comment : allComments) {
            commentDtoMap.put(comment.getId(), toFlatDto(comment));
        }

        // 부모-자식 관계 설정 (반복문으로 처리)
        for (UserCommentDto dto : commentDtoMap.values()) {
            if (dto.getParentId() != null) {
                UserCommentDto parentDto = commentDtoMap.get(dto.getParentId());
                parentDto.getReplies().add(dto);
            }
        }

        // 최상위 댓글만 반환
        List<UserCommentDto> topLevelComments = commentDtoMap.values().stream()
                .filter(dto -> dto.getParentId() == null)
                .collect(Collectors.toList());

        return UserPostDto.builder()
                .id(userPost.getId())
                .nickName(userPost.getNickName())
                .title(userPost.getTitle())
                .content(userPost.getContent())
                .createdDate(userPost.getCreatedDate())
                .modifiedDate(userPost.getModifiedDate())
                .viewCount(userPost.getViewCount())
                .commentCount(userPost.getCommentCount())
                .likeCount(userPost.getLikeCount())
                .comments(topLevelComments)
                .build();
    }

    private UserCommentDto toFlatDto(UserComment comment) {
        return UserCommentDto.builder()
                .id(comment.getId())
                .nickName(comment.getNickName())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .modifiedDate(comment.getModifiedDate())
                .isDeleted(comment.getIsDeleted())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .replies(new ArrayList<>()) // 초기 빈 리스트 설정
                .build();
    }



    @Operation(summary = "게시글 작성 페이지")
    @GetMapping("/front-user/user-post/add-post")
    public String addPost(Model model,@RequestParam String nickName)
    {
        model.addAttribute("userPostDto", new UserPostDto());
        return "add-post";
    }

    @Operation(summary = "게시글 수정 페이지")
    @GetMapping("/front-user/user-post/edit-post")
    public String editPost(Model model,@RequestParam String nickName,@RequestParam Long postId)
    {
        model.addAttribute("userPostDto", new UserPostDto());
        Optional<UserPost> userPostByPostId = userPostService.getUserPostByPostId(postId);
        model.addAttribute("beforeTitle",userPostByPostId.get().getTitle());
        model.addAttribute("beforeContent",userPostByPostId.get().getContent());
        model.addAttribute("postId",postId);
        return "edit-post";
    }

    @Operation(summary = "게시글 삭제하기")
    @PostMapping("/front-user/user-post/del-post")
    @ResponseBody
    public String delPost(Long id, String nickName) {
        Optional<UserPost> userPostOpt = userPostService.getUserPostByPostId(id);
        if (userPostOpt.isEmpty() || !userPostOpt.get().getNickName().equals(nickName)) {
            return nickName;
        }

        UserPost userPost = userPostOpt.get();
        if (userPost.getCommentCount() > 0) {
            return nickName; // 댓글이 있으면 삭제 불가
        }

        frontUserService.downPostCount(nickName);
        //userLikeService.deleteLikesByPost를 호출해 게시물과 연결된 좋아요 데이터를 한 번에 삭제합니다.
        //userLikeService.deleteLikesByPost(userPost);
        userPostService.deletePost(userPost);

        return nickName;
    }




    public Page<UserPost> getUserBoards(String searchText, Pageable pageable, String nickName) {
        List<UserPost> oriUserBoards = userPostService.getUserBoardList(searchText);

        FrontUserInfo user = frontUserService.getUserByNickName(nickName); // ✅ Optional 아님
        Set<UserBlock> userBlocks = user.getUserBlocks();

        Set<String> userBlocksSet = new HashSet<>();
        for (UserBlock userBlock : userBlocks) {
            userBlocksSet.add(userBlock.getBlockedNickName());
        }

        List<UserPost> userPostList = new ArrayList<>();
        for (UserPost oriUserBoard : oriUserBoards) {
            if (userBlocksSet.contains(oriUserBoard.getNickName())) continue;
            userPostList.add(oriUserBoard);
        }

        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), userPostList.size());

        return new PageImpl<>(userPostList.subList(start, end), pageable, userPostList.size());
    }

}
