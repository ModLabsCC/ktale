package ktale.platform

import ktale.api.commands.CommandContext
import ktale.api.commands.CommandDefinition
import ktale.api.commands.CommandRegistry
import ktale.api.commands.CommandResult

/**
 * Platform boundary for command IO.
 *
 * ## Responsibilities
 * - Let core register/unregister commands with the host (if the host supports it).
 * - Allow the platform adapter to connect inbound command input to a [CommandRegistry].
 *
 * ## Anti-goal
 * This does not define a parsing engine. Platforms decide how input is tokenized and how
 * help/completions are implemented (if at all).
 */
public interface PlatformCommandBridge {
    /**
     * Called by core when a command is registered.
     *
     * Platforms may use this to expose the command to the host so that input routes into KTale.
     */
    public fun onRegister(definition: CommandDefinition)

    /**
     * Called by core when a command is unregistered.
     */
    public fun onUnregister(name: String)

    /**
     * Binds inbound command execution to a registry.
     *
     * Platform adapters should call this once they have a [registry] instance to dispatch into.
     */
    public fun bind(registry: CommandRegistry)

    /**
     * Convenience method for platform adapters to dispatch an inbound command to the bound registry.
     *
     * Implementations may throw if [bind] has not been called.
     */
    public fun dispatchInbound(context: CommandContext): CommandResult
}


