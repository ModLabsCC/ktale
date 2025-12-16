package ktale.api.config

/**
 * A versioned migration from one schema version to the next.
 *
 * ## Design note
 * Migrations operate on *text* to avoid locking KTale into a particular parsing model.
 * Core may provide higher-level helpers for structured migrations, but the public contract stays minimal.
 */
public interface ConfigMigration {
    /** Schema version this migration expects. */
    public val fromVersion: Int

    /** Schema version after this migration is applied. Usually `fromVersion + 1`. */
    public val toVersion: Int

    /**
     * Applies the migration.
     *
     * @param oldText config content at [fromVersion]
     * @return migrated config content at [toVersion]
     */
    public fun migrate(oldText: String): String
}


