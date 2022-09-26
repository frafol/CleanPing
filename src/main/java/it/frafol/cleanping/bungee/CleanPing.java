package it.frafol.cleanping.bungee;

import it.frafol.cleanping.bungee.commands.PingCommand;
import it.frafol.cleanping.bungee.commands.ReloadCommand;
import it.frafol.cleanping.bungee.enums.BungeeConfig;
import it.frafol.cleanping.bungee.objects.TextFile;
import net.md_5.bungee.api.plugin.Plugin;
import org.simpleyaml.configuration.file.YamlFile;

public class CleanPing extends Plugin {

	private TextFile configTextFile;
	public static CleanPing instance;

	public static CleanPing getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {

		instance = this;

		getLogger().info("\n§d   ___ _                 ___ _           \n" +
				"  / __| |___ __ _ _ _   | _ (_)_ _  __ _ \n" +
				" | (__| / -_) _` | ' \\  |  _/ | ' \\/ _` |\n" +
				"  \\___|_\\___\\__,_|_||_| |_| |_|_||_\\__, |\n" +
				"                                   |___/ \n");

		getLogger().info("§7Loading §dconfiguration§7...");
		configTextFile = new TextFile(getDataFolder().toPath(), "config.yml");

		getLogger().info("§7Loading §dcommands§7...");
		getProxy().getPluginManager().registerCommand(this, new PingCommand());
		getProxy().getPluginManager().registerCommand(this, new ReloadCommand());

		if (BungeeConfig.STATS.get(Boolean.class)) {

			new Metrics(this, 16459);

			getLogger().info("§7Metrics loaded §dsuccessfully§7!");

		}

		getLogger().info("§7Plugin §dsuccessfully §7loaded!");
	}

	public YamlFile getConfigTextFile() {
		return getInstance().configTextFile.getConfig();
	}

	@Override
	public void onDisable() {

		getLogger().info("§7Clearing §dinstances§7...");
		instance = null;

		getLogger().info("§7Plugin successfully §ddisabled§7!");
	}

}