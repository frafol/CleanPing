package it.frafol.cleanping.velocity.commands;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import it.frafol.cleanping.velocity.CleanPing;
import it.frafol.cleanping.velocity.enums.VelocityConfig;
import it.frafol.cleanping.velocity.enums.VelocityMessages;
import it.frafol.cleanping.velocity.enums.VelocityRedis;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Optional;
import java.util.UUID;

public final class PingCommand  {

    public static void register(ProxyServer proxyServer, CleanPing plugin) {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal("cleanping")
                .requires(src -> src.hasPermission(VelocityConfig.PING_PERMISSION.get(String.class)))
                .executes(ctx -> {

                    if (!(ctx.getSource() instanceof Player)) {
                        ctx.getSource().sendMessage(LegacyComponentSerializer.legacySection()
                                .deserialize(VelocityMessages.USAGE.color()
                                        .replace("%prefix%", VelocityMessages.PREFIX.color())
                                ));
                        return Command.SINGLE_SUCCESS;
                    }

                    final Player player = (Player) ctx.getSource();
                    final long ping = player.getPing();

                    final String color = colorBasedOnPing(ping);

                    player.sendMessage(LegacyComponentSerializer.legacySection()
                            .deserialize(VelocityMessages.PING.color()
                                    .replace("%prefix%", VelocityMessages.PREFIX.color())
                                    .replace("%ping%", color + player.getPing())
                            ));
                    return Command.SINGLE_SUCCESS;
                })
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("player", StringArgumentType.word())
                        .requires(src -> src.hasPermission(VelocityConfig.PING_OTHERS_PERMISSION.get(String.class)))
                        .suggests((ctx, builder) -> {

                            String partialName;

                            try {
                                partialName = ctx.getArgument("player", String.class).toLowerCase();
                            } catch (IllegalArgumentException ignored) {
                                partialName = "";
                            }

                            if (partialName.isEmpty()) {

                                proxyServer.getAllPlayers().stream()
                                        .map(Player::getUsername)
                                        .forEach(builder::suggest);
                                return builder.buildFuture();

                            }

                            String finalPartialName = partialName;
                            proxyServer.getAllPlayers().stream()
                                    .map(Player::getUsername)
                                    .filter(name -> name.toLowerCase().startsWith(finalPartialName))
                                    .forEach(builder::suggest);

                            return builder.buildFuture();

                        })
                        .executes(ctx -> {
                            final String argument = StringArgumentType.getString(ctx, "player");

                            if (VelocityRedis.REDIS.get(Boolean.class) && proxyServer.getPluginManager().isLoaded("redisbungee")) {
                                final RedisBungeeAPI redisBungeeAPI = RedisBungeeAPI.getRedisBungeeApi();

                                final UUID uuid = redisBungeeAPI.getUuidFromName(argument);
                                if (uuid == null) {
                                    return -1;
                                }

                                if (!redisBungeeAPI.isPlayerOnline(uuid)) {
                                    ctx.getSource().sendMessage(LegacyComponentSerializer.legacy('§')
                                            .deserialize(VelocityMessages.NOT_ONLINE.color()
                                                    .replace("%prefix%", VelocityMessages.PREFIX.color())
                                                    .replace("%user%", argument)
                                            ));

                                    return Command.SINGLE_SUCCESS;
                                }

                                if (!(ctx.getSource() instanceof Player)) {
                                    return Command.SINGLE_SUCCESS;
                                }

                                final Player player = (Player) ctx.getSource();
                                final String send_message = argument + ";" + uuid + ";" + redisBungeeAPI.getProxy(uuid) + ";" + player.getUniqueId();
                                redisBungeeAPI.sendChannelMessage("CleanPing-Request", send_message);
                                return Command.SINGLE_SUCCESS;
                            }

                            final Optional<Player> optionalTarget = proxyServer.getPlayer(argument);
                            if (optionalTarget.isPresent()) {
                                final Player target = optionalTarget.get();

                                if (!(VelocityConfig.OTHERS_PING_OPTION.get(Boolean.class))) {
                                    ctx.getSource().sendMessage(LegacyComponentSerializer.legacySection()
                                            .deserialize(VelocityMessages.USAGE.color()
                                                    .replace("%prefix%", VelocityMessages.PREFIX.color())
                                            ));

                                    return Command.SINGLE_SUCCESS;
                                }

                                final long ping = target.getPing();

                                if (!(VelocityConfig.DYNAMIC_PING.get(Boolean.class))) {
                                    ctx.getSource().sendMessage(LegacyComponentSerializer.legacySection()
                                            .deserialize(VelocityMessages.OTHERS_PING.color()
                                                    .replace("%prefix%", VelocityMessages.PREFIX.color())
                                                    .replace("%user%", argument)
                                                    .replace("%ping%", Long.toString(target.getPing()))
                                            ));

                                    return Command.SINGLE_SUCCESS;
                                }

                                final String color = colorBasedOnPing(ping);

                                ctx.getSource().sendMessage(LegacyComponentSerializer.legacySection()
                                        .deserialize(VelocityMessages.OTHERS_PING.color()
                                                .replace("%prefix%", VelocityMessages.PREFIX.color())
                                                .replace("%user%", argument)
                                                .replace("%ping%", color + target.getPing())
                                        ));
                            } else {
                                ctx.getSource().sendMessage(LegacyComponentSerializer.legacySection()
                                        .deserialize(VelocityMessages.NOT_ONLINE.color()
                                                .replace("%prefix%", VelocityMessages.PREFIX.color())
                                                .replace("%user%", argument)
                                        ));

                                return -1;
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                ).build();
        final BrigadierCommand command = new BrigadierCommand(node);
        final CommandMeta meta = proxyServer.getCommandManager().metaBuilder(command)
                .aliases("ping").plugin(plugin).build();
        proxyServer.getCommandManager().register(meta, command);
	}

    private static String colorBasedOnPing(long ping) {
        if (ping < VelocityConfig.MEDIUM_MS.get(Integer.class)) {
            return VelocityConfig.LOW_MS_COLOR.color();
        } else if (ping > VelocityConfig.MEDIUM_MS.get(Integer.class) && ping < VelocityConfig.HIGH_MS.get(Integer.class)) {
            return VelocityConfig.MEDIUM_MS_COLOR.color();
        } else {
            return VelocityConfig.HIGH_MS_COLOR.color();
        }
    }
}