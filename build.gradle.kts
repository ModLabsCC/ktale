import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
    kotlin("jvm") version "2.2.21"
    id("maven-publish")
}

group = "cc.modlabs"
version = System.getenv("VERSION_OVERRIDE") ?: Calendar.getInstance(TimeZone.getTimeZone("UTC")).run {
    "${get(Calendar.YEAR)}.${get(Calendar.MONTH) + 1}.${get(Calendar.DAY_OF_MONTH)}.${String.format("%02d%02d", get(Calendar.HOUR_OF_DAY), get(Calendar.MINUTE))}"
}

repositories {
    maven("https://nexus.modlabs.cc/repository/maven-mirrors/")
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("cc.modlabs:KlassicX:${project.ext["klassicXVersion"] as String}")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    withType<KotlinCompile> {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<Jar>("sourcesJar") {
    description = "Generates the sources jar for this project."
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

publishing {
    repositories {
        maven {
            name = "ModLabs"
            url = uri("https://nexus.modlabs.cc/repository/maven-public/")
            credentials {
                username = System.getenv("NEXUS_USER")
                password = System.getenv("NEXUS_PASS")
            }
        }
        mavenLocal()
    }
    publications {
        create<MavenPublication>("maven") {

            from(components["java"])

            artifact(tasks.named("sourcesJar"))

            pom {
                name.set("KTale")
                description.set("A utility library designed to simplify plugin development with Hytale and Kotlin.")
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