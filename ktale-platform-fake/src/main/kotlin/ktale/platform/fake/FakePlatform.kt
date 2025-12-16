package ktale.platform.fake

import ktale.api.logging.KtaleLogger
import ktale.core.logging.SimpleConsoleLogger
import ktale.platform.Platform
import ktale.platform.PlatformClock
import ktale.platform.PlatformCommandBridge
import ktale.platform.PlatformLoggerFactory
import ktale.platform.PlatformSchedulerHooks

/**
 * Fake platform implementation for tests and demos.
 *
 * ## Design note
 * This platform is intentionally *fully controllable* and does not mimic any real server behavior.
 * The goal is to let KTale core behavior be tested without a real server runtime.
 */
public class FakePlatform(
    override val clock: DeterministicClock = DeterministicClock(),
    public val schedulerHooks: FakeSchedulerHooks = FakeSchedulerHooks(clock),
    public val commandBridge: FakeCommandBridge = FakeCommandBridge(),
) : Platform {
    override val platformId: String = "fake"

    override val loggers: PlatformLoggerFactory = object : PlatformLoggerFactory {
        override fun logger(name: String): KtaleLogger = SimpleConsoleLogger(name, nowEpochMillis = clock::nowEpochMillis)
    }

    override val scheduler: PlatformSchedulerHooks
        get() = schedulerHooks

    override val commands: PlatformCommandBridge
        get() = commandBridge
}


