package it.frafol.cleanping.velocity.enums;

import it.frafol.cleanping.velocity.CleanPing;
import org.jetbrains.annotations.NotNull;

public enum VelocityRedis {

    REDIS("redis.enabled");

    private final String path;
    public static final CleanPing instance = CleanPing.getInstance();

    VelocityRedis(String path) {
        this.path = path;
    }

    public <T> T get(@NotNull Class<T> clazz) {
        return clazz.cast(instance.getRedisTextFile().getConfig().get(path));
    }

    public @NotNull String color() {
        return get(String.class).replace("&", "§");
    }

}