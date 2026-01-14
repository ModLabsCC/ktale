package cc.modlabs.ktale.ui

import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType
import com.hypixel.hytale.server.core.ui.builder.EventData
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder

/**
 * String helpers for common Hytale UI property paths used by [UICommandBuilder.set].
 *
 * This keeps UI wiring concise and makes it easy to unit test path generation without a running server.
 */
object UiPath {
    @JvmStatic fun value(node: String): String = "$node.Value"
    @JvmStatic fun text(node: String): String = "$node.Text"
    @JvmStatic fun visible(node: String): String = "$node.Visible"
    @JvmStatic fun styleTextColor(node: String): String = "$node.Style.TextColor"

    /**
     * Common pattern: a container like `#PrefixBold` contains an inner `#CheckBox`.
     * The example code uses: `"#PrefixBold #CheckBox.Value"`.
     */
    @JvmStatic fun checkboxValue(containerNode: String): String = "$containerNode #CheckBox.Value"
}

/**
 * Key helpers for [EventData.of].
 */
object UiEventKey {
    /** `@Prefix`, `@Nickname`, ... */
    @JvmStatic fun data(name: String): String = if (name.startsWith("@")) name else "@$name"
    /** `"Action"` */
    const val ACTION: String = "Action"
}

// --- UICommandBuilder convenience ---

/** Appends a `.ui` page path (example: `"Pages/ChatCustomization_Editor.ui"`). */
fun UICommandBuilder.page(path: String): UICommandBuilder = this.append(path)

fun UICommandBuilder.value(node: String, value: String): UICommandBuilder = this.set(UiPath.value(node), value)
fun UICommandBuilder.value(node: String, value: Boolean): UICommandBuilder = this.set(UiPath.value(node), value)
fun UICommandBuilder.text(node: String, text: String): UICommandBuilder = this.set(UiPath.text(node), text)
fun UICommandBuilder.visible(node: String, visible: Boolean): UICommandBuilder = this.set(UiPath.visible(node), visible)
fun UICommandBuilder.textColor(node: String, hex: String): UICommandBuilder = this.set(UiPath.styleTextColor(node), hex)
fun UICommandBuilder.checkbox(containerNode: String, checked: Boolean): UICommandBuilder =
    this.set(UiPath.checkboxValue(containerNode), checked)

// --- UIEventBuilder convenience ---

/**
 * Binds a UI event to send a single key/value pair.
 *
 * Examples:
 * - ValueChanged: `EventData.of("@Prefix", "#PrefixField.Value")`
 * - Activating: `EventData.of("Action", "Save")`
 */
fun UIEventBuilder.bind(
    type: CustomUIEventBindingType,
    node: String,
    key: String,
    value: String,
    capture: Boolean = false,
): UIEventBuilder = this.addEventBinding(type, node, EventData.of(key, value), capture)

/** Shorthand for ValueChanged + default value path (`#Node.Value`). */
fun UIEventBuilder.onValueChanged(
    node: String,
    dataKey: String,
    valuePath: String = UiPath.value(node),
    capture: Boolean = false,
): UIEventBuilder = bind(CustomUIEventBindingType.ValueChanged, node, UiEventKey.data(dataKey), valuePath, capture)

/** Shorthand for Activating + constant action value. */
fun UIEventBuilder.onActivate(
    node: String,
    action: String,
    capture: Boolean = false,
): UIEventBuilder = bind(CustomUIEventBindingType.Activating, node, UiEventKey.ACTION, action, capture)

