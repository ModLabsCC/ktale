/*
 * SPECULATION RATIONALE:
 * We do not know Hytale's server threading model. Possible models include:
 * - single-threaded "main loop" with background workers
 * - region/world partitioned threads
 * - fully async actor-style model
 *
 * CONFIDENCE: LOW
 *
 * This file lives in `ktale.experimental.hypothesis` and is OPTIONAL/REMOVABLE.
 * Core modules must not depend on it.
 */
package ktale.experimental.hypothesis

/**
 * Hypothetical execution contexts that a server might provide.
 *
 * KTale's `sync`/`async` terminology is intentionally abstract; this enum is only a speculative mapping aid.
 */
public enum class ThreadingHypothesis {
    MAIN_THREAD,
    WORKER_POOL,
    REGION_THREAD,
    ASYNC_RUNTIME,
}


