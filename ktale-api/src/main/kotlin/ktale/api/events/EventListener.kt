package ktale.api.events

/**
 * Java-friendly event listener functional interface.
 *
 * Kotlin users can still pass lambdas naturally; Java users can pass lambdas or method references
 * without touching Kotlin function types.
 */
public fun interface EventListener<E : Event> {
    public fun onEvent(event: E)
}


