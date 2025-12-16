package ktale.platform.fake

import ktale.api.commands.CommandContext
import ktale.api.commands.CommandDefinition
import ktale.api.commands.CommandRegistry
import ktale.api.commands.CommandResult
import ktale.platform.PlatformCommandBridge
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

/**
 * Minimal command bridge for the fake platform.
 *
 * This bridge captures registrations for introspection and dispatches inbound commands into the bound registry.
 */
public class FakeCommandBridge : PlatformCommandBridge {
    private val byName = ConcurrentHashMap<String, CommandDefinition>()
    private val registryRef = AtomicReference<CommandRegistry?>(null)

    override fun onRegister(definition: CommandDefinition) {
        byName[definition.name.lowercase()] = definition
    }

    override fun onUnregister(name: String) {
        byName.remove(name.lowercase())
    }

    override fun bind(registry: CommandRegistry) {
        registryRef.set(registry)
    }

    override fun dispatchInbound(context: CommandContext): CommandResult {
        val registry = registryRef.get() ?: error("FakeCommandBridge.bind(registry) was not called")
        return registry.dispatch(context)
    }

    /** Returns whether a command is registered (by primary name). */
    public fun isRegistered(name: String): Boolean = byName.containsKey(name.lowercase())
}


