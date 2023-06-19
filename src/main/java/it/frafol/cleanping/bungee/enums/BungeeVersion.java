package it.frafol.cleanping.bungee.enums;

import it.frafol.cleanping.bungee.CleanPing;
import org.jetbrains.annotations.NotNull;

public enum BungeeVersion {

    VERSION("version");

    private final String path;
    public static final CleanPing instance = CleanPing.getInstance();

    BungeeVersion(String path) {
        this.path = path;
    }

    public <T> T get(@NotNull Class<T> clazz) {
        return clazz.cast(instance.getVersionTextFile().get(path));
    }

    public @NotNull String color() {
        return get(String.class).replace("&", "§");
    }
}