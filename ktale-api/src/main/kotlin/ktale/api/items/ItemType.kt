package ktale.api.items

import ktale.api.identity.Key

/**
 * Item/material/block identifiers are modeled as [Key]s instead of enums.
 *
 * This avoids rewriting the SDK when the host's registry shape is known.
 */
public interface ItemType {
    public val key: Key
    public val displayName: String?
}


