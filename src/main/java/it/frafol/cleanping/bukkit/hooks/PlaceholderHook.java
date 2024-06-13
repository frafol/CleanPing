package it.frafol.cleanping.bukkit.hooks;

import it.frafol.cleanping.bukkit.CleanPing;
import it.frafol.cleanping.bukkit.enums.SpigotConfig;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaceholderHook extends PlaceholderExpansion {

    public final CleanPing plugin;

    public PlaceholderHook(CleanPing plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "cleanping";
    }

    @Override
    public String getAuthor() {
        return "frafol";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String placeholder) {

        if (placeholder.equalsIgnoreCase("ping")) {

            if (player.getPlayer() == null || !player.getPlayer().isOnline()) {
                return "";
            }

            if (CleanPing.hasGetPingMethod()) {
                return player.getPlayer().getPing() + "";
            }

            return plugin.getPing(player.getPlayer()) + "";
        }

        if (placeholder.equalsIgnoreCase("coloured_ping")) {

            if (player.getPlayer() == null || !player.getPlayer().isOnline()) {
                return "";
            }

            if (CleanPing.hasGetPingMethod()) {
                return colorBasedOnPing(player.getPlayer().getPing()) + player.getPlayer().getPing();
            }

            return colorBasedOnPing(plugin.getPing(player.getPlayer())) + plugin.getPing(player.getPlayer());
        }

        for (Player players : plugin.getServer().getOnlinePlayers()) {

            if (player == null || !player.isOnline() || player.getPlayer() == null) {
                return "";
            }

            if (placeholder.equalsIgnoreCase("ping_" + players.getName())) {

                if (!players.isOnline()) {
                    return "";
                }

                if (CleanPing.hasGetPingMethod()) {
                    return players.getPing() + "";
                }

                return plugin.getPing(players) + "";
            }

            if (placeholder.equalsIgnoreCase("coloured_ping_" + players.getName())) {

                if (!players.isOnline()) {
                    return "";
                }

                if (CleanPing.hasGetPingMethod()) {
                    return colorBasedOnPing(players.getPing()) + players.getPing();
                }

                return colorBasedOnPing(plugin.getPing(players)) + plugin.getPing(players);
            }

            if (placeholder.equalsIgnoreCase("difference_" + players.getName())) {

                if (!players.isOnline()) {
                    return "";
                }

                if (CleanPing.hasGetPingMethod()) {
                    return getDifference(players.getPing(), player.getPlayer().getPing()) + "";
                }

                return getDifference(plugin.getPing(players), plugin.getPing(player.getPlayer())) + "";
            }

            for (Player players2 : plugin.getServer().getOnlinePlayers()) {

                if (placeholder.equalsIgnoreCase("difference_" + players.getName() + "_" + players2.getName())) {

                    if (!players.isOnline() || !players2.isOnline()) {
                        return "";
                    }

                    if (CleanPing.hasGetPingMethod()) {
                        return getDifference(players.getPing(), players2.getPing()) + "";
                    }

                    return getDifference(plugin.getPing(players), plugin.getPing(players2)) + "";
                }
            }
        }

        return null;
    }

    private static String colorBasedOnPing(long ping) {
        if (ping < SpigotConfig.MEDIUM_MS.get(Integer.class)) {
            return SpigotConfig.LOW_MS_COLOR.color();
        } else if (ping > SpigotConfig.MEDIUM_MS.get(Integer.class) && ping < SpigotConfig.HIGH_MS.get(Integer.class)) {
            return SpigotConfig.MEDIUM_MS_COLOR.color();
        } else {
            return SpigotConfig.HIGH_MS_COLOR.color();
        }
    }

    private int getDifference(long ping1, long ping2) {
        if (ping1 > ping2) {
            return (int) Math.abs(ping1 - ping2);
        }
        return (int) Math.abs(ping2 - ping1);
    }
}