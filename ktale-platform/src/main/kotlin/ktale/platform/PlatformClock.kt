package ktale.platform

/**
 * Platform-provided time source.
 *
 * ## Design note
 * KTale avoids tying itself to any specific time API (tick counters, wall clocks, etc.).
 * This contract provides both:
 * - a wall-ish clock ([nowEpochMillis]) for timestamps/logging
 * - a monotonic clock ([monotonicNanos]) for scheduling/drift-safe measurements
 */
public interface PlatformClock {
    /** Current wall-ish time in epoch milliseconds. */
    public fun nowEpochMillis(): Long

    /**
     * Current monotonic time in nanoseconds.
     *
     * Values have no meaning as absolute timestamps; only differences are meaningful.
     */
    public fun monotonicNanos(): Long
}


