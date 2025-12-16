package ktale.platform.hytale

import ktale.api.logging.KtaleLogger
import ktale.api.logging.LogLevel
import ktale.platform.Platform
import ktale.platform.PlatformClock
import ktale.platform.PlatformCommandBridge
import ktale.platform.PlatformLoggerFactory
import ktale.platform.PlatformSchedulerHooks
import java.time.Duration

/**
 * Placeholder platform adapter for Hytale.
 *
 * ## Important: intentionally incomplete
 * The Hytale server software and API do not exist yet (for us), so this module must not
 * pretend we can integrate. This class exists only to:
 * - provide a compile-time location for a future adapter
 * - make uncertainty explicit through TODOs
 *
 * ## Rules
 * - No assumptions about real APIs
 * - No concrete integration logic
 * - Stubs must fail loudly if used
 */
public class HytalePlatformPlaceholder : Platform {
    override val platformId: String = "hytale-placeholder"

    override val clock: PlatformClock = object : PlatformClock {
        override fun nowEpochMillis(): Long = error("TODO(Hytale): Provide real time source from host runtime")
        override fun monotonicNanos(): Long = error("TODO(Hytale): Provide monotonic clock from host runtime")
    }

    override val loggers: PlatformLoggerFactory = object : PlatformLoggerFactory {
        override fun logger(name: String): KtaleLogger = object : KtaleLogger {
            override fun log(
                level: LogLevel,
                message: String,
                throwable: Throwable?,
                context: Map<String, Any?>,
            ) {
                error("TODO(Hytale): Provide real logging backend. Tried to log [$level] $name: $message")
            }
        }
    }

    override val scheduler: PlatformSchedulerHooks = object : PlatformSchedulerHooks {
        override fun runSync(task: Runnable) = error("TODO(Hytale): Wire sync scheduling to host")
        override fun runAsync(task: Runnable) = error("TODO(Hytale): Wire async scheduling to host")
        override fun runSyncDelayed(delay: Duration, task: Runnable) =
            error("TODO(Hytale): Wire delayed sync scheduling to host (delay=$delay)")

        override fun runAsyncDelayed(delay: Duration, task: Runnable) =
            error("TODO(Hytale): Wire delayed async scheduling to host (delay=$delay)")

        override fun runSyncRepeating(initialDelay: Duration, interval: Duration, task: Runnable) =
            error("TODO(Hytale): Wire repeating sync scheduling to host (initialDelay=$initialDelay interval=$interval)")

        override fun runAsyncRepeating(initialDelay: Duration, interval: Duration, task: Runnable) =
            error("TODO(Hytale): Wire repeating async scheduling to host (initialDelay=$initialDelay interval=$interval)")
    }

    override val commands: PlatformCommandBridge = object : PlatformCommandBridge {
        override fun onRegister(definition: ktale.api.commands.CommandDefinition) {
            error("TODO(Hytale): Register command with host (name=${definition.name})")
        }

        override fun onUnregister(name: String) {
            error("TODO(Hytale): Unregister command with host (name=$name)")
        }

        override fun bind(registry: ktale.api.commands.CommandRegistry) {
            error("TODO(Hytale): Bind inbound command execution to registry")
        }

        override fun dispatchInbound(context: ktale.api.commands.CommandContext): ktale.api.commands.CommandResult {
            error("TODO(Hytale): Dispatch inbound command from host into KTale")
        }
    }
}


