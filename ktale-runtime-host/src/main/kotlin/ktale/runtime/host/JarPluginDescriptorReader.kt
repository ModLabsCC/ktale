package ktale.runtime.host

import java.io.InputStream
import java.nio.file.Path
import java.util.Properties
import java.util.jar.JarFile

/**
 * Reads `ktale-plugin.properties` from a plugin jar.
 *
 * Expected keys:
 * - `id` (required)
 * - `main` (required) fully qualified class name implementing `ktale.api.KtalePlugin`
 */
public object JarPluginDescriptorReader {
    public const val DEFAULT_RESOURCE: String = "ktale-plugin.properties"

    public fun read(jarPath: Path, resourceName: String = DEFAULT_RESOURCE): PluginDescriptor {
        JarFile(jarPath.toFile()).use { jar ->
            val entry = jar.getJarEntry(resourceName)
                ?: error("Missing $resourceName in plugin jar: $jarPath")
            jar.getInputStream(entry).use { stream ->
                return parse(stream)
            }
        }
    }

    public fun parse(input: InputStream): PluginDescriptor {
        val props = Properties()
        props.load(input)
        val id = props.getProperty("id")?.trim().orEmpty()
        val main = props.getProperty("main")?.trim().orEmpty()
        require(id.isNotBlank()) { "ktale-plugin.properties missing required key: id" }
        require(main.isNotBlank()) { "ktale-plugin.properties missing required key: main" }
        return PluginDescriptor(id, main)
    }
}


