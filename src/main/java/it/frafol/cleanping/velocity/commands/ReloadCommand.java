package it.frafol.cleanping.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import it.frafol.cleanping.velocity.CleanPing;
import it.frafol.cleanping.velocity.enums.VelocityConfig;
import it.frafol.cleanping.velocity.enums.VelocityMessages;
import it.frafol.cleanping.velocity.objects.TextFile;
import net.kyori.adventure.text.Component;

public class ReloadCommand implements SimpleCommand {

    public final CleanPing PLUGIN;

    public ReloadCommand(CleanPing plugin) {
        this.PLUGIN = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if (!source.hasPermission(VelocityConfig.RELOAD_PERMISSION.get(String.class))) {
            source.sendMessage(Component.text(VelocityMessages.NO_PERMISSION.color()
                    .replace("%prefix%", VelocityMessages.PREFIX.color())));
            return;
        }

        TextFile.reloadAll();
        source.sendMessage(Component.text(VelocityMessages.RELOADED.color()
                .replace("%prefix%", VelocityMessages.PREFIX.color())));
    }
}
