// 그레이들 빌드 설정 파일(Kotlin DSL). start.spring.io에서 생성한 내용을 그대로 사용했습니다.
plugins {
	java
	id("org.springframework.boot") version "4.1.2"                 // bootRun, bootJar 같은 스프링 부트 태스크 제공
	id("io.spring.dependency-management") version "1.1.7"         // 스프링 부트가 검증한 라이브러리 버전을 자동으로 맞춰 줌 → 아래 의존성에 버전을 안 적어도 된다.
}

group = "com"
version = "0.0.1-SNAPSHOT"
description = "rest-api"

java {
	toolchain {
		// 컴파일/실행에 Java 25를 사용한다. 설치돼 있지 않으면 settings.gradle.kts의 foojay 플러그인이 자동으로 내려받는다.
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral() // 라이브러리를 내려받을 저장소
}

dependencies {
	implementation("org.springframework.boot:spring-boot-h2console")           // /h2-console 웹 콘솔
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")    // JPA(Hibernate) + 스프링 데이터 JPA(Repository 자동 구현)
	implementation("org.springframework.boot:spring-boot-starter-validation")  // @NotBlank 같은 입력값 검증(이후 작성/수정 API에서 사용 예정)
	implementation("org.springframework.boot:spring-boot-starter-webmvc")      // 스프링 MVC + 내장 톰캣 + Jackson(JSON 변환)
	compileOnly("org.projectlombok:lombok")                                    // @Getter 등 롬복: 컴파일할 때만 필요
	developmentOnly("org.springframework.boot:spring-boot-devtools")           // 코드 변경 시 자동 재시작(개발 중에만)
	runtimeOnly("com.h2database:h2")                                           // H2 DB 드라이버: 실행할 때만 필요
	annotationProcessor("org.projectlombok:lombok")                            // 롬복이 컴파일 시점에 코드를 생성하도록
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")  // MockMvc, JUnit 5, AssertJ, Hamcrest 등
	testCompileOnly("org.projectlombok:lombok")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
	useJUnitPlatform() // JUnit 5로 테스트 실행
}
