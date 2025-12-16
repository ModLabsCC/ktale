package ktale.platform

/**
 * Platform boundary for KTale.
 *
 * ## Design note (architectural rule)
 * This module defines *only* the boundary. It contains:
 * - no game logic
 * - no assumptions about Hytale APIs
 * - no entity/world modeling
 *
 * ## Unknown host model (explicit uncertainty)
 * KTale deliberately does not commit (yet) to whether it will be used as:
 * - a plugin/mod SDK hosted by an official server runtime, or
 * - a bundled/custom server software distribution that embeds KTale as its core.
 *
 * Both models can be represented by an implementation of this boundary without changing `ktale-api`.
 *
 * Core implementations may depend on this boundary, but the boundary must remain portable
 * across unknown future server APIs.
 */
public interface Platform {
    /** Stable identifier for diagnostics (e.g. "fake", "hytale", "custom"). */
    public val platformId: String

    /** Platform-provided time source. */
    public val clock: PlatformClock

    /** Platform-provided logging backend. */
    public val loggers: PlatformLoggerFactory

    /**
     * Scheduler hooks for sync/async execution.
     *
     * Platforms define what "sync" and "async" mean.
     */
    public val scheduler: PlatformSchedulerHooks

    /**
     * Command IO bridge.
     *
     * This is the platform boundary for command registration and inbound command execution.
     */
    public val commands: PlatformCommandBridge
}


