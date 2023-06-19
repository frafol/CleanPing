package it.frafol.cleanping.velocity.enums;

import it.frafol.cleanping.velocity.CleanPing;
import org.jetbrains.annotations.NotNull;

public enum VelocityConfig {

    PING_PERMISSION("permissions.ping"),
    PING_OTHERS_PERMISSION("permissions.others_ping"),
    RELOAD_PERMISSION("permissions.reload"),
    UPDATE_CHECK("settings.update_check"),
    AUTO_UPDATE("settings.auto_update"),

    LOW_MS_COLOR("dynamic.low_ms_color"),
    MEDIUM_MS_COLOR("dynamic.medium_ms_color"),
    HIGH_MS_COLOR("dynamic.high_ms_color"),

    MEDIUM_MS("dynamic.medium_ms"),
    HIGH_MS("dynamic.high_ms"),

    OTHERS_PING_OPTION("settings.others_ping_enabled"),

    STATS("settings.stats"),

    DYNAMIC_PING("settings.dynamic_ping");

    private final String path;

    VelocityConfig(String path) {
        this.path = path;
    }

    public <T> T get(@NotNull Class<T> clazz) {
        return clazz.cast(CleanPing.getInstance().getConfigTextFile().getConfig().get(path));
    }

    public @NotNull String color() {
        return get(String.class).replace("&", "§");
    }

}