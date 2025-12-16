package ktale.api.events

/**
 * Relative ordering for event listeners.
 *
 * ## Design note
 * This is intentionally *not* modeled after any existing game plugin API.
 * The goal is to provide a generic ordering mechanism that can map onto
 * any future platform behavior.
 */
public enum class EventPriority {
    /**
     * Earliest observers.
     *
     * Typical use: validation, cheap pre-checks, early routing.
     */
    EARLY,

    /** Default priority for most listeners. */
    NORMAL,

    /**
     * Later observers.
     *
     * Typical use: modifications that should see effects from NORMAL listeners.
     */
    LATE,

    /**
     * Last observers.
     *
     * Typical use: metrics, logging, state mirroring (avoid mutating event here).
     */
    FINAL,
}


