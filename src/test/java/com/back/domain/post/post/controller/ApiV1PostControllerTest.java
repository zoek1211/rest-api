package com.back.domain.post.post.controller;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 글 API 통합 테스트입니다. 실제 HTTP 요청 → JSON 응답이라는 "겉으로 보이는 동작"만 검증합니다.
 * 내부 구현(서비스, 리포지터리)을 바꿔도 API 동작이 같으면 테스트는 그대로 통과해야 합니다.
 *
 * <ul>
 *   <li>{@code @SpringBootTest}: 실제 앱과 똑같이 스프링 컨테이너 전체를 띄운다(DB, JPA, 컨트롤러 모두 진짜).</li>
 *   <li>{@code @AutoConfigureMockMvc}: 실제 톰캣을 띄우지 않고도 HTTP 요청을 흉내 낼 수 있는 {@link MockMvc}를 준비한다.</li>
 *   <li>{@code @ActiveProfiles("test")}: application-test.yaml을 사용한다 → 인메모리 H2(db_test).
 *       개발용 파일 DB(db_dev)를 건드리지 않는다.</li>
 *   <li>{@code @Transactional}: 테스트 메서드 하나가 끝나면 DB 변경을 <b>롤백</b>한다.
 *       그래서 삭제 테스트를 해도 다음 테스트에서는 샘플데이터(글 3개, 댓글 5개)가 그대로 있다.</li>
 * </ul>
 *
 * <p>테스트 데이터는 {@code SampleData}가 앱 시작 시 만든 글 1~3번, 댓글 1~5번을 전제로 합니다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ApiV1PostControllerTest {
    @Autowired
    private MockMvc mvc;

    /** 영속성 컨텍스트를 직접 flush/clear 하기 위해 주입받는다(글 삭제 테스트에서 사용). */
    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("글 다건조회")
    void getItems() throws Exception {
        // jsonPath 문법: $ = 응답 JSON의 루트, $[0] = 배열의 첫 번째 원소, $[0].title = 그 원소의 title 필드
        mvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())                      // HTTP 200
                .andExpect(jsonPath("$", hasSize(3)))             // 글 3개
                .andExpect(jsonPath("$[0].id").value(1))          // id 순 정렬이므로 첫 번째는 1번 글
                .andExpect(jsonPath("$[0].title").value("제목 1"))
                .andExpect(jsonPath("$[0].content").value("내용 1"))
                .andExpect(jsonPath("$[2].title").value("제목 3"));
    }

    @Test
    @DisplayName("글 단건조회")
    void getItem() throws Exception {
        mvc.perform(get("/api/v1/posts/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.createDate").isNotEmpty()) // JPA Auditing이 날짜를 채웠는지
                .andExpect(jsonPath("$.modifyDate").isNotEmpty())
                .andExpect(jsonPath("$.title").value("제목 2"))
                .andExpect(jsonPath("$.content").value("내용 2"))
                // 엔티티가 아니라 DTO로 응답하므로 comments는 없어야 한다.
                .andExpect(jsonPath("$.comments").doesNotExist())
                // $.* = 루트 객체의 모든 필드 → 정확히 5개(id, createDate, modifyDate, title, content)
                .andExpect(jsonPath("$.*", hasSize(5)));
    }

    @Test
    @DisplayName("글 삭제 - 댓글이 있는 글도 함께 삭제된다")
    void delete() throws Exception {
        // 1번 글에는 댓글이 3개 달려 있다.
        mvc.perform(get("/api/v1/posts/1/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("1번 글이 삭제되었습니다."))
                .andExpect(jsonPath("$.data").value(nullValue())); // RsData<Void> → data는 null

        // 테스트 전체가 하나의 트랜잭션이라, 그대로 두면 delete 쿼리가 DB에 나가지 않고 롤백된다.
        // flush()로 지금까지의 변경(delete)을 DB에 강제로 보낸다.
        // 이때 댓글이 함께 삭제되지 않았다면(cascade REMOVE가 없다면) FK 제약 위반 예외가 터져 테스트가 실패한다.
        em.flush();
        // clear()로 1차 캐시(영속성 컨텍스트)를 비워서, 다음 조회가 메모리가 아닌 DB에서 다시 읽도록 한다.
        em.clear();

        mvc.perform(get("/api/v1/posts"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", not(hasItem(1)))); // $[*].id = 모든 글의 id 목록 → 1이 없어야 한다.
    }
}
