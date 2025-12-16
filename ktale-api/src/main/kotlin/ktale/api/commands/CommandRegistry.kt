package ktale.api.commands

/**
 * Registry for command definitions.
 *
 * Implementations must not perform IO directly; platform adapters bridge registration and input.
 */
public interface CommandRegistry {
    /**
     * Registers [definition].
     *
     * @throws IllegalArgumentException if the command name or alias is invalid or conflicts.
     */
    public fun register(definition: CommandDefinition)

    /**
     * Unregisters a command by its primary name.
     *
     * Implementations should also remove aliases that point to the definition.
     */
    public fun unregister(name: String)

    /**
     * Dispatches a command execution.
     *
     * Platform adapters typically call this after receiving user input, providing a [context]
     * that contains tokens and sender info.
     */
    public fun dispatch(context: CommandContext): CommandResult
}


