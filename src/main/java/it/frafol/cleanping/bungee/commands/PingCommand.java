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

import java.util.UUID;

public class PingCommand extends Command {

	public PingCommand() {

		super("cleanping","","ping");

	}


	@Override
	public void execute(CommandSender source, String[] args) {

		if (!(source instanceof ProxiedPlayer)) {
			source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.ONLY_PLAYERS.color()
					.replace("%prefix%", BungeeMessages.PREFIX.color())));
			return;
		}

		final ProxiedPlayer player = (ProxiedPlayer) source;

		if(args.length == 0) {

			final long ping = player.getPing();

			if (source.hasPermission(BungeeConfig.PING_PERMISSION.get(String.class))) {

				if (!(BungeeConfig.DYNAMIC_PING.get(Boolean.class))) {

					source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.PING.color()
							.replace("%prefix%", BungeeMessages.PREFIX.color())
							.replace("%ping%", String.valueOf(player.getPing()))));

					return;

				}

				if (ping < BungeeConfig.MEDIUM_MS.get(Integer.class)) {
					source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.PING.color()
							.replace("%prefix%", BungeeMessages.PREFIX.color())
							.replace("%ping%", BungeeConfig.LOW_MS_COLOR.color() + player.getPing())));

				} else if (ping > BungeeConfig.MEDIUM_MS.get(Integer.class)
						&& ping < BungeeConfig.HIGH_MS.get(Integer.class)) {
					source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.PING.color()
							.replace("%prefix%", BungeeMessages.PREFIX.color())
							.replace("%ping%", BungeeConfig.MEDIUM_MS_COLOR.color() + player.getPing())));

				} else {
					source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.PING.color()
							.replace("%prefix%", BungeeMessages.PREFIX.color())
							.replace("%ping%", BungeeConfig.HIGH_MS_COLOR.color() + player.getPing())));
				}

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
							.replace("%ping%", String.valueOf(ProxyServer.getInstance().getPlayer(args[0]).getPing()))));

					return;

				}

				if (ping < BungeeConfig.MEDIUM_MS.get(Integer.class)) {

					source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.OTHERS_PING.color()
							.replace("%prefix%", BungeeMessages.PREFIX.color())
							.replace("%user%", (args[0]))
							.replace("%ping%", BungeeConfig.LOW_MS_COLOR.color() + ProxyServer.getInstance().getPlayer(args[0]).getPing())));

				} else if (ping > BungeeConfig.MEDIUM_MS.get(Integer.class)
						&& ping < BungeeConfig.HIGH_MS.get(Integer.class)) {

					source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.OTHERS_PING.color()
							.replace("%prefix%", BungeeMessages.PREFIX.color())
							.replace("%user%", (args[0]))
							.replace("%ping%", BungeeConfig.MEDIUM_MS_COLOR.color() + ProxyServer.getInstance().getPlayer(args[0]).getPing())));

				} else {
					source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.OTHERS_PING.color()
							.replace("%prefix%", BungeeMessages.PREFIX.color())
							.replace("%user%", (args[0]))
							.replace("%ping%", BungeeConfig.HIGH_MS_COLOR.color() + ProxyServer.getInstance().getPlayer(args[0]).getPing())));
				}
				
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

				final String send_message = target + ";" + uuid + ";" + redisBungeeAPI.getProxy(uuid) + ";" + player.getUniqueId();
				redisBungeeAPI.sendChannelMessage("CleanPing-Request", send_message);

			}

		} else {

			source.sendMessage(TextComponent.fromLegacyText(BungeeMessages.USAGE.color()
					.replace("%prefix%", BungeeMessages.PREFIX.color())));

		}

	}

}