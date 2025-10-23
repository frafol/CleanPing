package it.frafol.cleanping.bukkit;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import com.tchristofferson.configupdater.ConfigUpdater;
import it.frafol.cleanping.bukkit.commands.PingCommand;
import it.frafol.cleanping.bukkit.commands.ReloadCommand;
import it.frafol.cleanping.bukkit.commands.utils.TabComplete;
import it.frafol.cleanping.bukkit.enums.SpigotConfig;
import it.frafol.cleanping.bukkit.enums.SpigotMessages;
import it.frafol.cleanping.bukkit.enums.SpigotVersion;
import it.frafol.cleanping.bukkit.hooks.PlaceholderHook;
import it.frafol.cleanping.bukkit.objects.Lag;
import it.frafol.cleanping.bukkit.objects.Placeholder;
import it.frafol.cleanping.bukkit.objects.TextFile;
import lombok.Getter;
import lombok.SneakyThrows;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import net.byteflux.libby.relocation.Relocation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class CleanPing extends JavaPlugin {

	private TextFile configTextFile;
	private TextFile messagesTextFile;
	private TextFile versionTextFile;

	private static Field recentTps;

	@Getter
	public static CleanPing instance;

	boolean isWindows = System.getProperty("os.name").startsWith("Windows");
	public boolean updated = false;

	@SneakyThrows
	@Override
	public void onEnable() {

		instance = this;

		BukkitLibraryManager bukkitLibraryManager = new BukkitLibraryManager(this);

		Library yaml;
		Relocation yamlrelocation = new Relocation("yaml", "it{}frafol{}libs{}yaml");
		yaml = Library.builder()
				.groupId("me{}carleslc{}Simple-YAML")
				.artifactId("Simple-Yaml")
				.relocate(yamlrelocation)
				.version("1.8.4")
				.build();

		Relocation updaterrelocation = new Relocation("updater", "it{}frafol{}libs{}updater");
		Library updater = Library.builder()
				.groupId("com{}tchristofferson")
				.artifactId("ConfigUpdater")
				.version("2.1-SNAPSHOT")
				.relocate(updaterrelocation)
				.url("https://github.com/frafol/Config-Updater/releases/download/compile/ConfigUpdater-2.1-SNAPSHOT.jar")
				.build();

		Relocation schedulerrelocation = new Relocation("scheduler", "it{}frafol{}libs{}scheduler");
		Library scheduler = Library.builder()
				.groupId("com{}github{}Anon8281")
				.artifactId("UniversalScheduler")
				.version("0.1.6")
				.relocate(schedulerrelocation)
				.build();

		bukkitLibraryManager.addJitPack();

		try {
			bukkitLibraryManager.loadLibrary(yaml);
		} catch (RuntimeException ignored) {
			getLogger().severe("Failed to load Simple-YAML library. Trying to download it from GitHub...");
			yaml = Library.builder()
					.groupId("me{}carleslc{}Simple-YAML")
					.artifactId("Simple-Yaml")
					.relocate(yamlrelocation)
					.version("1.8.4")
					.url("https://github.com/Carleslc/Simple-YAML/releases/download/1.8.4/Simple-Yaml-1.8.4.jar")
					.build();
		}

		bukkitLibraryManager.loadLibrary(yaml);
		bukkitLibraryManager.loadLibrary(updater);
		bukkitLibraryManager.loadLibrary(scheduler);

		getLogger().info("\n   ___ _                 ___ _           \n" +
				"  / __| |___ __ _ _ _   | _ (_)_ _  __ _ \n" +
				" | (__| / -_) _` | ' \\  |  _/ | ' \\/ _` |\n" +
				"  \\___|_\\___\\__,_|_||_| |_| |_|_||_\\__, |\n" +
				"                                   |___/ \n");

		getLogger().info("Server version: " + getServer().getBukkitVersion());
		getLogger().info("Loading configuration...");
		configTextFile = new TextFile(getDataFolder().toPath(), "config.yml");
		messagesTextFile = new TextFile(getDataFolder().toPath(), "messages.yml");
		versionTextFile = new TextFile(getDataFolder().toPath(), "version.yml");
		File configFile = new File(getDataFolder(), "config.yml");
		File messagesFile = new File(getDataFolder(), "messages.yml");

		if (!getDescription().getVersion().equals(SpigotVersion.VERSION.get(String.class))) {

			getLogger().info("Creating new configurations...");
			try {
				ConfigUpdater.update(this, "config.yml", configFile, Collections.emptyList());
				ConfigUpdater.update(this, "messages.yml", messagesFile, Collections.emptyList());
			} catch (IOException exception) {
				getLogger().severe("Unable to update configuration file, please update it manually.");
			}

			versionTextFile.getConfig().set("version", getDescription().getVersion());
			versionTextFile.getConfig().save();
			configTextFile = new TextFile(getDataFolder().toPath(), "config.yml");
			messagesTextFile = new TextFile(getDataFolder().toPath(), "messages.yml");
			versionTextFile = new TextFile(getDataFolder().toPath(), "version.yml");
		}

		if (!hasGetPingMethod()) {

			getLogger().info("Loading commands for legacy versions...");
			Objects.requireNonNull(getCommand("ping")).setExecutor(new it.frafol.cleanping.bukkit.commands.legacy.PingCommand(this));
			Objects.requireNonNull(getCommand("cleanping")).setExecutor(new it.frafol.cleanping.bukkit.commands.legacy.PingCommand(this));
			Objects.requireNonNull(getCommand("pingreload")).setExecutor(new it.frafol.cleanping.bukkit.commands.legacy.ReloadCommand(this));
			Objects.requireNonNull(getCommand("cleanpingreload")).setExecutor(new it.frafol.cleanping.bukkit.commands.legacy.ReloadCommand(this));

		} else {

			getLogger().info("Loading commands...");
			Objects.requireNonNull(getCommand("ping")).setExecutor(new PingCommand(this));
			Objects.requireNonNull(getCommand("cleanping")).setExecutor(new PingCommand(this));
			Objects.requireNonNull(getCommand("pingreload")).setExecutor(new ReloadCommand(this));
			Objects.requireNonNull(getCommand("cleanpingreload")).setExecutor(new ReloadCommand(this));

		}

		Objects.requireNonNull(getCommand("ping")).setTabCompleter(new TabComplete());
		Objects.requireNonNull(getCommand("cleanping")).setTabCompleter(new TabComplete());

		if (SpigotConfig.MONITOR.get(Boolean.class)) {
			monitorPing();
		}

		if (SpigotConfig.STATS.get(Boolean.class)) {
			new Metrics(this, 16505);
			getLogger().info("Metrics loaded successfully!");
		}

		if (SpigotConfig.UPDATE_CHECK.get(Boolean.class)) {
			new UpdateCheck(this).getVersion(version -> {

				if (Integer.parseInt(getDescription().getVersion().replace(".", "")) < Integer.parseInt(version.replace(".", ""))) {

					if (SpigotConfig.AUTO_UPDATE.get(Boolean.class) && !updated) {
						autoUpdate();
						return;
					}

					if (!updated) {
						getLogger().warning("§eThere is a new update available, download it on SpigotMC!");
					}
				}

				if (Integer.parseInt(getDescription().getVersion().replace(".", "")) > Integer.parseInt(version.replace(".", ""))) {
					getLogger().warning("§eYou are using a development version, please report any bugs!");
				}

			});
		}

		if (isPAPI()) {
			new PlaceholderHook(this).register();
			getLogger().info("PlaceholderAPI found, placeholders enabled.");
		}

		getLogger().info("Plugin successfully loaded!");
	}

	public YamlFile getConfigTextFile() {
		return configTextFile.getConfig();
	}

	public YamlFile getMessagesTextFile() {
		return messagesTextFile.getConfig();
	}

	public YamlFile getVersionTextFile() {
		return versionTextFile.getConfig();
	}

	public static boolean hasGetPingMethod() {
		try {
			Player.class.getDeclaredMethod("getPing");
			return true;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}

	private boolean isPAPI() {
		return getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
	}

	@SneakyThrows
	public int getPing(Player player) {
		if (hasGetPingMethod()) return player.getPing();
		String v = getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		try {
			Class<?> CraftPlayerClass = Class.forName("org.bukkit.craftbukkit." + v + ".entity.CraftPlayer");
			Object CraftPlayer = CraftPlayerClass.cast(player);
			Method getHandle = CraftPlayer.getClass().getMethod("getHandle");
			Object EntityPlayer = getHandle.invoke(CraftPlayer);
			Field ping = EntityPlayer.getClass().getDeclaredField("ping");
			return ping.getInt(EntityPlayer);
		} catch (Exception ignored) {
			getLogger().severe("Unable to get a player's ping, please report this error on the Discord Server.");
		}

		return 0;
	}

	private void monitorPing() {
		TaskScheduler scheduler = UniversalScheduler.getScheduler(this);
		scheduler.runTaskTimer(new Lag(), 100L, 1L);
		Map<UUID, Integer> lagging = new HashMap<>();
		scheduler.runTaskTimerAsynchronously(() -> {
			for (Player players : getServer().getOnlinePlayers()) {
				if (getPing(players) < SpigotConfig.MAX_PING.get(Integer.class) || Lag.getTPS() < 19.5) continue;
				if (lagging.containsKey(players.getUniqueId())) lagging.replace(players.getUniqueId(), lagging.get(players.getUniqueId()) + 1);
				else lagging.put(players.getUniqueId(), 1);
				if (lagging.get(players.getUniqueId()).equals(SpigotConfig.MAX_FLAGS.get(Integer.class))) sendLaggingMessage(players, getPing(players));
			}
		}, SpigotConfig.FLAG_DELAY.get(Integer.class) * 20L, SpigotConfig.FLAG_DELAY.get(Integer.class) * 20L);
	}

	private void sendLaggingMessage(Player player, Integer ping) {
		if (!hasGetPingMethod()) {
            player.sendMessage(SpigotMessages.LAGGING.color()
                    .replace("%prefix%", SpigotMessages.PREFIX.color())
                    .replace("%ping%", String.valueOf(ping)));
            return;
        }

        player.sendMessage(Placeholder.translate(SpigotMessages.LAGGING.get(String.class)
                .replace("%prefix%", SpigotMessages.PREFIX.color())
                .replace("%ping%", String.valueOf(ping))));
	}

	public void autoUpdate() {

		if (isWindows) {
			return;
		}

		String fileUrl = "https://github.com/frafol/CleanPing/releases/download/release/CleanPing.jar";
		String destination = "./plugins/";

		String fileName = getFileNameFromUrl(fileUrl);
		File outputFile = new File(destination, fileName);

		downloadFile(fileUrl, outputFile);
		updated = true;
		getLogger().warning("CleanPing successfully updated, a restart is required.");
	}

	private String getFileNameFromUrl(String fileUrl) {
		int slashIndex = fileUrl.lastIndexOf('/');
		if (slashIndex != -1 && slashIndex < fileUrl.length() - 1) {
			return fileUrl.substring(slashIndex + 1);
		}
		throw new IllegalArgumentException("Invalid file URL");
	}

	@SneakyThrows
	private void downloadFile(String fileUrl, File outputFile) {
		URL url = new URL(fileUrl);
		try (InputStream inputStream = url.openStream()) {
			Files.copy(inputStream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}

	@Override
	public void onDisable() {

		getLogger().info("Clearing instances...");
		instance = null;

		getLogger().info("Plugin successfully disabled!");
	}
}