package it.frafol.cleanping.bukkit;

import it.frafol.cleanping.bukkit.commands.PingCommand;
import it.frafol.cleanping.bukkit.commands.ReloadCommand;
import it.frafol.cleanping.bukkit.commands.utils.TabComplete;
import it.frafol.cleanping.bukkit.enums.SpigotConfig;
import it.frafol.cleanping.bukkit.objects.TextFile;
import lombok.SneakyThrows;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.simpleyaml.configuration.file.YamlFile;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

public class CleanPing extends JavaPlugin {

	private TextFile configTextFile;
	private TextFile messagesTextFile;
	public static CleanPing instance;

	public static CleanPing getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {

		instance = this;

		BukkitLibraryManager bukkitLibraryManager = new BukkitLibraryManager(this);

		Library yaml = Library.builder()
				.groupId("me{}carleslc{}Simple-YAML")
				.artifactId("Simple-Yaml")
				.version("1.8.4")
				.build();

		bukkitLibraryManager.addJitPack();
		bukkitLibraryManager.loadLibrary(yaml);

		getLogger().info("\n   ___ _                 ___ _           \n" +
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

		if (isFolia()) {
			getLogger().warning("Support for Folia has not been tested and is only for experimental purposes.");
		}

		getLogger().info("Loading configuration...");
		configTextFile = new TextFile(getDataFolder().toPath(), "config.yml");
		messagesTextFile = new TextFile(getDataFolder().toPath(), "messages.yml");

		getLogger().info("Loading commands...");

		if (Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_7_R")
				|| Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_8_R")
				|| Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_9_R")
				|| Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_10_R")
				|| Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_11_R")
				|| Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_12_R")
				|| Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_13_R")
				|| Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_14_R")
				|| Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_15_R")) {

			Objects.requireNonNull(getCommand("ping")).setExecutor(new it.frafol.cleanping.bukkit.commands.legacy.PingCommand(this));
			Objects.requireNonNull(getCommand("cleanping")).setExecutor(new it.frafol.cleanping.bukkit.commands.legacy.PingCommand(this));
			Objects.requireNonNull(getCommand("pingreload")).setExecutor(new it.frafol.cleanping.bukkit.commands.legacy.ReloadCommand(this));
			Objects.requireNonNull(getCommand("cleanpingreload")).setExecutor(new it.frafol.cleanping.bukkit.commands.legacy.ReloadCommand(this));

		} else {

			Objects.requireNonNull(getCommand("ping")).setExecutor(new PingCommand(this));
			Objects.requireNonNull(getCommand("cleanping")).setExecutor(new PingCommand(this));
			Objects.requireNonNull(getCommand("pingreload")).setExecutor(new ReloadCommand(this));
			Objects.requireNonNull(getCommand("cleanpingreload")).setExecutor(new ReloadCommand(this));

		}

		Objects.requireNonNull(getCommand("ping")).setTabCompleter(new TabComplete());
		Objects.requireNonNull(getCommand("cleanping")).setTabCompleter(new TabComplete());

		if (SpigotConfig.STATS.get(Boolean.class)) {

			new Metrics(this, 16505);

			getLogger().info("Metrics loaded successfully!");

		}

		if (!isFolia()) {
			if (SpigotConfig.UPDATE_CHECK.get(Boolean.class)) {
				new UpdateCheck(this).getVersion(version -> {
					if (!this.getDescription().getVersion().equals(version)) {
						getLogger().warning("There is a new update available, download it on SpigotMC!");
					}
				});
			}
		} else {
			getLogger().severe("Folia does not support the update checker.");
		}


		getLogger().info("Plugin successfully loaded!");
	}

	public static boolean isFolia() {
		try {
			Class.forName("io.papermc.paper.threadedregions.RegionizedServerInitEvent");
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}

	public YamlFile getConfigTextFile() {
		return getInstance().configTextFile.getConfig();
	}

	public YamlFile getMessagesTextFile() {
		return getInstance().messagesTextFile.getConfig();
	}

	@SneakyThrows
	public static int getPing(Player player) { // Legacy Method.
		String v = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		try {
			Class<?> CraftPlayerClass = Class.forName("org.bukkit.craftbukkit." + v + ".entity.CraftPlayer");
			Object CraftPlayer = CraftPlayerClass.cast(player);
			Method getHandle = CraftPlayer.getClass().getMethod("getHandle");
			Object EntityPlayer = getHandle.invoke(CraftPlayer);
			Field ping = EntityPlayer.getClass().getDeclaredField("ping");
			return ping.getInt(EntityPlayer);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	@Override
	public void onDisable() {

		getLogger().info("Clearing instances...");
		instance = null;

		getLogger().info("Plugin successfully disabled!");
	}

}