package ktale.platform

import ktale.api.logging.KtaleLogger

/**
 * Factory for platform-backed loggers.
 *
 * Platforms may route logs to consoles, files, structured telemetry, or remote sinks.
 */
public interface PlatformLoggerFactory {
    /**
     * Creates a logger scoped to [name].
     *
     * Conventionally, platforms should scope by plugin id (e.g. "myplugin") and/or component.
     */
    public fun logger(name: String): KtaleLogger
}


