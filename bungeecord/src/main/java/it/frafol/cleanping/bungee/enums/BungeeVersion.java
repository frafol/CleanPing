package it.frafol.cleanping.bungee.enums;

import it.frafol.cleanping.bungee.CleanPing;

public enum BungeeVersion {

    VERSION("version");

    private final String path;
    public static final CleanPing instance = CleanPing.getInstance();

    BungeeVersion(String path) {
        this.path = path;
    }

    public <T> T get(Class<T> clazz) {
        return clazz.cast(instance.getVersionTextFile().get(path));
    }

    public String color() {
        return get(String.class).replace("&", "§");
    }
}