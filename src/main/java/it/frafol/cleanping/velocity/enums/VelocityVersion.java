package it.frafol.cleanping.velocity.enums;

import it.frafol.cleanping.velocity.CleanPing;
import org.jetbrains.annotations.NotNull;

public enum VelocityVersion {

    VERSION("version");

    private final String path;
    public static final CleanPing instance = CleanPing.getInstance();

    VelocityVersion(String path) {
        this.path = path;
    }

    public <T> T get(@NotNull Class<T> clazz) {
        return clazz.cast(instance.getVersionTextFile().getConfig().get(path));
    }

}