package ktale.core.commands

import ktale.api.commands.CommandContext
import ktale.api.commands.CommandDefinition
import ktale.api.commands.CommandRegistry
import ktale.api.commands.CommandResult
import ktale.api.commands.Permission
import java.util.concurrent.ConcurrentHashMap

/**
 * Minimal in-memory command registry and dispatcher (logic only).
 *
 * ## Design note
 * - No IO: platforms handle input and output routing.
 * - No parsing engine: dispatch uses [CommandContext.args] as already-tokenized input.
 * - No hierarchy assumptions: commands are flat and may implement their own sub-routing.
 */
public class SimpleCommandRegistry : CommandRegistry {
    private val byName = ConcurrentHashMap<String, CommandDefinition>()
    private val aliasToName = ConcurrentHashMap<String, String>()

    override fun register(definition: CommandDefinition) {
        val name = normalize(definition.name)
        require(name.isNotBlank()) { "Command name must not be blank" }

        if (byName.putIfAbsent(name, definition) != null) {
            throw IllegalArgumentException("Command already registered: $name")
        }

        for (aliasRaw in definition.aliases) {
            val alias = normalize(aliasRaw)
            require(alias.isNotBlank()) { "Alias must not be blank" }
            if (alias == name) continue
            val existing = aliasToName.putIfAbsent(alias, name)
            if (existing != null) {
                byName.remove(name)
                aliasToName.entries.removeIf { it.value == name }
                throw IllegalArgumentException("Alias '$alias' already registered for '$existing'")
            }
        }
    }

    override fun unregister(name: String) {
        val key = normalize(name)
        byName.remove(key)
        aliasToName.entries.removeIf { it.value == key }
    }

    override fun dispatch(context: CommandContext): CommandResult {
        val key = normalize(context.label)
        val name = aliasToName[key] ?: key
        val def = byName[name] ?: return UnknownCommand(name)

        val perm = def.permission
        if (perm != null && !context.sender.hasPermission(perm)) return CommandResult.NoPermission

        return def.execute(context)
    }

    private fun normalize(s: String): String = s.trim().lowercase()

    private data class UnknownCommand(val name: String) : CommandResult.UsageError {
        override val message: String = "Unknown command: $name"
    }
}


