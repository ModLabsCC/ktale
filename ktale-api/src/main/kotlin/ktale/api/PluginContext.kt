package ktale.api

import ktale.api.commands.CommandRegistry
import ktale.api.config.ConfigManager
import ktale.api.events.EventBus
import ktale.api.logging.KtaleLogger
import ktale.api.scheduler.Scheduler
import ktale.api.services.ServiceRegistry

/**
 * Per-plugin access to platform-provided facilities.
 *
 * ## Stability rules
 * - This is the *only* object a plugin needs to keep around.
 * - It is platform-agnostic: implementations live in platform adapters.
 * - It is intentionally capability-oriented instead of exposing a giant "Server" object.
 */
public interface PluginContext {
    /** A human-readable plugin identifier (stable across reloads). */
    public val pluginId: String

    /** A logger scoped to this plugin. */
    public val logger: KtaleLogger

    /** Event publishing and subscription. */
    public val events: EventBus

    /** Scheduling API for sync/async/delayed/repeating work. */
    public val scheduler: Scheduler

    /** Command registration and dispatch (logic only; IO bridges are platform-specific). */
    public val commands: CommandRegistry

    /** Typed configuration access with versioned migrations. */
    public val configs: ConfigManager

    /**
     * Service registry used as "DI-light".
     *
     * This is intentionally runtime-based (not compile-time DI) to remain adaptable
     * to unknown server/container lifecycles.
     */
    public val services: ServiceRegistry
}


