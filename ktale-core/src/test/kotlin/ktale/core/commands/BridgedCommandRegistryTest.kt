package ktale.core.commands

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ktale.api.commands.CommandContext
import ktale.api.commands.CommandDefinition
import ktale.api.commands.CommandResult
import ktale.api.commands.CommandSender
import ktale.platform.PlatformCommandBridge

class BridgedCommandRegistryTest : FunSpec({
    test("register/unregister notify PlatformCommandBridge") {
        val bridge = mockk<PlatformCommandBridge>(relaxed = true)
        val base = SimpleCommandRegistry()
        val reg = BridgedCommandRegistry(base, bridge)

        val def = object : CommandDefinition {
            override val name = "ping"
            override val aliases = emptySet<String>()
            override val description: String? = null
            override val permission = null
            override fun execute(context: CommandContext): CommandResult = CommandResult.Success
        }

        reg.register(def)
        verify { bridge.onRegister(def) }

        reg.unregister("ping")
        verify { bridge.onUnregister("ping") }
    }

    test("dispatch still delegates to underlying registry") {
        val bridge = mockk<PlatformCommandBridge>(relaxed = true)
        val base = SimpleCommandRegistry()
        val reg = BridgedCommandRegistry(base, bridge)

        val def = object : CommandDefinition {
            override val name = "ping"
            override val aliases = emptySet<String>()
            override val description: String? = null
            override val permission = null
            override fun execute(context: CommandContext): CommandResult = CommandResult.Success
        }
        reg.register(def)

        val sender = mockk<CommandSender>()
        every { sender.name } returns "Tester"
        every { sender.sendMessage(any()) } returns Unit
        every { sender.hasPermission(any()) } returns true

        val ctx = object : CommandContext {
            override val sender: CommandSender = sender
            override val label: String = "ping"
            override val args: List<String> = emptyList()
        }

        reg.dispatch(ctx) shouldBe CommandResult.Success
    }
})


