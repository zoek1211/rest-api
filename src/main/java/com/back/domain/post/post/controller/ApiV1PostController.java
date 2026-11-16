package com.back.domain.post.post.controller;

import com.back.domain.post.post.dto.PostDto;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import com.back.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 글(Post) REST API 컨트롤러입니다.
 *
 * <p><b>{@code @RestController}</b> = {@code @Controller} + {@code @ResponseBody}
 * <br>메서드가 반환한 객체를 HTML 뷰 이름으로 보지 않고, Jackson으로 JSON으로 바꿔 응답 본문에 그대로 씁니다.
 * (타임리프처럼 화면을 그리지 않고 데이터만 주는 방식. 화면은 프론트 앱이 그린다.)
 *
 * <p><b>{@code @RequestMapping("/api/v1/posts")}</b>
 * <br>이 클래스의 모든 메서드 URL 앞에 공통으로 붙는 접두어입니다.
 * <ul>
 *   <li>{@code /api}: 화면이 아니라 API라는 표시</li>
 *   <li>{@code /v1}: API 버전. 나중에 응답 형식을 크게 바꿔야 하면 {@code /v2}를 새로 만들고,
 *       기존 앱(v1을 쓰는 클라이언트)은 그대로 동작하게 둘 수 있다.</li>
 *   <li>{@code /posts}: 리소스 이름은 복수형</li>
 * </ul>
 *
 * <p><b>컨트롤러에 {@code @Transactional}을 붙인 이유</b>
 * <br>Post의 comments는 지연 로딩이고, 댓글 삭제는 더티체킹(orphanRemoval)에 의존합니다.
 * 요청 처리 전체를 하나의 트랜잭션으로 묶어야 이런 기능이 안전하게 동작합니다.
 * 조회는 {@code readOnly = true}를 줘서 변경 감지를 생략하게 해 성능을 조금 아낍니다.
 */
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class ApiV1PostController {
    private final PostService postService;

    /**
     * 글 다건조회: {@code GET /api/v1/posts}
     *
     * <p>엔티티 리스트를 DTO 리스트로 변환해서 돌려줍니다. 스프링이 {@code List<PostDto>}를 JSON 배열로 바꿉니다.
     * <br>{@code .map(PostDto::new)}는 {@code .map(post -> new PostDto(post))}를 줄여 쓴 메서드 참조입니다.
     */
    @GetMapping
    @Transactional(readOnly = true)
    public List<PostDto> getItems() {
        List<Post> items = postService.findAll();

        return items
                .stream()
                .map(PostDto::new)
                .toList();
    }

    /**
     * 글 단건조회: {@code GET /api/v1/posts/{id}}
     *
     * <p>{@code @PathVariable int id}: URL의 {@code {id}} 자리 값(문자열 "2")을 int 2로 바꿔서 받습니다.
     * <br>{@code .get()}: Optional에서 값을 꺼냅니다. 글이 없으면 NoSuchElementException → 500 응답.
     * (없는 글에 대한 404 처리는 이번 범위 밖입니다.)
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public PostDto getItem(
            @PathVariable int id
    ) {
        Post post = postService.findById(id).get();

        return new PostDto(post);
    }

    /**
     * 글 삭제: {@code GET /api/v1/posts/{id}/delete}
     *
     * <p><b>주의</b>: 원래 삭제는 GET이 아니라 {@code DELETE /api/v1/posts/{id}}로 해야 맞습니다.
     * GET은 "조회"용이라 브라우저나 검색엔진이 마음대로 호출할 수 있기 때문입니다.
     * 지금은 브라우저 주소창으로 쉽게 테스트하려고 임시로 GET을 썼습니다.
     *
     * <p>응답은 조회가 아니므로 {@link RsData}로 감쌉니다. 추가 데이터가 없으니 {@code RsData<Void>}.
     */
    @GetMapping("/{id}/delete")
    @Transactional // 쓰기 작업이므로 readOnly가 아닌 일반 트랜잭션
    public RsData<Void> delete(
            @PathVariable int id
    ) {
        Post post = postService.findById(id).get();

        postService.delete(post); // cascade REMOVE → 이 글의 댓글들이 먼저 delete된다.

        return new RsData<>(
                "200-1",
                "%d번 글이 삭제되었습니다.".formatted(id) // String.formatted(): String.format()의 인스턴스 메서드 버전(Java 15+)
        );
    }
}
