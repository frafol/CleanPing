package it.frafol.cleanping.hytale.objects;

import it.frafol.cleanping.hytale.enums.HytaleMessages;
import lombok.Getter;

@Getter
public class Placeholder {

    public static String translate(String string) {
        string = string.replace("{prefix}", HytaleMessages.PREFIX.get(String.class));
        return color(string);
    }

    public static String color(String input) {
        // TODO: Message colors
        return input;
    }
}
