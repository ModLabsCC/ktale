package ktale.platform

import ktale.api.scheduler.TaskHandle
import java.time.Duration

/**
 * Platform scheduler hooks.
 *
 * ## Design note
 * This is a low-level hook surface used by `ktale-core` and adapters to map KTale scheduling concepts
 * onto a host runtime.
 *
 * Implementations should be deterministic where possible and make [TaskHandle.cancel] idempotent.
 */
public interface PlatformSchedulerHooks {
    public fun runSync(task: Runnable): TaskHandle
    public fun runAsync(task: Runnable): TaskHandle

    public fun runSyncDelayed(delay: Duration, task: Runnable): TaskHandle
    public fun runAsyncDelayed(delay: Duration, task: Runnable): TaskHandle

    public fun runSyncRepeating(initialDelay: Duration, interval: Duration, task: Runnable): TaskHandle
    public fun runAsyncRepeating(initialDelay: Duration, interval: Duration, task: Runnable): TaskHandle
}


