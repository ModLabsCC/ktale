package ktale.api.autoregister;

import ktale.api.events.Event;
import ktale.api.events.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as an event subscriber for auto-registration.
 *
 * <p>Host runtimes may scan plugin jars and register these methods automatically.
 * This annotation is intentionally in Java so both Java and Kotlin can use it cleanly.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubscribeEvent {
    Class<? extends Event> value();
    EventPriority priority() default EventPriority.NORMAL;
    boolean ignoreCancelled() default false;
}


