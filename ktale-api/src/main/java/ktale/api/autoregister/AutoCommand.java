package ktale.api.autoregister;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a {@code ktale.api.commands.CommandDefinition} class as eligible for auto-registration.
 *
 * <p>Standalone hosts can discover these classes in a plugin jar and register them without manual wiring.
 * The class must have a public no-arg constructor.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoCommand {}


