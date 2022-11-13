package it.frafol.cleanping.bungee.commands;

import it.frafol.cleanping.bungee.enums.BungeeConfig;
import it.frafol.cleanping.bungee.enums.BungeeMessages;
import it.frafol.cleanping.bungee.objects.TextFile;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class ReloadCommand extends Command {

    public ReloadCommand() {

        super("cleanpingreload","","pingreload");

    }

    @Override
    public void execute(CommandSender source, String[] strings) {

        if (!source.hasPermission(BungeeConfig.RELOAD_PERMISSION.get(String.class))) {
            source.sendMessage(new TextComponent(BungeeMessages.NO_PERMISSION.color()
                    .replace("%prefix%", BungeeMessages.PREFIX.color())));
            return;
        }

        TextFile.reloadAll();

        source.sendMessage(new TextComponent(BungeeMessages.RELOADED.color()
                .replace("%prefix%", BungeeMessages.PREFIX.color())));
    }
}
