package ktale.examples;

import ktale.api.PluginContext;
import ktale.api.commands.CommandContext;
import ktale.api.commands.CommandResult;
import ktale.api.events.Event;
import ktale.api.events.EventListener;
import ktale.api.events.EventPriority;
import ktale.core.commands.Commands;
import ktale.platform.fake.FakePlayer;
import ktale.platform.fake.FakeServer;

import java.time.Duration;

/**
 * This class exists to prove "Java-first" KTale API ergonomics at compile time.
 *
 * It is not a runtime demo and does not depend on any test framework.
 */
public final class JavaExamplePluginUsage {
    private JavaExamplePluginUsage() {}

    public static void compileOnlyExample() {
        FakeServer server = new FakeServer();
        PluginContext ctx = server.createContext("java-demo");

        // Service registry
        ctx.getServices().register(String.class, "hello");

        // Events (Java-first subscribe)
        ctx.getEvents().subscribe(TestEvent.class, EventPriority.NORMAL, new EventListener<TestEvent>() {
            @Override
            public void onEvent(TestEvent event) {
                ctx.getLogger().info("Received event from Java");
            }
        });

        // Commands (Java fluent builder from ktale-core)
        ctx.getCommands().register(
                Commands.command("ping")
                        .executor((CommandContext c) -> CommandResult.Success.INSTANCE)
                        .build()
        );

        // Scheduler (Java Runnable + java.time.Duration)
        ctx.getScheduler().runSyncDelayed(Duration.ofMillis(10), () -> ctx.getLogger().info("tick"));

        // Fake inbound command execution
        FakePlayer player = new FakePlayer("Alice");
        server.getPlatform().getCommandBridge().dispatchInbound(new CommandContext() {
            @Override public ktale.api.commands.CommandSender getSender() { return player; }
            @Override public String getLabel() { return "ping"; }
            @Override public java.util.List<String> getArgs() { return java.util.Collections.emptyList(); }
        });
    }

    public static final class TestEvent implements Event {}
}


