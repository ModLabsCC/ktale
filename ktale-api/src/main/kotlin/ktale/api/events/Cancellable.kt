package ktale.api.events

/**
 * Capability for events that can be cancelled.
 *
 * Cancellation semantics are event-defined.
 * For example, a platform adapter might treat cancellation as "do not execute default behavior",
 * while other events may use it as a hint to later phases.
 */
public interface Cancellable {
    /** Whether the event has been cancelled. */
    public var isCancelled: Boolean
}


