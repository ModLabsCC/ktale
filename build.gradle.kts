plugins {
    // Match real plugin projects: allow Kotlin version to be controlled externally (e.g. via gradle.properties systemProp.kotlin_version).
    val kotlin_version: String by System.getProperties()
    kotlin("jvm").version(kotlin_version)
    `java-library`
}

group = "cc.modlabs"
version = System.getenv("VERSION_OVERRIDE") ?: "1.0-SNAPSHOT"

repositories {
    maven("https://nexus.modlabs.cc/repository/maven-mirrors/")
}

dependencies {
    // Provided by the Hytale server runtime; we only compile against it.
    compileOnly("com.hypixel.hytale:Server:2026.01.13-dcad8778f")

    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.mockk:mockk:1.13.14")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        // Kotlin's supported JVM targets lag behind Java toolchains. Use string target for maximum compatibility.
        // If your Kotlin version supports 25, this will work; otherwise set systemProp.ktale_jvm_target (e.g. 21).
        val target: String = (System.getProperty("ktale_jvm_target") ?: "25").trim()
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(target))
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    withSourcesJar()
}