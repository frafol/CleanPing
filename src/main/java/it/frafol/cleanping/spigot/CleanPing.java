package it.frafol.cleanping.spigot;

import it.frafol.cleanping.spigot.commands.PingCommand;
import it.frafol.cleanping.spigot.commands.ReloadCommand;
import it.frafol.cleanping.spigot.enums.SpigotConfig;
import it.frafol.cleanping.spigot.objects.TextFile;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.Objects;

public class CleanPing extends JavaPlugin {

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

		getLogger().info("Server version: " + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".");
		if (Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_6_R")
				|| Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_5_R")
				|| Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_4_R")
				|| Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_3_R")
				|| Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_2_R")
				|| Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_1_R")
				|| Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_0_R")) {
			getLogger().severe("Support for your version was declined.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		getLogger().info("Loading configuration...");
		configTextFile = new TextFile(getDataFolder().toPath(), "config.yml");

		getLogger().info("Loading §dcommands...");
		Objects.requireNonNull(getCommand("ping")).setExecutor(new PingCommand(this));
		Objects.requireNonNull(getCommand("cleanping")).setExecutor(new PingCommand(this));
		Objects.requireNonNull(getCommand("pingreload")).setExecutor(new ReloadCommand(this));
		Objects.requireNonNull(getCommand("cleanpingreload")).setExecutor(new ReloadCommand(this));

		if (SpigotConfig.STATS.get(Boolean.class)) {

			new Metrics(this, 16505);

			getLogger().info("Metrics loaded successfully!");

		}

		getLogger().info("Plugin successfully loaded!");
	}

	public YamlFile getConfigTextFile() {
		return getInstance().configTextFile.getConfig();
	}

	@Override
	public void onDisable() {

		getLogger().info("Clearing instances...");
		instance = null;

		getLogger().info("Plugin successfully disabled!");
	}

}