package it.frafol.cleanping.hytale.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import it.frafol.cleanping.hytale.CleanPing;
import it.frafol.cleanping.hytale.enums.HytaleConfig;
import it.frafol.cleanping.hytale.enums.HytaleMessages;
import it.frafol.cleanping.hytale.objects.Placeholder;
import it.frafol.cleanping.hytale.objects.TextFile;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class ReloadCommand extends AbstractCommand {

    private final CleanPing plugin;

    public ReloadCommand(CleanPing plugin, String name, String description) {
        super(name, description);
        this.plugin = plugin;
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        CommandSender sender = context.sender();

        if (!PermissionsModule.get().hasPermission(sender.getUuid(), HytaleConfig.RELOAD_PERMISSION.get(String.class))) {
            sender.sendMessage(Message.raw(Placeholder.translate(HytaleMessages.NO_PERMISSION.get(String.class))
                    .replace("%prefix%", Placeholder.translate(HytaleMessages.PREFIX.get(String.class)))));
            return CompletableFuture.completedFuture(null);
        }

        TextFile.reloadAll();

        sender.sendMessage(Message.raw(Placeholder.translate(HytaleMessages.RELOADED.get(String.class))
                .replace("%prefix%", Placeholder.translate(HytaleMessages.PREFIX.get(String.class)))));

        return CompletableFuture.completedFuture(null);
    }
}
