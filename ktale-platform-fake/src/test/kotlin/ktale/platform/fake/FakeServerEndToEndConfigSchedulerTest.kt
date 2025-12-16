package ktale.platform.fake

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import ktale.api.KtalePlugin
import ktale.api.PluginContext
import ktale.api.config.ConfigCodec
import ktale.api.config.ConfigKey
import ktale.api.config.ConfigMigration
import java.time.Duration

class FakeServerEndToEndConfigSchedulerTest : FunSpec({
    test("plugin can load config and schedule deterministic work via FakeServer") {
        val server = FakeServer()

        val key = object : ConfigKey<TestCfg> {
            override val id: String = "demo.yml"
            override val version: Int = 1
            override val codec: ConfigCodec<TestCfg> = object : ConfigCodec<TestCfg> {
                override fun decode(text: String): TestCfg {
                    // Extremely small, format-agnostic "codec" for tests:
                    // expects a single line: message: <value>
                    val line = text.lines().firstOrNull().orEmpty()
                    val prefix = "message:"
                    val v = line.substringAfter(prefix, "").trim()
                    return TestCfg(message = if (v.isBlank()) "hello" else v)
                }

                override fun encode(value: TestCfg): String = "message: ${value.message}"
            }
            override fun defaultValue(): TestCfg = TestCfg(message = "hello")
            override val migrations: List<ConfigMigration> = emptyList()
        }

        val plugin = object : KtalePlugin {
            override fun onLoad(context: PluginContext) = Unit

            override fun onEnable(context: PluginContext) {
                val cfg = context.configs.load(key)
                var ran = 0
                context.scheduler.runSyncDelayed(Duration.ofMillis(10), Runnable {
                    ran++
                    context.services.register(Int::class.java, ran, replace = true)
                    context.logger.info("Ran scheduled task: ${cfg.message}")
                })

                // deterministically run the scheduled task
                server.platform.schedulerHooks.advanceBy(Duration.ofMillis(10), runDueAfterAdvance = true)
                context.services.require(Int::class.java) shouldBe 1
            }

            override fun onDisable(context: PluginContext) = Unit
        }

        server.runPlugin("demo", plugin)
    }
})

private data class TestCfg(val message: String)


