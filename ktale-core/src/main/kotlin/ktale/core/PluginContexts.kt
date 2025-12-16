package ktale.core

import ktale.api.PluginContext
import ktale.api.commands.CommandRegistry
import ktale.api.config.ConfigManager
import ktale.api.events.EventBus
import ktale.api.logging.KtaleLogger
import ktale.api.scheduler.Scheduler
import ktale.api.services.ServiceRegistry
import ktale.platform.Platform

/**
 * Default core implementation of [PluginContext].
 *
 * ## Design note
 * This is intentionally "dumb wiring":
 * it composes capability interfaces without introducing extra lifecycle assumptions.
 */
public class DefaultPluginContext(
    override val pluginId: String,
    platform: Platform,
    override val events: EventBus,
    override val scheduler: Scheduler,
    override val commands: CommandRegistry,
    override val configs: ConfigManager,
    override val services: ServiceRegistry,
) : PluginContext {
    override val logger: KtaleLogger = platform.loggers.logger(pluginId)
}


