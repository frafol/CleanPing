package it.frafol.cleanping.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import it.frafol.cleanping.velocity.enums.VelocityConfig;
import net.kyori.adventure.text.Component;

import java.util.Optional;

public class PingCommand implements SimpleCommand {

	private final ProxyServer proxyServer;

	public PingCommand(ProxyServer server) {
		this.proxyServer = server;
	}

	@Override
	public void execute(SimpleCommand.Invocation invocation) {

		final CommandSource source = invocation.source();

		if(invocation.arguments().length == 0) {

			if(!(source instanceof Player)) {
				source.sendMessage(Component.text(VelocityConfig.ONLY_PLAYERS.color()
						.replace("%prefix%", VelocityConfig.PREFIX.color())));
				return;
			}

			final Player player = (Player) source;
			final long ping = player.getPing();

			if (source.hasPermission(VelocityConfig.PING_PERMISSION.get(String.class))) {

				if (ping < VelocityConfig.MEDIUM_MS.get(Integer.class)) {
					source.sendMessage(Component.text(VelocityConfig.PING.color()
							.replace("%prefix%", VelocityConfig.PREFIX.color())
							.replace("%ping%", VelocityConfig.LOW_MS_COLOR.color() + player.getPing())));

				} else if (ping > VelocityConfig.MEDIUM_MS.get(Integer.class)
						&& ping < VelocityConfig.HIGH_MS.get(Integer.class)) {
					source.sendMessage(Component.text(VelocityConfig.PING.color()
							.replace("%prefix%", VelocityConfig.PREFIX.color())
							.replace("%ping%", VelocityConfig.MEDIUM_MS_COLOR.color() + player.getPing())));

				} else {
					source.sendMessage(Component.text(VelocityConfig.PING.color()
							.replace("%prefix%", VelocityConfig.PREFIX.color())
							.replace("%ping%", VelocityConfig.HIGH_MS_COLOR.color() + player.getPing())));
				}

			} else {
				source.sendMessage(Component.text(VelocityConfig.NO_PERMISSION.color()
						.replace("%prefix%", VelocityConfig.PREFIX.color())));
			}

		} else if (invocation.arguments().length == 1) {

			if (!source.hasPermission(VelocityConfig.PING_OTHERS_PERMISSION.get(String.class))) {
				source.sendMessage(Component.text(VelocityConfig.NO_PERMISSION.color()
						.replace("%prefix%", VelocityConfig.PREFIX.color())));
				return;
			}

			Optional<Player> target = proxyServer.getPlayer(invocation.arguments()[0]);


			if(!target.isPresent()) {
				source.sendMessage(Component.text(VelocityConfig.NOT_ONLINE.color()
						.replace("%prefix%", VelocityConfig.PREFIX.color())
						.replace("%user%", (invocation.arguments()[0]))));
				return;
			}

			if (!(VelocityConfig.OTHERS_PING_OPTION.get(Boolean.class))) {
				source.sendMessage(Component.text(VelocityConfig.USAGE.color()
						.replace("%prefix%", VelocityConfig.PREFIX.color())));
				return;
			}

			final long ping = target.get().getPing();

			if (!(VelocityConfig.DYNAMIC_PING.get(Boolean.class))) {
				source.sendMessage(Component.text(VelocityConfig.OTHERS_PING.color()
						.replace("%prefix%", VelocityConfig.PREFIX.color())
						.replace("%user%", (invocation.arguments()[0]))
						.replace("%ping%", "" + target.get().getPing())));
			}

			if (ping < VelocityConfig.MEDIUM_MS.get(Integer.class)) {
				source.sendMessage(Component.text(VelocityConfig.OTHERS_PING.color()
						.replace("%prefix%", VelocityConfig.PREFIX.color())
						.replace("%user%", (invocation.arguments()[0]))
						.replace("%ping%", VelocityConfig.LOW_MS_COLOR.color() + target.get().getPing())));

			} else if (ping > VelocityConfig.MEDIUM_MS.get(Integer.class)
					&& ping < VelocityConfig.HIGH_MS.get(Integer.class)) {
				source.sendMessage(Component.text(VelocityConfig.OTHERS_PING.color()
						.replace("%prefix%", VelocityConfig.PREFIX.color())
						.replace("%user%", (invocation.arguments()[0]))
						.replace("%ping%", VelocityConfig.MEDIUM_MS_COLOR.color() + target.get().getPing())));

			} else {
				source.sendMessage(Component.text(VelocityConfig.OTHERS_PING.color()
						.replace("%prefix%", VelocityConfig.PREFIX.color())
						.replace("%user%", (invocation.arguments()[0]))
						.replace("%ping%", VelocityConfig.HIGH_MS_COLOR.color() + target.get().getPing())));
			}

		} else {
			source.sendMessage(Component.text(VelocityConfig.USAGE.color()
					.replace("%prefix%", VelocityConfig.PREFIX.color())));

		}

	}

}