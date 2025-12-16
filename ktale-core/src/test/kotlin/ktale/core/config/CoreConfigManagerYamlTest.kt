package ktale.core.config

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import ktale.api.config.ConfigKey
import ktale.api.config.ConfigMigration
import ktale.core.config.yaml.YamlConfigCodec
import ktale.core.logging.SimpleConsoleLogger
import kotlinx.serialization.Serializable

class CoreConfigManagerYamlTest : FunSpec({
    test("missing config loads defaults and persists with header") {
        val store = InMemoryConfigTextStore()
        val logger = SimpleConsoleLogger("test")
        val mgr = CoreConfigManager(store, logger)

        val key = object : ConfigKey<TestCfg> {
            override val id = "test.yml"
            override val version = 1
            override val codec = YamlConfigCodec(TestCfg.serializer())
            override fun defaultValue(): TestCfg = TestCfg(foo = "bar")
            override val migrations: List<ConfigMigration> = emptyList()
        }

        mgr.load(key) shouldBe TestCfg(foo = "bar")
        val raw = store.read("test.yml")!!
        raw.lines().first().trim() shouldBe "ktaleConfigVersion: 1"
    }

    test("migrations run on text before decode") {
        val store = InMemoryConfigTextStore()
        val logger = SimpleConsoleLogger("test")
        val mgr = CoreConfigManager(store, logger)

        // Stored v0: foo: old
        store.write(
            "test.yml",
            """
            ktaleConfigVersion: 0
            foo: old
            """.trimIndent()
        )

        val key = object : ConfigKey<TestCfg> {
            override val id = "test.yml"
            override val version = 1
            override val codec = YamlConfigCodec(TestCfg.serializer())
            override fun defaultValue(): TestCfg = TestCfg(foo = "default")
            override val migrations: List<ConfigMigration> = listOf(
                object : ConfigMigration {
                    override val fromVersion: Int = 0
                    override val toVersion: Int = 1
                    override fun migrate(oldText: String): String = oldText.replace("foo: old", "foo: migrated")
                }
            )
        }

        mgr.load(key) shouldBe TestCfg(foo = "migrated")
        store.read("test.yml")!!.lines().first().trim() shouldBe "ktaleConfigVersion: 1"
    }

    test("decode failure falls back to defaults and overwrites stored value") {
        val store = InMemoryConfigTextStore()
        val logger = SimpleConsoleLogger("test")
        val mgr = CoreConfigManager(store, logger)

        store.write(
            "test.yml",
            """
            ktaleConfigVersion: 1
            this is not yaml:
            """.trimIndent()
        )

        val key = object : ConfigKey<TestCfg> {
            override val id = "test.yml"
            override val version = 1
            override val codec = YamlConfigCodec(TestCfg.serializer())
            override fun defaultValue(): TestCfg = TestCfg(foo = "safe")
            override val migrations: List<ConfigMigration> = emptyList()
        }

        mgr.load(key) shouldBe TestCfg(foo = "safe")
        val rewritten = store.read("test.yml")!!
        key.codec.decode(rewritten.lines().drop(1).joinToString("\n")) shouldBe TestCfg(foo = "safe")
    }
})

@Serializable
private data class TestCfg(val foo: String)


