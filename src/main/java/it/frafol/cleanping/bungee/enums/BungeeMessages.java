package it.frafol.cleanping.bungee.enums;

import it.frafol.cleanping.bungee.CleanPing;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum BungeeMessages {

    PREFIX("messages.prefix"),

    USAGE("messages.usage"),

    ONLY_PLAYERS("messages.only_players"),
    NOT_ONLINE("messages.not_online"),

    NO_PERMISSION("messages.no_permission"),

    PING("messages.ping"),
    OTHERS_PING("messages.others_ping"),
    PING_DIFFERENCE("messages.difference_ping"),

    LAGGING("messages.lagging"),

    RELOADED("messages.reloaded");

    private final String path;
    public static final CleanPing instance = CleanPing.getInstance();

    BungeeMessages(String path) {
        this.path = path;
    }

    public <T> T get(@NotNull Class<T> clazz) {
        return clazz.cast(instance.getMessagesTextFile().get(path));
    }

    public String color() {
        String hex = convertHexColors(get(String.class));
        return hex.replace("&", "§");
    }

    public static String convertHexColors(String str) {
        Pattern unicode = Pattern.compile("\\\\u\\+[a-fA-F0-9]{4}");
        Matcher match = unicode.matcher(str);
        while (match.find()) {
            String code = str.substring(match.start(),match.end());
            str = str.replace(code,Character.toString((char) Integer.parseInt(code.replace("\\u+",""),16)));
            match = unicode.matcher(str);
        }
        Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
        match = pattern.matcher(str);
        while (match.find()) {
            String color = str.substring(match.start(),match.end());
            str = str.replace(color, ChatColor.of(color.replace("&","")) + "");
            match = pattern.matcher(str);
        }
        return ChatColor.translateAlternateColorCodes('&',str);
    }
}
