package cc.modlabs.ktale.ext

import com.hypixel.hytale.server.core.inventory.ItemStack

/**
 * Creates a new [ItemStack] with the given [id] and [count].
 */
fun itemStack(id: String, count: Int = 1): ItemStack = ItemStack(id, count)
