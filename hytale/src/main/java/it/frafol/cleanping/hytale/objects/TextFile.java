package it.frafol.cleanping.hytale.objects;

import org.simpleyaml.configuration.file.YamlFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TextFile {

    private YamlFile yamlFile;

    private static final List<TextFile> list = new ArrayList<>();

    public TextFile(Path path, String fileName, String internalName) {
        try {
            if (!Files.exists(path)) Files.createDirectory(path);
            Path configPath = path.resolve(fileName);
            if (!Files.exists(configPath)) {
                try (InputStream in = this.getClass().getResourceAsStream("/" + internalName)) {
                    if (in != null) {
                        Files.copy(in, configPath);
                    }
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
