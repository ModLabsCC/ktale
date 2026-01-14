package cc.modlabs.ktale.entitystats

import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue as HytaleEntityStatValue
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType as HytaleEntityStatType
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import javax.annotation.Nullable

/**
 * Entity stat helper utilities for Hytale plugins.
 *
 * Kotlin-first usage is via extensions:
 * - [Player.entityStatMapOrNull]
 * - [EntityStatMap.metaSnapshot]
 * - [EntityStatMap.getStatValue]
 *
 * Java can use the static methods on [EntityStats].
 */
object EntityStats {
    /**
     * Returns the [EntityStatMap] for this player, or `null` if the player is not in a world yet or the component is missing.
     *
     * Java: `EntityStats.getPlayerEntityStatMap(player)`
     */
    @JvmStatic
    @Nullable
    fun getPlayerEntityStatMap(player: Player): EntityStatMap? = player.entityStatMapOrNull()

    /** Returns a list of all stat ids contained in [statMap]. */
    @JvmStatic
    fun getListOfEntityStatIds(statMap: EntityStatMap): List<String> =
        statMap.metaSnapshot().byId.keys.toList()

    /**
     * Builds a map of known stat enum -> stat metadata (id/index/min/max).
     *
     * Any ids that are not part of [cc.modlabs.ktale.entitystats.EntityStatType] are ignored.
     */
    @JvmStatic
    fun getKnownEntityStats(statMap: EntityStatMap): Map<EntityStatType, EntityStatValue> {
        val out = LinkedHashMap<EntityStatType, EntityStatValue>()
        val snap = statMap.metaSnapshot()
        for ((id, meta) in snap.byId) {
            val t = EntityStatType.fromId(id) ?: continue
            val v = EntityStatValue(id)
            v.index = meta.index
            v.min = meta.min
            v.max = meta.max
            out[t] = v
        }
        return out
    }

    /** Returns the index of the stat with [id], or -1 if not present. */
    @JvmStatic
    fun getEntityStatIndex(statMap: EntityStatMap, id: String): Int =
        statMap.metaSnapshot().byId[id]?.index ?: -1

    /** Returns the index of the stat with [type], or -1 if not present. */
    @JvmStatic
    fun getEntityStatIndex(statMap: EntityStatMap, type: HytaleEntityStatType): Int =
        getEntityStatIndex(statMap, type.id)

    /** Returns the underlying [EntityStatValue] for [id], or null if not present. */
    @JvmStatic
    @Nullable
    fun getEntityStatValue(statMap: EntityStatMap, id: String): HytaleEntityStatValue? {
        val index = getEntityStatIndex(statMap, id)
        return if (index >= 0) statMap.get(index) else null
    }

    /** Returns the underlying [EntityStatValue] for [type], or null if not present. */
    @JvmStatic
    @Nullable
    fun getEntityStatValue(statMap: EntityStatMap, type: HytaleEntityStatType): HytaleEntityStatValue? =
        getEntityStatValue(statMap, type.id)

    /** Reads current stat value for [id], or -1f if not present. */
    @JvmStatic
    fun getStatValue(statMap: EntityStatMap, id: String): Float =
        getEntityStatValue(statMap, id)?.get() ?: -1f

    /** Reads current stat value for [type], or -1f if not present. */
    @JvmStatic
    fun getStatValue(statMap: EntityStatMap, type: HytaleEntityStatType): Float =
        getStatValue(statMap, type.id)

    /** Reads min stat value for [id], or -1f if not present. */
    @JvmStatic
    fun getMinStatValue(statMap: EntityStatMap, id: String): Float =
        getEntityStatValue(statMap, id)?.min ?: -1f

    /** Reads max stat value for [id], or -1f if not present. */
    @JvmStatic
    fun getMaxStatValue(statMap: EntityStatMap, id: String): Float =
        getEntityStatValue(statMap, id)?.max ?: -1f

    /** Writes current stat value for [id] if present; no-op if missing. */
    @JvmStatic
    fun setStatValue(statMap: EntityStatMap, id: String, value: Float) {
        val index = getEntityStatIndex(statMap, id)
        if (index >= 0) statMap.setStatValue(index, value)
    }

    /** Writes current stat value for [type] if present; no-op if missing. */
    @JvmStatic
    fun setStatValue(statMap: EntityStatMap, type: HytaleEntityStatType, value: Float) {
        setStatValue(statMap, type.id, value)
    }

    /** Sets current value to min for [id] if present; no-op if missing. */
    @JvmStatic
    fun setMinStatValue(statMap: EntityStatMap, id: String) {
        val v = getMinStatValue(statMap, id)
        if (v >= 0f) setStatValue(statMap, id, v)
    }

    /** Sets current value to max for [id] if present; no-op if missing. */
    @JvmStatic
    fun setMaxStatValue(statMap: EntityStatMap, id: String) {
        val v = getMaxStatValue(statMap, id)
        if (v >= 0f) setStatValue(statMap, id, v)
    }

    /** Sets current value to max+1 for [id] if present; no-op if missing. */
    @JvmStatic
    fun setMaxStatValuePlusOne(statMap: EntityStatMap, id: String) {
        val v = getMaxStatValue(statMap, id)
        if (v >= 0f) setStatValue(statMap, id, v + 1f)
    }
}

/**
 * Snapshot of the ids/indices/min/max contained in an [EntityStatMap].
 *
 * This mirrors the “generateEntityStatData” approach but avoids global mutable state.
 */
data class EntityStatMetaSnapshot(
    /** id -> metadata */
    val byId: Map<String, EntityStatMeta>,
)

/** Single stat metadata entry. */
data class EntityStatMeta(
    val id: String,
    val index: Int,
    val min: Float,
    val max: Float,
)

/**
 * Gets the [EntityStatMap] from a [Player], or `null` if world/reference/components are unavailable.
 */
@Nullable
fun Player.entityStatMapOrNull(): EntityStatMap? {
    val world: World = this.world ?: return null
    val playerRef: Ref<EntityStore> = this.reference ?: return null
    return world.entityStore.store.getComponent(playerRef, EntityStatMap.getComponentType())
}

/**
 * Builds a metadata snapshot (id/index/min/max) from this stat map.
 */
fun EntityStatMap.metaSnapshot(): EntityStatMetaSnapshot {
    val map = LinkedHashMap<String, EntityStatMeta>(this.size())
    for (i in 0 until this.size()) {
        val v = this.get(i) ?: continue
        val id = v.id
        map[id] = EntityStatMeta(id = id, index = i, min = v.min, max = v.max)
    }
    return EntityStatMetaSnapshot(byId = map)
}

/**
 * Kotlin convenience: reads current stat value by id (or -1f if missing).
 */
fun EntityStatMap.getStatValue(id: String): Float = EntityStats.getStatValue(this, id)

/**
 * Kotlin convenience: writes current stat value by id (no-op if missing).
 */
fun EntityStatMap.setStatValue(id: String, value: Float) = EntityStats.setStatValue(this, id, value)

/**
 * Kotlin convenience: builds a map of known stat enum -> metadata value.
 */
fun EntityStatMap.knownEntityStats(): Map<EntityStatType, EntityStatValue> =
    EntityStats.getKnownEntityStats(this)

