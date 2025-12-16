description = "KTale minimal default implementations (platform-neutral)."

plugins {
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":ktale-api"))
    implementation(project(":ktale-platform"))

    // Core must stay platform-agnostic; config parsing lives here behind KTale codecs.
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.9.0")
    implementation("com.charleskorn.kaml:kaml:0.76.0")
}


