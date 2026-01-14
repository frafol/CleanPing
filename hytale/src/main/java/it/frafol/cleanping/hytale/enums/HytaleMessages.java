package it.frafol.cleanping.hytale.enums;

import it.frafol.cleanping.hytale.CleanPing;

public enum HytaleMessages {

    PREFIX("messages.prefix"),

    USAGE("messages.usage"),

    ONLY_PLAYERS("messages.only_players"),
    NOT_ONLINE("messages.not_online"),

    NO_PERMISSION("messages.no_permission"),

    PING("messages.ping"),
    OTHERS_PING("messages.others_ping"),
    PING_DIFFERENCE("messages.difference_ping"),

    LAGGING("messages.lagging"),

    RELOADED("messages.reloaded");

    private final String path;
    public static final CleanPing instance = CleanPing.getInstance();

    HytaleMessages(String path) {
        this.path = path;
    }

    public String color() {
        return get(String.class);
    }

    public <T> T get(Class<T> clazz) {
        return clazz.cast(instance.getMessagesTextFile().get(path));
    }

}
