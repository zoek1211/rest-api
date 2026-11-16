package com.back;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 가장 기본적인 스모크 테스트입니다.
 * 스프링 컨테이너가 오류 없이 뜨는지(빈 등록, JPA 매핑, 설정 파일, 샘플데이터 생성)만 확인합니다.
 * 테스트 메서드가 비어 있어도, 컨텍스트 로딩에 실패하면 이 테스트가 실패합니다.
 */
@SpringBootTest
@ActiveProfiles("test") // 인메모리 H2를 쓰는 test 프로필
class BackApplicationTests {

    @Test
    void contextLoads() {
    }

}
