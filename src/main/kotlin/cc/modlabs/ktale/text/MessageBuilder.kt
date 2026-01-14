package cc.modlabs.ktale.text

import com.hypixel.hytale.server.core.Message
import java.awt.Color
import java.util.regex.Pattern

/**
 * Builder for creating Hytale [Message] objects from a MiniMessage-like format.
 *
 * Supports tags like `<red>`, `<bold>`, `<#RRGGBB>`, `<gradient:#FF0000:#0000FF>`, etc.
 *
 * Example:
 *
 * ```kotlin
 * val msg = MessageBuilder.fromMiniMessage("<red>Hello <bold>World</bold>!</red>")
 * player.sendMessage(msg)
 * ```
 */
class MessageBuilder {
    companion object {
        // Pattern to match MiniMessage tags: <tag> or <tag:value> or </tag>
        private val TAG_PATTERN: Pattern = Pattern.compile("<([/]?)([^>]+)>")

        // Named colors mapping
        private val NAMED_COLORS: Map<String, String> = mapOf(
            "black" to "#000000",
            "dark_blue" to "#0000AA",
            "dark_green" to "#00AA00",
            "dark_aqua" to "#00AAAA",
            "dark_red" to "#AA0000",
            "dark_purple" to "#AA00AA",
            "gold" to "#FFAA00",
            "gray" to "#AAAAAA",
            "grey" to "#AAAAAA",
            "dark_gray" to "#555555",
            "dark_grey" to "#555555",
            "blue" to "#5555FF",
            "green" to "#55FF55",
            "aqua" to "#55FFFF",
            "red" to "#FF5555",
            "light_purple" to "#FF55FF",
            "yellow" to "#FFFF55",
            "white" to "#FFFFFF",
            "reset" to "#FFFFFF",
        )

        private data class GradientInfo(val startColor: String, val endColor: String)
        private data class MessageNode(val message: Message, val tagName: String, val gradientInfo: GradientInfo? = null)

        /**
         * Parses a MiniMessage-like string and converts it to a Hytale [Message].
         *
         * @param miniMessage The MiniMessage formatted string
         * @return A Hytale [Message]
         */
        fun fromMiniMessage(miniMessage: String): Message {
            if (!miniMessage.contains('<')) {
                // No tags, return simple raw message
                return Message.raw(miniMessage)
            }

            val root = Message.empty()
            val stack = mutableListOf<MessageNode>()
            stack.add(MessageNode(root, "", null))

            var lastIndex = 0
            val matcher = TAG_PATTERN.matcher(miniMessage)

            while (matcher.find()) {
                val start = matcher.start()
                val end = matcher.end()
                val isClosing = matcher.group(1) == "/"
                val tagContent = matcher.group(2)
                val tagName = parseTagName(tagContent)

                // Add text before the tag
                if (start > lastIndex) {
                    val text = miniMessage.substring(lastIndex, start)
                    if (text.isNotEmpty()) {
                        val currentNode = stack.lastOrNull()
                        val gradientInfo = findGradientInfo(stack)
                        if (gradientInfo != null) {
                            applyGradientText(currentNode?.message ?: root, text, gradientInfo)
                        } else {
                            currentNode?.message?.insert(text)
                        }
                    }
                }

                if (isClosing) {
                    // Closing tag - pop from stack until we find matching tag
                    for (i in stack.size - 1 downTo 1) {
                        if (stack[i].tagName == tagName) {
                            while (stack.size > i) {
                                stack.removeAt(stack.size - 1)
                            }
                            break
                        }
                    }
                } else {
                    // Opening tag - create new message and apply formatting
                    val message = Message.empty()
                    val gradientInfo = applyTag(message, tagContent, tagName)
                    stack.lastOrNull()?.message?.insert(message)
                    stack.add(MessageNode(message, tagName, gradientInfo))
                }

                lastIndex = end
            }

            // Add remaining text
            if (lastIndex < miniMessage.length) {
                val text = miniMessage.substring(lastIndex)
                if (text.isNotEmpty()) {
                    val currentNode = stack.lastOrNull()
                    val gradientInfo = findGradientInfo(stack)
                    if (gradientInfo != null) {
                        applyGradientText(currentNode?.message ?: root, text, gradientInfo)
                    } else {
                        currentNode?.message?.insert(text)
                    }
                }
            }

            // If root has children, join them, otherwise return root
            val children = root.children
            return when {
                children.isEmpty() -> root
                children.size == 1 -> children[0]
                else -> Message.join(*children.toTypedArray())
            }
        }

        /**
         * Applies a tag to a [Message]. Returns [GradientInfo] if this is a gradient tag, otherwise null.
         */
        private fun applyTag(message: Message, tagContent: String, tagName: String): GradientInfo? {
            val parts = tagContent.split(':')
            val baseTagName = parts[0].lowercase()
            val tagValue = if (parts.size > 1) parts[1] else null

            when (baseTagName) {
                "gradient" -> {
                    if (parts.size >= 3) {
                        val startColor = parseColor(parts[1]) ?: return null
                        val endColor = parseColor(parts[2]) ?: return null
                        return GradientInfo(startColor, endColor)
                    }
                    return null
                }
                "bold", "b" -> {
                    message.bold(true)
                    return null
                }
                "italic", "i" -> {
                    message.italic(true)
                    return null
                }
                "underlined", "u" -> return null
                "strikethrough", "s" -> return null
                "obfuscated", "obf" -> return null
                "monospace" -> {
                    message.monospace(true)
                    return null
                }
                "color" -> {
                    if (tagValue != null) {
                        val color = parseColor(tagValue)
                        if (color != null) message.color(color)
                    }
                    return null
                }
                "click" -> return null
                "hover" -> return null
                "link" -> {
                    if (tagValue != null) message.link(tagValue)
                    return null
                }
                else -> {
                    val colorHex = NAMED_COLORS[baseTagName]
                    if (colorHex != null) {
                        message.color(colorHex)
                        return null
                    } else if (baseTagName.startsWith("#")) {
                        if (isValidHex(baseTagName)) message.color(baseTagName)
                        return null
                    }
                    return null
                }
            }
        }

        private fun findGradientInfo(stack: List<MessageNode>): GradientInfo? {
            for (i in stack.size - 1 downTo 0) {
                stack[i].gradientInfo?.let { return it }
            }
            return null
        }

        private fun applyGradientText(parentMessage: Message, text: String, gradientInfo: GradientInfo) {
            if (text.isEmpty()) return

            val startColor = hexToRgb(gradientInfo.startColor) ?: return
            val endColor = hexToRgb(gradientInfo.endColor) ?: return

            val chars = text.toCharArray()
            val charCount = chars.size
            if (charCount == 0) return

            for (i in chars.indices) {
                val ratio = if (charCount > 1) i.toDouble() / (charCount - 1) else 0.0
                val interpolatedColor = interpolateColor(startColor, endColor, ratio)
                val hexColor = rgbToHex(interpolatedColor)

                val charMessage = Message.raw(chars[i].toString()).color(hexColor)
                parentMessage.insert(charMessage)
            }
        }

        private fun hexToRgb(hex: String): Triple<Int, Int, Int>? {
            val cleanHex = hex.removePrefix("#")
            if (cleanHex.length != 6) return null

            return try {
                val r = cleanHex.substring(0, 2).toInt(16)
                val g = cleanHex.substring(2, 4).toInt(16)
                val b = cleanHex.substring(4, 6).toInt(16)
                Triple(r, g, b)
            } catch (_: NumberFormatException) {
                null
            }
        }

        private fun rgbToHex(rgb: Triple<Int, Int, Int>): String =
            String.format("#%02X%02X%02X", rgb.first, rgb.second, rgb.third)

        private fun interpolateColor(
            start: Triple<Int, Int, Int>,
            end: Triple<Int, Int, Int>,
            ratio: Double,
        ): Triple<Int, Int, Int> {
            val r = (start.first + (end.first - start.first) * ratio).toInt().coerceIn(0, 255)
            val g = (start.second + (end.second - start.second) * ratio).toInt().coerceIn(0, 255)
            val b = (start.third + (end.third - start.third) * ratio).toInt().coerceIn(0, 255)
            return Triple(r, g, b)
        }

        private fun parseColor(colorValue: String): String? {
            val trimmed = colorValue.trim()

            if (trimmed.startsWith("#")) {
                return if (isValidHex(trimmed)) trimmed else null
            }

            val namedColor = NAMED_COLORS[trimmed.lowercase()]
            if (namedColor != null) return namedColor

            if (trimmed.length == 6 && isValidHex("#$trimmed")) {
                return "#$trimmed"
            }

            return null
        }

        private fun isValidHex(hex: String): Boolean =
            hex.matches(Regex("#[0-9A-Fa-f]{6}"))

        private fun parseTagName(tagContent: String): String =
            tagContent.split(':')[0].lowercase()

        public fun builder(): MessageBuilder = MessageBuilder()
    }

