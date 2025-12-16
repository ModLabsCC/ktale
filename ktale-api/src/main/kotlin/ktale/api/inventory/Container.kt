package ktale.api.inventory

import ktale.api.identity.Key
import ktale.api.items.ItemStack

/**
 * Minimal container/inventory abstraction.
 *
 * ## Design note
 * KTale doesn't assume a particular slot model (grid vs list vs equipment). This is a generic interface
 * that can represent backpacks, equipment, bags, etc. through [ContainerKind] and slot indexing.
 */
public interface Container {
    /** Stable identifier for this container (best-effort). */
    public val id: Key

    /** Kind identifier (e.g. "equipment", "backpack", "brewery_bag"). */
    public val kind: ContainerKind

    /** Number of slots in this container. */
    public fun size(): Int

    public fun get(slot: Int): ItemStack?
    public fun set(slot: Int, stack: ItemStack?)
}


