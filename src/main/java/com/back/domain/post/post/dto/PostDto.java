package com.back.domain.post.post.dto;

import com.back.domain.post.post.entity.Post;

import java.time.LocalDateTime;

/**
 * 글 API의 응답 형태를 정의하는 DTO(Data Transfer Object)입니다.
 *
 * <p><b>왜 엔티티(Post)를 그대로 응답하지 않을까?</b>
 * <ol>
 *   <li><b>순환참조</b>: Post → comments → PostComment → post → Post → ... 를 Jackson이 따라가다가 무한 루프에 빠진다.</li>
 *   <li><b>불필요한 노출</b>: 글 목록 화면에는 댓글이 필요 없는데, 엔티티를 그대로 내보내면 댓글까지 다 나간다.</li>
 *   <li><b>API 스펙과 DB 구조의 분리</b>: 프론트가 "title을 subject로 바꿔 주세요"라고 해도
 *       엔티티(DB 컬럼)는 그대로 두고 DTO만 바꾸면 된다.</li>
 * </ol>
 *
 * <p><b>왜 record일까?</b>
 * <br>record는 Java 16+의 불변 데이터 클래스입니다. 선언만 하면 아래가 자동으로 만들어집니다.
 * <ul>
 *   <li>모든 필드를 받는 생성자(canonical constructor)</li>
 *   <li>필드 이름과 같은 접근자 메서드: {@code id()}, {@code title()} ... (getXxx가 아님)</li>
 *   <li>equals(), hashCode(), toString()</li>
 * </ul>
 * Jackson은 record의 접근자를 인식해서 {@code {"id":1, "createDate":..., "title":..., "content":...}} 형태로 직렬화합니다.
 */
public record PostDto(
        int id,
        LocalDateTime createDate,
        LocalDateTime modifyDate,
        String title,
        String content
) {
    /**
     * 엔티티를 받아서 DTO로 변환하는 보조 생성자.
     * <br>record의 보조 생성자는 반드시 {@code this(...)}로 기본 생성자를 호출해야 합니다.
     * <br>덕분에 컨트롤러에서 {@code .map(PostDto::new)}처럼 메서드 참조로 짧게 변환할 수 있습니다.
     */
    public PostDto(Post post) {
        this(
                post.getId(),
                post.getCreateDate(),
                post.getModifyDate(),
                post.getTitle(),
                post.getContent()
        );
    }
}
