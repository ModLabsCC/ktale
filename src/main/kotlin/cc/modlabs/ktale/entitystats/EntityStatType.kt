package cc.modlabs.ktale.entitystats

import java.util.Locale

/**
 * A small set of commonly used entity stat identifiers as a typed enum.
 *
 * This is intentionally *heuristic*: it maps enum names to ids via a stable convention:
 * `AMMO` -> `Ammo`, `STAMINA_REGEN_DELAY` -> `StaminaRegenDelay`.
 *
 * If you need custom stats, keep using string ids directly.
 */
enum class EntityStatType {
    AMMO,
    DEPLOYABLE_PREVIEW,
    GLIDING_ACTIVE,
    HEALTH,
    IMMUNITY,
    MAGIC_CHARGES,
    MANA,
    OXYGEN,
    SIGNATURE_CHARGES,
    SIGNATURE_ENERGY,
    STAMINA,
    STAMINA_REGEN_DELAY;

    /** Returns the Hytale stat id for this enum value. */
    fun id(): String {
        val parts = name.lowercase(Locale.ROOT).split("_")
        val result = StringBuilder()
        for (part in parts) {
            if (part.isEmpty()) continue
            result.append(part[0].uppercaseChar()).append(part.substring(1))
        }
        return result.toString()
    }

    companion object {
        private val BY_ID: Map<String, EntityStatType> =
            entries.associateBy { it.id() }

        /** Reverse lookup by Hytale stat id (e.g. `"Health"`). */
        @JvmStatic
        fun fromId(id: String): EntityStatType? = BY_ID[id]
    }
}

