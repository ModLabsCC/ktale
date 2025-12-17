description = "KTale runtime dependency manifest + resolver (for standalone host runtimes)."

dependencies {
    // Maven Resolver (Aether) - used for downloading plugin dependencies at runtime.
    api("org.apache.maven.resolver:maven-resolver-impl:1.9.22")
    api("org.apache.maven.resolver:maven-resolver-connector-basic:1.9.22")
    api("org.apache.maven.resolver:maven-resolver-transport-http:1.9.22")
    api("org.apache.maven.resolver:maven-resolver-util:1.9.22")
}


