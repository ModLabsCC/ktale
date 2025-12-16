package ktale.core.events

import ktale.api.events.Cancellable
import ktale.api.events.Event
import ktale.api.events.EventBus
import ktale.api.events.EventListener
import ktale.api.events.EventPriority
import ktale.api.events.EventSubscription
import java.util.concurrent.CopyOnWriteArrayList

/**
 * A minimal, deterministic, in-memory event bus.
 *
 * ## Design note
 * - Uses type-exact subscriptions (no class hierarchy walking) to avoid surprising dispatch costs.
 * - Is synchronous: event dispatch happens on the calling thread.
 * - Thread-safe subscription/unsubscription for typical plugin usage.
 */
public class SimpleEventBus : EventBus {
    private data class RegisteredListener<E : Event>(
        val type: Class<E>,
        val priority: EventPriority,
        val ignoreCancelled: Boolean,
        val listener: EventListener<E>,
        val token: Any = Any(),
    )

    private val listeners = CopyOnWriteArrayList<RegisteredListener<out Event>>()

    override fun <E : Event> post(event: E): E {
        @Suppress("UNCHECKED_CAST")
        val typed = listeners
            .asSequence()
            .filter { it.type == event.javaClass }
            .map { it as RegisteredListener<E> }
            .sortedBy { it.priority.ordinal }
            .toList()

        val cancelled = (event as? Cancellable)?.isCancelled == true
        for (reg in typed) {
            if (reg.ignoreCancelled && cancelled) continue
            reg.listener.onEvent(event)
        }
        return event
    }

    override fun <E : Event> subscribe(
        type: Class<E>,
        listener: EventListener<E>,
        priority: EventPriority,
        ignoreCancelled: Boolean,
    ): EventSubscription {
        val reg = RegisteredListener(type, priority, ignoreCancelled, listener)
        listeners.add(reg)
        return object : EventSubscription {
            private var unsubscribed = false
            override fun unsubscribe() {
                if (unsubscribed) return
                unsubscribed = true
                listeners.removeIf { it.token == reg.token }
            }
        }
    }
}


