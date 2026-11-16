plugins {
	// build.gradle.kts의 toolchain(Java 25)이 PC에 없으면 JDK를 자동으로 내려받아 주는 플러그인.
	// 받은 JDK는 ~/.gradle/jdks 에 저장된다.
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "rest-api"
