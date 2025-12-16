package ktale.api.config

/**
 * Encodes and decodes a typed configuration object.
 *
 * ## Design note
 * The codec is separated from the store to avoid committing KTale to a specific format (YAML/TOML/JSON),
 * while still enabling typed config objects.
 *
 * Codecs can be replaced without changing plugin code that uses [ConfigKey].
 */
public interface ConfigCodec<T : Any> {
    /** Parses [text] into a typed config object. */
    public fun decode(text: String): T

    /** Serializes [value] into text suitable for storage. */
    public fun encode(value: T): String
}


