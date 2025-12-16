package ktale.core.threading

import ktale.api.logging.KtaleLogger

/**
 * Conceptual thread guards (not enforced).
 *
 * ## Why this exists
 * We don't know the eventual server threading model (if any).
 * This is a lightweight mechanism that can be used to *document* expectations
 * and to optionally log diagnostics when expectations are violated.
 *
 * ## Non-goal
 * This does not attempt to control or enforce threads.
 */
public class ThreadGuards(
    private val logger: KtaleLogger,
) {
    /** Marker for "expected sync context". */
    public fun expectSync(note: String) {
        logger.debug("ThreadGuard(sync): $note")
    }

    /** Marker for "expected async context". */
    public fun expectAsync(note: String) {
        logger.debug("ThreadGuard(async): $note")
    }
}


