package it.frafol.cleanping.bukkit.commands.legacy;

import it.frafol.cleanping.bukkit.CleanPing;
import it.frafol.cleanping.bukkit.enums.SpigotConfig;
import it.frafol.cleanping.bukkit.enums.SpigotMessages;
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
				source.sendMessage(SpigotMessages.ONLY_PLAYERS.color()
						.replace("%prefix%", SpigotMessages.PREFIX.color()));
				return false;
			}

			final Player player = (Player) source;
			final long ping = CleanPing.getPing(player);

			if (source.hasPermission(SpigotConfig.PING_PERMISSION.get(String.class))) {

				if (ping < SpigotConfig.MEDIUM_MS.get(Integer.class)) {
					source.sendMessage(SpigotMessages.PING.color()
							.replace("%prefix%", SpigotMessages.PREFIX.color())
							.replace("%ping%", SpigotConfig.LOW_MS_COLOR.color() + ping));

				} else if (ping > SpigotConfig.MEDIUM_MS.get(Integer.class)
						&& ping < SpigotConfig.HIGH_MS.get(Integer.class)) {
					source.sendMessage(SpigotMessages.PING.color()
							.replace("%prefix%", SpigotMessages.PREFIX.color())
							.replace("%ping%", SpigotConfig.MEDIUM_MS_COLOR.color() + ping));

				} else {
					source.sendMessage(SpigotMessages.PING.color()
							.replace("%prefix%", SpigotMessages.PREFIX.color())
							.replace("%ping%", SpigotConfig.HIGH_MS_COLOR.color() + ping));
				}

			} else {
				source.sendMessage(SpigotMessages.NO_PERMISSION.color()
						.replace("%prefix%", SpigotMessages.PREFIX.color()));
			}

		} else if (args.length == 1) {

			if (!source.hasPermission(SpigotConfig.PING_OTHERS_PERMISSION.get(String.class))) {
				source.sendMessage(SpigotMessages.NO_PERMISSION.color()
						.replace("%prefix%", SpigotMessages.PREFIX.color()));
				return false;
			}


			if (plugin.getServer().getPlayer(args[0]) == null) {
				source.sendMessage(SpigotMessages.NOT_ONLINE.color()
						.replace("%prefix%", SpigotMessages.PREFIX.color())
						.replace("%user%", (args[0])));
				return false;
			}

			if (!(SpigotConfig.OTHERS_PING_OPTION.get(Boolean.class))) {
				source.sendMessage(SpigotMessages.USAGE.color()
						.replace("%prefix%", SpigotMessages.PREFIX.color()));
				return false;
			}

			final long ping = Objects.requireNonNull(plugin.getServer().getPlayer(args[0])).getPing();

			if (!(SpigotConfig.DYNAMIC_PING.get(Boolean.class))) {
				source.sendMessage(SpigotMessages.OTHERS_PING.color()
						.replace("%prefix%", SpigotMessages.PREFIX.color())
						.replace("%user%", (args[0]))
						.replace("%ping%", "" + CleanPing.getPing(plugin.getServer().getPlayer(args[0]))));
			}

			if (ping < SpigotConfig.MEDIUM_MS.get(Integer.class)) {
				source.sendMessage(SpigotMessages.OTHERS_PING.color()
						.replace("%prefix%", SpigotMessages.PREFIX.color())
						.replace("%user%", (args[0]))
						.replace("%ping%", SpigotConfig.LOW_MS_COLOR.color() + CleanPing.getPing(plugin.getServer().getPlayer(args[0]))));

			} else if (ping > SpigotConfig.MEDIUM_MS.get(Integer.class)
					&& ping < SpigotConfig.HIGH_MS.get(Integer.class)) {
				source.sendMessage(SpigotMessages.OTHERS_PING.color()
						.replace("%prefix%", SpigotMessages.PREFIX.color())
						.replace("%user%", (args[0]))
						.replace("%ping%", SpigotConfig.MEDIUM_MS_COLOR.color() + CleanPing.getPing(plugin.getServer().getPlayer(args[0]))));

			} else {
				source.sendMessage(SpigotMessages.OTHERS_PING.color()
						.replace("%prefix%", SpigotMessages.PREFIX.color())
						.replace("%user%", (args[0]))
						.replace("%ping%", SpigotConfig.HIGH_MS_COLOR.color() + CleanPing.getPing(plugin.getServer().getPlayer(args[0]))));
			}

		} else {
			source.sendMessage(SpigotMessages.USAGE.color()
					.replace("%prefix%", SpigotMessages.PREFIX.color()));

		}
		return false;
	}
}