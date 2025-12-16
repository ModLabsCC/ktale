package ktale.core.runtime

import ktale.api.PluginContext
import ktale.api.commands.CommandRegistry
import ktale.api.config.ConfigManager
import ktale.api.events.EventBus
import ktale.api.prefabs.PrefabStore
import ktale.api.scheduler.Scheduler
import ktale.api.services.ServiceRegistry
import ktale.core.DefaultPluginContext
import ktale.core.commands.BridgedCommandRegistry
import ktale.core.commands.SimpleCommandRegistry
import ktale.core.config.ConfigTextStore
import ktale.core.config.CoreConfigManager
import ktale.core.events.SimpleEventBus
import ktale.core.scheduler.HookBackedScheduler
import ktale.core.services.SimpleServiceRegistry
import ktale.core.threading.ThreadGuards
import ktale.platform.Platform

/**
 * Minimal "Day-1" core runtime wiring helper.
 *
 * ## Design note
 * This is *optional* glue:
 * - platform adapters may choose to build their own contexts
 * - fake servers can use this to reduce boilerplate
 *
 * This class intentionally does not guess storage locations; callers provide a [ConfigTextStore].
 */
public class CoreRuntime(
    private val platform: Platform,
    private val configStore: ConfigTextStore,
) {
    public val prefabs: PrefabStore? = null
    public val services: ServiceRegistry = SimpleServiceRegistry()
    public val events: EventBus = SimpleEventBus()
    public val scheduler: Scheduler = HookBackedScheduler(platform.scheduler)

    private val baseCommands: CommandRegistry = SimpleCommandRegistry()
    public val commands: CommandRegistry = BridgedCommandRegistry(baseCommands, platform.commands)

    public val configs: ConfigManager = CoreConfigManager(configStore, platform.loggers.logger("ktale-config"))

    public fun threadGuards(pluginId: String): ThreadGuards =
        ThreadGuards(platform.loggers.logger("$pluginId-threading"))

    public fun pluginContext(pluginId: String): PluginContext =
        DefaultPluginContext(
            pluginId = pluginId,
            platform = platform,
            events = events,
            scheduler = scheduler,
            commands = commands,
            configs = configs,
            services = services,
            prefabs = prefabs,
        )
}


