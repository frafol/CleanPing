package it.frafol.cleanping.velocity;

import com.google.inject.Inject;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import it.frafol.cleanping.velocity.commands.PingCommand;
import it.frafol.cleanping.velocity.commands.ReloadCommand;
import it.frafol.cleanping.velocity.enums.VelocityConfig;
import it.frafol.cleanping.velocity.enums.VelocityMessages;
import it.frafol.cleanping.velocity.enums.VelocityRedis;
import it.frafol.cleanping.velocity.enums.VelocityVersion;
import it.frafol.cleanping.velocity.hooks.RedisListener;
import it.frafol.cleanping.velocity.objects.TextFile;
import lombok.Getter;
import lombok.SneakyThrows;
import net.byteflux.libby.Library;
import net.byteflux.libby.VelocityLibraryManager;
import net.byteflux.libby.relocation.Relocation;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.slf4j.Logger;
import ru.vyarus.yaml.updater.YamlUpdater;
import ru.vyarus.yaml.updater.util.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@Plugin(
		id = "cleanping",
		name = "CleanPing",
		version = "1.6.1",
		dependencies = {@Dependency(id = "redisbungee", optional = true)},
		description = "Adds /ping command to check your and player's ping.",
		authors = { "frafol" })

public class CleanPing {

	private final Logger logger;
	private final ProxyServer server;
	private final Path path;
	private final Metrics.Factory metricsFactory;

	boolean isWindows = System.getProperty("os.name").startsWith("Windows");

	private TextFile messagesTextFile;
	private TextFile configTextFile;
	private TextFile redisTextFile;
	private TextFile versionTextFile;

	@Getter
	private static CleanPing instance;

	public boolean updated = false;

	@Inject
	public CleanPing(Logger logger, ProxyServer server, @DataDirectory Path path, Metrics.Factory metricsFactory) {
		this.server = server;
		this.logger = logger;
		this.path = path;
		this.metricsFactory = metricsFactory;
	}

	@Inject
	public PluginContainer container;

	@Subscribe
	public void onProxyInitialization(ProxyInitializeEvent event) {

		instance = this;

		VelocityLibraryManager<CleanPing> velocityLibraryManager = new VelocityLibraryManager<>(getLogger(), path, getServer().getPluginManager(), this);

		Library yaml;
		Relocation yamlrelocation = new Relocation("yaml", "it{}frafol{}libs{}yaml");
		yaml = Library.builder()
				.groupId("me{}carleslc{}Simple-YAML")
				.artifactId("Simple-Yaml")
				.relocate(yamlrelocation)
				.version("1.8.4")
				.build();

		Relocation updaterelocation = new Relocation("updater", "it{}frafol{}libs{}updater");
		Library updater = Library.builder()
				.groupId("ru{}vyarus")
				.artifactId("yaml-config-updater")
				.relocate(updaterelocation)
				.version("1.4.2")
				.build();

		velocityLibraryManager.addJitPack();
		velocityLibraryManager.addMavenCentral();
		velocityLibraryManager.loadLibrary(updater);

		try {
			velocityLibraryManager.loadLibrary(yaml);
		} catch (RuntimeException ignored) {
			logger.error("Failed to load Simple-YAML library. Trying to download it from GitHub...");
			yaml = Library.builder()
					.groupId("me{}carleslc{}Simple-YAML")
					.artifactId("Simple-Yaml")
					.relocate(yamlrelocation)
					.version("1.8.4")
					.url("https://github.com/Carleslc/Simple-YAML/releases/download/1.8.4/Simple-Yaml-1.8.4.jar")
					.build();
		}

		velocityLibraryManager.loadLibrary(yaml);

		logger.info("\n   ___ _                 ___ _           \n" +
				"  / __| |___ __ _ _ _   | _ (_)_ _  __ _ \n" +
				" | (__| / -_) _` | ' \\  |  _/ | ' \\/ _` |\n" +
				"  \\___|_\\___\\__,_|_||_| |_| |_|_||_\\__, |\n" +
				"                                   |___/ \n");

		logger.info("Loading configuration...");
		loadFiles();
		updateConfig();

		logger.info("Loading commands...");
		PingCommand.register(server, this);
		ReloadCommand.register(server, this);

		if (VelocityConfig.MONITOR.get(Boolean.class)) {
			monitorPing();
		}

		if (VelocityConfig.STATS.get(Boolean.class)) {
			metricsFactory.make(this, 16458);
			logger.info("Metrics loaded successfully!");
		}

		if (VelocityRedis.REDIS.get(Boolean.class) && server.getPluginManager().isLoaded("redisbungee")) {
			final RedisBungeeAPI redisBungeeAPI = RedisBungeeAPI.getRedisBungeeApi();
			server.getEventManager().register(this, new RedisListener(this));
			redisBungeeAPI.registerPubSubChannels("CleanPing-Request");
			redisBungeeAPI.registerPubSubChannels("CleanPing-Response");
			logger.info("Hooked into RedisBungee successfully!");
		}

		if (VelocityConfig.UPDATE_CHECK.get(Boolean.class)) {
			new UpdateCheck(this).getVersion(version -> {

				if (!container.getDescription().getVersion().isPresent()) {
					return;
				}

				if (Integer.parseInt(container.getDescription().getVersion().get().replace(".", "")) < Integer.parseInt(version.replace(".", ""))) {

					if (VelocityConfig.AUTO_UPDATE.get(Boolean.class) && !updated) {
						autoUpdate();
						return;
					}

					if (!updated) {
						logger.warn("There is a new update available, download it on SpigotMC!");
					}
				}

				if (Integer.parseInt(container.getDescription().getVersion().get().replace(".", "")) > Integer.parseInt(version.replace(".", ""))) {
					logger.warn("You are using a development version, please report any bugs!");
				}

			});
		}

		logger.info("Plugin successfully loaded!");
	}

