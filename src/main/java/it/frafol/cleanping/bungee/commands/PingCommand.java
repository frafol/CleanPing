package it.frafol.cleanping.bungee.commands;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import it.frafol.cleanping.bungee.enums.BungeeConfig;
import it.frafol.cleanping.bungee.enums.BungeeMessages;
import it.frafol.cleanping.bungee.enums.BungeeRedis;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PingCommand extends Command implements TabExecutor {

	public PingCommand() {
		super("cleanping","","ping");
	}

	@Override
	public void execute(CommandSender source, String[] args) {

		if (args.length == 0) {

			if (!(source instanceof ProxiedPlayer)) {
				source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.ONLY_PLAYERS.color()
						.replace("%prefix%", BungeeMessages.PREFIX.color())));
				return;
			}

			final ProxiedPlayer player = (ProxiedPlayer) source;
			final long ping = player.getPing();

			if (source.hasPermission(BungeeConfig.PING_PERMISSION.get(String.class))) {

				if (!(BungeeConfig.DYNAMIC_PING.get(Boolean.class))) {
					source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.PING.color()
							.replace("%prefix%", BungeeMessages.PREFIX.color())
							.replace("%ping%", String.valueOf(ping))));
					return;
				}

				source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.PING.color()
						.replace("%prefix%", BungeeMessages.PREFIX.color())
						.replace("%ping%", colorBasedOnPing(ping) + ping)));

			} else {
				source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.NO_PERMISSION.color()
						.replace("%prefix%", BungeeMessages.PREFIX.color())));
			}

		} else if (args.length == 1) {

			if (!(BungeeRedis.REDIS.get(Boolean.class) || ProxyServer.getInstance().getPluginManager().getPlugin("RedisBungee") != null)
					|| ProxyServer.getInstance().getPlayer(args[0]) != null) {

				if (!source.hasPermission(BungeeConfig.PING_OTHERS_PERMISSION.get(String.class))) {
					source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.NO_PERMISSION.color()
							.replace("%prefix%", BungeeMessages.PREFIX.color())));
					return;
				}

				if (ProxyServer.getInstance().getPlayer(args[0]) == null) {
					source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.NOT_ONLINE.color()
							.replace("%prefix%", BungeeMessages.PREFIX.color())
							.replace("%user%", (args[0]))));
					return;
				}

				if (!(BungeeConfig.OTHERS_PING_OPTION.get(Boolean.class))) {
					source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.USAGE.color()
							.replace("%prefix%", BungeeMessages.PREFIX.color())));
					return;
				}

				final long ping = ProxyServer.getInstance().getPlayer(args[0]).getPing();

				if (!(BungeeConfig.DYNAMIC_PING.get(Boolean.class))) {
					source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.OTHERS_PING.color()
							.replace("%prefix%", BungeeMessages.PREFIX.color())
							.replace("%user%", (args[0]))
							.replace("%ping%", String.valueOf(ping))));
					return;
				}

				source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.OTHERS_PING.color()
						.replace("%prefix%", BungeeMessages.PREFIX.color())
						.replace("%user%", (args[0]))
						.replace("%ping%", colorBasedOnPing(ping) + ping)));

			} else {

				if (!source.hasPermission(BungeeConfig.PING_OTHERS_PERMISSION.get(String.class))) {
					source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.NO_PERMISSION.color()
							.replace("%prefix%", BungeeMessages.PREFIX.color())));
					return;
				}

				final RedisBungeeAPI redisBungeeAPI = RedisBungeeAPI.getRedisBungeeApi();
				final String target = args[0];

				if (redisBungeeAPI.getUuidFromName(target) == null) {
					return;
				}

				final UUID uuid = redisBungeeAPI.getUuidFromName(target);

				if (!redisBungeeAPI.isPlayerOnline(uuid)) {
					source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.NOT_ONLINE.color()
							.replace("%prefix%", BungeeMessages.PREFIX.color())
							.replace("%user%", (args[0]))));
					return;
				}

				if (!(source instanceof ProxiedPlayer)) {
					return;
				}

				final ProxiedPlayer player = (ProxiedPlayer) source;
				final String send_message = target + ";" + uuid + ";" + redisBungeeAPI.getProxy(uuid) + ";" + player.getUniqueId();
				redisBungeeAPI.sendChannelMessage("CleanPing-Request", send_message);
			}

		} else if (args.length == 2) {

			if (!source.hasPermission(BungeeConfig.DIFFERENCE_PING_PERMISSION.get(String.class))) {
				source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.NO_PERMISSION.color()
						.replace("%prefix%", BungeeMessages.PREFIX.color())));
				return;
			}

			if (ProxyServer.getInstance().getPlayer(args[0]) == null) {
				source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.NOT_ONLINE.color()
						.replace("%prefix%", BungeeMessages.PREFIX.color())
						.replace("%user%", (args[0]))));
				return;
			}

			if (ProxyServer.getInstance().getPlayer(args[1]) == null) {
				source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.NOT_ONLINE.color()
						.replace("%prefix%", BungeeMessages.PREFIX.color())
						.replace("%user%", (args[0]))));
				return;
			}

			if (!(BungeeConfig.DIFFERENCE_PING_OPTION.get(Boolean.class))) {
				source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.USAGE.color()
						.replace("%prefix%", BungeeMessages.PREFIX.color())));
				return;
			}

			final long ping1 = ProxyServer.getInstance().getPlayer(args[0]).getPing();
			final long ping2 = ProxyServer.getInstance().getPlayer(args[1]).getPing();

			source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.PING_DIFFERENCE.color()
					.replace("%prefix%", BungeeMessages.PREFIX.color())
					.replace("%arg1%", (args[0]))
					.replace("%arg2%", (args[1]))
					.replace("%difference%", getDifference(ping1, ping2).toString())));
		} else {
			source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.USAGE.color()
					.replace("%prefix%", BungeeMessages.PREFIX.color())));
		}

	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String @NotNull [] args) {

		if (args.length != 1) {
			return Collections.emptyList();
		}

		String partialName = args[0].toLowerCase();

		List<String> completions = new ArrayList<>();
		for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
			if (player.getName().toLowerCase().startsWith(partialName)) {
				completions.add(player.getName());
			}
		}

		return completions;
	}

	private static String colorBasedOnPing(long ping) {
		if (ping < BungeeConfig.MEDIUM_MS.get(Integer.class)) {
			return BungeeConfig.LOW_MS_COLOR.color();
		} else if (ping > BungeeConfig.MEDIUM_MS.get(Integer.class) && ping < BungeeConfig.HIGH_MS.get(Integer.class)) {
			return BungeeConfig.MEDIUM_MS_COLOR.color();
		} else {
			return BungeeConfig.HIGH_MS_COLOR.color();
		}
	}

	private Integer getDifference(long ping1, long ping2) {
		if (ping1 > ping2) {
			return (int) Math.abs(ping1 - ping2);
		}
		return (int) Math.abs(ping2 - ping1);
	}
}