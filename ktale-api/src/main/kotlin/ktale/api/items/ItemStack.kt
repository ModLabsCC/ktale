package ktale.api.items

import ktale.api.identity.Key

/**
 * Minimal stack of an [ItemType].
 *
 * ## Design note
 * Metadata is modeled as a string-keyed map to avoid committing to a host NBT/JSON/etc system.
 *
 * ## ID note
 * The [type] key is an opaque string identifier; IDs like `Cloth_Block_Wool_Blue` are valid.
 */
public interface ItemStack {
    public val type: Key
    public val amount: Int
    public val meta: Map<String, String>
}


