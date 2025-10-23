package it.frafol.cleanping.bungee.enums;

import it.frafol.cleanping.bungee.CleanPing;

public enum BungeeRedis {

    REDIS("redis.enabled");

    private final String path;
    public static final CleanPing instance = CleanPing.getInstance();

    BungeeRedis(String path) {
        this.path = path;
    }

    public <T> T get(Class<T> clazz) {
        return clazz.cast(instance.getRedisTextFile().get(path));
    }

    public String color() {
        return get(String.class).replace("&", "§");
    }

}