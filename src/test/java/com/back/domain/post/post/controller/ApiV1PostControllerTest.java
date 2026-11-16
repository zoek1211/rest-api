package com.back.domain.post.post.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
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
}
