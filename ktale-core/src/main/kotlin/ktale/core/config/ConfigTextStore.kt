package ktale.core.config

/**
 * Text-based config storage boundary used by core config implementations.
 *
 * ## Design note
 * This is internal to core on purpose:
 * - `ktale-api` stays minimal and platform-agnostic.
 * - Platforms can swap the storage mechanism without altering plugin-facing contracts.
 */
public interface ConfigTextStore {
    public fun read(id: String): String?
    public fun write(id: String, text: String)
}


