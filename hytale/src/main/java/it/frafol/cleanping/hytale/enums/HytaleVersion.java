package it.frafol.cleanping.hytale.enums;

import it.frafol.cleanping.hytale.CleanPing;

public enum HytaleVersion {

    VERSION("version");

    private final String path;
    public static final CleanPing instance = CleanPing.getInstance();

    HytaleVersion(String path) {
        this.path = path;
    }

    public <T> T get(Class<T> clazz) {
        return clazz.cast(instance.getVersionTextFile().get(path));
    }

}