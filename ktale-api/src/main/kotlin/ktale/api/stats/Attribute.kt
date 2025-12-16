package ktale.api.stats

import ktale.api.identity.Key

/**
 * Minimal numeric attribute.
 *
 * ## Examples
 * - health (current/max)
 * - mana (current/max)
 *
 * ## Design note
 * The host may represent these differently; this is a capability surface for plugins.
 */
public interface Attribute {
    public val key: Key
    public fun current(): Double
    public fun max(): Double?
    public fun setCurrent(value: Double)
    public fun setMax(value: Double)
}


