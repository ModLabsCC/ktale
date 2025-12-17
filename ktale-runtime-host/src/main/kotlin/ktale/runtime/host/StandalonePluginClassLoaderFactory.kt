package ktale.runtime.host

import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Path

/**
 * Builds an isolated plugin classloader for a standalone host.
 *
 * ## Design note
 * - Parent is the host classloader (provides KTale + server runtime types).
 * - URLs include plugin jar + resolved dependency jars.
 * - Classloader strategy (parent-first vs child-first) is a host decision; we start parent-first.
 */
public object StandalonePluginClassLoaderFactory {
    public fun create(pluginJar: Path, dependencyJars: List<Path>, parent: ClassLoader): URLClassLoader {
        val urls: Array<URL> = (listOf(pluginJar) + dependencyJars)
            .distinct()
            .map { it.toUri().toURL() }
            .toTypedArray()
        return URLClassLoader(urls, parent)
    }
}


