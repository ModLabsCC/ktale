package ktale.platform.fake

import ktale.api.scheduler.TaskHandle
import ktale.platform.PlatformSchedulerHooks
import java.time.Duration
import java.util.PriorityQueue
import java.util.concurrent.atomic.AtomicLong

/**
 * Fully controllable scheduler hooks backed by a [DeterministicClock].
 *
 * ## Control surface
 * The platform API only exposes scheduling *hooks*; this fake exposes extra methods for tests:
 * - [runDueSync] / [runDueAsync]
 * - [advanceBy] (optionally auto-running due tasks)
 *
 * ## Semantics
 * - "sync" and "async" are modeled as two independent queues.
 * - Tasks only run when the test/server calls a control method.
 */
public class FakeSchedulerHooks(
    private val clock: DeterministicClock,
) : PlatformSchedulerHooks {
    private data class Scheduled(
        val dueNanos: Long,
        val intervalNanos: Long?,
        val task: Runnable,
        val handle: FakeTaskHandle,
        val id: Long,
    )

    private val idSeq = AtomicLong(0L)

    private val syncQueue = PriorityQueue<Scheduled>(compareBy({ it.dueNanos }, { it.id }))
    private val asyncQueue = PriorityQueue<Scheduled>(compareBy({ it.dueNanos }, { it.id }))

    override fun runSync(task: Runnable): TaskHandle =
        schedule(syncQueue, Duration.ZERO, null, task)

    override fun runAsync(task: Runnable): TaskHandle =
        schedule(asyncQueue, Duration.ZERO, null, task)

    override fun runSyncDelayed(delay: Duration, task: Runnable): TaskHandle =
        schedule(syncQueue, delay, null, task)

    override fun runAsyncDelayed(delay: Duration, task: Runnable): TaskHandle =
        schedule(asyncQueue, delay, null, task)

    override fun runSyncRepeating(initialDelay: Duration, interval: Duration, task: Runnable): TaskHandle =
        schedule(syncQueue, initialDelay, interval, task)

    override fun runAsyncRepeating(initialDelay: Duration, interval: Duration, task: Runnable): TaskHandle =
        schedule(asyncQueue, initialDelay, interval, task)

    private fun schedule(
        q: PriorityQueue<Scheduled>,
        delay: Duration,
        interval: Duration?,
        task: Runnable,
    ): TaskHandle {
        val handle = FakeTaskHandle()
        val due = clock.monotonicNanos() + delay.toNanos()
        val id = idSeq.incrementAndGet()
        q.add(
            Scheduled(
                dueNanos = due,
                intervalNanos = interval?.toNanos(),
                task = task,
                handle = handle,
                id = id,
            )
        )
        return handle
    }

    /** Runs all due sync tasks at the current clock time. */
    public fun runDueSync() {
        runDue(syncQueue)
    }

    /** Runs all due async tasks at the current clock time. */
    public fun runDueAsync() {
        runDue(asyncQueue)
    }

    /** Advances the clock by [duration]. */
    public fun advanceBy(duration: Duration, runDueAfterAdvance: Boolean = true) {
        clock.advanceBy(duration)
        if (runDueAfterAdvance) {
            runDueSync()
            runDueAsync()
        }
    }

    private fun runDue(q: PriorityQueue<Scheduled>) {
        val now = clock.monotonicNanos()
        while (true) {
            val next = q.peek() ?: return
            if (next.dueNanos > now) return
            q.poll()
            if (!next.handle.isCancelled) {
                next.task.run()
            }
            val interval = next.intervalNanos
            if (interval != null && !next.handle.isCancelled) {
                q.add(next.copy(dueNanos = next.dueNanos + interval))
            }
        }
    }
}


