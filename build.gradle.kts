import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    idea
    id("org.springframework.boot") version "3.1.7"
    id("io.spring.dependency-management") version "1.1.3"
    id("com.avast.gradle.docker-compose") version "0.17.4"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.noarg") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.allopen") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"
    id("org.jmailen.kotlinter") version "3.14.0"
}

group = "io.mustelidae"
version = "1.1.1"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenLocal()
    maven("https://jitpack.io")
    mavenCentral()
}

ext["log4j2.version"] = "2.17.1"

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation("com.github.usingsky:KoreanLunarCalendar:0.3.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")

    implementation("com.github.kittinunf.fuel:fuel:2.3.1")

    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:4.9.0")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")

    testImplementation("io.mockk:mockk:1.13.7")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.1.0")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(module = "spring-boot-starter-tomcat")
    }
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation("org.springframework.boot:spring-boot-starter-undertow") {
        exclude("io.undertow", "undertow-websockets-jsr")
    }
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(group = "junit", module = "junit")
    }
    testImplementation("org.springframework.boot:spring-boot-starter-hateoas")
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
    testImplementation("org.testcontainers:mysql:1.19.3")

}

allOpen {
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("org.springframework.data.mongodb.core.mapping.Document")
}

noArg {
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("org.springframework.data.mongodb.core.mapping.Document")
}

tasks.withType<Test> {
    useJUnitPlatform()
    failFast = true

    testLogging {
        events.plus(TestLogEvent.FAILED)
        events.plus(TestLogEvent.STANDARD_ERROR)

        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true

        info.events = debug.events
        info.exceptionFormat = debug.exceptionFormat
    }

    addTestListener(object : TestListener {
        override fun beforeSuite(suite: TestDescriptor) {}
        override fun beforeTest(testDescriptor: TestDescriptor) {}
        override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
        override fun afterSuite(suite: TestDescriptor, result: TestResult) {
            if(suite.parent == null){
                val output = "Results: ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} successes, ${result.failedTestCount} failures, ${result.skippedTestCount} skipped)"
                val startItem = "|"
                val endItem = "  |"
                val repeatLength = startItem.length + output.length + endItem.length
                println("\n${"-".repeat(repeatLength)}\n|  $output  |\n${"-".repeat(repeatLength)}")
            }
        }
    })

    minHeapSize = "1024m"
    maxHeapSize = "4096m"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "21"
    }
}

tasks.register("version") {
    println(version)
}
