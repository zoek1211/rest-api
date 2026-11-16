package com.back.domain.post.postComment.controller;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import com.back.domain.post.postComment.dto.PostCommentDto;
import com.back.domain.post.postComment.entity.PostComment;
import com.back.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 댓글(PostComment) REST API 컨트롤러입니다.
 *
 * <p><b>URL 설계: {@code /api/v1/posts/{postId}/comments}</b>
 * <br>댓글은 항상 특정 글에 속하므로, URL도 "글 아래의 댓글"이라는 계층 구조로 표현했습니다.
 * 예) {@code /posts/1/comments/2} = "1번 글의 2번 댓글"
 * <br>클래스 레벨 매핑에 {@code {postId}}가 들어 있으므로 모든 메서드가 {@code @PathVariable int postId}를 받을 수 있습니다.
 *
 * <p>댓글 전용 서비스가 없고 {@link PostService}를 씁니다. 댓글은 항상 "글을 먼저 찾고 → 그 글의 댓글을 다루는" 흐름이기 때문입니다.
 * (그 밖의 공통 설명은 {@code ApiV1PostController} 주석 참고)
 */
@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class ApiV1PostCommentController {
    private final PostService postService;

    /**
     * 댓글 다건조회: {@code GET /api/v1/posts/{postId}/comments}
     *
     * <p>{@code post.getComments()}를 처음 호출하는 순간 지연 로딩(LAZY)으로 댓글 select 쿼리가 나갑니다.
     * 트랜잭션(readOnly) 안이므로 문제없이 로딩됩니다. Post의 {@code @OrderBy("id")} 덕분에 id 순으로 옵니다.
     */
    @GetMapping
    @Transactional(readOnly = true)
    public List<PostCommentDto> getItems(
            @PathVariable int postId
    ) {
        Post post = postService.findById(postId).get();

        return post.getComments()
                .stream()
                .map(PostCommentDto::new)
                .toList();
    }

    /**
     * 댓글 단건조회: {@code GET /api/v1/posts/{postId}/comments/{id}}
     *
     * <p>URL 변수가 두 개({@code postId}, {@code id})이므로 {@code @PathVariable}도 두 개입니다.
     * 파라미터 이름과 URL 변수 이름이 같아서 자동으로 연결됩니다.
     * <br>{@code post.findCommentById(id)}로 찾기 때문에, 다른 글의 댓글 id를 넣으면 찾지 못합니다.
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public PostCommentDto getItem(
            @PathVariable int postId,
            @PathVariable int id
    ) {
        Post post = postService.findById(postId).get();
        PostComment postComment = post.findCommentById(id).get();

        return new PostCommentDto(postComment);
    }

    /**
     * 댓글 삭제: {@code GET /api/v1/posts/{postId}/comments/{id}/delete}
     * (GET으로 삭제하는 것은 임시입니다. 이유는 ApiV1PostController.delete 주석 참고)
     *
     * <p><b>{@code @Transactional}이 꼭 필요한 이유</b>
     * <br>{@code postService.deleteComment()}는 DELETE 쿼리를 직접 실행하지 않고,
     * 글의 comments 리스트에서 댓글을 빼기만 합니다. 실제 delete 쿼리는 트랜잭션이 커밋될 때
     * JPA가 변경을 감지(더티체킹)하고 orphanRemoval 규칙에 따라 실행합니다.
     * 트랜잭션이 없으면 이 감지가 일어나지 않아서, 응답은 "삭제되었습니다"인데 DB에는 그대로 남습니다.
     */
    @GetMapping("/{id}/delete")
    @Transactional
    public RsData<Void> delete(
            @PathVariable int postId,
            @PathVariable int id
    ) {
        Post post = postService.findById(postId).get();
        PostComment postComment = post.findCommentById(id).get();

        postService.deleteComment(post, postComment);

        return new RsData<>(
                "200-1",
                "%d번 댓글이 삭제되었습니다.".formatted(id)
        );
    }
}
