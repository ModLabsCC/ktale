package ktale.core.services

import ktale.api.services.ServiceRegistry
import java.util.concurrent.ConcurrentHashMap

/**
 * Minimal thread-safe service registry.
 *
 * ## Design note
 * This is intentionally tiny and runtime-oriented to keep KTale adaptable to unknown host lifecycles.
 */
public class SimpleServiceRegistry : ServiceRegistry {
    private val services = ConcurrentHashMap<Class<*>, Any>()

    override fun <T : Any> register(type: Class<T>, instance: T, replace: Boolean) {
        if (!replace) {
            val existing = services.putIfAbsent(type, instance)
            if (existing != null) {
                throw IllegalStateException("Service already registered for type ${type.name}")
            }
        } else {
            services[type] = instance
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(type: Class<T>): T? = services[type] as T?

    override fun <T : Any> require(type: Class<T>): T =
        get(type) ?: throw NoSuchElementException("Missing service for type ${type.name}")

    override fun <T : Any> unregister(type: Class<T>) {
        services.remove(type)
    }
}


