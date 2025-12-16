/*
 * SPECULATION RATIONALE:
 * We do not know how Hytale will represent commands (string-based, structured, chat-driven, etc.).
 * This sketch exists to reason about what the platform adapter might need to translate.
 *
 * CONFIDENCE: LOW
 *
 * This file lives in `ktale.experimental.hypothesis` and is OPTIONAL/REMOVABLE.
 * Core modules must not depend on it.
 */
package ktale.experimental.hypothesis

/**
 * Hypothetical shapes of inbound command input a host might provide.
 */
public sealed interface CommandRoutingHypothesis {
    /** Host provides raw text after a leading slash (or similar). */
    public data class RawLine(val line: String) : CommandRoutingHypothesis

    /** Host provides a pre-tokenized representation. */
    public data class Tokens(val label: String, val args: List<String>) : CommandRoutingHypothesis

    /** Host provides a structured argument model. */
    public data object Structured : CommandRoutingHypothesis
}


