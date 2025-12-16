package ktale.platform.fake

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import ktale.api.KtalePlugin
import ktale.api.PluginContext
import ktale.api.commands.CommandContext
import ktale.api.commands.CommandDefinition
import ktale.api.commands.CommandResult

class FakeServerSmokeTest : FunSpec({
    test("fake server can run plugin lifecycle and dispatch commands") {
        val server = FakeServer()

        val plugin = object : KtalePlugin {
            override fun onLoad(context: PluginContext) = Unit

            override fun onEnable(context: PluginContext) {
                context.commands.register(
                    object : CommandDefinition {
                        override val name: String = "ping"
                        override val aliases: Set<String> = emptySet()
                        override val description: String? = null
                        override val permission = null
                        override fun execute(context: CommandContext): CommandResult = CommandResult.Success
                    }
                )
            }

            override fun onDisable(context: PluginContext) = Unit
        }

        server.runPlugin("test", plugin) { ctx ->
            val sender = FakePlayer("Alice")
            val res = server.platform.commandBridge.dispatchInbound(
                object : CommandContext {
                    override val sender = sender
                    override val label = "ping"
                    override val args = emptyList<String>()
                }
            )
            res shouldBe CommandResult.Success
        }
    }
})


