package it.frafol.cleanping.bukkit.commands.utils;

import it.frafol.cleanping.bukkit.CleanPing;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter {

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {

        final List<String> list = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("ping")
                || command.getName().equalsIgnoreCase("cleanping")) {

            for (Player players : CleanPing.getInstance().getServer().getOnlinePlayers()) {
                list.add(players.getName());
            }

        }
        return list;
    }
}
