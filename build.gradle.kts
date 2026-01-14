plugins {
    // Match real plugin projects: allow Kotlin version to be controlled externally (e.g. via gradle.properties systemProp.kotlin_version).
    val kotlin_version: String by System.getProperties()
    kotlin("jvm").version(kotlin_version)
    `java-library`
    `maven-publish`
    id("org.jetbrains.dokka") version "2.0.0"
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
    val requestedJvmTargetStr: String = (System.getProperty("ktale_jvm_target") ?: "25").trim()
    val isTestBuild: Boolean = gradle.startParameter.taskNames.any { it.contains("test", ignoreCase = true) || it.contains("check", ignoreCase = true) }

    // Hytale plugins are expected to run on JVM 25.
    // Kotlin may lag behind in classfile targets; clamp to the max supported by Kotlin (currently 24).
    val effectiveJvmTargetStr: String = if (isTestBuild) {
        // Tests should run on JDK 21; emit JVM 21 bytecode so the test JVM can load KTale classes.
        "21"
    } else try {
        org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(requestedJvmTargetStr)
        requestedJvmTargetStr
    } catch (_: Throwable) {
        logger.warn("Requested ktale_jvm_target=$requestedJvmTargetStr but Kotlin doesn't support it; using 24 for Kotlin bytecode.")
        "24"
    }

    // Use JDK 21 to *compile*, but we can still emit newer Kotlin bytecode targets when supported.
    // (This matches your requirement: JDK 21, target JVM 25 — with Kotlin clamped until it supports 25.)
    jvmToolchain(21)
    compilerOptions {
        // Kotlin's supported JVM targets lag behind Java toolchains. Use string target for maximum compatibility.
        // If your Kotlin version supports 25, this will work; otherwise set systemProp.ktale_jvm_target (e.g. 21).
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(effectiveJvmTargetStr))
    }
}

java {
    toolchain {
        // JDK used for compilation.
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
}

// Ensure tests run on JDK 21 explicitly (even if the developer machine has other JDKs installed).
tasks.withType<Test>().configureEach {
    javaLauncher.set(javaToolchains.launcherFor { languageVersion.set(JavaLanguageVersion.of(21)) })
}

publishing {
    repositories {
        mavenLocal()
        val user = System.getenv("NEXUS_USER")
        val pass = System.getenv("NEXUS_PASS")
        if (!user.isNullOrBlank() && !pass.isNullOrBlank()) {
            maven {
                name = "ModLabs"
                url = uri("https://nexus.modlabs.cc/repository/maven-public/")
                credentials {
                    username = user
                    password = pass
                }
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name.set("KTale")
                description.set("Kotlin extensions + utilities for Hytale Server plugin development.")
                url.set("https://github.com/ModLabsCC/ktale")
                licenses {
                    license {
                        name.set("GPL-3.0")
                        url.set("https://github.com/ModLabsCC/ktale/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("ModLabsCC")
                        name.set("ModLabsCC")
                        email.set("contact@modlabs.cc")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/ModLabsCC/ktale.git")
                    developerConnection.set("scm:git:git@github.com:ModLabsCC/ktale.git")
                    url.set("https://github.com/ModLabsCC/ktale")
                }
            }
        }
    }
}