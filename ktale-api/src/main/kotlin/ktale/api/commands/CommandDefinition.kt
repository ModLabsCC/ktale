package ktale.api.commands

/**
 * Declarative command definition.
 *
 * ## Design note
 * This is a *pure definition type* (no IO).
 * A DSL-friendly builder is expected to live in `ktale-core`, but this contract is shaped
 * so that builders can target it without depending on any platform details.
 */
public interface CommandDefinition {
    /** Primary name for the command (as registered). */
    public val name: String

    /** Alternative names that map to this definition. */
    public val aliases: Set<String>

    /** Human-readable description. */
    public val description: String?

    /** Permission required to execute this command, if any. */
    public val permission: Permission?

    /** Executes the command. */
    public fun execute(context: CommandContext): CommandResult
}


