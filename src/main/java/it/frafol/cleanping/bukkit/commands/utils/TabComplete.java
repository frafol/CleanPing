package it.frafol.cleanping.bukkit.commands.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TabComplete implements TabCompleter {

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }

        return sender.getServer().getOnlinePlayers().stream()
                .filter(player ->
                        !isVanished(player) && player.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    private boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) {
                return true;
            }
        }
        return false;
    }
}
