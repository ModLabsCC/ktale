package cc.modlabs.ktale.ext

import com.hypixel.hytale.math.vector.Vector3i
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType
import com.hypixel.hytale.server.core.universe.world.SetBlockSettings
import com.hypixel.hytale.server.core.universe.world.World

/**
 * Returns the [BlockType] at the given coordinates, or `null` if the chunk is unloaded.
 */
fun World.getBlockTypeAt(x: Int, y: Int, z: Int): BlockType? = getBlockType(Vector3i(x, y, z))

/**
 * Breaks the block at [x],[y],[z].
 *
 * @param settings block-update flags, defaults to [SetBlockSettings.PERFORM_BLOCK_UPDATE].
 */
fun World.breakBlockAt(
    x: Int,
    y: Int,
    z: Int,
    settings: Int = SetBlockSettings.PERFORM_BLOCK_UPDATE,
) {
    breakBlock(x, y, z, settings)
}

/**
 * Breaks the block at [pos].
 *
 * @param settings block-update flags, defaults to [SetBlockSettings.PERFORM_BLOCK_UPDATE].
 */
fun World.breakBlockAt(
    pos: Vector3i,
    settings: Int = SetBlockSettings.PERFORM_BLOCK_UPDATE,
) {
    breakBlock(pos.x, pos.y, pos.z, settings)
}
