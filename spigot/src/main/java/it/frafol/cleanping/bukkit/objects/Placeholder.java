package it.frafol.cleanping.bukkit.objects;

import it.frafol.cleanping.bukkit.enums.SpigotConfig;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Placeholder {

    private final String key;
    private final String value;

    public Placeholder(String key, String value) {
        this.key = "%" + key + "%";this.value = value;
    }

    public static String translate(String message) {

        if (supportsMiniMessage() && SpigotConfig.MINIMESSAGE.get(Boolean.class)) {
            final MiniMessage miniMessage = MiniMessage.miniMessage();
            Component component = miniMessage.deserialize(message);
            return LegacyComponentSerializer.legacySection().serialize(component);
        }

        if (!containsHexColor(message)) {
            return message.replace("&", "§");
        }

        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch) {
                builder.append("&").append(c);
            }

            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }

        return message.replace("&", "§");
    }

    private static boolean containsHexColor(String message) {
        String hexColorPattern = "(?i)&#[a-f0-9]{6}";
        return message.matches(".*" + hexColorPattern + ".*");
    }

    private static boolean supportsMiniMessage() {
        try {
            MiniMessage mm = MiniMessage.miniMessage();
            mm.deserialize("supports");
            return true;
        } catch (Throwable e) {
            return false;
        }
    }
}
