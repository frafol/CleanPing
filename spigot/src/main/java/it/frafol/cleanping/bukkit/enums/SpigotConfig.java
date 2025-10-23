package it.frafol.cleanping.bukkit.enums;

import it.frafol.cleanping.bukkit.CleanPing;
import it.frafol.cleanping.bukkit.objects.Placeholder;
import org.jetbrains.annotations.NotNull;

public enum SpigotConfig {

    PING_PERMISSION("permissions.ping"),
    PING_OTHERS_PERMISSION("permissions.others_ping"),
    DIFFERENCE_PING_PERMISSION("permissions.difference_ping"),
    RELOAD_PERMISSION("permissions.reload"),
    UPDATE_CHECK("settings.update_check"),
    AUTO_UPDATE("settings.auto_update"),

    LOW_MS_COLOR("dynamic.low_ms_color"),
    MEDIUM_MS_COLOR("dynamic.medium_ms_color"),
    HIGH_MS_COLOR("dynamic.high_ms_color"),

    MEDIUM_MS("dynamic.medium_ms"),
    HIGH_MS("dynamic.high_ms"),

    MONITOR("monitor.enabled"),
    FLAG_DELAY("monitor.flag_delay"),
    MAX_PING("monitor.max_ping"),
    MAX_FLAGS("monitor.max_flags"),

    OTHERS_PING_OPTION("settings.others_ping_enabled"),
    DIFFERENCE_PING_OPTION("settings.difference_ping_enabled"),

    STATS("settings.stats"),
    MINIMESSAGE("settings.minimessage"),

    DYNAMIC_PING("settings.dynamic_ping");

    private final String path;
    public static final CleanPing instance = CleanPing.getInstance();

    SpigotConfig(String path) {
        this.path = path;
    }

    public @NotNull String color() {
        return Placeholder.translate(get(String.class)).replace("&", "§");
    }

    public <T> T get(@NotNull Class<T> clazz) {
        return clazz.cast(instance.getConfigTextFile().get(path));
    }

}