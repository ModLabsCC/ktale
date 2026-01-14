package cc.modlabs.ktale.entitystats

/**
 * Java-friendly mutable holder for entity stat metadata.
 * - id
 * - index
 * - min / max
 */
class EntityStatValue(
    val id: String,
) {
    var index: Int = -1
    var min: Float = 0f
    var max: Float = 0f
}