	@Subscribe
	public void onProxyShutdown(ProxyShutdownEvent event) {
		logger.info("Clearing instances...");
		instance = null;
		logger.info("Plugin successfully disabled!");
	}

	private void monitorPing() {
		Map<UUID, Integer> lagging = new HashMap<>();
		getServer().getScheduler().buildTask(this, () -> {
            for (Player players : getServer().getAllPlayers()) {
                if (players.getPing() < VelocityConfig.MAX_PING.get(Integer.class)) continue;
                if (lagging.containsKey(players.getUniqueId())) lagging.replace(players.getUniqueId(), lagging.get(players.getUniqueId()) + 1);
                else lagging.put(players.getUniqueId(), 1);
                if (lagging.get(players.getUniqueId()).equals(VelocityConfig.MAX_FLAGS.get(Integer.class))) sendLaggingMessage(players, (int) players.getPing());
            }
        }).delay(VelocityConfig.FLAG_DELAY.get(Integer.class), TimeUnit.SECONDS).repeat(VelocityConfig.FLAG_DELAY.get(Integer.class), TimeUnit.SECONDS).schedule();
	}

	private void sendLaggingMessage(Player player, Integer ping) {
		player.sendMessage(LegacyComponentSerializer.legacy('§').deserialize(VelocityMessages.LAGGING.color()
				.replace("%prefix%", VelocityMessages.PREFIX.color())
				.replace("%ping%", String.valueOf(ping))));
	}

	private void loadFiles() {
		configTextFile = new TextFile(path, "config.yml");
		messagesTextFile = new TextFile(path, "messages.yml");
		redisTextFile = new TextFile(path, "redis.yml");
		versionTextFile = new TextFile(path, "version.yml");
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
		if (container.getDescription().getVersion().isPresent() && (!container.getDescription().getVersion().get().equals(VelocityVersion.VERSION.get(String.class)))) {

			logger.info("Creating new configurations...");
			YamlUpdater.create(new File(path + "/config.yml"), FileUtils.findFile("https://raw.githubusercontent.com/frafol/CleanPing/main/src/main/resources/config.yml"))
					.backup(true)
					.update();
			YamlUpdater.create(new File(path + "/messages.yml"), FileUtils.findFile("https://raw.githubusercontent.com/frafol/CleanPing/main/src/main/resources/messages.yml"))
					.backup(true)
					.update();
			YamlUpdater.create(new File(path + "/redis.yml"), FileUtils.findFile("https://raw.githubusercontent.com/frafol/CleanPing/main/src/main/resources/redis.yml"))
					.backup(true)
					.update();
			versionTextFile.getConfig().set("version", container.getDescription().getVersion().get());
			versionTextFile.getConfig().save();
			loadFiles();
		}
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
		logger.warn("CleanPing successfully updated, a restart is required.");
	}
}