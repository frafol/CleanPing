package it.frafol.cleanping.hytale.objects;

import lombok.SneakyThrows;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TextFile {

    private YamlFile yamlFile;

    private static final List<TextFile> list = new ArrayList<>();

    public TextFile(Path path, String fileName) {
        try {
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }

            Path configPath = path.resolve(fileName);

            if (!Files.exists(configPath)) {
                try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
                    Files.copy(Objects.requireNonNull(in), configPath);
                }
            }

            yamlFile = new YamlFile(configPath.toFile());
            yamlFile.load();

            list.add(this);
        } catch (Exception ignored) {}
    }

    public YamlFile getConfig() {return yamlFile;}

    public void reload() {
        try {
        yamlFile.load();
        } catch (Exception ignored) {}
    }

    public static void reloadAll() {
        list.forEach(TextFile::reload);
    }
}
