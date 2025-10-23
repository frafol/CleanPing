package it.frafol.cleanping.velocity.enums;

import it.frafol.cleanping.velocity.CleanPing;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

public enum VelocityMessages {

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

    VelocityMessages(String path) {
        this.path = path;
    }

    public <T> T get(@NotNull Class<T> clazz) {
        return clazz.cast(CleanPing.getInstance().getMessagesTextFile().getConfig().get(path));
    }

    public @NotNull String color() {

        if (VelocityConfig.MINIMESSAGE.get(Boolean.class)) {
            MiniMessage miniMessage = MiniMessage.miniMessage();
            Component component = miniMessage.deserialize(get(String.class));
            return LegacyComponentSerializer.legacySection().serialize(component);
        }

        return get(String.class).replace("&", "§");
    }

}