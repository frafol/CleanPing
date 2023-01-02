package it.frafol.cleanping.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import it.frafol.cleanping.velocity.commands.PingCommand;
import it.frafol.cleanping.velocity.commands.ReloadCommand;
import it.frafol.cleanping.velocity.enums.VelocityConfig;
import it.frafol.cleanping.velocity.objects.TextFile;
import lombok.Getter;
import net.byteflux.libby.Library;
import net.byteflux.libby.VelocityLibraryManager;
import org.slf4j.Logger;

import java.nio.file.Path;

@Getter
@Plugin(
		id = "cleanping",
		name = "CleanPing",
		version = "1.1",
		description = "Adds /ping command to check your and player's ping.",
		authors = { "frafol" })

public class CleanPing {

	private final Logger logger;
	private final ProxyServer server;
	private final Path path;
	private final Metrics.Factory metricsFactory;

	private TextFile messagesTextFile;
	private TextFile configTextFile;
	private static CleanPing instance;

	public static CleanPing getInstance() {
		return instance;
	}

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

		Library yaml = Library.builder()
				.groupId("me{}carleslc{}Simple-YAML")
				.artifactId("Simple-Yaml")
				.version("1.8.3")
				.build();

		velocityLibraryManager.addJitPack();
		velocityLibraryManager.loadLibrary(yaml);

		logger.info("\n§d   ___ _                 ___ _           \n" +
				"  / __| |___ __ _ _ _   | _ (_)_ _  __ _ \n" +
				" | (__| / -_) _` | ' \\  |  _/ | ' \\/ _` |\n" +
				"  \\___|_\\___\\__,_|_||_| |_| |_|_||_\\__, |\n" +
				"                                   |___/ \n");

		logger.info("§7Loading §dconfiguration§7...");
		configTextFile = new TextFile(path, "config.yml");
		messagesTextFile = new TextFile(path, "messages.yml");

		logger.info("§7Loading §dcommands§7...");
		getInstance().getServer().getCommandManager().register
				(server.getCommandManager().metaBuilder("ping").aliases("cleanping")
						.build(), new PingCommand(server));
		getInstance().getServer().getCommandManager().register
				(server.getCommandManager().metaBuilder("pingreload").aliases("cleanpingreload")
						.build(), new ReloadCommand(this));

		if (VelocityConfig.STATS.get(Boolean.class)) {

			metricsFactory.make(this, 16458);

			logger.info("§7Metrics loaded §dsuccessfully§7!");

		}

		if (VelocityConfig.UPDATE_CHECK.get(Boolean.class)) {
			new UpdateCheck(this).getVersion(version -> {
				if (container.getDescription().getVersion().isPresent()) {
					if (!container.getDescription().getVersion().get().equals(version)) {
						logger.warn("There is a new update available, download it on SpigotMC!");
					}
				}
			});
		}

		logger.info("§7Plugin §dsuccessfully §7loaded!");
	}

	@Subscribe
	public void onProxyShutdown(ProxyShutdownEvent event) {

		logger.info("§7Clearing §dinstances§7...");
		instance = null;

		logger.info("§7Plugin successfully §ddisabled§7!");
	}

}