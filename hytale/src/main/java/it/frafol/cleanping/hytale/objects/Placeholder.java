package it.frafol.cleanping.hytale.objects;

import com.hypixel.hytale.server.core.Message;
import com.wiflow.placeholderapi.WiFlowPlaceholderAPI;
import it.frafol.cleanping.hytale.enums.HytaleMessages;
import it.frafol.cleanping.hytale.hooks.HookInitializer;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Placeholder {

    private static final Map<Character, Color> COLOR_MAP = new HashMap<>();
    private static final Pattern COLOR_PATTERN = Pattern.compile("[&§]([0-9a-fk-or])");

    static {
        COLOR_MAP.put('0', new Color(0x000000));
        COLOR_MAP.put('1', new Color(0x0000AA));
        COLOR_MAP.put('2', new Color(0x00AA00));
        COLOR_MAP.put('3', new Color(0x00AAAA));
        COLOR_MAP.put('4', new Color(0xAA0000));
        COLOR_MAP.put('5', new Color(0xAA00AA));
        COLOR_MAP.put('6', new Color(0xFFAA00));
        COLOR_MAP.put('7', new Color(0xAAAAAA));
        COLOR_MAP.put('8', new Color(0x555555));
        COLOR_MAP.put('9', new Color(0x5555FF));
        COLOR_MAP.put('a', new Color(0x55FF55));
        COLOR_MAP.put('b', new Color(0x55FFFF));
        COLOR_MAP.put('c', new Color(0xFF5555));
        COLOR_MAP.put('d', new Color(0xFF55FF));
        COLOR_MAP.put('e', new Color(0xFFFF55));
        COLOR_MAP.put('f', new Color(0xFFFFFF));
    }
    
    public static String translate(String string) {
        string = string.replace("{prefix}", HytaleMessages.PREFIX.get(String.class));
        return string;
    }

    public static Message format(String text) {
        if (text == null || text.isEmpty()) return Message.raw("");
        List<Message> messages = new ArrayList<>();
        Matcher matcher = COLOR_PATTERN.matcher(text);
        int lastIndex = 0;
        Color currentColor = Color.WHITE;
        boolean bold = false;
        boolean italic = false;
        while (matcher.find()) {
            if (matcher.start() > lastIndex) {
                String textSegment = text.substring(lastIndex, matcher.start());
                if (!textSegment.isEmpty()) {
                    Message msg = Message.raw(textSegment).color(currentColor);
                    if (bold) msg = msg.bold(true);
                    if (italic) msg = msg.italic(true);
                    messages.add(msg);
                }
            }
            char colorCode = matcher.group(1).charAt(0);
            if (COLOR_MAP.containsKey(colorCode)) {
                currentColor = COLOR_MAP.get(colorCode);
            } else if (colorCode == 'r') {
                currentColor = Color.WHITE;
                bold = false;
                italic = false;
            } else if (colorCode == 'l') bold = true;
            else if (colorCode == 'o') italic = true;
            lastIndex = matcher.end();
        }

        if (lastIndex < text.length()) {
            String textSegment = text.substring(lastIndex);
            if (!textSegment.isEmpty()) {
                Message msg = Message.raw(textSegment).color(currentColor);
                if (bold)
                    msg = msg.bold(true);
                if (italic)
                    msg = msg.italic(true);
                messages.add(msg);
            }
        }

        if (messages.isEmpty()) return Message.raw("");
        return Message.join(messages.toArray(new Message[0]));
    }
}
