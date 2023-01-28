package it.frafol.cleanping.velocity.enums;

import it.frafol.cleanping.velocity.CleanPing;
import org.jetbrains.annotations.NotNull;

public enum VelocityMessages {

    PREFIX("messages.prefix"),

    USAGE("messages.usage"),

    ONLY_PLAYERS("messages.only_players"),
    NOT_ONLINE("messages.not_online"),

    NO_PERMISSION("messages.no_permission"),

    PING("messages.ping"),
    OTHERS_PING("messages.others_ping"),

    RELOADED("messages.reloaded");

    private final String path;
    public static final CleanPing instance = CleanPing.getInstance();

    VelocityMessages(String path) {
        this.path = path;
    }

    public <T> T get(@NotNull Class<T> clazz) {
        return clazz.cast(instance.getMessagesTextFile().getConfig().get(path));
    }

    public @NotNull String color() {
        return get(String.class).replace("&", "§");
    }

}