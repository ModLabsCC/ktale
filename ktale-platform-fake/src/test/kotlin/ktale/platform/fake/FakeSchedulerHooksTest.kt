package ktale.platform.fake

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Duration

class FakeSchedulerHooksTest : FunSpec({
    test("delayed sync task runs only after time advance") {
        val platform = FakePlatform()
        val scheduler = platform.schedulerHooks

        var ran = 0
        scheduler.runSyncDelayed(Duration.ofMillis(50), Runnable { ran++ })

        scheduler.runDueSync()
        ran shouldBe 0

        scheduler.advanceBy(Duration.ofMillis(49), runDueAfterAdvance = true)
        ran shouldBe 0

        scheduler.advanceBy(Duration.ofMillis(1), runDueAfterAdvance = true)
        ran shouldBe 1
    }

    test("repeating task repeats at interval and can be cancelled") {
        val platform = FakePlatform()
        val scheduler = platform.schedulerHooks

        var ran = 0
        val handle = scheduler.runSyncRepeating(
            initialDelay = Duration.ZERO,
            interval = Duration.ofMillis(10),
            task = Runnable { ran++ },
        )

        scheduler.runDueSync()
        ran shouldBe 1

        scheduler.advanceBy(Duration.ofMillis(10), runDueAfterAdvance = true)
        ran shouldBe 2

        handle.cancel()
        scheduler.advanceBy(Duration.ofMillis(100), runDueAfterAdvance = true)
        ran shouldBe 2
    }
})


