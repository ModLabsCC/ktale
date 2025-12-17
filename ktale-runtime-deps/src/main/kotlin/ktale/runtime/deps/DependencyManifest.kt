package ktale.runtime.deps

/**
 * A runtime dependency manifest.
 *
 * ## Purpose
 * This exists for the "KTale as a bundled/standalone server" host model:
 * plugins can remain unshaded (clean jars) while the host downloads dependencies on demand.
 *
 * ## Non-goal
 * This does not prescribe *how* classpaths are modified.
 * A host runtime can use the resolved jar files to build isolated classloaders.
 */
public data class DependencyManifest(
    /** Maven coordinates in `group:artifact:version` form. */
    public val coordinates: List<String>,
    /** Optional additional repositories (id + url). */
    public val repositories: List<Repository>,
) {
    public data class Repository(
        public val id: String,
        public val url: String,
        /**
         * Optional env var name for username.
         *
         * IMPORTANT: actual secrets must not be stored in jars; only env var names are recorded.
         */
        public val usernameEnv: String? = null,
        /** Optional env var name for password/token. */
        public val passwordEnv: String? = null,
    )
}


