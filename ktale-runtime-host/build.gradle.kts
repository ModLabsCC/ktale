description = "Standalone KTale plugin host (loads plugin jars + resolves deps, builds isolated classloaders)."

dependencies {
    implementation(project(":ktale-api"))
    implementation(project(":ktale-platform"))
    implementation(project(":ktale-core"))
    implementation(project(":ktale-runtime-deps"))
}


