import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
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
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
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

    // Publishing: keep it CI-safe by always enabling `mavenLocal()`, and only adding remote repos when creds exist.
    extensions.configure<JavaPluginExtension> {
        withSourcesJar()
    }

    extensions.configure<PublishingExtension> {
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
                    description.set("A speculative, adapter-based Kotlin server SDK foundation for Hytale (Day-1 oriented).")
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
}