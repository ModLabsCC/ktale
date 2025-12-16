package ktale.core.scheduler

import ktale.api.scheduler.Scheduler
import ktale.api.scheduler.TaskHandle
import ktale.platform.PlatformSchedulerHooks
import java.time.Duration

/**
 * Scheduler implementation backed by [PlatformSchedulerHooks].
 *
 * ## Design note
 * The KTale public contract uses Java-first types (Runnable / java.time.Duration) so Java plugins
 * can use the SDK without friction. Kotlin ergonomics are provided via extension helpers in `ktale-api`.
 *
 * "Sync" vs "async" semantics remain platform-defined; this class is a thin adapter.
 */
public class HookBackedScheduler(
    private val hooks: PlatformSchedulerHooks,
) : Scheduler {
    override fun runSync(task: Runnable): TaskHandle = hooks.runSync(task)

    override fun runAsync(task: Runnable): TaskHandle = hooks.runAsync(task)

    override fun runSyncDelayed(delay: Duration, task: Runnable): TaskHandle = hooks.runSyncDelayed(delay, task)

    override fun runAsyncDelayed(delay: Duration, task: Runnable): TaskHandle = hooks.runAsyncDelayed(delay, task)

    override fun runSyncRepeating(initialDelay: Duration, interval: Duration, task: Runnable): TaskHandle =
        hooks.runSyncRepeating(initialDelay, interval, task)

    override fun runAsyncRepeating(initialDelay: Duration, interval: Duration, task: Runnable): TaskHandle =
        hooks.runAsyncRepeating(initialDelay, interval, task)
}


