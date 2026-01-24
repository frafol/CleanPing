package it.frafol.cleanping.hytale;

import com.hypixel.hytale.protocol.packets.connection.PongType;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import it.frafol.cleanping.hytale.commands.PingCommand;
import it.frafol.cleanping.hytale.commands.ReloadCommand;
import it.frafol.cleanping.hytale.enums.HytaleConfig;
import it.frafol.cleanping.hytale.enums.HytaleMessages;
import it.frafol.cleanping.hytale.enums.HytaleVersion;
import it.frafol.cleanping.hytale.hooks.HookInitializer;
import it.frafol.cleanping.hytale.objects.Lag;
import it.frafol.cleanping.hytale.objects.Placeholder;
import it.frafol.cleanping.hytale.objects.TextFile;
import net.byteflux.libby.HytaleLibraryManager;
import net.byteflux.libby.Library;
import org.simpleyaml.configuration.file.YamlFile;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import ru.vyarus.yaml.updater.YamlUpdater;
import ru.vyarus.yaml.updater.util.FileUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class CleanPing extends JavaPlugin {

	private TextFile configTextFile;
	private TextFile messagesTextFile;
	private TextFile versionTextFile;

	private static CleanPing instance;

	public CleanPing(JavaPluginInit init) {
		super(init);
		instance = this;
	}

	@Override
	protected void start() {
		getLogger().at(Level.INFO).log("\n   ___ _                 ___ _           \n" +
				"  / __| |___ __ _ _ _   | _ (_)_ _  __ _ \n" +
				" | (__| / -_) _` | ' \\  |  _/ | ' \\/ _` |\n" +
				"  \\___|_\\___\\__,_|_||_| |_| |_|_||_\\__, |\n" +
				"                                   |___/ \n");

		//getLogger().at(Level.INFO).log("Server version: " + HytaleServer.get());
		getLogger().at(Level.INFO).log("Loading configuration...");

		loadLibraries();
		Path dataPath = getDataDirectory();
		configTextFile = new TextFile(dataPath, "config.yml", "cleaping_config.yml");
		messagesTextFile = new TextFile(dataPath, "messages.yml", "cleanping_messages.yml");
		versionTextFile = new TextFile(dataPath, "version.yml", "cleanping_version.yml");

		if (!getVersionFromPom().equals(HytaleVersion.VERSION.get(String.class))) {
			getLogger().at(Level.INFO).log("Creating new configurations...");
			YamlUpdater.create(new File(getDataDirectory() + "/config.yml"),
							FileUtils.findFile("https://raw.githubusercontent.com/frafol/CleanPing/refs/heads/hytale/hytale/src/main/resources/cleanping_config.yml"))
					.backup(true)
					.update();
			YamlUpdater.create(new File(getDataDirectory() + "/messages.yml"),
							FileUtils.findFile("https://raw.githubusercontent.com/frafol/CleanPing/refs/heads/hytale/hytale/src/main/resources/cleanping_messages.yml"))
					.backup(true)
					.update();
			versionTextFile.getConfig().set("version", getVersionFromPom());
            try {
                versionTextFile.getConfig().save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            configTextFile = new TextFile(dataPath, "config.yml", "cleaping_config.yml");
			messagesTextFile = new TextFile(dataPath, "messages.yml", "cleanping_messages.yml");
			versionTextFile = new TextFile(dataPath, "version.yml", "cleanping_version.yml");
		}

		getLogger().at(Level.INFO).log("Loading commands...");
		getCommandRegistry().registerCommand(new PingCommand(this, "ping", "Check your ping or others' ping"));
		getCommandRegistry().registerCommand(new ReloadCommand(this, "pingreload", "Reload the plugin configuration"));

		if (Boolean.TRUE.equals(HytaleConfig.MONITOR.get(Boolean.class))) monitorPing();
		if (HytaleConfig.UPDATE_CHECK.get(Boolean.class)) HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> UpdateCheck.checkForUpdates(this, getVersionFromPom(), "cmke27xel000201s6butfvcb7"), 0, 1, TimeUnit.HOURS);
		HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> HookInitializer.initializeHooks(this), 10, TimeUnit.SECONDS);
		getLogger().at(Level.INFO).log("Plugin successfully loaded!");
	}

	@Override
	protected void shutdown() {
		getLogger().at(Level.INFO).log("Clearing instances...");
		instance = null;
		getLogger().at(Level.INFO).log("Plugin successfully disabled!");
	}

	public static CleanPing getInstance() {
		return instance;
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

	public long getPing(PlayerRef player) {
		var pingInfo = player.getPacketHandler().getPingInfo(PongType.Direct);
		var metric = pingInfo.getPingMetricSet();
		double avgValue = metric.getLastValue();
		return (long) (avgValue / 1000.0);
	}

	private void loadLibraries() {
		HytaleLibraryManager hytaleLibraryManager = new HytaleLibraryManager(this);
		Library yaml = Library.builder().groupId("me{}carleslc{}Simple-YAML").artifactId("Simple-Yaml").version("1.8.4").url("https://github.com/Carleslc/Simple-YAML/releases/download/1.8.4/Simple-Yaml-1.8.4.jar").build();
		hytaleLibraryManager.addMavenCentral();
		hytaleLibraryManager.addJitPack();
		hytaleLibraryManager.loadLibrary(yaml);
	}

	private void monitorPing() {
		ScheduledExecutorService scheduler = HytaleServer.SCHEDULED_EXECUTOR;
		Map<UUID, Integer> lagging = new HashMap<>();
		scheduler.scheduleAtFixedRate(() -> {
			Universe universe = Universe.get();
			for (var player : universe.getPlayers()) {
				int ping = Math.toIntExact(getPing(player));
				if (ping < HytaleConfig.MAX_PING.get(Integer.class) || Lag.getTPS() < 19.5) continue;
				lagging.merge(player.getUuid(), 1, Integer::sum);
				if (lagging.get(player.getUuid()).equals(HytaleConfig.MAX_FLAGS.get(Integer.class))) {
					sendLaggingMessage(player.getUuid(), ping);
				}
			}
		}, 100L, 1L, TimeUnit.MILLISECONDS);
	}

	private void sendLaggingMessage(UUID playerUUID, int ping) {
		var player = Universe.get().getPlayer(playerUUID);
		if (player == null) return;
		String message = Placeholder.translate(HytaleMessages.LAGGING.get(String.class)
				.replace("%prefix%", HytaleMessages.PREFIX.color())
				.replace("%ping%", String.valueOf(ping)));
		player.sendMessage(Placeholder.format(message));
	}

	public String getVersionFromPom() {
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("pom.xml")) {
			if (inputStream == null) {
				return HytaleVersion.VERSION.get(String.class);
			}
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(inputStream);
			NodeList versionNodes = doc.getElementsByTagName("version");
			if (versionNodes.getLength() > 0) {
				return versionNodes.item(0).getTextContent().trim();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return HytaleVersion.VERSION.get(String.class);
	}
}
