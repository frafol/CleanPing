package it.frafol.cleanping.bungee;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import it.frafol.cleanping.bungee.commands.PingCommand;
import it.frafol.cleanping.bungee.commands.ReloadCommand;
import it.frafol.cleanping.bungee.enums.BungeeConfig;
import it.frafol.cleanping.bungee.enums.BungeeMessages;
import it.frafol.cleanping.bungee.enums.BungeeRedis;
import it.frafol.cleanping.bungee.enums.BungeeVersion;
import it.frafol.cleanping.bungee.hooks.RedisListener;
import it.frafol.cleanping.bungee.objects.TextFile;
import lombok.Getter;
import lombok.SneakyThrows;
import net.byteflux.libby.BungeeLibraryManager;
import net.byteflux.libby.Library;
import net.byteflux.libby.relocation.Relocation;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.simpleyaml.configuration.file.YamlFile;
import ru.vyarus.yaml.updater.YamlUpdater;
import ru.vyarus.yaml.updater.util.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CleanPing extends Plugin {

	private TextFile configTextFile;
	private TextFile messagesTextFile;
	private TextFile redisTextFile;
	private TextFile versionTextFile;

	@Getter
	public static CleanPing instance;

	boolean isWindows = System.getProperty("os.name").startsWith("Windows");
	public boolean updated = false;

	@Override
	public void onEnable() {

		instance = this;

		BungeeLibraryManager bungeeLibraryManager = new BungeeLibraryManager(this);

		Library yaml;
		Relocation yamlrelocation = new Relocation("yaml", "it{}frafol{}libs{}yaml");
		yaml = Library.builder()
				.groupId("me{}carleslc{}Simple-YAML")
				.artifactId("Simple-Yaml")
				.relocate(yamlrelocation)
				.version("1.8.4")
				.build();

		Relocation updatereolocation = new Relocation("updater", "it{}frafol{}libs{}updater");
		Library updater = Library.builder()
				.groupId("ru{}vyarus")
				.artifactId("yaml-config-updater")
				.relocate(updatereolocation)
				.version("1.4.2")
				.build();

		bungeeLibraryManager.addJitPack();
		bungeeLibraryManager.addMavenCentral();
		bungeeLibraryManager.loadLibrary(updater);

		try {
			bungeeLibraryManager.loadLibrary(yaml);
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

		bungeeLibraryManager.loadLibrary(yaml);

		getLogger().info("\n§d   ___ _                 ___ _           \n" +
				"  / __| |___ __ _ _ _   | _ (_)_ _  __ _ \n" +
				" | (__| / -_) _` | ' \\  |  _/ | ' \\/ _` |\n" +
				"  \\___|_\\___\\__,_|_||_| |_| |_|_||_\\__, |\n" +
				"                                   |___/ \n");

		getLogger().info("§7Loading §dconfiguration§7...");
		loadFiles();
		updateConfig();

		getLogger().info("§7Loading §dcommands§7...");
		getProxy().getPluginManager().registerCommand(this, new PingCommand());
		getProxy().getPluginManager().registerCommand(this, new ReloadCommand());

		if (BungeeRedis.REDIS.get(Boolean.class) && getProxy().getPluginManager().getPlugin("RedisBungee") != null) {

			final RedisBungeeAPI redisBungeeAPI = RedisBungeeAPI.getRedisBungeeApi();

			getProxy().getPluginManager().registerListener(this, new RedisListener(this));

			redisBungeeAPI.registerPubSubChannels("CleanPing-Request");
			redisBungeeAPI.registerPubSubChannels("CleanPing-Response");

			getLogger().info("§7Hooked into RedisBungee §dsuccessfully§7!");

		}

		if (BungeeConfig.MONITOR.get(Boolean.class)) {
			monitorPing();
		}

		if (BungeeConfig.STATS.get(Boolean.class)) {
			new Metrics(this, 16459);
			getLogger().info("§7Metrics loaded §dsuccessfully§7!");
		}

		if (BungeeConfig.UPDATE_CHECK.get(Boolean.class)) {
			new UpdateCheck(this).getVersion(version -> {

				if (Integer.parseInt(getDescription().getVersion().replace(".", "")) < Integer.parseInt(version.replace(".", ""))) {

					if (BungeeConfig.AUTO_UPDATE.get(Boolean.class) && !updated) {
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

		getLogger().info("§7Plugin §dsuccessfully §7loaded!");
	}

	public YamlFile getConfigTextFile() {
		return getInstance().configTextFile.getConfig();
	}
	public YamlFile getMessagesTextFile() {
		return getInstance().messagesTextFile.getConfig();
	}
	public YamlFile getRedisTextFile() {
		return getInstance().redisTextFile.getConfig();
	}
	public YamlFile getVersionTextFile() {
		return getInstance().versionTextFile.getConfig();
	}

	@Override
	public void onDisable() {
		getLogger().info("§7Clearing §dinstances§7...");
		instance = null;
		getLogger().info("§7Plugin successfully §ddisabled§7!");
	}

	private void loadFiles() {
		configTextFile = new TextFile(getDataFolder().toPath(), "config.yml");
		messagesTextFile = new TextFile(getDataFolder().toPath(), "messages.yml");
		redisTextFile = new TextFile(getDataFolder().toPath(), "redis.yml");
		versionTextFile = new TextFile(getDataFolder().toPath(), "version.yml");
	}

	private void monitorPing() {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		Map<UUID, Integer> lagging = new HashMap<>();
		scheduler.scheduleAtFixedRate(() -> {
			for (ProxiedPlayer players : getProxy().getPlayers()) {
				if (players.getPing() < BungeeConfig.MAX_PING.get(Integer.class)) continue;
				if (lagging.containsKey(players.getUniqueId())) lagging.replace(players.getUniqueId(), lagging.get(players.getUniqueId()) + 1);
				else lagging.put(players.getUniqueId(), 1);
				if (lagging.get(players.getUniqueId()).equals(BungeeConfig.MAX_FLAGS.get(Integer.class))) sendLaggingMessage(players, players.getPing());
			}
		}, BungeeConfig.FLAG_DELAY.get(Integer.class), BungeeConfig.FLAG_DELAY.get(Integer.class), TimeUnit.SECONDS);
	}

	private void sendLaggingMessage(ProxiedPlayer player, Integer ping) {
		player.sendMessage(TextComponent.fromLegacy(BungeeMessages.LAGGING.color()
				.replace("%prefix%", BungeeMessages.PREFIX.color())
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
		getLogger().warning("§eCleanPing successfully updated, a restart is required.");
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

	@SneakyThrows
	private void updateConfig() {
		if (!getDescription().getVersion().equals(BungeeVersion.VERSION.get(String.class))) {
			getLogger().info("§7Creating new §dconfigurations§7...");
			YamlUpdater.create(new File(getDataFolder().toPath() + "/config.yml"), FileUtils.findFile("https://raw.githubusercontent.com/frafol/CleanPing/main/src/main/resources/config.yml"))
					.backup(true)
					.update();
			YamlUpdater.create(new File(getDataFolder().toPath() + "/messages.yml"), FileUtils.findFile("https://raw.githubusercontent.com/frafol/CleanPing/main/src/main/resources/messages.yml"))
					.backup(true)
					.update();
			YamlUpdater.create(new File(getDataFolder().toPath() + "/redis.yml"), FileUtils.findFile("https://raw.githubusercontent.com/frafol/CleanPing/main/src/main/resources/redis.yml"))
					.backup(true)
					.update();
			versionTextFile.getConfig().set("version", getDescription().getVersion());
			versionTextFile.getConfig().save();
			loadFiles();
		}
	}
}