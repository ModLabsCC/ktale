package ktale.api.commands

/**
 * Entity that can execute commands (e.g. player, console, remote admin, script).
 *
 * KTale intentionally models this as a capability surface, not as a concrete actor hierarchy.
 */
public interface CommandSender {
    /** Display name of the sender. */
    public val name: String

    /** Sends a message to the sender. */
    public fun sendMessage(message: String)

    /**
     * Checks whether the sender has [permission].
     *
     * Platforms decide whether permissions are hierarchical, wildcard-based, etc.
     */
    public fun hasPermission(permission: Permission): Boolean
}


