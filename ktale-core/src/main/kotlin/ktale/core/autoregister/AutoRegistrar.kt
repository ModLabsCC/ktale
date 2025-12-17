package ktale.core.autoregister

import ktale.api.PluginContext
import ktale.api.autoregister.AutoCommand
import ktale.api.autoregister.SubscribeEvent
import ktale.api.commands.CommandDefinition
import ktale.api.events.Event
import ktale.api.events.EventListener
import ktale.api.events.EventPriority
import java.lang.reflect.Method
import java.nio.file.Path
import java.util.jar.JarFile

/**
 * Plug-and-play auto registration for commands and event listeners.
 *
 * ## Design note
 * This lives in `ktale-core` because it is a convenience implementation, not a stable contract.
 * It is safe to omit in other host runtimes.
 *
 * ## Rules (conventions)
 * - Commands:
 *   - classes that implement [CommandDefinition]
 *   - annotated with [AutoCommand]
 *   - public no-arg constructor
 * - Event listeners:
 *   - any class with methods annotated [SubscribeEvent]
 *   - listener class must have a public no-arg constructor
 */
public object AutoRegistrar {
    public fun registerAllFromJar(pluginJar: Path, classLoader: ClassLoader, context: PluginContext) {
        val classNames = readClassNames(pluginJar)
        val classes = classNames.mapNotNull { name ->
            try {
                Class.forName(name, true, classLoader)
            } catch (_: Throwable) {
                null
            }
        }
        registerAllFromClasses(classes, context)
    }

    public fun registerAllFromClasses(classes: List<Class<*>>, context: PluginContext) {
        classes.forEach { clazz ->
            tryRegisterCommand(clazz, context)
            tryRegisterListener(clazz, context)
        }
    }

    private fun readClassNames(jar: Path): List<String> =
        JarFile(jar.toFile()).use { jf ->
            jf.entries().asSequence()
                .filter { !it.isDirectory }
                .map { it.name }
                .filter { it.endsWith(".class") }
                .filter { !it.contains('$') } // skip inner/anonymous classes by default
                .map { it.removeSuffix(".class").replace('/', '.') }
                .toList()
        }

    private fun tryRegisterCommand(clazz: Class<*>, context: PluginContext) {
        if (!CommandDefinition::class.java.isAssignableFrom(clazz)) return
        if (!clazz.isAnnotationPresent(AutoCommand::class.java)) return
        val ctor = clazz.getDeclaredConstructor()
        ctor.isAccessible = true
        val def = ctor.newInstance() as CommandDefinition
        context.commands.register(def)
    }

    private fun tryRegisterListener(clazz: Class<*>, context: PluginContext) {
        val methods = clazz.declaredMethods.filter { it.isAnnotationPresent(SubscribeEvent::class.java) }
        if (methods.isEmpty()) return

        val ctor = clazz.getDeclaredConstructor()
        ctor.isAccessible = true
        val instance = ctor.newInstance()
        for (m in methods) {
            registerMethodListener(instance, m, context)
        }
    }

    private fun registerMethodListener(instance: Any, method: Method, context: PluginContext) {
        val ann = method.getAnnotation(SubscribeEvent::class.java)
        val eventType = ann.value.java
        val priority: EventPriority = ann.priority
        val ignoreCancelled: Boolean = ann.ignoreCancelled

        method.isAccessible = true

        @Suppress("UNCHECKED_CAST")
        context.events.subscribe(
            eventType as Class<Event>,
            EventListener { e -> method.invoke(instance, e) },
            priority,
            ignoreCancelled,
        )
    }
}


