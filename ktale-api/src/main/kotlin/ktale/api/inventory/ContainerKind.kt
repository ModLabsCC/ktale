package ktale.api.inventory

import ktale.api.identity.Key

/**
 * A container kind identifier.
 *
 * This is a [Key]-backed identifier to avoid hardcoding all possible container types.
 */
public class ContainerKind public constructor(public val key: Key) {
    override fun toString(): String = key.toString()
    override fun equals(other: Any?): Boolean = other is ContainerKind && other.key == key
    override fun hashCode(): Int = key.hashCode()

    public companion object {
        @JvmStatic public fun of(value: String): ContainerKind = ContainerKind(Key.of(value))
    }
}


