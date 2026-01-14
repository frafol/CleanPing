package it.frafol.cleanping.hytale;

import com.hypixel.hytale.protocol.packets.connection.PongType;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import it.frafol.cleanping.hytale.commands.PingCommand;
import it.frafol.cleanping.hytale.commands.ReloadCommand;
import it.frafol.cleanping.hytale.enums.HytaleConfig;
import it.frafol.cleanping.hytale.enums.HytaleMessages;
import it.frafol.cleanping.hytale.enums.HytaleVersion;
import it.frafol.cleanping.hytale.objects.Lag;
import it.frafol.cleanping.hytale.objects.Placeholder;
import it.frafol.cleanping.hytale.objects.TextFile;
import org.simpleyaml.configuration.file.YamlFile;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
	protected void setup() {
		getLogger().at(Level.INFO).log("\n   ___ _                 ___ _           \n" +
				"  / __| |___ __ _ _ _   | _ (_)_ _  __ _ \n" +
				" | (__| / -_) _` | ' \\  |  _/ | ' \\/ _` |\n" +
				"  \\___|_\\___\\__,_|_||_| |_| |_|_||_\\__, |\n" +
				"                                   |___/ \n");

		//getLogger().at(Level.INFO).log("Server version: " + Universe.get().getServer().getVersion());
		getLogger().at(Level.INFO).log("Loading configuration...");

		Path dataPath = getDataDirectory();
		configTextFile = new TextFile(dataPath, "config.yml");
		messagesTextFile = new TextFile(dataPath, "messages.yml");
		versionTextFile = new TextFile(dataPath, "version.yml");

		if (!getVersionFromPom().equals(HytaleVersion.VERSION.get(String.class))) {
			getLogger().at(Level.INFO).log("Creating new configurations...");
			// TODO Update configs
			versionTextFile.getConfig().set("version", getVersionFromPom());
            try {
                versionTextFile.getConfig().save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            configTextFile = new TextFile(dataPath, "config.yml");
			messagesTextFile = new TextFile(dataPath, "messages.yml");
			versionTextFile = new TextFile(dataPath, "version.yml");
		}

		getLogger().at(Level.INFO).log("Loading commands...");

		getCommandRegistry().registerCommand(new PingCommand(this, "ping", "Check your ping or others' ping"));
		getCommandRegistry().registerCommand(new ReloadCommand(this, "pingreload", "Reload the plugin configuration"));

		if (Boolean.TRUE.equals(HytaleConfig.MONITOR.get(Boolean.class))) {
			monitorPing();
		}

		HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> UpdateCheck.checkForUpdates(this, getVersionFromPom(), "cmke27xel000201s6butfvcb7"), 0, 1, TimeUnit.HOURS);
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
