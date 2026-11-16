package com.back.domain.post.post.entity;

import com.back.domain.post.postComment.entity.PostComment;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;

/**
 * 글(Post) 엔티티입니다. DB의 {@code post} 테이블과 연결됩니다.
 *
 * <p>id, createDate, modifyDate는 {@link BaseEntity}에서 물려받습니다.
 *
 * <p><b>애그리거트 루트(Aggregate Root)</b>
 * <br>댓글은 글 없이 존재할 수 없습니다. 그래서 댓글 추가/조회/삭제를 글이 책임지도록
 * {@link #addComment}, {@link #findCommentById}, {@link #deleteComment} 메서드를 글에 두었습니다.
 * 이 프로젝트에 댓글 전용 Repository/Service가 없는 이유입니다.
 */
@Entity // 이 클래스를 JPA가 관리하는 엔티티로 등록한다. 클래스 이름 Post → 테이블 이름 post.
@Getter
@NoArgsConstructor // JPA는 엔티티를 조회할 때 기본 생성자로 객체를 만든 뒤 필드를 채운다. 그래서 기본 생성자가 반드시 필요하다.
public class Post extends BaseEntity {
    private String title;   // → title 컬럼
    private String content; // → content 컬럼

    /**
     * 이 글에 달린 댓글 목록 (1:N 관계).
     *
     * <ul>
     *   <li>{@code mappedBy = "post"}: 관계의 주인은 {@link PostComment#getPost()} 쪽이라는 뜻입니다.
     *       실제 외래키(post_id) 컬럼은 post_comment 테이블에 있고, 이 리스트는 "읽기용 거울"입니다.
     *       그래서 post 테이블에는 comments 컬럼이 생기지 않습니다.</li>
     *   <li>{@code cascade = PERSIST}: 글을 저장할 때, 리스트에 새로 추가된 댓글도 함께 insert합니다.
     *       그래서 {@code post.addComment(...)}만 호출해도 댓글이 저장됩니다(SampleData 참고).</li>
     *   <li>{@code cascade = REMOVE}: 글을 삭제하면 댓글들을 먼저 delete한 뒤 글을 delete합니다.
     *       이게 없으면 댓글이 글을 외래키로 참조하고 있어서 글 삭제가 FK 제약 위반으로 실패합니다.</li>
     *   <li>{@code orphanRemoval = true}: 리스트에서 빠진 댓글(고아 객체)을 DB에서도 delete합니다.
     *       그래서 {@code comments.remove(댓글)}만 해도 댓글 삭제 쿼리가 나갑니다.
     *       단, 트랜잭션 안에서 실행돼야 변경 감지(더티체킹)가 동작합니다.</li>
     *   <li>{@code @OrderBy("id")}: DB에서 댓글을 불러올 때 {@code order by id}를 붙여 항상 id 순으로 가져옵니다.</li>
     * </ul>
     *
     * <p>{@code final}로 선언하고 바로 {@code new ArrayList<>()}로 초기화해서 null이 될 일이 없게 했습니다.
     */
    @OneToMany(mappedBy = "post", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    @OrderBy("id")
    private final List<PostComment> comments = new ArrayList<>();

    /** 새 글을 만들 때 쓰는 생성자. id와 날짜는 저장 시점에 자동으로 정해지므로 받지 않는다. */
    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    /**
     * 이 글에 댓글을 추가합니다.
     *
     * <p>양방향 관계에서는 양쪽을 모두 맞춰 줘야 합니다.
     * <ol>
     *   <li>{@code new PostComment(this, content)}: 댓글 → 글 방향(외래키 post_id)을 설정</li>
     *   <li>{@code comments.add(...)}: 글 → 댓글 방향(메모리상의 리스트)을 설정</li>
     * </ol>
     * 2번이 있어야 같은 트랜잭션 안에서 {@code getComments()}를 했을 때 새 댓글이 바로 보이고,
     * cascade PERSIST로 insert도 됩니다.
     */
    public PostComment addComment(String content) {
        PostComment postComment = new PostComment(this, content);
        comments.add(postComment);

        return postComment;
    }

    /**
     * 이 글의 댓글 중에서 id가 일치하는 댓글을 찾습니다.
     *
     * <p>"다른 글의 댓글"은 찾지 않는다는 점이 중요합니다.
     * 예를 들어 {@code /posts/2/comments/1}을 요청하면, 1번 댓글은 1번 글의 댓글이므로 찾지 못합니다.
     *
     * <p>결과가 없을 수 있으므로 null 대신 {@link Optional}로 감싸서 돌려줍니다.
     */
    public Optional<PostComment> findCommentById(int id) {
        return comments
                .stream()
                .filter(comment -> comment.getId() == id)
                .findFirst();
    }

    /**
     * 이 글에서 댓글을 제거합니다.
     *
     * <p>DELETE 쿼리를 직접 호출하지 않는다는 점이 핵심입니다. 리스트에서 빼기만 하면
     * 트랜잭션이 끝날 때 JPA가 "고아가 된 댓글"을 감지해서({@code orphanRemoval = true}) delete 쿼리를 실행합니다.
     *
     * @return 실제로 제거됐으면 true
     */
    public boolean deleteComment(PostComment postComment) {
        if (postComment == null) return false;

        return comments.remove(postComment); // equals()로 찾는다 → BaseEntity의 id 기준 equals가 쓰인다.
    }
}
