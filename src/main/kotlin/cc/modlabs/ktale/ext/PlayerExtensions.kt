package cc.modlabs.ktale.ext

import cc.modlabs.ktale.text.MessageBuilder
import com.hypixel.hytale.math.vector.Transform
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.inventory.ItemStack
import com.hypixel.hytale.server.core.io.PacketHandler
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.util.EventTitleUtil
import com.hypixel.hytale.server.core.util.NotificationUtil

/**
 * Sends a pre-built [Message] to this player.
 */
fun Player.send(message: Message) {
    this.sendMessage(message)
}

/**
 * Sends a MiniMessage-like formatted string (via [MessageBuilder]) to this player.
 *
 * Example:
 *
 * ```kotlin
 * player.send("<red>Hello <bold>World</bold></red>")
 * ```
 */
fun Player.send(text: String) {
    this.send(MessageBuilder.fromMiniMessage(text))
}

/** Sends raw text to this player (no tag parsing). */
fun Player.sendRaw(text: String) {
    this.send(Message.raw(text))
}

/** Explicit MiniMessage-like send (same as [send]). */
fun Player.sendMini(text: String) {
    this.send(MessageBuilder.fromMiniMessage(text))
}

/**
 * Displays a title to the player with a primary and secondary message, and an optional major title.
 *
 * @param primary The primary message to display as the main title.
 * @param secondary The secondary message to display as the subtitle.
 * @param major Indicates if the title is a major event title. Defaults to false.
 */
fun Player.showTitle(primary: String, secondary: String, major: Boolean = false) {
    this.world?.execute {
        EventTitleUtil.showEventTitleToPlayer(
            this.playerRef,
            primary.toMiniMessage(),
            secondary.toMiniMessage(),
            major
        )
    }
}

/**
 * Clears the player's inventory.
 *
 * This is equivalent to calling `player.inventory.clear()` but avoids the overhead of creating a new inventory.
 */
fun Player.clearInventory() {
    this.inventory.clear()
}

/**
 * Teleports the player to the specified target player.
 *
 * @param targetPlayer The player to which the current player will be teleported.
 */
fun Player.teleport(targetPlayer: Player) {
    val world = this.world ?: return
    world.execute {
        val ref = this.reference ?: return@execute
        val store = ref.store
        val targetPlayerTransform = targetPlayer.playerRef.transform
        val teleportComponent = Teleport(targetPlayer.world, targetPlayerTransform)

        store.addComponent(ref, Teleport.getComponentType(), teleportComponent)
    }
}

/**
 * Teleports the player to a target world using the specified transformation.
 *
 * @param targetWorld The target world to which the player will be teleported.
 * @param transform The transformation specifying the location and orientation of the player in the target world.
 */
fun Player.teleport(targetWorld: World, transform: Transform) {
    world?.execute {
        val ref = this.reference ?: return@execute
        val store = ref.store
        val teleportComponent = Teleport(transform)

        store.addComponent(ref, Teleport.getComponentType(), teleportComponent)
    }
}

/**
 * Retrieves the packet handler associated with the player.
 *
 * @return The `PacketHandler` instance linked to the player's reference.
 */
fun Player.getPacketHandler(): PacketHandler {
    return this.playerRef.packetHandler
}

/**
 * Sends a notification to the player with a primary message, secondary message, and an optional icon.
 *
 * @param primary The primary message to display in the notification.
 * @param secondary The secondary message to display in the notification.
 * @param iconName The name of the icon to display alongside the notification. Defaults to "Weapon_Sword_Mithril".
 */
fun Player.sendNotification(primary: String, secondary: String, iconName: String = "Weapon_Sword_Mithril") {
    this.playerRef.sendNotification(primary, secondary, iconName)
}

/**
 * Sends a notification to the player with a custom icon and message components.
 *
 * @param primary The primary message to be displayed in the notification.
 * @param secondary The secondary message to be displayed in the notification.
 * @param iconName The name of the item icon to be displayed with the notification. Defaults to "Weapon_Sword_Mithril".
 */
fun PlayerRef.sendNotification(primary: String, secondary: String, iconName: String = "Weapon_Sword_Mithril") {
    val packetHandler = getPacketHandler()
    val icon = ItemStack(iconName, 1).toPacket()

    NotificationUtil.sendNotification(
        packetHandler,
        primary.toMiniMessage(),
        secondary.toMiniMessage(),
        icon
    )
}

/**
 * Disconnects the player from the server with the specified reason.
 *
 * @param reason The reason for kicking the player, displayed upon disconnection.
 */
fun Player.kick(reason: String) {
    this.playerRef.packetHandler.disconnect(reason)
}

/**
 * Displays a title with an accompanying icon to the player.
 *
 * The method executes on the player's associated world and shows the specified primary and secondary
 * titles, along with the given icon and animation settings.
 *
 * @param primary The primary title text to be displayed to the player.
 * @param secondary The secondary title text to be displayed beneath the primary title.
 * @param major A flag indicating whether the title should be displayed as a major event (default is false).
 * @param iconPath The file path for the icon to be displayed alongside the title.
 * @param duration The total duration (in seconds) for which the title will remain on the screen.
 * @param fadeInDuration The duration (in seconds) of the fade-in animation for the title and icon.
 * @param fadeOutDuration The duration (in seconds) of the fade-out animation for the title and icon.
 */
fun Player.showTitleWithIcon(primary: String, secondary: String, major: Boolean = false, iconPath: String, duration: Float, fadeInDuration: Float, fadeOutDuration: Float) {
    this.world?.execute {
        EventTitleUtil.showEventTitleToPlayer(
            this.playerRef,
            primary.toMiniMessage(),
            secondary.toMiniMessage(),
            major,
            iconPath,
            duration,
            fadeInDuration,
            fadeOutDuration)
    }
}