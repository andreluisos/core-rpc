plugins {
    id("java")
}

group = "org.jnvim"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("org.msgpack:jackson-dataformat-msgpack:0.9.8")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError") // Log test events
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL // Include full stack traces
        showStandardStreams = true // Display test output in the console
    }
}
