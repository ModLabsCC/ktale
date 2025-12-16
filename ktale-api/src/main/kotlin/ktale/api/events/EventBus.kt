package ktale.api.events

/**
 * Event publishing and subscription.
 *
 * ## Key constraints
 * - No dependency on a real server runtime.
 * - No assumptions about threading: platforms decide what "sync" means.
 * - Subscription is type-based and capability-oriented (e.g. [Cancellable]).
 *
 * Implementations should be deterministic and test-friendly.
 */
public interface EventBus {
    /**
     * Publishes an event to all listeners.
     *
     * @return the same [event] instance for convenience.
     */
    public fun <E : Event> post(event: E): E

    /**
     * Subscribes a listener for a specific event type.
     *
     * @param type the exact event class to subscribe to
     * @param priority relative order of invocation
     * @param ignoreCancelled if `true`, the listener is skipped when [event] is [Cancellable] and cancelled
     */
    public fun <E : Event> subscribe(
        type: Class<E>,
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        listener: EventListener<E>,
    ): EventSubscription
}

/**
 * Kotlin convenience overload.
 */
public inline fun <reified E : Event> EventBus.subscribe(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    noinline listener: (E) -> Unit,
): EventSubscription = subscribe(E::class.java, priority, ignoreCancelled, EventListener(listener))

/**
 * Java convenience overload.
 */
public fun <E : Event> EventBus.subscribe(
    type: Class<E>,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    listener: (E) -> Unit,
): EventSubscription = subscribe(type, priority, ignoreCancelled, EventListener(listener))


