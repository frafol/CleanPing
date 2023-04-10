package it.frafol.cleanping.bungee;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import it.frafol.cleanping.bungee.commands.PingCommand;
import it.frafol.cleanping.bungee.commands.ReloadCommand;
import it.frafol.cleanping.bungee.enums.BungeeConfig;
import it.frafol.cleanping.bungee.enums.BungeeRedis;
import it.frafol.cleanping.bungee.hooks.RedisListener;
import it.frafol.cleanping.bungee.objects.TextFile;
import net.byteflux.libby.BungeeLibraryManager;
import net.byteflux.libby.Library;
import net.md_5.bungee.api.plugin.Plugin;
import org.simpleyaml.configuration.file.YamlFile;

public class CleanPing extends Plugin {

	private TextFile configTextFile;
	private TextFile messagesTextFile;
	private TextFile redisTextFile;
	public static CleanPing instance;

	public static CleanPing getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {

		instance = this;

		BungeeLibraryManager bungeeLibraryManager = new BungeeLibraryManager(this);

		Library yaml = Library.builder()
				.groupId("me{}carleslc{}Simple-YAML")
				.artifactId("Simple-Yaml")
				.version("1.8.4")
				.build();

		bungeeLibraryManager.addJitPack();
		bungeeLibraryManager.loadLibrary(yaml);

		getLogger().info("\n§d   ___ _                 ___ _           \n" +
				"  / __| |___ __ _ _ _   | _ (_)_ _  __ _ \n" +
				" | (__| / -_) _` | ' \\  |  _/ | ' \\/ _` |\n" +
				"  \\___|_\\___\\__,_|_||_| |_| |_|_||_\\__, |\n" +
				"                                   |___/ \n");

		getLogger().info("§7Loading §dconfiguration§7...");
		configTextFile = new TextFile(getDataFolder().toPath(), "config.yml");
		messagesTextFile = new TextFile(getDataFolder().toPath(), "messages.yml");
		redisTextFile = new TextFile(getDataFolder().toPath(), "redis.yml");

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

		if (BungeeConfig.STATS.get(Boolean.class)) {

			new Metrics(this, 16459);

			getLogger().info("§7Metrics loaded §dsuccessfully§7!");

		}

		if (BungeeConfig.UPDATE_CHECK.get(Boolean.class)) {
			new UpdateCheck(this).getVersion(version -> {
				if (!this.getDescription().getVersion().equals(version)) {
					getLogger().warning("§eThere is a new update available, download it on SpigotMC!");
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

	@Override
	public void onDisable() {

		getLogger().info("§7Clearing §dinstances§7...");
		instance = null;

		getLogger().info("§7Plugin successfully §ddisabled§7!");
	}

}