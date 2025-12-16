package ktale.api.scheduler

import java.time.Duration

/**
 * Schedules work in sync/async contexts.
 *
 * ## Design note
 * KTale does not assume a tick loop, thread affinity, or coroutine availability.
 * "Sync" and "async" are *platform-defined* concepts; a platform adapter decides what
 * constitutes the main thread (if any), and how async is executed.
 */
public interface Scheduler {
    /** Runs [task] as soon as possible in the platform's "sync" context. */
    public fun runSync(task: Runnable): TaskHandle

    /** Runs [task] as soon as possible in the platform's "async" context. */
    public fun runAsync(task: Runnable): TaskHandle

    /** Runs [task] once after [delay] in the platform's "sync" context. */
    public fun runSyncDelayed(delay: Duration, task: Runnable): TaskHandle

    /** Runs [task] once after [delay] in the platform's "async" context. */
    public fun runAsyncDelayed(delay: Duration, task: Runnable): TaskHandle

    /**
     * Runs [task] repeatedly with the given [interval] in the platform's "sync" context.
     *
     * Implementations should attempt to avoid drift, but exact semantics are platform-defined.
     */
    public fun runSyncRepeating(
        initialDelay: Duration,
        interval: Duration,
        task: Runnable,
    ): TaskHandle

    /** Async variant of [runSyncRepeating]. */
    public fun runAsyncRepeating(
        initialDelay: Duration,
        interval: Duration,
        task: Runnable,
    ): TaskHandle
}


