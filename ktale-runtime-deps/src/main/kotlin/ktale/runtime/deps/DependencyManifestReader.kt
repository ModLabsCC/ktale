package ktale.runtime.deps

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/**
 * Reads `.dependencies` and `.repositories` resources from a jar/plugin classloader.
 *
 * ## File format
 * - `.dependencies`: one Maven coordinate per line (`group:artifact:version`), `#` comments allowed.
 * - `.repositories`: `id url` per line, `#` comments allowed.
 */
public object DependencyManifestReader {
    public fun fromResources(
        classLoader: ClassLoader,
        dependenciesResource: String = ".dependencies",
        repositoriesResource: String = ".repositories",
    ): DependencyManifest {
        val coords = readLines(classLoader.getResourceAsStream(dependenciesResource))
        val repos = readLines(classLoader.getResourceAsStream(repositoriesResource))
            .mapNotNull { parseRepoLine(it) }
        return DependencyManifest(coords, repos)
    }

    private fun readLines(stream: InputStream?): List<String> {
        if (stream == null) return emptyList()
        stream.use {
            val r = BufferedReader(InputStreamReader(it, StandardCharsets.UTF_8))
            return r.lineSequence()
                .map { line -> line.trim() }
                .filter { line -> line.isNotEmpty() && !line.startsWith("#") }
                .toList()
        }
    }

    private fun parseRepoLine(line: String): DependencyManifest.Repository? {
        // Supported formats:
        // - "id url"
        // - "id url USER_ENV PASS_ENV"
        val parts = line.split(Regex("\\s+"))
        if (parts.size < 2) return null
        val id = parts[0].trim()
        val url = parts[1].trim()
        if (id.isEmpty() || url.isEmpty()) return null
        val userEnv = parts.getOrNull(2)?.trim()?.takeIf { it.isNotEmpty() }
        val passEnv = parts.getOrNull(3)?.trim()?.takeIf { it.isNotEmpty() }
        return DependencyManifest.Repository(id, url, userEnv, passEnv)
    }
}


