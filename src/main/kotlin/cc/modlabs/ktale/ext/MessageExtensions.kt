package cc.modlabs.ktale.ext

import cc.modlabs.ktale.text.MessageBuilder
import com.hypixel.hytale.server.core.Message

fun String.toMiniMessage(): Message {
    return MessageBuilder.fromMiniMessage(this)
}