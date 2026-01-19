package it.frafol.cleanping.hytale.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import it.frafol.cleanping.hytale.CleanPing;
import it.frafol.cleanping.hytale.enums.HytaleConfig;
import it.frafol.cleanping.hytale.enums.HytaleMessages;
import it.frafol.cleanping.hytale.objects.PermissionsUtil;
import it.frafol.cleanping.hytale.objects.Placeholder;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.Arrays;

public class PingCommand extends AbstractCommand {

	private final CleanPing plugin;

	public PingCommand(CleanPing plugin, String name, String description) {
		super(name, description);
		this.plugin = plugin;
		this.requirePermission(Objects.requireNonNull(HytaleConfig.PING_PERMISSION.get(String.class)));
		this.addAliases("cleanping", "cping", "playerping");
		this.setAllowsExtraArguments(true);
	}

	@Override
	protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
		CommandSender sender = context.sender();
		String input = context.getInputString().trim();
		String[] args = input.isEmpty() ? new String[0] : input.split("\\s+");
		if (args.length > 0 && args[0].equalsIgnoreCase(this.getName())) {
			args = Arrays.copyOfRange(args, 1, args.length);
		}

		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(Placeholder.format(Placeholder.translate(HytaleMessages.ONLY_PLAYERS.get(String.class))
						.replace("%prefix%", Placeholder.translate(HytaleMessages.PREFIX.get(String.class)))));
				return CompletableFuture.completedFuture(null);
			}

			PlayerRef player = Universe.get().getPlayer(sender.getUuid());
			if (player == null) {
				sender.sendMessage(Placeholder.format(Placeholder.translate(HytaleMessages.NOT_ONLINE.get(String.class))
						.replace("%prefix%", Placeholder.translate(HytaleMessages.PREFIX.get(String.class)))
						.replace("%user%", sender.getDisplayName())));
				return CompletableFuture.completedFuture(null);
			}
			long ping = getPing(player);

			if (PermissionsUtil.hasPermission(sender.getUuid(), HytaleConfig.PING_PERMISSION.get(String.class))) {
				if (!HytaleConfig.DYNAMIC_PING.get(Boolean.class)) {
					sender.sendMessage(Placeholder.format(Placeholder.translate(HytaleMessages.PING.get(String.class))
							.replace("%prefix%", Placeholder.translate(HytaleMessages.PREFIX.get(String.class)))
							.replace("%ping%", String.valueOf(ping))));
					return CompletableFuture.completedFuture(null);
				}

				sender.sendMessage(Placeholder.format(Placeholder.translate(HytaleMessages.PING.get(String.class))
						.replace("%prefix%", Placeholder.translate(HytaleMessages.PREFIX.get(String.class)))
						.replace("%ping%", colorBasedOnPing(ping) + ping)));
			} else {
				sender.sendMessage(Placeholder.format(Placeholder.translate(HytaleMessages.NO_PERMISSION.get(String.class))
						.replace("%prefix%", Placeholder.translate(HytaleMessages.PREFIX.get(String.class)))));
			}

		} else if (args.length == 1) {
			if (!PermissionsUtil.hasPermission(sender.getUuid(), HytaleConfig.PING_OTHERS_PERMISSION.get(String.class))) {
				sender.sendMessage(Placeholder.format(Placeholder.translate(HytaleMessages.NO_PERMISSION.get(String.class))
						.replace("%prefix%", Placeholder.translate(HytaleMessages.PREFIX.get(String.class)))));
				return CompletableFuture.completedFuture(null);
			}

			String[] finalArgs = args;
			PlayerRef target = Universe.get().getPlayers().stream()
					.filter(p -> p.getUsername().equalsIgnoreCase(finalArgs[0]))
					.findFirst()
					.orElse(null);

			if (target == null) {
				sender.sendMessage(Placeholder.format(Placeholder.translate(HytaleMessages.NOT_ONLINE.get(String.class))
						.replace("%prefix%", Placeholder.translate(HytaleMessages.PREFIX.get(String.class)))
						.replace("%user%", args[0])));
				return CompletableFuture.completedFuture(null);
			}

			if (!HytaleConfig.OTHERS_PING_OPTION.get(Boolean.class)) {
				sender.sendMessage(Placeholder.format(Placeholder.translate(HytaleMessages.USAGE.get(String.class))
						.replace("%prefix%", Placeholder.translate(HytaleMessages.PREFIX.get(String.class)))));
				return CompletableFuture.completedFuture(null);
			}

			long ping = getPing(target);

			if (!HytaleConfig.DYNAMIC_PING.get(Boolean.class)) {
				sender.sendMessage(Placeholder.format(Placeholder.translate(HytaleMessages.OTHERS_PING.get(String.class))
						.replace("%prefix%", Placeholder.translate(HytaleMessages.PREFIX.get(String.class)))
						.replace("%user%", args[0])
						.replace("%ping%", String.valueOf(ping))));
				return CompletableFuture.completedFuture(null);
			}

			sender.sendMessage(Placeholder.format(Placeholder.translate(HytaleMessages.OTHERS_PING.get(String.class))
					.replace("%prefix%", Placeholder.translate(HytaleMessages.PREFIX.get(String.class)))
					.replace("%user%", args[0])
					.replace("%ping%", colorBasedOnPing(ping) + ping)));
		} else if (args.length == 2) {
			if (!PermissionsUtil.hasPermission(sender.getUuid(), HytaleConfig.DIFFERENCE_PING_PERMISSION.get(String.class))) {
				sender.sendMessage(Placeholder.format(Placeholder.translate(HytaleMessages.NO_PERMISSION.get(String.class))
						.replace("%prefix%", Placeholder.translate(HytaleMessages.PREFIX.get(String.class)))));
				return CompletableFuture.completedFuture(null);
			}

			String[] finalArgs1 = args;
			PlayerRef player1 = Universe.get().getPlayers().stream()
					.filter(p -> p.getUsername().equalsIgnoreCase(finalArgs1[0]))
					.findFirst()
					.orElse(null);

			String[] finalArgs2 = args;
			PlayerRef player2 = Universe.get().getPlayers().stream()
					.filter(p -> p.getUsername().equalsIgnoreCase(finalArgs2[1]))
					.findFirst()
					.orElse(null);

			if (player1 == null) {
				sender.sendMessage(Placeholder.format(Placeholder.translate(HytaleMessages.NOT_ONLINE.get(String.class))
						.replace("%prefix%", Placeholder.translate(HytaleMessages.PREFIX.get(String.class)))
						.replace("%user%", args[0])));
				return CompletableFuture.completedFuture(null);
			}

			if (player2 == null) {
				sender.sendMessage(Placeholder.format(Placeholder.translate(HytaleMessages.NOT_ONLINE.get(String.class))
						.replace("%prefix%", Placeholder.translate(HytaleMessages.PREFIX.get(String.class)))
						.replace("%user%", args[1])));
				return CompletableFuture.completedFuture(null);
			}

			if (!HytaleConfig.DIFFERENCE_PING_OPTION.get(Boolean.class)) {
				sender.sendMessage(Placeholder.format(Placeholder.translate(HytaleMessages.USAGE.get(String.class))
						.replace("%prefix%", Placeholder.translate(HytaleMessages.PREFIX.get(String.class)))));
				return CompletableFuture.completedFuture(null);
			}

			long ping1 = getPing(player1);
			long ping2 = getPing(player2);
			long difference = Math.abs(ping1 - ping2);

			sender.sendMessage(Placeholder.format(Placeholder.translate(HytaleMessages.PING_DIFFERENCE.get(String.class))
					.replace("%prefix%", Placeholder.translate(HytaleMessages.PREFIX.get(String.class)))
					.replace("%arg1%", args[0])
					.replace("%arg2%", args[1])
					.replace("%difference%", String.valueOf(difference))));
		} else {
			sender.sendMessage(Placeholder.format(Placeholder.translate(HytaleMessages.USAGE.get(String.class))
					.replace("%prefix%", Placeholder.translate(HytaleMessages.PREFIX.get(String.class)))));
		}

		return CompletableFuture.completedFuture(null);
	}

	private String colorBasedOnPing(long ping) {
		if (ping < HytaleConfig.MEDIUM_MS.get(Integer.class)) {
			return HytaleConfig.LOW_MS_COLOR.color();
		} else if (ping > HytaleConfig.MEDIUM_MS.get(Integer.class) && ping < HytaleConfig.HIGH_MS.get(Integer.class)) {
			return HytaleConfig.MEDIUM_MS_COLOR.color();
		} else {
			return HytaleConfig.HIGH_MS_COLOR.color();
		}
	}

	private long getPing(PlayerRef player) {
		return plugin.getPing(player);
	}
}
