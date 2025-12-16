package ktale.core.config

import ktale.api.config.ConfigKey
import ktale.api.config.ConfigManager
import ktale.api.logging.KtaleLogger

/**
 * Minimal config manager that loads/saves text configs via a [ConfigTextStore].
 *
 * ## Versioning
 * This implementation stores configs as text and applies [ConfigKey.migrations] on the raw text.
 * Version detection is intentionally simple and format-agnostic; it relies on a tiny convention:
 *
 * - The first non-empty, non-comment line may be: `ktaleConfigVersion: <int>`
 *
 * Platforms and plugins are free to ignore this convention and provide their own ConfigManager.
 */
public class CoreConfigManager(
    private val store: ConfigTextStore,
    private val logger: KtaleLogger,
) : ConfigManager {
    override fun <T : Any> load(key: ConfigKey<T>): T {
        val existing = store.read(key.id)
        if (existing == null) {
            val default = key.defaultValue()
            save(key, default)
            return default
        }

        val (storedVersion, textWithoutHeader) = parseVersionHeader(existing)
        var version = storedVersion ?: 0
        var migratedText = textWithoutHeader

        if (version < key.version) {
            val migrations = key.migrations.sortedBy { it.fromVersion }
            for (m in migrations) {
                if (m.fromVersion != version) continue
                migratedText = m.migrate(migratedText)
                version = m.toVersion
            }
        }

        if (version != key.version) {
            logger.warn("Config '${key.id}' could not be fully migrated (have=$version want=${key.version}); using best-effort decode.")
        }

        return try {
            key.codec.decode(migratedText)
        } catch (t: Throwable) {
            logger.error("Failed to decode config '${key.id}', falling back to defaults.", t)
            val default = key.defaultValue()
            save(key, default)
            default
        }.also {
            // Persist migrations (if any) along with header.
            store.write(key.id, renderWithHeader(key.version, key.codec.encode(it)))
        }
    }

    override fun <T : Any> save(key: ConfigKey<T>, value: T) {
        val encoded = key.codec.encode(value)
        store.write(key.id, renderWithHeader(key.version, encoded))
    }

    private fun renderWithHeader(version: Int, body: String): String =
        "ktaleConfigVersion: $version\n$body"

    private fun parseVersionHeader(text: String): Pair<Int?, String> {
        val lines = text.lines()
        for ((idx, raw) in lines.withIndex()) {
            val line = raw.trim()
            if (line.isEmpty()) continue
            if (line.startsWith("#")) continue
            if (line.startsWith("//")) continue
            val prefix = "ktaleConfigVersion:"
            if (line.startsWith(prefix)) {
                val v = line.removePrefix(prefix).trim().toIntOrNull()
                val rest = lines.drop(idx + 1).joinToString("\n")
                return v to rest
            }
            break
        }
        return null to text
    }
}


