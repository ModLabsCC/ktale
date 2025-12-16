package ktale.core.commands

import ktale.api.commands.CommandContext
import ktale.api.commands.CommandResult

/**
 * Java-friendly command executor functional interface.
 *
 * `ktale-api` exposes [ktale.api.commands.CommandDefinition.execute] as a Kotlin method,
 * but Java plugins typically prefer functional interfaces for wiring.
 */
public fun interface CommandExecutor {
    public fun execute(context: CommandContext): CommandResult
}


