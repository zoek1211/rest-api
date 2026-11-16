package com.back.domain.post.post.repository;

import com.back.domain.post.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 글(Post) 테이블에 접근하는 리포지터리입니다.
 *
 * <p>인터페이스만 선언했는데 구현 클래스가 없습니다. 스프링 데이터 JPA가 실행 시점에
 * 구현체(프록시)를 자동으로 만들어 빈으로 등록해 주기 때문입니다.
 *
 * <p>{@code JpaRepository<Post, Integer>}의 의미
 * <ul>
 *   <li>{@code Post}: 다룰 엔티티 타입</li>
 *   <li>{@code Integer}: 그 엔티티의 PK(id) 타입. BaseEntity의 {@code int id}에 맞춘 것이다.</li>
 * </ul>
 *
 * <p>상속만으로 바로 쓸 수 있는 메서드 예시:
 * {@code save()}, {@code findById()}, {@code findAll()}, {@code findAll(Sort)}, {@code count()}, {@code delete()}
 *
 * <p>댓글(PostComment) 리포지터리는 따로 없습니다. 댓글은 글을 통해서만 다루기 때문입니다(Post의 cascade 참고).
 */
public interface PostRepository extends JpaRepository<Post, Integer> {
}
