package ktale.runtime.host

/**
 * Minimal standalone-host plugin descriptor.
 *
 * ## Design note (explicitly host-specific)
 * This is NOT part of `ktale-api` because it assumes a particular packaging model:
 * a plugin jar carries a properties file naming its entrypoint class.
 */
public data class PluginDescriptor(
    public val id: String,
    public val mainClass: String,
)


