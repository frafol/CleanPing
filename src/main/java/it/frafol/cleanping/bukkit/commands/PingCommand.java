package it.frafol.cleanping.bukkit.commands;

import it.frafol.cleanping.bukkit.CleanPing;
import it.frafol.cleanping.bukkit.enums.SpigotConfig;
import it.frafol.cleanping.bukkit.enums.SpigotMessages;
import it.frafol.cleanping.bukkit.objects.Placeholder;
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
	public boolean onCommand(org.bukkit.command.@NotNull CommandSender source, @NotNull Command command, @NotNull String s, String @NotNull [] args) {

		if (args.length == 0) {

			if (!(source instanceof Player)) {
				source.sendMessage(Placeholder.translate(SpigotMessages.ONLY_PLAYERS.get(String.class))
						.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class))));
				return false;
			}

			final Player player = (Player) source;
			final long ping = player.getPing();

			if (source.hasPermission(SpigotConfig.PING_PERMISSION.get(String.class))) {

				if (!(SpigotConfig.DYNAMIC_PING.get(Boolean.class))) {
					source.sendMessage(Placeholder.translate(SpigotMessages.PING.get(String.class))
							.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class)))
							.replace("%ping%", String.valueOf(player.getPing())));
					return false;
				}

				if (ping < SpigotConfig.MEDIUM_MS.get(Integer.class)) {
					source.sendMessage(Placeholder.translate(SpigotMessages.PING.get(String.class))
							.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class)))
							.replace("%ping%", Placeholder.translate(SpigotConfig.LOW_MS_COLOR.get(String.class)) + player.getPing()));

				} else if (ping > SpigotConfig.MEDIUM_MS.get(Integer.class)
						&& ping < SpigotConfig.HIGH_MS.get(Integer.class)) {
					source.sendMessage(Placeholder.translate(SpigotMessages.PING.get(String.class))
							.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class)))
							.replace("%ping%", Placeholder.translate(SpigotConfig.MEDIUM_MS_COLOR.get(String.class)) + player.getPing()));

				} else {
					source.sendMessage(Placeholder.translate(SpigotMessages.PING.get(String.class))
							.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class)))
							.replace("%ping%", Placeholder.translate(SpigotConfig.HIGH_MS_COLOR.get(String.class)) + player.getPing()));
				}

			} else {
				source.sendMessage(Placeholder.translate(SpigotMessages.NO_PERMISSION.get(String.class))
						.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class))));
			}

		} else if (args.length == 1) {

			if (!source.hasPermission(Placeholder.translate(SpigotConfig.PING_OTHERS_PERMISSION.get(String.class)))) {
				source.sendMessage(Placeholder.translate(SpigotMessages.NO_PERMISSION.get(String.class))
						.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class))));
				return false;
			}


			if (plugin.getServer().getPlayer(args[0]) == null) {
				source.sendMessage(Placeholder.translate(SpigotMessages.NOT_ONLINE.get(String.class))
						.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class)))
						.replace("%user%", (args[0])));
				return false;
			}

			if (!(SpigotConfig.OTHERS_PING_OPTION.get(Boolean.class))) {
				source.sendMessage(Placeholder.translate(SpigotMessages.USAGE.get(String.class))
						.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class))));
				return false;
			}

			final long ping = Objects.requireNonNull(plugin.getServer().getPlayer(args[0])).getPing();

			if (!(SpigotConfig.DYNAMIC_PING.get(Boolean.class))) {
				source.sendMessage(Placeholder.translate(SpigotMessages.OTHERS_PING.get(String.class))
						.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class)))
						.replace("%user%", (args[0]))
						.replace("%ping%", String.valueOf(Objects.requireNonNull(plugin.getServer().getPlayer(args[0])).getPing())));
				return false;
			}

			if (ping < SpigotConfig.MEDIUM_MS.get(Integer.class)) {
				source.sendMessage(Placeholder.translate(SpigotMessages.OTHERS_PING.get(String.class))
						.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class)))
						.replace("%user%", (args[0]))
						.replace("%ping%", Placeholder.translate(SpigotConfig.LOW_MS_COLOR.get(String.class)) +
								Objects.requireNonNull(plugin.getServer().getPlayer(args[0])).getPing()));

			} else if (ping > SpigotConfig.MEDIUM_MS.get(Integer.class)
					&& ping < SpigotConfig.HIGH_MS.get(Integer.class)) {
				source.sendMessage(Placeholder.translate(SpigotMessages.OTHERS_PING.get(String.class))
						.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class)))
						.replace("%user%", (args[0]))
						.replace("%ping%", Placeholder.translate(SpigotConfig.MEDIUM_MS_COLOR.get(String.class)) +
								Objects.requireNonNull(plugin.getServer().getPlayer(args[0])).getPing()));

			} else {
				source.sendMessage(Placeholder.translate(SpigotMessages.OTHERS_PING.get(String.class))
						.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class)))
						.replace("%user%", (args[0]))
						.replace("%ping%", Placeholder.translate(SpigotConfig.HIGH_MS_COLOR.get(String.class)) +
								Objects.requireNonNull(plugin.getServer().getPlayer(args[0])).getPing()));
			}

		} else {
			source.sendMessage(Placeholder.translate(SpigotMessages.USAGE.get(String.class))
					.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class))));
		}
		return false;
	}
}