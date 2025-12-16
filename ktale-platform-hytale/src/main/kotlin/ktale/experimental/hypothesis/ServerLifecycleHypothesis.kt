/*
 * SPECULATION RATIONALE:
 * Hytale server lifecycle contracts are unknown. Many server runtimes have phases roughly like
 * "load -> enable -> disable", but we do not know:
 * - whether plugins are isolated per world/realm
 * - whether reloads exist
 * - whether enable is synchronous or async
 *
 * CONFIDENCE: MEDIUM
 *
 * This file lives in `ktale.experimental.hypothesis` and is OPTIONAL/REMOVABLE.
 * Core modules must not depend on it.
 */
package ktale.experimental.hypothesis

/**
 * Hypothetical lifecycle phases a host runtime might expose.
 *
 * ## Non-guarantee
 * These are NOT promises about Hytale. They are a vocabulary to discuss potential mapping.
 */
public enum class ServerLifecycleHypothesis {
    DISCOVERY,
    LOAD,
    ENABLE,
    DISABLE,
    SHUTDOWN,
}


