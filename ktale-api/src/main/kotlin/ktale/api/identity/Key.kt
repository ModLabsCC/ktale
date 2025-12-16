package ktale.api.identity

/**
 * Opaque identifier for game-facing registries (items, blocks, materials, models, prefabs, etc.).
 *
 * ## Design note
 * - This is intentionally *not* an enum: registries may be huge and may evolve.
 * - This is intentionally *not* tied to any host API: it can map to strings, hashes, resource locations, etc.
 * - Java-friendly: this is a normal class (not a Kotlin value class).
 *
 * ## Example
 * IDs can be plain strings such as `Cloth_Block_Wool_Blue` (as seen in Hytale UI/tooling).
 * KTale preserves the string verbatim; it does not enforce casing or separators.
 */
public class Key public constructor(public val value: String) {
    init {
        require(value.isNotBlank()) { "Key must not be blank" }
    }

    override fun toString(): String = value
    override fun equals(other: Any?): Boolean = other is Key && other.value == value
    override fun hashCode(): Int = value.hashCode()

    public companion object {
        @JvmStatic
        public fun of(value: String): Key = Key(value)
    }
}


