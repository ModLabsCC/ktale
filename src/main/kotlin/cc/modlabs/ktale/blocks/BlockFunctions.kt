package cc.modlabs.ktale.blocks

import com.hypixel.hytale.math.vector.Vector3i
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockBreakingDropType
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockGathering
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType
import com.hypixel.hytale.server.core.asset.type.blocktype.config.CustomModelTexture
import com.hypixel.hytale.server.core.inventory.ItemStack
import com.hypixel.hytale.server.core.modules.item.ItemModule
import com.hypixel.hytale.server.core.universe.world.SetBlockSettings
import com.hypixel.hytale.server.core.universe.world.World
import java.util.function.BiConsumer
import javax.annotation.Nullable

/**
 * Block helper utilities for Hytale plugins.
 *
 * Notes:
 * - KTale does **not** assume a specific “spawn item entity” API (it’s server-impl specific / may change).
 *   So [dropBlock] takes a drop callback (Kotlin lambda or Java [BiConsumer]) that you can connect to your
 *   preferred drop/spawn method.
 */
object BlockFunctions {
    /**
     * Breaks the block at [blockPos], computes drops and invokes [dropper] for each [ItemStack].
     *
     * @param dropBreakingDropType if true, uses block gathering/breaking config to compute the item(s) to drop.
     * @param dropPos where drops should appear (defaults to [blockPos]).
     * @param setBlockSettings forwarded to `World.breakBlock(...)` (defaults to [SetBlockSettings.PERFORM_BLOCK_UPDATE]).
     */
    @JvmStatic
    fun dropBlock(
        world: World,
        blockPos: Vector3i,
        dropBreakingDropType: Boolean,
        dropper: BiConsumer<ItemStack, Vector3i>,
    ) {
        dropBlock(world, blockPos, blockPos, dropBreakingDropType, SetBlockSettings.PERFORM_BLOCK_UPDATE, dropper)
    }

    @JvmStatic
    fun dropBlock(
        world: World,
        blockPos: Vector3i,
        @Nullable dropPos: Vector3i?,
        dropBreakingDropType: Boolean,
        dropper: BiConsumer<ItemStack, Vector3i>,
    ) {
        dropBlock(world, blockPos, dropPos, dropBreakingDropType, SetBlockSettings.PERFORM_BLOCK_UPDATE, dropper)
    }

    @JvmStatic
    fun dropBlock(
        world: World,
        blockPos: Vector3i,
        @Nullable dropPos: Vector3i?,
        dropBreakingDropType: Boolean,
        setBlockSettings: Int,
        dropper: BiConsumer<ItemStack, Vector3i>,
    ) {
        val actualDropPos = dropPos ?: blockPos
        val drops = breakBlockAndCollectDrops(world, blockPos, dropBreakingDropType, setBlockSettings)
        for (item in drops) {
            dropper.accept(item, actualDropPos)
        }
    }

    /**
     * Breaks the block at [blockPos] and returns the [ItemStack]s that *should* be dropped.
     *
     * This mirrors the logic you posted:
     * - break the block
     * - if [dropBreakingDropType] use `BlockType.gathering.breaking` to choose itemId OR droplistId
     * - otherwise default to dropping the block's id as an item
     */
    @JvmStatic
    fun breakBlockAndCollectDrops(
        world: World,
        blockPos: Vector3i,
        dropBreakingDropType: Boolean,
        setBlockSettings: Int = SetBlockSettings.PERFORM_BLOCK_UPDATE,
    ): List<ItemStack> {
        val blockType: BlockType? = world.getBlockType(blockPos)

        // Break block first (same as the reference code).
        world.breakBlock(blockPos.x, blockPos.y, blockPos.z, setBlockSettings)

        if (blockType == null) return emptyList()

        return computeBlockDrops(blockType, dropBreakingDropType)
    }

