package it.frafol.cleanping.spigot.commands;

import it.frafol.cleanping.spigot.CleanPing;
import it.frafol.cleanping.spigot.enums.SpigotConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PingCommand implements CommandExecutor {

	public final CleanPing plugin;

	public PingCommand(CleanPing plugin) {

		this.plugin = plugin;

	}


	@Override
	public boolean onCommand(org.bukkit.command.@NotNull CommandSender source, @NotNull Command command, @NotNull String s, String[] args) {

		if (args.length == 0) {

			if (!(source instanceof Player)) {
				source.sendMessage(SpigotConfig.ONLY_PLAYERS.color()
						.replace("%prefix%", SpigotConfig.PREFIX.color()));
				return false;
			}

			final Player player = (Player) source;
			final long ping = player.getPing();

			if (source.hasPermission(SpigotConfig.PING_PERMISSION.get(String.class))) {

				if (ping < SpigotConfig.MEDIUM_MS.get(Integer.class)) {
					source.sendMessage(SpigotConfig.PING.color()
							.replace("%prefix%", SpigotConfig.PREFIX.color())
							.replace("%ping%", SpigotConfig.LOW_MS_COLOR.color() + player.getPing()));

				} else if (ping > SpigotConfig.MEDIUM_MS.get(Integer.class)
						&& ping < SpigotConfig.HIGH_MS.get(Integer.class)) {
					source.sendMessage(SpigotConfig.PING.color()
							.replace("%prefix%", SpigotConfig.PREFIX.color())
							.replace("%ping%", SpigotConfig.MEDIUM_MS_COLOR.color() + player.getPing()));

				} else {
					source.sendMessage(SpigotConfig.PING.color()
							.replace("%prefix%", SpigotConfig.PREFIX.color())
							.replace("%ping%", SpigotConfig.HIGH_MS_COLOR.color() + player.getPing()));
				}

			} else {
				source.sendMessage(SpigotConfig.NO_PERMISSION.color()
						.replace("%prefix%", SpigotConfig.PREFIX.color()));
			}

		} else if (args.length == 1) {

			if (!source.hasPermission(SpigotConfig.PING_OTHERS_PERMISSION.get(String.class))) {
				source.sendMessage(SpigotConfig.NO_PERMISSION.color()
						.replace("%prefix%", SpigotConfig.PREFIX.color()));
				return false;
			}


			if (plugin.getServer().getPlayer(args[0]) == null) {
				source.sendMessage(SpigotConfig.NOT_ONLINE.color()
						.replace("%prefix%", SpigotConfig.PREFIX.color())
						.replace("%user%", (args[0])));
				return false;
			}

			if (!(SpigotConfig.OTHERS_PING_OPTION.get(Boolean.class))) {
				source.sendMessage(SpigotConfig.USAGE.color()
						.replace("%prefix%", SpigotConfig.PREFIX.color()));
				return false;
			}

			final long ping = Objects.requireNonNull(plugin.getServer().getPlayer(args[0])).getPing();

			if (!(SpigotConfig.DYNAMIC_PING.get(Boolean.class))) {
				source.sendMessage(SpigotConfig.OTHERS_PING.color()
						.replace("%prefix%", SpigotConfig.PREFIX.color())
						.replace("%user%", (args[0]))
						.replace("%ping%", "" +
								Objects.requireNonNull(plugin.getServer().getPlayer(args[0])).getPing()));
			}

			if (ping < SpigotConfig.MEDIUM_MS.get(Integer.class)) {
				source.sendMessage(SpigotConfig.OTHERS_PING.color()
						.replace("%prefix%", SpigotConfig.PREFIX.color())
						.replace("%user%", (args[0]))
						.replace("%ping%", SpigotConfig.LOW_MS_COLOR.color() +
								Objects.requireNonNull(plugin.getServer().getPlayer(args[0])).getPing()));

			} else if (ping > SpigotConfig.MEDIUM_MS.get(Integer.class)
					&& ping < SpigotConfig.HIGH_MS.get(Integer.class)) {
				source.sendMessage(SpigotConfig.OTHERS_PING.color()
						.replace("%prefix%", SpigotConfig.PREFIX.color())
						.replace("%user%", (args[0]))
						.replace("%ping%", SpigotConfig.MEDIUM_MS_COLOR.color() +
								Objects.requireNonNull(plugin.getServer().getPlayer(args[0])).getPing()));

			} else {
				source.sendMessage(SpigotConfig.OTHERS_PING.color()
						.replace("%prefix%", SpigotConfig.PREFIX.color())
						.replace("%user%", (args[0]))
						.replace("%ping%", SpigotConfig.HIGH_MS_COLOR.color() +
								Objects.requireNonNull(plugin.getServer().getPlayer(args[0])).getPing()));
			}

		} else {
			source.sendMessage(SpigotConfig.USAGE.color()
					.replace("%prefix%", SpigotConfig.PREFIX.color()));

		}
		return false;
	}
}