package cc.modlabs.ktale.util

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Thread-safe cooldown manager.
 *
 * Tracks per-key cooldowns using monotonic time ([System.nanoTime]).
 * Keys can be anything – UUIDs, composite strings, custom data classes, etc.
 *
 * ```kotlin
 * val abilityCooldowns = Cooldowns<UUID>()
 *
 * if (abilityCooldowns.isReady(player.uuid!!)) {
 *     // fire ability
 *     abilityCooldowns.set(player.uuid!!, 3000L) // 3 seconds
 * }
 * ```
 *
 * For simple player-keyed cooldowns, use the pre-built [playerCooldowns] instance or
 * the `Player.hasCooldown` / `Player.setCooldown` extensions.
 */
class Cooldowns<K : Any> {

    private val expiry = ConcurrentHashMap<K, Long>()

    /** Sets a cooldown for [key] that expires after [durationMs] milliseconds. */
    fun set(key: K, durationMs: Long) {
        expiry[key] = System.nanoTime() + durationMs * 1_000_000L
    }

    /** Returns `true` if [key] still has an active (non-expired) cooldown. */
    fun isOnCooldown(key: K): Boolean {
        val exp = expiry[key] ?: return false
        if (System.nanoTime() >= exp) {
            expiry.remove(key)
            return false
        }
        return true
    }

    /** Returns `true` if [key] has no cooldown or it has expired. Inverse of [isOnCooldown]. */
    fun isReady(key: K): Boolean = !isOnCooldown(key)

    /** Returns the remaining cooldown in milliseconds, or 0 if ready. */
    fun remainingMs(key: K): Long {
        val exp = expiry[key] ?: return 0L
        val remaining = (exp - System.nanoTime()) / 1_000_000L
        if (remaining <= 0L) {
            expiry.remove(key)
            return 0L
        }
        return remaining
    }

    /** Clears the cooldown for [key]. */
    fun clear(key: K) {
        expiry.remove(key)
    }

    /** Clears all tracked cooldowns. */
    fun clearAll() {
        expiry.clear()
    }

    /** Removes expired entries. Call periodically if the key space is large. */
    fun purgeExpired() {
        val now = System.nanoTime()
        expiry.entries.removeIf { it.value <= now }
    }
}

/**
 * Composite key for cooldowns scoped by both player and category.
 *
 * ```kotlin
 * val cd = Cooldowns<ScopedKey>()
 * cd.set(ScopedKey(player.uuid!!, "fireball"), 5000L)
 * ```
 */
data class ScopedKey(val uuid: UUID, val scope: String)
