package ktale.api.stats

import ktale.api.identity.Key

/**
 * Capability: exposes a set of numeric attributes (health, mana, etc.).
 *
 * ## Design note
 * We avoid hardcoding a full stat system; attributes are resolved by [Key].
 */
public interface HasAttributes {
    /** Returns an attribute by key, or `null` if not supported. */
    public fun attribute(key: Key): Attribute?

    /** Returns supported attribute keys (best-effort). */
    public fun attributes(): List<Key>
}


