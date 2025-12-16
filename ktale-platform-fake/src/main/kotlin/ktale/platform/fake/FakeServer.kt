package ktale.platform.fake

import ktale.api.KtalePlugin
import ktale.api.PluginContext
import ktale.api.events.Event
// DefaultPluginContext is composed by CoreRuntime.
import ktale.core.events.SimpleEventBus
import ktale.core.runtime.CoreRuntime

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
    private val runtime = CoreRuntime(platform, ktale.core.config.InMemoryConfigTextStore())

    public val events: SimpleEventBus get() = runtime.events as SimpleEventBus
    public val commands get() = runtime.commands
    public val scheduler get() = runtime.scheduler
    public val services get() = runtime.services
    public val configs get() = runtime.configs

    public fun createContext(pluginId: String): PluginContext =
        runtime.pluginContext(pluginId)

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


