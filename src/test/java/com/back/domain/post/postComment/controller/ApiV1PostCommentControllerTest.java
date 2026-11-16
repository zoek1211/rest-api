package com.back.domain.post.postComment.controller;

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
 * 댓글 API 통합 테스트입니다. (어노테이션 설명은 {@code ApiV1PostControllerTest} 주석 참고)
 *
 * <p>샘플데이터 기준: 1번 글에 댓글 1, 2, 3번("댓글 1-1" ~ "댓글 1-3"), 2번 글에 댓글 4, 5번이 있다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ApiV1PostCommentControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("댓글 다건조회")
    void getItems() throws Exception {
        mvc.perform(get("/api/v1/posts/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))                  // 1번 글의 댓글만 3개 (2번 글의 댓글은 섞이지 않음)
                .andExpect(jsonPath("$[0].content").value("댓글 1-1")) // @OrderBy("id") 덕분에 id 순
                .andExpect(jsonPath("$[2].content").value("댓글 1-3"));
    }

    @Test
    @DisplayName("댓글 단건조회")
    void getItem() throws Exception {
        mvc.perform(get("/api/v1/posts/1/comments/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.createDate").isNotEmpty())
                .andExpect(jsonPath("$.modifyDate").isNotEmpty())
                .andExpect(jsonPath("$.content").value("댓글 1-2"))
                // id, createDate, modifyDate, content 4개뿐 → post 필드가 노출되지 않는다(순환참조 없음).
                .andExpect(jsonPath("$.*", hasSize(4)));
    }

    @Test
    @DisplayName("댓글 삭제")
    void delete() throws Exception {
        mvc.perform(get("/api/v1/posts/1/comments/2/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("2번 댓글이 삭제되었습니다."))
                .andExpect(jsonPath("$.data").value(nullValue()));

        // 같은 트랜잭션 안에서 다시 조회 → 2번 댓글이 목록에서 빠졌는지 확인
        mvc.perform(get("/api/v1/posts/1/comments"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", not(hasItem(2))));
    }
}
