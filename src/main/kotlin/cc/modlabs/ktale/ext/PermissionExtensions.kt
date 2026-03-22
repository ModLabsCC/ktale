package cc.modlabs.ktale.ext

import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.permissions.PermissionsModule

/**
 * Adds a specific permission to the player's permissions.
 *
 * @param permission The permission string to be added to the player.
 */
fun Player.addPermission(permission: String) {
    PermissionsModule.get().addUserPermission(this.uuid!!, setOf(permission))
}

/**
 * Adds the specified set of permissions to the player.
 *
 * @param permissions A set of permission strings to be added to the player.
 */
fun Player.addPermissions(permissions: Set<String>) {
    PermissionsModule.get().addUserPermission(this.uuid!!, permissions)
}

/**
 * Removes a specific permission from the player.
 *
 * @param permission The permission string to be removed from the player.
 */
fun Player.removePermission(permission: String) {
    PermissionsModule.get().removeUserPermission(this.uuid!!, setOf(permission))
}

/**
 * Removes a set of permissions from the player.
 *
 * @param permissions A set of permission strings to be removed from the player.
 */
fun Player.removePermissions(permissions: Set<String>) {
    PermissionsModule.get().removeUserPermission(this.uuid!!, permissions)
}

/**
 * Adds the player to a specified permission group.
 *
 * @param groupName The name of the permission group to add the player to.
 */
fun Player.addToPermGroup(groupName: String) {
    PermissionsModule.get().addUserToGroup(this.uuid!!, groupName)
}

/**
 * Removes the player from the specified permission group.
 *
 * @param groupName The name of the permission group from which the player will be removed.
 */
fun Player.removeFromPermGroup(groupName: String) {
    PermissionsModule.get().removeUserFromGroup(this.uuid!!, groupName)
}

/**
 * Retrieves all the permission groups associated with the player.
 *
 * @return A set of strings representing the names of the permission groups that the player belongs to.
 */
fun Player.listAllPermGroups(): Set<String> {
    return PermissionsModule.get().getGroupsForUser(this.uuid!!)
}