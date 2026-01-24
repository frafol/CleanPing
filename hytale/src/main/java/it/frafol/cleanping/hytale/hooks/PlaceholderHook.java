package it.frafol.cleanping.hytale.hooks;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.wiflow.placeholderapi.context.PlaceholderContext;
import com.wiflow.placeholderapi.expansion.PlaceholderExpansion;
import it.frafol.cleanping.hytale.CleanPing;
import it.frafol.cleanping.hytale.enums.HytaleConfig;

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
    public String onPlaceholderRequest(PlaceholderContext placeholderContext, String placeholder) {

        PlayerRef player = Universe.get().getPlayer(placeholderContext.getPlayerUuid());
        if (player == null) return null;
        if (placeholder.equalsIgnoreCase("ping")) {
            return plugin.getPing(player) + "";
        }

        if (placeholder.equalsIgnoreCase("coloured_ping")) {
            return colorBasedOnPing(plugin.getPing(player)) + plugin.getPing(player);
        }

        for (PlayerRef players : Universe.get().getPlayers()) {

            if (!player.isValid()) return null;
            if (placeholder.equalsIgnoreCase("ping_" + players.getUsername())) {
                return plugin.getPing(players) + "";
            }

            if (placeholder.equalsIgnoreCase("coloured_ping_" + players.getUsername())) {
                return colorBasedOnPing(plugin.getPing(players)) + plugin.getPing(players);
            }

            if (placeholder.equalsIgnoreCase("difference_" + players.getUsername())) {
                return getDifference(plugin.getPing(players), plugin.getPing(player)) + "";
            }

            for (PlayerRef players2 : Universe.get().getPlayers()) {
                if (!players.isValid()) return null;
                if (placeholder.equalsIgnoreCase("difference_" + players.getUsername() + "_" + players2.getUsername())) {
                    return getDifference(plugin.getPing(players), plugin.getPing(players2)) + "";
                }
            }

            return null;
        }
        return null;
    }

    private static String colorBasedOnPing(long ping) {
        if (ping < HytaleConfig.MEDIUM_MS.get(Integer.class)) {
            return HytaleConfig.LOW_MS_COLOR.color();
        } else if (ping > HytaleConfig.MEDIUM_MS.get(Integer.class) && ping < HytaleConfig.HIGH_MS.get(Integer.class)) {
            return HytaleConfig.MEDIUM_MS_COLOR.color();
        } else {
            return HytaleConfig.HIGH_MS_COLOR.color();
        }
    }

    private int getDifference(long ping1, long ping2) {
        if (ping1 > ping2) {
            return (int) Math.abs(ping1 - ping2);
        }
        return (int) Math.abs(ping2 - ping1);
    }
}
