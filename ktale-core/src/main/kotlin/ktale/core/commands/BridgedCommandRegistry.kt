package ktale.core.commands

import ktale.api.commands.CommandContext
import ktale.api.commands.CommandDefinition
import ktale.api.commands.CommandRegistry
import ktale.api.commands.CommandResult
import ktale.platform.PlatformCommandBridge

/**
 * Command registry wrapper that notifies a [PlatformCommandBridge] on registration changes.
 *
 * ## Design note
 * `ktale-api` intentionally keeps command contracts IO-free. This wrapper is the point where
 * core can *optionally* bridge command registration into a host runtime without polluting
 * the logic-only registry itself.
 */
public class BridgedCommandRegistry(
    private val delegate: CommandRegistry,
    private val bridge: PlatformCommandBridge,
) : CommandRegistry {
    init {
        bridge.bind(this)
    }

    override fun register(definition: CommandDefinition) {
        delegate.register(definition)
        bridge.onRegister(definition)
    }

    override fun unregister(name: String) {
        delegate.unregister(name)
        bridge.onUnregister(name)
    }

    override fun dispatch(context: CommandContext): CommandResult = delegate.dispatch(context)
}


