package com.back.domain.post.postComment.dto;

import com.back.domain.post.postComment.entity.PostComment;

import java.time.LocalDateTime;

/**
 * 댓글 API의 응답 형태를 정의하는 DTO입니다. (DTO와 record를 쓰는 이유는 {@code PostDto} 주석 참고)
 *
 * <p>엔티티 {@link PostComment}에는 {@code post} 필드(어느 글의 댓글인지)가 있지만, DTO에는 넣지 않았습니다.
 * <ul>
 *   <li>요청 URL({@code /posts/{postId}/comments})에 이미 글 번호가 들어 있으므로 다시 보낼 필요가 없다.</li>
 *   <li>post를 넣으면 글 → 댓글 → 글 ... 순환참조 문제가 다시 생긴다.</li>
 * </ul>
 */
public record PostCommentDto(
        int id,
        LocalDateTime createDate,
        LocalDateTime modifyDate,
        String content
) {
    /** 엔티티 → DTO 변환용 보조 생성자. {@code .map(PostCommentDto::new)}로 쓴다. */
    public PostCommentDto(PostComment postComment) {
        this(
                postComment.getId(),
                postComment.getCreateDate(),
                postComment.getModifyDate(),
                postComment.getContent()
        );
    }
}
