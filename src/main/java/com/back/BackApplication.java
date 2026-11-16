package com.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 애플리케이션 시작점(main)입니다.
 *
 * <p>{@code @SpringBootApplication}은 아래 세 가지를 합친 어노테이션입니다.
 * <ul>
 *   <li>{@code @SpringBootConfiguration}: 이 클래스 자체가 설정 클래스가 된다.</li>
 *   <li>{@code @EnableAutoConfiguration}: 의존성(webmvc, data-jpa, h2 ...)을 보고
 *       톰캣, DataSource, JPA 등을 자동으로 설정한다.</li>
 *   <li>{@code @ComponentScan}: 이 클래스가 있는 패키지({@code com.back})와 그 하위 패키지에서
 *       {@code @Component}, {@code @Service}, {@code @RestController} 등을 찾아 스프링 빈으로 등록한다.
 *       그래서 이 클래스는 항상 최상위 패키지에 둔다.</li>
 * </ul>
 *
 * <p>{@code @EnableJpaAuditing}은 JPA Auditing 기능을 켭니다.
 * 이 설정이 있어야 {@link com.back.global.jpa.entity.BaseEntity}의
 * {@code @CreatedDate}, {@code @LastModifiedDate} 필드에 시간이 자동으로 채워집니다.
 * 빼먹으면 createDate/modifyDate가 null로 저장됩니다.
 */
@SpringBootApplication
@EnableJpaAuditing
public class BackApplication {

    public static void main(String[] args) {
        // 내장 톰캣을 띄우고(기본 포트 8080) 스프링 컨테이너(ApplicationContext)를 만든다.
        SpringApplication.run(BackApplication.class, args);
    }

}
