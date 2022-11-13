package it.frafol.cleanping.bukkit;

import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateCheck {

    public final CleanPing PLUGIN;

    public UpdateCheck(CleanPing plugin) {
        this.PLUGIN = plugin;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(PLUGIN, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=105475")
                    .openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                PLUGIN.getLogger().severe("Unable to check for updates: " + exception.getMessage());
            }
        });
    }
}