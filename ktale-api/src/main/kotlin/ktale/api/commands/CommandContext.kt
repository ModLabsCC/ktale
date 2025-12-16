package ktale.api.commands

/**
 * Context for a command execution.
 *
 * ## Design note
 * Parsing is intentionally not part of this contract.
 * A platform adapter may provide only tokenized args, or pre-parsed structured args.
 */
public interface CommandContext {
    /** Sender of the command. */
    public val sender: CommandSender

    /** The label used to invoke the command (may be an alias). */
    public val label: String

    /** Tokenized arguments after the label (no quoting guarantees). */
    public val args: List<String>
}


