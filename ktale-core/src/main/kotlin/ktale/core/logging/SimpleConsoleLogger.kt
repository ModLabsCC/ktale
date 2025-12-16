package ktale.core.logging

import ktale.api.logging.KtaleLogger
import ktale.api.logging.LogLevel
import java.time.Instant

/**
 * Minimal console logger used by the fake platform and as a fallback.
 */
public class SimpleConsoleLogger(
    private val name: String,
    private val nowEpochMillis: () -> Long = { System.currentTimeMillis() },
) : KtaleLogger {
    override fun log(level: LogLevel, message: String, throwable: Throwable?, context: Map<String, Any?>) {
        val ts = Instant.ofEpochMilli(nowEpochMillis()).toString()
        val ctx = if (context.isEmpty()) "" else " $context"
        val line = "[$ts] [$level] [$name] $message$ctx"
        if (level >= LogLevel.WARN) {
            System.err.println(line)
            throwable?.printStackTrace(System.err)
        } else {
            println(line)
            throwable?.printStackTrace(System.out)
        }
    }
}


