package ktale.core.commands

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import ktale.api.commands.CommandContext
import ktale.api.commands.CommandDefinition
import ktale.api.commands.CommandResult
import ktale.api.commands.CommandSender
import ktale.api.commands.Permission

class SimpleCommandRegistryTest : FunSpec({
    test("dispatch resolves aliases and checks permissions") {
        val registry = SimpleCommandRegistry()

        val sender = mockk<CommandSender>()
        every { sender.name } returns "Tester"
        every { sender.sendMessage(any()) } returns Unit
        every { sender.hasPermission(any()) } returns false

        val ctx = object : CommandContext {
            override val sender: CommandSender = sender
            override val label: String = "KT"
            override val args: List<String> = emptyList()
        }

        val perm = Permission.of("ktale.test")
        registry.register(
            object : CommandDefinition {
                override val name: String = "kt"
                override val aliases: Set<String> = setOf("KT", "kT2")
                override val description: String? = null
                override val permission: Permission? = perm
                override fun execute(context: CommandContext): CommandResult = CommandResult.Success
            }
        )

        registry.dispatch(ctx) shouldBe CommandResult.NoPermission

        every { sender.hasPermission(perm) } returns true
        registry.dispatch(ctx) shouldBe CommandResult.Success
    }

    test("unknown command returns usage error") {
        val registry = SimpleCommandRegistry()
        val sender = mockk<CommandSender>()
        every { sender.name } returns "Tester"
        every { sender.sendMessage(any()) } returns Unit
        every { sender.hasPermission(any()) } returns true

        val ctx = object : CommandContext {
            override val sender: CommandSender = sender
            override val label: String = "doesnotexist"
            override val args: List<String> = emptyList()
        }

        val res = registry.dispatch(ctx)
        (res is CommandResult.UsageError) shouldBe true
    }
})


