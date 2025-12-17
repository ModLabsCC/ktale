package ktale.core.config

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

/**
 * Simple file-backed config store.
 *
 * ## Design note
 * This is platform-neutral and intended for standalone hosts or platform adapters that store configs on disk.
 * The [id] passed in is treated as a relative filename. No path traversal is allowed.
 */
public class FileConfigTextStore(
    private val baseDir: Path,
) : ConfigTextStore {
    init {
        Files.createDirectories(baseDir)
    }

    override fun read(id: String): String? {
        val path = resolveSafe(id) ?: return null
        if (!Files.exists(path)) return null
        return Files.readString(path, StandardCharsets.UTF_8)
    }

    override fun write(id: String, text: String) {
        val path = resolveSafe(id) ?: return
        Files.createDirectories(path.parent)
        Files.writeString(path, text, StandardCharsets.UTF_8)
    }

    private fun resolveSafe(id: String): Path? {
        val trimmed = id.trim()
        if (trimmed.isEmpty()) return null
        // Extremely conservative: disallow absolute paths and parent traversal.
        if (trimmed.contains("..")) return null
        val p = baseDir.resolve(trimmed).normalize()
        if (!p.startsWith(baseDir.normalize())) return null
        return p
    }
}


