package ktale.core.events

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import ktale.api.events.Cancellable
import ktale.api.events.Event
import ktale.api.events.EventPriority

class SimpleEventBusTest : FunSpec({
    test("dispatches in priority order") {
        val bus = SimpleEventBus()
        val order = mutableListOf<String>()

        bus.subscribe(TestEvent::class.java, EventPriority.LATE, false) { order += "late" }
        bus.subscribe(TestEvent::class.java, EventPriority.EARLY, false) { order += "early" }
        bus.subscribe(TestEvent::class.java, EventPriority.NORMAL, false) { order += "normal" }
        bus.subscribe(TestEvent::class.java, EventPriority.FINAL, false) { order += "final" }

        bus.post(TestEvent())

        order shouldBe listOf("early", "normal", "late", "final")
    }

    test("ignoreCancelled skips listeners") {
        val bus = SimpleEventBus()
        val order = mutableListOf<String>()

        bus.subscribe(TestCancellableEvent::class.java, EventPriority.NORMAL, ignoreCancelled = true) {
            order += "ignored"
        }
        bus.subscribe(TestCancellableEvent::class.java, EventPriority.NORMAL, ignoreCancelled = false) {
            order += "always"
        }

        bus.post(TestCancellableEvent(isCancelled = true))

        order shouldBe listOf("always")
    }
})

private class TestEvent : Event

private data class TestCancellableEvent(override var isCancelled: Boolean) : Event, Cancellable


