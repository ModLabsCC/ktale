package ktale.examples

import ktale.api.commands.CommandContext
import ktale.api.commands.CommandResult
import ktale.api.events.Event
import ktale.api.events.subscribe
import ktale.api.services.register
import ktale.core.commands.Commands
import ktale.platform.fake.FakePlayer
import ktale.platform.fake.FakeServer
import java.time.Duration

/**
 * This file exists to prove Kotlin-first ergonomics at compile time.
 *
 * It is not a runtime demo and does not depend on any test framework.
 */
object KotlinExamplePluginUsage {
    fun compileOnlyExample() {
        val server = FakeServer()
        val ctx = server.createContext("kotlin-demo")

        // Service registry (Kotlin reified helpers)
        ctx.services.register("hello", replace = true)

        // Events (Kotlin reified subscribe)
        ctx.events.subscribe<TestEvent> {
            ctx.logger.info("Received event from Kotlin")
        }

        // Commands (Kotlin DSL builder from ktale-core)
        ctx.commands.register(
            Commands.command("ping") {
                aliases("p")
                execute { _: CommandContext -> CommandResult.Success }
            }
        )

        // Scheduler (Kotlin extension helpers live in ktale-api)
        ctx.scheduler.runSyncDelayed(Duration.ofMillis(10)) {
            ctx.logger.info("tick")
        }

        // Fake inbound command execution
        val player = FakePlayer("Alice")
        server.platform.commandBridge.dispatchInbound(
            object : CommandContext {
                override val sender = player
                override val label = "ping"
                override val args = emptyList<String>()
            }
        )
    }

    class TestEvent : Event
}


