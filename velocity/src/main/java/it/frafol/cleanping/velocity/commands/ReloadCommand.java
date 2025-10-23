package it.frafol.cleanping.velocity.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import it.frafol.cleanping.velocity.CleanPing;
import it.frafol.cleanping.velocity.enums.VelocityConfig;
import it.frafol.cleanping.velocity.enums.VelocityMessages;
import it.frafol.cleanping.velocity.objects.TextFile;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class ReloadCommand {

    public static void register(ProxyServer proxyServer, CleanPing plugin) {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal("pingreload")
                .requires(src -> src.hasPermission(VelocityConfig.RELOAD_PERMISSION.get(String.class)))
                .executes(ctx -> {
                    TextFile.reloadAll();
                    ctx.getSource().sendMessage(LegacyComponentSerializer.legacySection()
                            .deserialize(VelocityMessages.RELOADED.color()
                                .replace("%prefix%", VelocityMessages.PREFIX.color())
                            ));
                    return Command.SINGLE_SUCCESS;
                }).build();
        final BrigadierCommand command = new BrigadierCommand(node);
        final CommandMeta meta = proxyServer.getCommandManager().metaBuilder(command)
                .aliases("cleanpingreload").plugin(plugin).build();
        proxyServer.getCommandManager().register(meta, command);
    }
}
