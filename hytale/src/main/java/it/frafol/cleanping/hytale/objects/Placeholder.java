package it.frafol.cleanping.hytale.objects;

import com.hypixel.hytale.server.core.Message;
import it.frafol.cleanping.hytale.enums.HytaleMessages;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.awt.*;
import java.util.*;

public class Placeholder {

    private static final Map<Character, Color> COLOR_MAP = new HashMap<>();
    private static final Pattern PATTERN = Pattern.compile(
            "[&§]([0-9a-fk-or])|" +
                    "[&§]#([A-Fa-f0-9]{6})|" +
                    "<#([A-Fa-f0-9]{6})>|" +
                    "</#([A-Fa-f0-9]{6})>|" +
                    "<(bold|italic|reset|b|i|/bold|/italic|/b|/i)>|" +
                    "<gradient:#([A-Fa-f0-9]{6}):#([A-Fa-f0-9]{6})>|" +
                    "(</gradient>)"
    );

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
        Matcher matcher = PATTERN.matcher(text);
        int lastIndex = 0;

        Deque<Color> colorStack = new ArrayDeque<>();
        colorStack.push(Color.WHITE);

        Color gradientStart = null;
        Color gradientEnd = null;
        boolean inGradient = false;

        boolean bold = false;
        boolean italic = false;

        while (matcher.find()) {
            if (matcher.start() > lastIndex) {
                String content = text.substring(lastIndex, matcher.start());
                if (inGradient) {
                    messages.add(applyGradient(content, gradientStart, gradientEnd, bold, italic));
                } else {
                    messages.add(applyStyles(content, colorStack.peek(), bold, italic));
                }
            }

            if (matcher.group(1) != null) {
                char code = matcher.group(1).charAt(0);
                if (COLOR_MAP.containsKey(code)) {
                    colorStack.pop();
                    colorStack.push(COLOR_MAP.get(code));
                    inGradient = false;
                } else if (code == 'r') {
                    resetState(colorStack);
                    bold = false; italic = false; inGradient = false;
                } else if (code == 'l') bold = true;
                else if (code == 'o') italic = true;
            }
            else if (matcher.group(2) != null || matcher.group(3) != null) {
                String hex = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
                colorStack.push(new Color(Integer.parseInt(hex, 16)));
                inGradient = false;
            }
            else if (matcher.group(4) != null) {
                if (colorStack.size() > 1) colorStack.pop();
            }
            else if (matcher.group(5) != null) {
                String tag = matcher.group(5).toLowerCase();
                switch (tag) {
                    case "bold": case "b": bold = true; break;
                    case "/bold": case "/b": bold = false; break;
                    case "italic": case "i": italic = true; break;
                    case "/italic": case "/i": italic = false; break;
                    case "reset":
                        resetState(colorStack);
                        bold = false; italic = false; inGradient = false;
                        break;
                }
            }

            else if (matcher.group(6) != null && matcher.group(7) != null) {
                gradientStart = new Color(Integer.parseInt(matcher.group(6), 16));
                gradientEnd = new Color(Integer.parseInt(matcher.group(7), 16));
                inGradient = true;
            }

            else if (matcher.group(8) != null) {
                inGradient = false;
            }

            lastIndex = matcher.end();
        }

        if (lastIndex < text.length()) {
            String content = text.substring(lastIndex);
            if (inGradient) {
                messages.add(applyGradient(content, gradientStart, gradientEnd, bold, italic));
            } else {
                messages.add(applyStyles(content, colorStack.peek(), bold, italic));
            }
        }

        return messages.isEmpty() ? Message.raw("") : Message.join(messages.toArray(new Message[0]));
    }

    private static void resetState(Deque<Color> stack) {
        stack.clear();
        stack.push(Color.WHITE);
    }

    private static Message applyStyles(String content, Color color, boolean bold, boolean italic) {
        Message msg = Message.raw(content).color(color);
        if (bold) msg = msg.bold(true);
        if (italic) msg = msg.italic(true);
        return msg;
    }

    private static Message applyGradient(String content, Color start, Color end, boolean bold, boolean italic) {
        List<Message> gradientParts = new ArrayList<>();
        int length = content.length();

        for (int i = 0; i < length; i++) {
            float ratio = (length > 1) ? (float) i / (length - 1) : 0;
            Color interpolated = interpolate(start, end, ratio);
            gradientParts.add(applyStyles(String.valueOf(content.charAt(i)), interpolated, bold, italic));
        }

        return Message.join(gradientParts.toArray(new Message[0]));
    }

    private static Color interpolate(Color start, Color end, float ratio) {
        int r = (int) (start.getRed() + (end.getRed() - start.getRed()) * ratio);
        int g = (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * ratio);
        int b = (int) (start.getBlue() + (end.getBlue() - start.getBlue()) * ratio);
        return new Color(r, g, b);
    }
}
