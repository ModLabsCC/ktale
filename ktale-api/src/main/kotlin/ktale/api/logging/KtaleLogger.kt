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
    /** Logs a message at [level] with optional structured [context]. */
    public fun log(level: LogLevel, message: String, throwable: Throwable?, context: Map<String, Any?>)

    /** Java-friendly overload. */
    public fun log(level: LogLevel, message: String) {
        log(level, message, null, emptyMap())
    }

    /** Java-friendly overload. */
    public fun log(level: LogLevel, message: String, throwable: Throwable?) {
        log(level, message, throwable, emptyMap())
    }

    public fun trace(message: String) = log(LogLevel.TRACE, message)
    public fun trace(message: String, throwable: Throwable?) = log(LogLevel.TRACE, message, throwable)
    public fun trace(message: String, throwable: Throwable?, context: Map<String, Any?>) =
        log(LogLevel.TRACE, message, throwable, context)

    public fun debug(message: String) = log(LogLevel.DEBUG, message)
    public fun debug(message: String, throwable: Throwable?) = log(LogLevel.DEBUG, message, throwable)
    public fun debug(message: String, throwable: Throwable?, context: Map<String, Any?>) =
        log(LogLevel.DEBUG, message, throwable, context)

    public fun info(message: String) = log(LogLevel.INFO, message)
    public fun info(message: String, throwable: Throwable?) = log(LogLevel.INFO, message, throwable)
    public fun info(message: String, throwable: Throwable?, context: Map<String, Any?>) =
        log(LogLevel.INFO, message, throwable, context)

    public fun warn(message: String) = log(LogLevel.WARN, message)
    public fun warn(message: String, throwable: Throwable?) = log(LogLevel.WARN, message, throwable)
    public fun warn(message: String, throwable: Throwable?, context: Map<String, Any?>) =
        log(LogLevel.WARN, message, throwable, context)

    public fun error(message: String) = log(LogLevel.ERROR, message)
    public fun error(message: String, throwable: Throwable?) = log(LogLevel.ERROR, message, throwable)
    public fun error(message: String, throwable: Throwable?, context: Map<String, Any?>) =
        log(LogLevel.ERROR, message, throwable, context)
}


