package it.frafol.cleanping.bukkit.objects;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Placeholder { private final String key;private final String value;

    public Placeholder(String key, String value) {
        this.key = "%" + key + "%";this.value = value;
    }

    public static @NotNull String translate(String message) {

        if (Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_16_R")
                || Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_17_R")
                || Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_18_R")
                || Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_19_R")) {

            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);

            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                message = message.replace(color, ChatColor.of(color) + "");
                matcher = pattern.matcher(message);
            }
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
