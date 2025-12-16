package ktale.core.commands

import ktale.api.commands.CommandContext
import ktale.api.commands.CommandDefinition
import ktale.api.commands.CommandResult
import ktale.api.commands.Permission

/**
 * Command builders for both Kotlin and Java.
 *
 * ## Design note
 * - This lives in `ktale-core` because it is an implementation detail (builders create [CommandDefinition] instances).
 * - `ktale-api` stays implementation-free.
 */
public object Commands {
    /**
     * Java-friendly fluent builder entrypoint.
     *
     * Example (Java):
     * `Commands.command("ping").executor(ctx -> CommandResult.Success).build()`
     */
    @JvmStatic
    public fun command(name: String): FluentBuilder = FluentBuilder(name)

    /**
     * Kotlin DSL entrypoint.
     *
     * Example (Kotlin):
     * `command("ping") { execute { CommandResult.Success } }`
     */
    public fun command(name: String, block: DslBuilder.() -> Unit): CommandDefinition =
        DslBuilder(name).apply(block).build()

    public class FluentBuilder internal constructor(
        private val name: String,
    ) {
        private val aliases: MutableSet<String> = linkedSetOf()
        private var description: String? = null
        private var permission: Permission? = null
        private var executor: CommandExecutor? = null

        public fun aliases(vararg aliases: String): FluentBuilder = apply { this.aliases += aliases }
        public fun description(description: String?): FluentBuilder = apply { this.description = description }
        public fun permission(permission: Permission?): FluentBuilder = apply { this.permission = permission }
        public fun executor(executor: CommandExecutor): FluentBuilder = apply { this.executor = executor }

        public fun build(): CommandDefinition {
            val exec = executor ?: error("Commands.command('$name') is missing an executor")
            return SimpleCommandDefinition(
                name = name,
                aliases = aliases.toSet(),
                description = description,
                permission = permission,
                executor = exec,
            )
        }
    }

    @DslMarker
    public annotation class CommandDsl

    @CommandDsl
    public class DslBuilder internal constructor(
        private val name: String,
    ) {
        private val aliases: MutableSet<String> = linkedSetOf()
        public var description: String? = null
        public var permission: Permission? = null
        private var executor: CommandExecutor? = null

        public fun aliases(vararg aliases: String) {
            this.aliases += aliases
        }

        public fun execute(block: (CommandContext) -> CommandResult) {
            executor = CommandExecutor(block)
        }

        public fun build(): CommandDefinition {
            val exec = executor ?: error("command('$name') is missing an execute { ... } block")
            return SimpleCommandDefinition(
                name = name,
                aliases = aliases.toSet(),
                description = description,
                permission = permission,
                executor = exec,
            )
        }
    }

    private class SimpleCommandDefinition(
        override val name: String,
        override val aliases: Set<String>,
        override val description: String?,
        override val permission: Permission?,
        private val executor: CommandExecutor,
    ) : CommandDefinition {
        override fun execute(context: CommandContext): CommandResult = executor.execute(context)
    }
}


