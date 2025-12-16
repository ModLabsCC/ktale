package ktale.platform.fake

import ktale.api.scheduler.TaskHandle
import java.util.concurrent.atomic.AtomicBoolean

internal class FakeTaskHandle : TaskHandle {
    private val cancelled = AtomicBoolean(false)

    override fun cancel() {
        cancelled.set(true)
    }

    override val isCancelled: Boolean
        get() = cancelled.get()
}


