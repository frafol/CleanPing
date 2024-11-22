package it.frafol.cleanping.bukkit.commands.legacy;

import it.frafol.cleanping.bukkit.CleanPing;
import it.frafol.cleanping.bukkit.enums.SpigotConfig;
import it.frafol.cleanping.bukkit.enums.SpigotMessages;
import it.frafol.cleanping.bukkit.objects.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PingCommand implements CommandExecutor {

	public final CleanPing plugin;

	public PingCommand(CleanPing plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender source, @NotNull Command command, @NotNull String s, String @NotNull [] args) {

		if (args.length == 0) {

			if (!(source instanceof Player)) {
				source.sendMessage(SpigotMessages.ONLY_PLAYERS.color()
						.replace("%prefix%", SpigotMessages.PREFIX.color()));
				return false;
			}

			final Player player = (Player) source;
			final long ping = plugin.getPing(player);

			if (source.isOp() || source.hasPermission(SpigotConfig.PING_PERMISSION.get(String.class))) {

				if (!(SpigotConfig.DYNAMIC_PING.get(Boolean.class))) {
					source.sendMessage(SpigotMessages.PING.color()
							.replace("%prefix%", SpigotMessages.PREFIX.color())
							.replace("%ping%", String.valueOf(ping)));
					return false;
				}

				source.sendMessage(SpigotMessages.PING.color()
						.replace("%prefix%", SpigotMessages.PREFIX.color())
						.replace("%ping%", colorBasedOnPing(ping) + ping));

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

			final long ping = plugin.getPing(plugin.getServer().getPlayer(args[0]));

			if (!(SpigotConfig.DYNAMIC_PING.get(Boolean.class))) {
				source.sendMessage(SpigotMessages.OTHERS_PING.color()
						.replace("%prefix%", SpigotMessages.PREFIX.color())
						.replace("%user%", (args[0]))
						.replace("%ping%", String.valueOf(ping)));
				return false;
			}

			source.sendMessage(SpigotMessages.OTHERS_PING.color()
					.replace("%prefix%", SpigotMessages.PREFIX.color())
					.replace("%user%", (args[0]))
					.replace("%ping%", colorBasedOnPing(ping) + ping));

		} else if (args.length == 2) {

			if (!source.hasPermission(SpigotConfig.DIFFERENCE_PING_PERMISSION.get(String.class))) {
				source.sendMessage(SpigotMessages.NO_PERMISSION.color()
						.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class))));
				return false;
			}

			if (plugin.getServer().getPlayer(args[0]) == null) {
				source.sendMessage(SpigotMessages.NOT_ONLINE.color()
						.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.color()))
						.replace("%user%", (args[0])));
				return false;
			}

			if (plugin.getServer().getPlayer(args[1]) == null) {
				source.sendMessage(SpigotMessages.NOT_ONLINE.color()
						.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.get(String.class)))
						.replace("%user%", (args[0])));
				return false;
			}

			if (!(SpigotConfig.DIFFERENCE_PING_OPTION.get(Boolean.class))) {
				source.sendMessage(SpigotMessages.USAGE.color()
						.replace("%prefix%", Placeholder.translate(SpigotMessages.PREFIX.color())));
				return false;
			}

			final long ping1 = plugin.getPing(plugin.getServer().getPlayer(args[0]));
			final long ping2 = plugin.getPing(plugin.getServer().getPlayer(args[1]));
			final long difference = getDifference(ping1, ping2);

			source.sendMessage(Placeholder.translate(SpigotMessages.PING_DIFFERENCE.color())
					.replace("%prefix%", SpigotMessages.PREFIX.color())
					.replace("%arg1%", (args[0]))
					.replace("%arg2%", (args[1]))
					.replace("%difference%", String.valueOf(difference)));
			return false;

		} else {
			source.sendMessage(SpigotMessages.USAGE.color()
					.replace("%prefix%", SpigotMessages.PREFIX.color()));

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