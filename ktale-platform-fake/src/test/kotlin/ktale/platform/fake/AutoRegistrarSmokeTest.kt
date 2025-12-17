package ktale.platform.fake

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import ktale.api.autoregister.AutoCommand
import ktale.api.autoregister.SubscribeEvent
import ktale.api.commands.CommandContext
import ktale.api.commands.CommandDefinition
import ktale.api.commands.CommandResult
import ktale.api.events.Event
import ktale.core.autoregister.AutoRegistrar

class AutoRegistrarSmokeTest : FunSpec({
    test("auto-registers commands (by interface+annotation) and event listeners (by annotation)") {
        val server = FakeServer()
        val ctx = server.createContext("autoreg")

        AutoRegistrar.registerAllFromClasses(
            listOf(AutoPingCommand::class.java, TestListener::class.java),
            ctx,
        )

        // Command got bridged into FakeCommandBridge.
        server.platform.commandBridge.isRegistered("ping") shouldBe true

        // Event listener is subscribed and receives events.
        server.post(TestEvent())
        TestListener.seen shouldBe 1
    }
})

private class TestEvent : Event

@AutoCommand
private class AutoPingCommand : CommandDefinition {
    override val name: String = "ping"
    override val aliases: Set<String> = emptySet()
    override val description: String? = null
    override val permission = null
    override fun execute(context: CommandContext): CommandResult = CommandResult.Success
}

private class TestListener {
    companion object {
        @JvmStatic
        var seen: Int = 0
    }

    @SubscribeEvent(TestEvent::class)
    fun onTest(e: TestEvent) {
        // Increment a static counter so we can assert that reflection invocation worked.
        seen++
    }
}


