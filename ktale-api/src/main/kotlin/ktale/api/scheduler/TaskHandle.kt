package ktale.api.scheduler

/**
 * Handle for a scheduled task.
 *
 * Implementations must make [cancel] idempotent.
 */
public interface TaskHandle {
    /** Cancels the task. Safe to call multiple times. */
    public fun cancel()

    /** Whether [cancel] has been requested. */
    public val isCancelled: Boolean
}


