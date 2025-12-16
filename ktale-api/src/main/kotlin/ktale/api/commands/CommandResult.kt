package ktale.api.commands

/**
 * Result of executing a command.
 *
 * This is intentionally lightweight and does not prescribe a UX.
 * Platforms may map these results to their own messaging conventions.
 */
public sealed interface CommandResult {
    /** Indicates success. */
    public data object Success : CommandResult

    /**
     * Indicates failure with a message suitable for end users.
     *
     * Platforms may choose to hide or transform messages.
     */
    public interface Failure : CommandResult {
        public val message: String
    }

    /** Sender lacks required permission. */
    public data object NoPermission : Failure {
        override val message: String = "You do not have permission to use that command."
    }

    /** Provided arguments are invalid for the chosen command route. */
    public interface UsageError : Failure
}


