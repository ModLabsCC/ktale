package ktale.platform.fake

import ktale.api.KtalePlugin
import ktale.api.PluginContext
import ktale.api.events.Event
import ktale.core.DefaultPluginContext
import ktale.core.commands.SimpleCommandRegistry
import ktale.core.config.CoreConfigManager
import ktale.core.config.InMemoryConfigTextStore
import ktale.core.events.SimpleEventBus
import ktale.core.scheduler.HookBackedScheduler
import ktale.core.services.SimpleServiceRegistry

/**
 * Fully controllable fake server runtime.
 *
 * ## Purpose
 * - Allows testing KTale-based plugins without any real server.
 * - Provides deterministic time + manual scheduler execution.
 * - Provides event simulation by posting into the core event bus.
 */
public class FakeServer(
    public val platform: FakePlatform = FakePlatform(),
) {
    public val events: SimpleEventBus = SimpleEventBus()
    public val commands: SimpleCommandRegistry = SimpleCommandRegistry()
    public val scheduler: HookBackedScheduler = HookBackedScheduler(platform.schedulerHooks)
    public val services: SimpleServiceRegistry = SimpleServiceRegistry()
    public val configs: CoreConfigManager = CoreConfigManager(InMemoryConfigTextStore(), platform.loggers.logger("fake-config"))

    init {
        platform.commandBridge.bind(commands)
    }

    public fun createContext(pluginId: String): PluginContext =
        DefaultPluginContext(
            pluginId = pluginId,
            platform = platform,
            events = events,
            scheduler = scheduler,
            commands = commands,
            configs = configs,
            services = services,
        )

    /** Simulates the plugin lifecycle using the fake runtime. */
    public fun runPlugin(pluginId: String, plugin: KtalePlugin, block: (PluginContext) -> Unit = {}) {
        val ctx = createContext(pluginId)
        plugin.onLoad(ctx)
        plugin.onEnable(ctx)
        try {
            block(ctx)
        } finally {
            plugin.onDisable(ctx)
        }
    }

    /** Posts an event into the core event bus. */
    public fun <E : Event> post(event: E): E = events.post(event)
}


