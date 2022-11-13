package it.frafol.cleanping.velocity;

import com.velocitypowered.api.event.Subscribe;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateCheck {

    public CleanPing PLUGIN;

    public static final CleanPing instance = CleanPing.getInstance();

    public UpdateCheck(CleanPing plugin) {
        this.PLUGIN = plugin;
    }

    @Subscribe
    public void getVersion(final Consumer<String> consumer) {
        instance.getServer().getScheduler().buildTask(PLUGIN, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=105475")
                    .openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                PLUGIN.getLogger().error("Unable to check for updates: " + exception.getMessage());
            }
        }).schedule();
    }
}