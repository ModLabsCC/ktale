package cc.modlabs.ktale.ext

import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.permissions.PermissionsModule

/**
 * Adds a specific permission to the player's permissions.
 *
 * @param permission The permission string to be added to the player.
 */
fun Player.addPermission(permission: String) {
    val permSet = emptySet<String>().toMutableSet()
    permSet += permission
    val permModule = PermissionsModule.get()
    permModule.addUserPermission(this.uuid!!, permSet)
}

/**
 * Adds the specified set of permissions to the player.
 *
 * @param permissions A set of permission strings to be added to the player.
 */
fun Player.addPermissions(permissions: Set<String>) {
    val permModule = PermissionsModule.get()
    permModule.addUserPermission(this.uuid!!, permissions)
}

/**
 * Removes a specific permission from the player.
 *
 * @param permission The permission string to be removed from the player.
 */
fun Player.removePermission(permission: String) {
    val permSet = emptySet<String>().toMutableSet()
    permSet += permission
    val permModule = PermissionsModule.get()
    permModule.removeUserPermission(this.uuid!!, permSet)
}

/**
 * Removes a set of permissions from the player.
 *
 * @param permissions A set of permission strings to be removed from the player.
 */
fun Player.removePermissions(permissions: Set<String>) {
    val permModule = PermissionsModule.get()
    permModule.removeUserPermission(this.uuid!!, permissions)
}

/**
 * Adds the player to a specified permission group.
 *
 * @param groupName The name of the permission group to add the player to.
 */
fun Player.addToPermGroup(groupName: String) {
    val permModule = PermissionsModule.get()
    permModule.addUserToGroup(this.uuid!!, groupName)
}

/**
 * Removes the player from the specified permission group.
 *
 * @param groupName The name of the permission group from which the player will be removed.
 */
fun Player.removeFromPermGroup(groupName: String) {
    val permModule = PermissionsModule.get()
    permModule.removeUserFromGroup(this.uuid!!, groupName)
}

/**
 * Retrieves all the permission groups associated with the player.
 *
 * @return A set of strings representing the names of the permission groups that the player belongs to.
 */
fun Player.listAllPermGroups(): Set<String> {
    val permModule = PermissionsModule.get()
    return permModule.getGroupsForUser(this.uuid!!)
}