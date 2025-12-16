package ktale.api.config

/**
 * Typed configuration access.
 *
 * Implementations are responsible for:
 * - locating storage
 * - applying [ConfigMigration] steps
 * - decoding via [ConfigCodec]
 *
 * The core module provides a default file-backed implementation; platforms may override.
 */
public interface ConfigManager {
    /**
     * Loads a configuration for [key], applying migrations if needed.
     *
     * Implementations should be resilient: when loading fails, return [ConfigKey.defaultValue]
     * and log a diagnostic (platform-defined logging).
     */
    public fun <T : Any> load(key: ConfigKey<T>): T

    /** Saves [value] for [key] using [ConfigKey.codec]. */
    public fun <T : Any> save(key: ConfigKey<T>, value: T)
}