    private var message: Message = Message.empty()

    /** Adds raw text to the message. */
    fun text(text: String): MessageBuilder {
        message.insert(text)
        return this
    }

    /** Adds a MiniMessage-like formatted text. */
    fun miniMessage(text: String): MessageBuilder {
        val parsed = fromMiniMessage(text)
        message.insert(parsed)
        return this
    }

    /** Sets the color. */
    fun color(color: String): MessageBuilder {
        message.color(color)
        return this
    }

    /** Sets the color. */
    fun color(color: Color): MessageBuilder {
        message.color(color)
        return this
    }

    /** Sets bold formatting. */
    fun bold(bold: Boolean = true): MessageBuilder {
        message.bold(bold)
        return this
    }

    /** Sets italic formatting. */
    fun italic(italic: Boolean = true): MessageBuilder {
        message.italic(italic)
        return this
    }

    /** Sets monospace formatting. */
    fun monospace(monospace: Boolean = true): MessageBuilder {
        message.monospace(monospace)
        return this
    }

    /** Sets a link. */
    fun link(url: String): MessageBuilder {
        message.link(url)
        return this
    }

    /** Inserts another message. */
    fun insert(other: Message): MessageBuilder {
        message.insert(other)
        return this
    }

    /** Builds the final [Message]. */
    fun build(): Message = message
}

