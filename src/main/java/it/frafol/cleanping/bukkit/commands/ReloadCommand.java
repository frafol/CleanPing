package it.frafol.cleanping.bukkit.commands;

import it.frafol.cleanping.bukkit.CleanPing;
import it.frafol.cleanping.bukkit.enums.SpigotConfig;
import it.frafol.cleanping.bukkit.enums.SpigotMessages;
import it.frafol.cleanping.bukkit.objects.TextFile;
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
            source.sendMessage(SpigotMessages.NO_PERMISSION.color()
                    .replace("%prefix%", SpigotMessages.PREFIX.color()));
            return false;
        }

        TextFile.reloadAll();

        source.sendMessage(SpigotMessages.RELOADED.color()
                .replace("%prefix%", SpigotMessages.PREFIX.color()));

        return false;
    }
}
