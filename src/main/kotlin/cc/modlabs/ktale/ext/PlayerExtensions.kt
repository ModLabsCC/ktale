package cc.modlabs.ktale.ext

import cc.modlabs.ktale.text.MessageBuilder
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.entity.entities.Player

/**
 * Sends a pre-built [Message] to this player.
 */
public fun Player.send(message: Message) {
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
public fun Player.send(text: String) {
    this.send(MessageBuilder.fromMiniMessage(text))
}

/** Sends raw text to this player (no tag parsing). */
public fun Player.sendRaw(text: String) {
    this.send(Message.raw(text))
}

/** Explicit MiniMessage-like send (same as [send]). */
public fun Player.sendMini(text: String) {
    this.send(MessageBuilder.fromMiniMessage(text))
}

