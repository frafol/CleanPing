package it.frafol.cleanping.hytale.hooks;

import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.common.semver.SemverRange;
import com.hypixel.hytale.server.core.HytaleServer;
import com.wiflow.placeholderapi.WiFlowPlaceholderAPI;
import com.wiflow.placeholderapi.expansion.PlaceholderExpansion;
import it.frafol.cleanping.hytale.CleanPing;

public class HookInitializer {

    public static void initializeHooks(CleanPing plugin) {
        if (isWiPlaceholderAPI()) {
            PlaceholderExpansion expansion = new WiPlaceholderHook(plugin);
            WiFlowPlaceholderAPI.registerExpansion(expansion);
        }
        if (isPlaceholderAPI()) {
            new PlaceholderHook(plugin).register();
        }
    }

    public static boolean isWiPlaceholderAPI() {
        return HytaleServer.get().getPluginManager().hasPlugin(PluginIdentifier.fromString("com.wiflow:WiFlowPlaceholderAPI"), SemverRange.fromString("*"))
                && WiFlowPlaceholderAPI.isInitialized();
    }

    public static boolean isPlaceholderAPI() {
        return HytaleServer.get().getPluginManager().hasPlugin(PluginIdentifier.fromString("HelpChat:PlaceholderAPI"), SemverRange.fromString("*"));
    }
}
