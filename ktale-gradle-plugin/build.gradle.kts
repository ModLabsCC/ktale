description = "Gradle plugin: emits .dependencies/.repositories resources for KTale runtime dependency resolution."

plugins {
    `java-gradle-plugin`
}

dependencies {
    // Compiled against Gradle APIs, provided by Gradle at runtime.
    compileOnly(gradleApi())
}

gradlePlugin {
    plugins {
        create("ktaleDeps") {
            id = "cc.modlabs.ktale-deps"
            implementationClass = "cc.modlabs.ktale.gradle.KtaleDepsPlugin"
            displayName = "KTale dependency manifest generator"
            description = "Generates .dependencies/.repositories resources for runtime dependency resolution."
        }
    }
}


