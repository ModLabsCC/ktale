package cc.modlabs.ktale.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.longs.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import java.util.UUID

class CooldownsTest : FunSpec({

    test("new cooldown is not on cooldown") {
        val cd = Cooldowns<String>()
        cd.isOnCooldown("test") shouldBe false
        cd.isReady("test") shouldBe true
        cd.remainingMs("test") shouldBe 0L
    }

    test("set cooldown reports as active") {
        val cd = Cooldowns<String>()
        cd.set("ability", 5000L)
        cd.isOnCooldown("ability") shouldBe true
        cd.isReady("ability") shouldBe false
        cd.remainingMs("ability") shouldBeGreaterThan 0L
    }

    test("expired cooldown reports as ready") {
        val cd = Cooldowns<String>()
        cd.set("short", 1L)
        Thread.sleep(5)
        cd.isOnCooldown("short") shouldBe false
        cd.isReady("short") shouldBe true
        cd.remainingMs("short") shouldBe 0L
    }

    test("clear removes cooldown") {
        val cd = Cooldowns<String>()
        cd.set("x", 60_000L)
        cd.isOnCooldown("x") shouldBe true
        cd.clear("x")
        cd.isOnCooldown("x") shouldBe false
    }

    test("clearAll removes all cooldowns") {
        val cd = Cooldowns<String>()
        cd.set("a", 60_000L)
        cd.set("b", 60_000L)
        cd.clearAll()
        cd.isOnCooldown("a") shouldBe false
        cd.isOnCooldown("b") shouldBe false
    }

    test("purgeExpired cleans up") {
        val cd = Cooldowns<String>()
        cd.set("expired", 1L)
        cd.set("active", 60_000L)
        Thread.sleep(5)
        cd.purgeExpired()
        cd.isOnCooldown("expired") shouldBe false
        cd.isOnCooldown("active") shouldBe true
    }

    test("remaining decreases over time") {
        val cd = Cooldowns<String>()
        cd.set("timer", 200L)
        val before = cd.remainingMs("timer")
        Thread.sleep(50)
        val after = cd.remainingMs("timer")
        before shouldBeGreaterThan after
    }

    test("ScopedKey differentiates by uuid and scope") {
        val cd = Cooldowns<ScopedKey>()
        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()
        cd.set(ScopedKey(uuid1, "fireball"), 60_000L)
        cd.isOnCooldown(ScopedKey(uuid1, "fireball")) shouldBe true
        cd.isOnCooldown(ScopedKey(uuid1, "heal")) shouldBe false
        cd.isOnCooldown(ScopedKey(uuid2, "fireball")) shouldBe false
    }

    test("remainingMs does not exceed duration") {
        val cd = Cooldowns<String>()
        cd.set("bounded", 100L)
        cd.remainingMs("bounded") shouldBeLessThanOrEqual 100L
    }
})
