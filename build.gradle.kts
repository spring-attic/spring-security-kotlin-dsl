import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
	dependencies {
		classpath("io.spring.gradle", "spring-build-conventions", "0.0.23.RELEASE")
		classpath("io.spring.nohttp", "nohttp-gradle", "0.0.2.RELEASE")
	}
	repositories {
		maven { setUrl("https://repo.spring.io/plugins-snapshot") }
		maven { setUrl("https://repo.spring.io/plugins-release") }
		maven { setUrl("https://plugins.gradle.org/m2/") }
	}
}

plugins {
	id("io.spring.nohttp") version "0.0.2.RELEASE"
	id("io.spring.dependency-management") version "1.0.8.RELEASE"
	kotlin("jvm") version "1.3.50"
	kotlin("plugin.spring") version "1.3.50"
}
apply<io.spring.gradle.convention.SpringModulePlugin>()
apply<io.spring.gradle.convention.RootProjectPlugin>()

group = "org.springframework.security.dsl"
version = "0.0.1.BUILD-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

dependencyManagement {
	imports {
		mavenBom("org.springframework:spring-framework-bom:5.2.0.RELEASE")
		mavenBom("org.springframework.security:spring-security-bom:5.2.0.RELEASE")
	}

	dependencies {
		dependency("junit:junit:4.12")
	}
}

dependencies {
	compile("org.springframework:spring-aop")
	compile("org.springframework:spring-beans")
	compile("org.springframework:spring-context")
	compile("org.springframework:spring-core")
	compile("org.springframework:spring-expression")
	compile("org.springframework:spring-web")
	compile("org.springframework:spring-webmvc")
	compile("javax.servlet:javax.servlet-api:4.0.1")
	implementation("org.springframework.security:spring-security-config")
	implementation("org.springframework.security:spring-security-core")
	implementation("org.springframework.security:spring-security-web")
	implementation("org.springframework.security:spring-security-oauth2-client")
	implementation("org.springframework.security:spring-security-oauth2-jose")
	implementation("org.springframework.security:spring-security-oauth2-resource-server")
	implementation("org.springframework.security:spring-security-saml2-service-provider")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	testImplementation("junit:junit")
	testImplementation("org.assertj:assertj-core:3.12.2")
	testImplementation("org.mockito:mockito-core:3.0.0")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.springframework:spring-test")

}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
	useJUnit()

	maxHeapSize = "1G"
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}
