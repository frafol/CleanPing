package it.frafol.cleanping.bungee.enums;

import it.frafol.cleanping.bungee.CleanPing;
import org.jetbrains.annotations.NotNull;

public enum BungeeRedis {

    REDIS("redis.enabled");

    private final String path;
    public static final CleanPing instance = CleanPing.getInstance();

    BungeeRedis(String path) {
        this.path = path;
    }

    public <T> T get(@NotNull Class<T> clazz) {
        return clazz.cast(instance.getRedisTextFile().get(path));
    }

    public @NotNull String color() {
        return get(String.class).replace("&", "§");
    }

}