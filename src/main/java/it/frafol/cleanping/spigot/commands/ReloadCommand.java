package it.frafol.cleanping.spigot.commands;

import it.frafol.cleanping.spigot.CleanPing;
import it.frafol.cleanping.spigot.enums.SpigotConfig;
import it.frafol.cleanping.spigot.objects.TextFile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

    public final CleanPing plugin;

    public ReloadCommand(CleanPing plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(org.bukkit.command.@NotNull CommandSender source, @NotNull Command command, @NotNull String s, String[] args) {

        if (!source.hasPermission(SpigotConfig.RELOAD_PERMISSION.get(String.class))) {
            source.sendMessage(SpigotConfig.NO_PERMISSION.color()
                    .replace("%prefix%", SpigotConfig.PREFIX.color()));
            return false;
        }

        TextFile.reloadAll();
        source.sendMessage(SpigotConfig.RELOADED.color()
                .replace("%prefix%", SpigotConfig.PREFIX.color()));

        return false;
    }
}
