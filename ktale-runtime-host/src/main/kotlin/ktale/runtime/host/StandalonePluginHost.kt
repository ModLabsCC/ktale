package ktale.runtime.host

import ktale.api.KtalePlugin
import ktale.core.autoregister.AutoRegistrar
import ktale.core.config.FileConfigTextStore
import ktale.core.runtime.CoreRuntime
import ktale.platform.Platform
import ktale.runtime.deps.DependencyManifestReader
import ktale.runtime.deps.MavenDependencyResolver
import java.net.URLClassLoader
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap

/**
 * Standalone KTale plugin host.
 *
 * ## Explicit assumptions (host-specific)
 * - Plugins are packaged as jars that contain:
 *   - `ktale-plugin.properties` with `id` + `main`, and optionally
 *   - `.dependencies` / `.repositories` for runtime resolution.
 * - The host builds an isolated classloader per plugin.
 *
 * This is intentionally *not* part of `ktale-api` because it assumes a packaging model.
 */
public class StandalonePluginHost(
    private val platform: Platform,
    private val cacheDir: Path,
    private val pluginDataDir: Path,
) {
    private val loaded = ConcurrentHashMap<String, LoadedPlugin>()

    public fun load(pluginJar: Path): LoadedPlugin {
        val descriptor = JarPluginDescriptorReader.read(pluginJar)

        if (loaded.containsKey(descriptor.id)) {
            error("Plugin already loaded: ${descriptor.id}")
        }

        val manifest = DependencyManifestReader.fromResources(
            classLoader = jarOnlyClassLoader(pluginJar),
        )

        val repos = buildRepos(manifest.repositories)
        val resolver = MavenDependencyResolver(cacheDir, repos)
        val depJars = resolver.resolve(manifest.coordinates)

        val cl = StandalonePluginClassLoaderFactory.create(pluginJar, depJars, parent = javaClass.classLoader)
        val plugin = instantiate(descriptor, cl)

        val runtime = CoreRuntime(platform, FileConfigTextStore(pluginDataDir.resolve(descriptor.id).resolve("config")))
        val ctx = runtime.pluginContext(descriptor.id)

        val loadedPlugin = LoadedPlugin(descriptor, plugin, cl, ctx, descriptorJarPath = pluginJar)
        loaded[descriptor.id] = loadedPlugin
        return loadedPlugin
    }

    public fun enable(id: String) {
        val p = loaded[id] ?: error("Plugin not loaded: $id")
        p.instance.onLoad(p.context)
        // Plug-and-play: discover + register commands/listeners before enabling.
        AutoRegistrar.registerAllFromJar(p.descriptorJarPath, p.classLoader, p.context)
        p.instance.onEnable(p.context)
    }

    public fun disable(id: String) {
        val p = loaded[id] ?: return
        try {
            p.instance.onDisable(p.context)
        } finally {
            p.classLoader.close()
            loaded.remove(id)
        }
    }

    private fun instantiate(descriptor: PluginDescriptor, cl: ClassLoader): KtalePlugin {
        val clazz = Class.forName(descriptor.mainClass, true, cl)
        require(KtalePlugin::class.java.isAssignableFrom(clazz)) {
            "Plugin main class does not implement KtalePlugin: ${descriptor.mainClass}"
        }
        val ctor = clazz.getDeclaredConstructor()
        ctor.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return ctor.newInstance() as KtalePlugin
    }

    private fun jarOnlyClassLoader(pluginJar: Path): ClassLoader =
        URLClassLoader(arrayOf(pluginJar.toUri().toURL()), null)

    private fun buildRepos(extra: List<ktale.runtime.deps.DependencyManifest.Repository>): List<org.eclipse.aether.repository.RemoteRepository> {
        val base = mutableListOf(
            MavenDependencyResolver.repo("modlabs-mirror", "https://nexus.modlabs.cc/repository/maven-mirrors/")
        )
        extra.forEach { r ->
            base += MavenDependencyResolver.repo(r.id, r.url, r.usernameEnv, r.passwordEnv)
        }
        return base
    }

    public data class LoadedPlugin(
        public val descriptor: PluginDescriptor,
        public val instance: KtalePlugin,
        public val classLoader: URLClassLoader,
        public val context: ktale.api.PluginContext,
        internal val descriptorJarPath: Path,
    )
}


