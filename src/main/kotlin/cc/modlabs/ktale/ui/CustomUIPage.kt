package cc.modlabs.ktale.ui

import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

/**
 * Small convenience base class for building interactive custom UI pages.
 *
 * It wraps Hytale’s [InteractiveCustomUIPage] and provides:
 * - [update] helpers (send a small UI delta after receiving events)
 * - a [UiBuildScope] to keep `cmd` + `events` wiring concise inside `build(...)`
 *
 * This is intentionally lightweight and stays close to the underlying Hytale API.
 *
 * Naming note: Hytale itself has a `com.hypixel...pages.CustomUIPage`. This class is
 * `cc.modlabs.ktale.ui.CustomUIPage`, so it does not conflict at runtime, but if you import both
 * in the same file you may need to disambiguate.
 */
abstract class CustomUIPage<T : Any>(
    playerRef: PlayerRef,
    lifetime: CustomPageLifetime,
    codec: BuilderCodec<T>,
) : InteractiveCustomUIPage<T>(playerRef, lifetime, codec) {

    /**
     * Convenience update: send command-only update.
     *
     * Equivalent to `sendUpdate(cmd, capture)`.
     */
    protected fun update(capture: Boolean = false, block: UICommandBuilder.() -> Unit) {
        val cmd = UICommandBuilder()
        cmd.block()
        sendUpdate(cmd, capture)
    }

    /**
     * Convenience update: send command + events update.
     *
     * Equivalent to `sendUpdate(cmd, events, capture)`.
     */
    protected fun update(capture: Boolean = false, block: (UICommandBuilder, UIEventBuilder) -> Unit) {
        val cmd = UICommandBuilder()
        val events = UIEventBuilder()
        block(cmd, events)
        sendUpdate(cmd, events, capture)
    }

    /**
     * Override this instead of overriding [InteractiveCustomUIPage.handleDataEvent] directly.
     *
     * The default implementation does nothing.
     */
    protected open fun onDataEvent(
        ref: Ref<EntityStore>,
        store: Store<EntityStore>,
        data: T,
    ) = Unit

    final override fun handleDataEvent(ref: Ref<EntityStore>, store: Store<EntityStore>, data: T) {
        super.handleDataEvent(ref, store, data)
        onDataEvent(ref, store, data)
    }
}

/**
 * Helper scope for building UI pages.
 *
 * Use this from your `build(...)` implementation to keep the code readable:
 *
 * ```
 * override fun build(ref, cmd, events, store) = ui(ref, store, cmd, events) {
 *   cmd.page("Pages/MyPage.ui")
 *   cmd.value("#Foo", "bar")
 *   events.onActivate("#Save", "Save")
 * }
 * ```
 */
class UiBuildScope(
    val ref: Ref<EntityStore>,
    val store: Store<EntityStore>,
    val cmd: UICommandBuilder,
    val events: UIEventBuilder,
)

/**
 * Convenience to create a [UiBuildScope] inside `build(...)`.
 */
inline fun ui(
    ref: Ref<EntityStore>,
    store: Store<EntityStore>,
    cmd: UICommandBuilder,
    events: UIEventBuilder,
    block: UiBuildScope.() -> Unit,
) {
    UiBuildScope(ref, store, cmd, events).block()
}

