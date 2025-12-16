import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.Test
import java.util.Calendar
import java.util.TimeZone

plugins {
    // Root is an aggregator. Subprojects apply Kotlin + publishing.
    kotlin("jvm") version "2.2.21" apply false
    kotlin("plugin.serialization") version "2.2.21" apply false
}

allprojects {
    group = "cc.modlabs"
    version = System.getenv("VERSION_OVERRIDE") ?: Calendar.getInstance(TimeZone.getTimeZone("UTC")).run {
        "${get(Calendar.YEAR)}.${get(Calendar.MONTH) + 1}.${get(Calendar.DAY_OF_MONTH)}.${
            String.format("%02d%02d", get(Calendar.HOUR_OF_DAY), get(Calendar.MINUTE))
        }"
    }

    repositories {
        maven("https://nexus.modlabs.cc/repository/maven-mirrors/")
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    // Root build only wires compilation + testing.
    // Publishing is configured per-module later (kept out of root to avoid Gradle plugin ordering pitfalls).

    dependencies {
        add("testImplementation", kotlin("test"))
        add("testImplementation", "io.kotest:kotest-runner-junit5:5.9.1")
        add("testImplementation", "io.kotest:kotest-assertions-core:5.9.1")
        add("testImplementation", "io.mockk:mockk:1.13.14")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    tasks.withType<KotlinCompile> {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    }

    extensions.configure<KotlinJvmProjectExtension> {
        jvmToolchain(21)
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}