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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ApiV1PostControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("글 다건조회")
    void getItems() throws Exception {
        mvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(1))
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
                .andExpect(jsonPath("$.createDate").isNotEmpty())
                .andExpect(jsonPath("$.modifyDate").isNotEmpty())
                .andExpect(jsonPath("$.title").value("제목 2"))
                .andExpect(jsonPath("$.content").value("내용 2"))
                .andExpect(jsonPath("$.comments").doesNotExist())
                .andExpect(jsonPath("$.*", hasSize(5)));
    }

    @Test
    @DisplayName("글 삭제 - 댓글이 있는 글도 함께 삭제된다")
    void delete() throws Exception {
        mvc.perform(get("/api/v1/posts/1/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("1번 글이 삭제되었습니다."))
                .andExpect(jsonPath("$.data").value(nullValue()));

        // DB에 실제로 반영시킨다. 댓글이 함께 삭제되지 않으면 FK 제약 위반으로 실패한다.
        em.flush();
        em.clear();

        mvc.perform(get("/api/v1/posts"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", not(hasItem(1))));
    }
}
