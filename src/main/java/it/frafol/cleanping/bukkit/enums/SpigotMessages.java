package it.frafol.cleanping.bukkit.enums;

import it.frafol.cleanping.bukkit.CleanPing;
import org.jetbrains.annotations.NotNull;

public enum SpigotMessages {

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

    SpigotMessages(String path) {
        this.path = path;
    }

    public @NotNull String color() {
        return get(String.class).replace("&", "§");
    }

    public <T> T get(@NotNull Class<T> clazz) {
        return clazz.cast(instance.getMessagesTextFile().get(path));
    }

}
