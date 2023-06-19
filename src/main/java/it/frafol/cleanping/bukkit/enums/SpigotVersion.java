package it.frafol.cleanping.bukkit.enums;

import it.frafol.cleanping.bukkit.CleanPing;
import org.jetbrains.annotations.NotNull;

public enum SpigotVersion {

    VERSION("version");

    private final String path;
    public static final CleanPing instance = CleanPing.getInstance();

    SpigotVersion(String path) {
        this.path = path;
    }

    public <T> T get(@NotNull Class<T> clazz) {
        return clazz.cast(instance.getVersionTextFile().get(path));
    }

    public @NotNull String color() {
        return get(String.class).replace("&", "§");
    }

}