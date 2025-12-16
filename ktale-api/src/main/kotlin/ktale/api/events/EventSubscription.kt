package ktale.api.events

/**
 * Handle for a listener registration.
 *
 * Implementations must make [unsubscribe] idempotent.
 */
public interface EventSubscription {
    /** Unregisters the listener. Safe to call multiple times. */
    public fun unsubscribe()
}


