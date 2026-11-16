package com.back.domain.post.post.service;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.repository.PostRepository;
import com.back.domain.post.postComment.entity.PostComment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 글과 댓글에 대한 비즈니스 로직을 담당하는 서비스입니다.
 *
 * <p>계층 구조: Controller(요청/응답) → <b>Service(로직)</b> → Repository(DB 접근)
 * <br>컨트롤러는 리포지터리를 직접 부르지 않고 항상 서비스를 거칩니다.
 * 지금은 대부분 리포지터리에 그대로 넘기는 얇은 메서드지만, 나중에 검증이나 권한 체크 같은 로직이
 * 생기면 여기에 추가됩니다.
 *
 * <p>{@code @Service}: 스프링 빈으로 등록한다(컴포넌트 스캔 대상).
 * <br>{@code @RequiredArgsConstructor}(롬복): {@code final} 필드를 받는 생성자를 만들어 준다.
 * 스프링은 생성자가 하나뿐이면 그 생성자로 의존성을 주입한다(생성자 주입).
 */
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    /** 전체 글 개수. 샘플데이터를 만들지 판단할 때 쓴다. */
    public long count() {
        return postRepository.count();
    }

    /** 글을 새로 작성(저장)한다. save() 후 반환된 객체에는 id와 날짜가 채워져 있다. */
    public Post write(String title, String content) {
        Post post = new Post(title, content);

        return postRepository.save(post);
    }

    /** id로 글 하나를 찾는다. 없을 수 있으므로 Optional로 돌려준다. */
    public Optional<Post> findById(int id) {
        return postRepository.findById(id);
    }

    /**
     * 모든 글을 id 오름차순으로 가져온다.
     *
     * <p>그냥 {@code findAll()}을 쓰면 SQL에 order by가 없어서 순서가 DB 마음대로입니다.
     * {@code Sort.by("id")}를 주면 {@code order by id}가 붙어서 항상 같은 순서가 보장됩니다.
     */
    public List<Post> findAll() {
        return postRepository.findAll(Sort.by("id"));
    }

    /**
     * 글에서 댓글을 삭제한다. 실제 삭제는 {@link Post#deleteComment}가 하고,
     * DB 반영은 트랜잭션 커밋 시점에 orphanRemoval로 일어난다.
     * 그래서 이 메서드를 부르는 쪽(컨트롤러)에 {@code @Transactional}이 있어야 한다.
     */
    public boolean deleteComment(Post post, PostComment postComment) {
        return post.deleteComment(postComment);
    }

    /** 글을 삭제한다. cascade REMOVE 덕분에 댓글이 먼저 삭제되고 글이 삭제된다. */
    public void delete(Post post) {
        postRepository.delete(post);
    }
}
