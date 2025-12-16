package ktale.api.logging

/**
 * Logging abstraction.
 *
 * ## Design note
 * KTale avoids prescribing a logging backend. Platforms may route logs to:
 * - console
 * - file
 * - structured telemetry
 * - remote sinks
 *
 * Implementations should treat [context] as *optional structured data*.
 */
public interface KtaleLogger {
    /** Logs a message at [level]. */
    public fun log(
        level: LogLevel,
        message: String,
        throwable: Throwable? = null,
        context: Map<String, Any?> = emptyMap(),
    )

    public fun trace(message: String, throwable: Throwable? = null, context: Map<String, Any?> = emptyMap()) {
        log(LogLevel.TRACE, message, throwable, context)
    }

    public fun debug(message: String, throwable: Throwable? = null, context: Map<String, Any?> = emptyMap()) {
        log(LogLevel.DEBUG, message, throwable, context)
    }

    public fun info(message: String, throwable: Throwable? = null, context: Map<String, Any?> = emptyMap()) {
        log(LogLevel.INFO, message, throwable, context)
    }

    public fun warn(message: String, throwable: Throwable? = null, context: Map<String, Any?> = emptyMap()) {
        log(LogLevel.WARN, message, throwable, context)
    }

    public fun error(message: String, throwable: Throwable? = null, context: Map<String, Any?> = emptyMap()) {
        log(LogLevel.ERROR, message, throwable, context)
    }
}


