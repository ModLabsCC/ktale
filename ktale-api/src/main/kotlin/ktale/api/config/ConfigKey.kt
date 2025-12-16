package ktale.api.config

/**
 * Typed handle for a configuration file/document.
 *
 * @param T strongly-typed config object type
 */
public interface ConfigKey<T : Any> {
    /**
     * Stable identifier for the config (often a filename without extension).
     *
     * Platform adapters may map this to a file, database row, or other store.
     */
    public val id: String

    /** Current schema version for this config. */
    public val version: Int

    /** Codec used to parse and serialize this config. */
    public val codec: ConfigCodec<T>

    /** Default value used when config is missing or cannot be loaded. */
    public fun defaultValue(): T

    /**
     * Migrations to apply when stored config version is older than [version].
     *
     * Implementations should list migrations in ascending order.
     */
    public val migrations: List<ConfigMigration>
}


