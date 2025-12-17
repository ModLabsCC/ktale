plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "ktale"

include(
    "ktale-api",
    "ktale-core",
    "ktale-platform",
    "ktale-platform-fake",
    "ktale-platform-hytale",
    "ktale-runtime-deps",
    "ktale-gradle-plugin",
    "ktale-runtime-host",
)