package ktale.api.prefabs

import ktale.api.identity.Key

/**
 * Storage/hosting for prefabs.
 *
 * ## Design note
 * We know "prefabs exist" as a concept, but we do not know:
 * - whether the host stores them on disk, in memory, or streams them
 * - whether they are world-scoped, server-scoped, or per-player
 *
 * This contract stays minimal and uses opaque [Prefab] data.
 */
public interface PrefabStore {
    /** Returns known prefab ids. */
    public fun list(): List<Key>

    /** Loads a prefab by id, or `null` if missing. */
    public fun load(id: Key): Prefab?

    /** Saves or overwrites a prefab. */
    public fun save(prefab: Prefab)

    /** Deletes a prefab if present. */
    public fun delete(id: Key)
}


