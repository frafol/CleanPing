package it.frafol.cleanping.bukkit.commands;

import it.frafol.cleanping.bukkit.CleanPing;
import it.frafol.cleanping.bukkit.enums.SpigotConfig;
import it.frafol.cleanping.bukkit.enums.SpigotMessages;
import it.frafol.cleanping.bukkit.objects.Placeholder;
import it.frafol.cleanping.bukkit.objects.TextFile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

    public final CleanPing plugin;

    public ReloadCommand(CleanPing plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender source, @NotNull Command command, @NotNull String s, String[] args) {

        if (!source.hasPermission(SpigotConfig.RELOAD_PERMISSION.get(String.class))) {
            source.sendMessage(Placeholder.translate(SpigotMessages.NO_PERMISSION.get(String.class))
                    .replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class))));
            return false;
        }

        TextFile.reloadAll();

        source.sendMessage(Placeholder.translate(SpigotMessages.RELOADED.get(String.class))
                .replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class))));

        return false;
    }
}
