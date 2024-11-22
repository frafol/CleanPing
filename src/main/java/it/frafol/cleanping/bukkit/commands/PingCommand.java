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

			if (source.isOp() || source.hasPermission(SpigotConfig.PING_PERMISSION.get(String.class))) {

				if (!(SpigotConfig.DYNAMIC_PING.get(Boolean.class))) {
					source.sendMessage(Placeholder.translate(SpigotMessages.PING.get(String.class))
							.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class)))
							.replace("%ping%", String.valueOf(player.getPing())));
					return false;
				}

				source.sendMessage(Placeholder.translate(SpigotMessages.PING.get(String.class))
						.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class)))
						.replace("%ping%", colorBasedOnPing(ping) + ping));

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
						.replace("%ping%", String.valueOf(ping)));
				return false;
			}

			source.sendMessage(Placeholder.translate(SpigotMessages.OTHERS_PING.get(String.class))
					.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class)))
					.replace("%user%", (args[0]))
					.replace("%ping%", colorBasedOnPing(ping) + ping));

		} else if (args.length == 2) {

			if (!source.hasPermission(Placeholder.translate(SpigotConfig.DIFFERENCE_PING_PERMISSION.get(String.class)))) {
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

			if (plugin.getServer().getPlayer(args[1]) == null) {
				source.sendMessage(Placeholder.translate(SpigotMessages.NOT_ONLINE.get(String.class))
						.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class)))
						.replace("%user%", (args[0])));
				return false;
			}

			if (!(SpigotConfig.DIFFERENCE_PING_OPTION.get(Boolean.class))) {
				source.sendMessage(Placeholder.translate(SpigotMessages.USAGE.get(String.class))
						.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class))));
				return false;
			}

			final long ping1 = Objects.requireNonNull(plugin.getServer().getPlayer(args[0])).getPing();
			final long ping2 = Objects.requireNonNull(plugin.getServer().getPlayer(args[1])).getPing();
			final long difference = getDifference(ping1, ping2);

			source.sendMessage(Placeholder.translate(SpigotMessages.PING_DIFFERENCE.get(String.class))
					.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class)))
					.replace("%arg1%", (args[0]))
					.replace("%arg2%", (args[1]))
					.replace("%difference%", String.valueOf(difference)));
			return false;

		} else {
			source.sendMessage(Placeholder.translate(SpigotMessages.USAGE.get(String.class))
					.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class))));
		}
		return false;
	}

	private static String colorBasedOnPing(long ping) {
		if (ping < SpigotConfig.MEDIUM_MS.get(Integer.class)) {
			return SpigotConfig.LOW_MS_COLOR.color();
		} else if (ping > SpigotConfig.MEDIUM_MS.get(Integer.class) && ping < SpigotConfig.HIGH_MS.get(Integer.class)) {
			return SpigotConfig.MEDIUM_MS_COLOR.color();
		} else {
			return SpigotConfig.HIGH_MS_COLOR.color();
		}
	}

	private Integer getDifference(long ping1, long ping2) {
		if (ping1 > ping2) {
			return (int) Math.abs(ping1 - ping2);
		}
		return (int) Math.abs(ping2 - ping1);
	}
}