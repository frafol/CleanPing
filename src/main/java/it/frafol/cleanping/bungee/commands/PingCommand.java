package it.frafol.cleanping.bungee.commands;

import it.frafol.cleanping.bungee.enums.BungeeConfig;
import it.frafol.cleanping.bungee.enums.BungeeMessages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PingCommand extends Command {

	public PingCommand() {

		super("cleanping","","ping");

	}


	@Override
	public void execute(CommandSender source, String[] args) {

		if(args.length == 0) {

			if(!(source instanceof ProxiedPlayer)) {
				source.sendMessage(new TextComponent(BungeeMessages.ONLY_PLAYERS.color()
						.replace("%prefix%", BungeeMessages.PREFIX.color())));
				return;
			}

			final ProxiedPlayer player = (ProxiedPlayer) source;
			final long ping = player.getPing();

			if (source.hasPermission(BungeeConfig.PING_PERMISSION.get(String.class))) {

				if (ping < BungeeConfig.MEDIUM_MS.get(Integer.class)) {
					source.sendMessage(new TextComponent(BungeeMessages.PING.color()
							.replace("%prefix%", BungeeMessages.PREFIX.color())
							.replace("%ping%", BungeeConfig.LOW_MS_COLOR.color() + player.getPing())));

				} else if (ping > BungeeConfig.MEDIUM_MS.get(Integer.class)
						&& ping < BungeeConfig.HIGH_MS.get(Integer.class)) {
					source.sendMessage(new TextComponent(BungeeMessages.PING.color()
							.replace("%prefix%", BungeeMessages.PREFIX.color())
							.replace("%ping%", BungeeConfig.MEDIUM_MS_COLOR.color() + player.getPing())));

				} else {
					source.sendMessage(new TextComponent(BungeeMessages.PING.color()
							.replace("%prefix%", BungeeMessages.PREFIX.color())
							.replace("%ping%", BungeeConfig.HIGH_MS_COLOR.color() + player.getPing())));
				}

			} else {
				source.sendMessage(new TextComponent(BungeeMessages.NO_PERMISSION.color()
						.replace("%prefix%", BungeeMessages.PREFIX.color())));
			}

		} else if (args.length == 1) {

			if (!source.hasPermission(BungeeConfig.PING_OTHERS_PERMISSION.get(String.class))) {
				source.sendMessage(new TextComponent(BungeeMessages.NO_PERMISSION.color()
						.replace("%prefix%", BungeeMessages.PREFIX.color())));
				return;
			}


			if (ProxyServer.getInstance().getPlayer(args[0]) == null) {
				source.sendMessage(new TextComponent(BungeeMessages.NOT_ONLINE.color()
						.replace("%prefix%", BungeeMessages.PREFIX.color())
						.replace("%user%", (args[0]))));
				return;
			}

			if (!(BungeeConfig.OTHERS_PING_OPTION.get(Boolean.class))) {
				source.sendMessage(new TextComponent(BungeeMessages.USAGE.color()
						.replace("%prefix%", BungeeMessages.PREFIX.color())));
				return;
			}

			final long ping = ProxyServer.getInstance().getPlayer(args[0]).getPing();

			if (!(BungeeConfig.DYNAMIC_PING.get(Boolean.class))) {
				source.sendMessage(new TextComponent(BungeeMessages.OTHERS_PING.color()
						.replace("%prefix%", BungeeMessages.PREFIX.color())
						.replace("%user%", (args[0]))
						.replace("%ping%", "" + ProxyServer.getInstance().getPlayer(args[0]).getPing())));
			}

			if (ping < BungeeConfig.MEDIUM_MS.get(Integer.class)) {
				source.sendMessage(new TextComponent(BungeeMessages.OTHERS_PING.color()
						.replace("%prefix%", BungeeMessages.PREFIX.color())
						.replace("%user%", (args[0]))
						.replace("%ping%", BungeeConfig.LOW_MS_COLOR.color() + ProxyServer.getInstance().getPlayer(args[0]).getPing())));

			} else if (ping > BungeeConfig.MEDIUM_MS.get(Integer.class)
					&& ping < BungeeConfig.HIGH_MS.get(Integer.class)) {
				source.sendMessage(new TextComponent(BungeeMessages.OTHERS_PING.color()
						.replace("%prefix%", BungeeMessages.PREFIX.color())
						.replace("%user%", (args[0]))
						.replace("%ping%", BungeeConfig.MEDIUM_MS_COLOR.color() + ProxyServer.getInstance().getPlayer(args[0]).getPing())));

			} else {
				source.sendMessage(new TextComponent(BungeeMessages.OTHERS_PING.color()
						.replace("%prefix%", BungeeMessages.PREFIX.color())
						.replace("%user%", (args[0]))
						.replace("%ping%", BungeeConfig.HIGH_MS_COLOR.color() + ProxyServer.getInstance().getPlayer(args[0]).getPing())));
			}

		} else {
			source.sendMessage(new TextComponent(BungeeMessages.USAGE.color()
					.replace("%prefix%", BungeeMessages.PREFIX.color())));

		}

	}

}