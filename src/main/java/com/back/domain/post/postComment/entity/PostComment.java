package com.back.domain.post.postComment.entity;

import com.back.domain.post.post.entity.Post;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

/**
 * 댓글(PostComment) 엔티티입니다. DB의 {@code post_comment} 테이블과 연결됩니다.
 * (클래스 이름 PostComment → 스네이크 케이스 테이블 이름 post_comment)
 *
 * <p>댓글은 항상 하나의 글에 속합니다. 댓글을 만들거나 지울 때는
 * 이 클래스를 직접 다루지 않고 {@link Post#addComment}, {@link Post#deleteComment}를 거칩니다.
 */
@Entity
@Getter
@NoArgsConstructor // JPA용 기본 생성자
public class PostComment extends BaseEntity {
    /**
     * 이 댓글이 속한 글 (N:1 관계).
     *
     * <ul>
     *   <li>{@code @ManyToOne}: 여러 댓글이 하나의 글을 가리킨다. post_comment 테이블에
     *       외래키 컬럼 {@code post_id}가 생긴다. 이쪽이 연관관계의 "주인"이다.</li>
     *   <li>{@code fetch = LAZY}(지연 로딩): 댓글을 조회할 때 글을 바로 같이 가져오지 않는다.
     *       {@code getPost().getTitle()}처럼 실제로 글 데이터를 쓸 때 그제서야 select 쿼리를 날린다.
     *       {@code @ManyToOne}의 기본값은 EAGER(즉시 로딩)라서, 불필요한 조인을 막으려고 LAZY로 바꿨다.</li>
     * </ul>
     *
     * <p>참고: 이 필드 때문에 엔티티를 JSON으로 바로 응답하면 글 → 댓글들 → 글 → ... 순환참조가 생깁니다.
     * DTO({@code PostCommentDto})로 변환해서 응답하는 이유 중 하나입니다.
     */
    @ManyToOne(fetch = LAZY)
    private Post post;
    private String content;

    /** {@link Post#addComment}에서만 호출하는 것을 권장한다. */
    public PostComment(Post post, String content) {
        this.post = post;
        this.content = content;
    }
}
