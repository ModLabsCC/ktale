package ktale.api.services

/**
 * Minimal service registry ("DI-light").
 *
 * ## Design note
 * A runtime registry keeps KTale adaptable to unknown host/container lifecycles.
 * This avoids forcing a DI framework choice on Day 1.
 */
public interface ServiceRegistry {
    /**
     * Registers a service instance.
     *
     * @param replace if `true`, replaces any existing service of the same [type]
     * @throws IllegalStateException if a service already exists and [replace] is `false`
     */
    public fun <T : Any> register(type: Class<T>, instance: T, replace: Boolean = false)

    /** Returns a service instance if registered, otherwise `null`. */
    public fun <T : Any> get(type: Class<T>): T?

    /** Returns a service instance or throws if missing. */
    public fun <T : Any> require(type: Class<T>): T

    /** Unregisters a service by type. */
    public fun <T : Any> unregister(type: Class<T>)
}

public inline fun <reified T : Any> ServiceRegistry.register(instance: T, replace: Boolean = false) {
    register(T::class.java, instance, replace)
}

public inline fun <reified T : Any> ServiceRegistry.get(): T? = get(T::class.java)

public inline fun <reified T : Any> ServiceRegistry.require(): T = require(T::class.java)

public inline fun <reified T : Any> ServiceRegistry.unregister() {
    unregister(T::class.java)
}


