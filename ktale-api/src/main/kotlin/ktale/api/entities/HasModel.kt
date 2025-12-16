package ktale.api.entities

import ktale.api.identity.Key

/**
 * Capability: the entity can expose and/or change its "model".
 *
 * ## Design note
 * The word "model" is used because that's what we *expect* the game UI exposes,
 * but the actual host mapping is unknown. This capability stays generic:
 * a model is just a [Key] into a host-provided catalog.
 */
public interface HasModel {
    /** Current model key. */
    public fun model(): Key

    /**
     * Requests a model change.
     *
     * Platforms decide validation rules and whether this is instant or eventual.
     */
    public fun setModel(model: Key)
}


