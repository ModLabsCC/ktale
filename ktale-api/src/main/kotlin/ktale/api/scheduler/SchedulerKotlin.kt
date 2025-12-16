package ktale.api.scheduler

import java.time.Duration

/**
 * Kotlin convenience overloads for [Scheduler].
 *
 * These helpers exist so that the *public contracts* can remain Java-first (Runnable / java.time.Duration),
 * while Kotlin call sites still feel natural.
 */
public fun Scheduler.runSync(task: () -> Unit): TaskHandle = runSync(Runnable(task))

public fun Scheduler.runAsync(task: () -> Unit): TaskHandle = runAsync(Runnable(task))

public fun Scheduler.runSyncDelayed(delay: Duration, task: () -> Unit): TaskHandle =
    runSyncDelayed(delay, Runnable(task))

public fun Scheduler.runAsyncDelayed(delay: Duration, task: () -> Unit): TaskHandle =
    runAsyncDelayed(delay, Runnable(task))

public fun Scheduler.runSyncRepeating(initialDelay: Duration, interval: Duration, task: () -> Unit): TaskHandle =
    runSyncRepeating(initialDelay, interval, Runnable(task))

public fun Scheduler.runAsyncRepeating(initialDelay: Duration, interval: Duration, task: () -> Unit): TaskHandle =
    runAsyncRepeating(initialDelay, interval, Runnable(task))


