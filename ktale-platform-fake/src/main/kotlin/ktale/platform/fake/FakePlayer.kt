package ktale.platform.fake

import ktale.api.commands.CommandSender
import ktale.api.commands.Permission

/**
 * Minimal fake player.
 *
 * This implements [CommandSender] so commands can be tested without a real server.
 */
public class FakePlayer(
    override val name: String,
    private val permissions: MutableSet<String> = mutableSetOf(),
) : CommandSender {
    public val messages: MutableList<String> = mutableListOf()

    override fun sendMessage(message: String) {
        messages += message
    }

    override fun hasPermission(permission: Permission): Boolean =
        permissions.contains(permission.value)

    public fun grant(permission: String) {
        permissions += permission
    }
}


