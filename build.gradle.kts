import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.3.72"
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.slf4j:slf4j-simple:1.7.25")
	implementation("org.slf4j:slf4j-api:1.7.25")
	implementation("org.testcontainers:kafka:1.13.0")
	implementation("org.testcontainers:junit-jupiter:1.13.0")
	implementation("org.apache.kafka:kafka-clients:2.0.0")
	implementation("javax.mail:mail:1.4.1")
	implementation("org.apache.commons:commons-csv:1.5")
	implementation("com.google.code.gson:gson:2.8.5")
	implementation("com.fasterxml.jackson.core:jackson-databind:2.9.6")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.6")
	implementation(kotlin("stdlib-jdk8"))
	testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
}

tasks.test {
	useJUnitPlatform()
}

// config JVM target to 1.8 for kotlin compilation tasks
tasks.withType<KotlinCompile>().configureEach {
	kotlinOptions.jvmTarget = "1.8"
}