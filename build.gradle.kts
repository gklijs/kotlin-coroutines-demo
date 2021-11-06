import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0-RC2"
    application
}

group = "tech.gklijs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.0-RC2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "14"
}

application {
    mainClass.set("MainKt")
}