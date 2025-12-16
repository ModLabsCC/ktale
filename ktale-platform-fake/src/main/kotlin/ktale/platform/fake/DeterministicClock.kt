package ktale.platform.fake

import ktale.platform.PlatformClock
import java.time.Duration
import java.util.concurrent.atomic.AtomicLong

/**
 * Deterministic clock for tests and fake servers.
 *
 * ## Design note
 * Both clocks advance together:
 * - [nowEpochMillis] is derived from [epochMillis].
 * - [monotonicNanos] is derived from [monoNanos].
 */
public class DeterministicClock(
    startEpochMillis: Long = 0L,
) : PlatformClock {
    private val epochMillis = AtomicLong(startEpochMillis)
    private val monoNanos = AtomicLong(0L)

    override fun nowEpochMillis(): Long = epochMillis.get()

    override fun monotonicNanos(): Long = monoNanos.get()

    /** Advances the clock by [duration]. */
    public fun advanceBy(duration: Duration) {
        val millis = duration.toMillis()
        val nanos = duration.toNanos()
        epochMillis.addAndGet(millis)
        monoNanos.addAndGet(nanos)
    }

    /** Sets absolute epoch millis (monotonic clock is unaffected). */
    public fun setEpochMillis(value: Long) {
        epochMillis.set(value)
    }
}