    /**
     * Computes the drops for a block type. Does **not** break the block and does **not** spawn drops.
     */
    @JvmStatic
    fun computeBlockDrops(blockType: BlockType, dropBreakingDropType: Boolean): List<ItemStack> {
        val itemsToDrop = ArrayList<ItemStack>()

        var dropBlockId: String? = blockType.id
        var usedItemModule = false

        if (dropBreakingDropType) {
            val blockGathering: BlockGathering? = blockType.gathering
            val blockBreakingDropType: BlockBreakingDropType? = blockGathering?.breaking

            if (blockBreakingDropType != null) {
                val itemId: String? = blockBreakingDropType.itemId
                val dropListId: String? = blockBreakingDropType.dropListId

                if (itemId != null) {
                    dropBlockId = itemId
                } else if (dropListId != null) {
                    val itemModule = ItemModule.get()
                    if (itemModule.isEnabled) {
                        val qty = blockBreakingDropType.quantity
                        for (i in 0 until qty) {
                            itemsToDrop.addAll(itemModule.getRandomItemDrops(dropListId))
                        }
                    }
                    usedItemModule = true
                }
            }
        }

        if (!usedItemModule && dropBlockId != null) {
            itemsToDrop.add(ItemStack(dropBlockId))
        }

        return itemsToDrop
    }

    /**
     * Heuristic: checks if a block is an ore by inspecting its custom model textures.
     */
    @JvmStatic
    fun isOre(blockType: BlockType): Boolean {
        val customModelTextures: Array<CustomModelTexture>? = blockType.customModelTexture
        if (customModelTextures == null) return false

        for (customModelTexture in customModelTextures) {
            val texture = customModelTexture.texture
            if (texture.contains("Ores") || texture.contains("Ore_")) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun isLog(blockType: BlockType): Boolean = isLog(blockType, true)

    @JvmStatic
    fun isLog(blockType: BlockType, includeBranches: Boolean): Boolean = isLogId(blockType.id, includeBranches)

    @JvmStatic
    fun isBranch(blockType: BlockType): Boolean = isBranchId(blockType.id)

    @JvmStatic
    fun isBranchId(blockId: String): Boolean = blockId.contains("_Branch_")

    @JvmStatic
    fun isLeavesBlock(blockType: BlockType): Boolean = isLeavesId(blockType.id)

    @JvmStatic
    fun isLeavesId(blockId: String): Boolean = blockId.contains("_Leaves")

    @JvmStatic
    fun isLogId(blockId: String, includeBranches: Boolean = true): Boolean =
        (blockId.endsWith("_Trunk") || blockId.endsWith("_Log")) || (includeBranches && isBranchId(blockId))
}

// --- Kotlin convenience overloads (idiomatic) ---

fun BlockType.isOre(): Boolean = BlockFunctions.isOre(this)
fun BlockType.isLog(includeBranches: Boolean = true): Boolean = BlockFunctions.isLog(this, includeBranches)
fun BlockType.isBranch(): Boolean = BlockFunctions.isBranch(this)
fun BlockType.isLeavesBlock(): Boolean = BlockFunctions.isLeavesBlock(this)

fun World.breakBlockAndCollectDrops(
    blockPos: Vector3i,
    dropBreakingDropType: Boolean,
    setBlockSettings: Int = SetBlockSettings.PERFORM_BLOCK_UPDATE,
): List<ItemStack> = BlockFunctions.breakBlockAndCollectDrops(this, blockPos, dropBreakingDropType, setBlockSettings)

fun World.dropBlock(
    blockPos: Vector3i,
    dropBreakingDropType: Boolean,
    dropPos: Vector3i? = blockPos,
    setBlockSettings: Int = SetBlockSettings.PERFORM_BLOCK_UPDATE,
    dropper: (ItemStack, Vector3i) -> Unit,
) {
    val actualDropPos = dropPos ?: blockPos
    val drops = BlockFunctions.breakBlockAndCollectDrops(this, blockPos, dropBreakingDropType, setBlockSettings)
    for (item in drops) dropper(item, actualDropPos)
}

