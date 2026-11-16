package com.back.global.jpa.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * 모든 엔티티가 공통으로 가지는 필드(id, 생성일, 수정일)를 모아 둔 부모 클래스입니다.
 *
 * <p>글(Post)과 댓글(PostComment)이 모두 이 클래스를 상속하므로,
 * id/createDate/modifyDate를 엔티티마다 반복해서 선언하지 않아도 됩니다.
 *
 * <p><b>{@code @MappedSuperclass}</b>
 * <br>이 클래스 자체는 테이블이 되지 않습니다. 대신 이 클래스의 필드가
 * 상속받은 자식 엔티티의 테이블 컬럼으로 들어갑니다.
 * (post 테이블에 id, create_date, modify_date 컬럼이 생기는 이유)
 *
 * <p><b>{@code @EntityListeners(AuditingEntityListener.class)}</b>
 * <br>엔티티가 저장(persist)되거나 수정(update)될 때 스프링 데이터 JPA의 리스너가 끼어들어
 * {@code @CreatedDate}, {@code @LastModifiedDate} 필드에 현재 시간을 넣어 줍니다.
 * 이 기능은 {@code @EnableJpaAuditing}(BackApplication)이 켜져 있어야 동작합니다.
 *
 * <p>{@code abstract}로 선언해서 BaseEntity 자체를 new로 만들지 못하게 막았습니다.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter // 롬복: 모든 필드에 getter를 만든다. setter는 일부러 만들지 않는다(id나 생성일을 밖에서 바꾸면 안 되므로).
public abstract class BaseEntity {
    /**
     * 기본키(PK).
     * <br>{@code IDENTITY} 전략은 id 생성을 DB의 auto_increment에 맡깁니다.
     * 그래서 save(insert)가 실행된 뒤에야 id 값이 정해집니다.
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    /** 처음 저장될 때의 시간. 이후에는 바뀌지 않는다. */
    @CreatedDate
    private LocalDateTime createDate;

    /** 마지막으로 수정된 시간. 처음 저장할 때는 createDate와 같은 값이 들어간다. */
    @LastModifiedDate
    private LocalDateTime modifyDate;

    /**
     * 두 엔티티가 "같은 데이터"인지 판단하는 기준을 id로 정합니다.
     *
     * <p>왜 필요할까? {@code Post.deleteComment()}에서 {@code comments.remove(postComment)}를 호출하면
     * List는 내부적으로 equals()로 지울 대상을 찾습니다. 기본 equals()는 "같은 객체(주소)인가"를 비교하므로,
     * 같은 id의 댓글이라도 다른 인스턴스이면 못 찾을 수 있습니다. id 기준으로 비교하면 이런 문제가 없습니다.
     *
     * <p>{@code getClass() != o.getClass()}로 클래스까지 비교하는 이유:
     * 글 1번과 댓글 1번은 id가 같아도 다른 데이터이기 때문입니다.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return id == that.id;
    }

    /** equals()를 재정의하면 hashCode()도 같은 기준(id)으로 재정의해야 HashSet/HashMap에서 올바르게 동작한다. */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
