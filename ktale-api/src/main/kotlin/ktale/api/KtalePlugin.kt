package ktale.api

/**
 * A KTale plugin entrypoint.
 *
 * ## Design note (intentional constraint)
 * KTale treats "unknown server APIs" as a first-class constraint.
 *
 * This interface is intentionally small and stable:
 * - Platform adapters provide a [PluginContext].
 * - Plugin code talks to contracts in `ktale-api`, not to platform types.
 * - No assumptions are made about threading, tick loops, or IO models.
 */
public interface KtalePlugin {
    /**
     * Called when the plugin is discovered and constructed, before it is enabled.
     *
     * This phase is for wiring services and reading static metadata.
     * Avoid registering listeners that assume a running server.
     */
    public fun onLoad(context: PluginContext)

    /**
     * Called when the plugin becomes active.
     *
     * This phase is for registering listeners, commands, and starting scheduled tasks.
     */
    public fun onEnable(context: PluginContext)

    /**
     * Called when the plugin is being disabled or unloaded.
     *
     * Implementations should cancel tasks and release resources.
     */
    public fun onDisable(context: PluginContext)
}


