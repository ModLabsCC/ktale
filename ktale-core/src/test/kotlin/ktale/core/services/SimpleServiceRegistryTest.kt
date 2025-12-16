package ktale.core.services

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.throwables.shouldThrow

class SimpleServiceRegistryTest : FunSpec({
    test("register/get/require/unregister works") {
        val reg = SimpleServiceRegistry()

        reg.get(String::class.java) shouldBe null
        shouldThrow<NoSuchElementException> { reg.require(String::class.java) }

        reg.register(String::class.java, "hello")
        reg.get(String::class.java) shouldBe "hello"
        reg.require(String::class.java) shouldBe "hello"

        reg.unregister(String::class.java)
        reg.get(String::class.java) shouldBe null
    }

    test("register without replace refuses overwrite") {
        val reg = SimpleServiceRegistry()
        reg.register(String::class.java, "a")
        shouldThrow<IllegalStateException> {
            reg.register(String::class.java, "b", replace = false)
        }
        reg.get(String::class.java) shouldNotBe "b"
    }

    test("register with replace overwrites") {
        val reg = SimpleServiceRegistry()
        reg.register(String::class.java, "a")
        reg.register(String::class.java, "b", replace = true)
        reg.get(String::class.java) shouldBe "b"
    }
})


