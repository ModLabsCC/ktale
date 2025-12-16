package ktale.api.entities

import ktale.api.identity.Key

/**
 * Minimal reference to an entity-like thing in the host runtime.
 *
 * ## Design note
 * KTale intentionally does not model a full entity system.
 * This is just a stable handle plugins can pass around.
 */
public interface EntityRef {
    /** Stable identifier for this entity within the host runtime. */
    public val id: Key

    /**
     * Broad kind/type identifier for routing/logging.
     *
     * This is not a class hierarchy; it's a registry-style key.
     */
    public val kind: Key
}


