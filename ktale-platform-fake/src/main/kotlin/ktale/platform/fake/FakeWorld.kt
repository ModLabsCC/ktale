package ktale.platform.fake

/**
 * Minimal fake world.
 *
 * ## Design note
 * This is intentionally tiny: KTale does not model full game entities/world state.
 * Plugins can store their own metadata externally if needed.
 */
public data class FakeWorld(
    public val id: String,
    public val name: String = id,
)


