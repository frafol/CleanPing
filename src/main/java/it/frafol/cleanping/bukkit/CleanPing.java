package it.frafol.cleanping.bukkit;

import com.tchristofferson.configupdater.ConfigUpdater;
import it.frafol.cleanping.bukkit.commands.PingCommand;
import it.frafol.cleanping.bukkit.commands.ReloadCommand;
import it.frafol.cleanping.bukkit.commands.utils.TabComplete;
import it.frafol.cleanping.bukkit.enums.SpigotConfig;
import it.frafol.cleanping.bukkit.enums.SpigotVersion;
import it.frafol.cleanping.bukkit.objects.TextFile;
import lombok.SneakyThrows;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
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
import java.util.Collections;
import java.util.Objects;

public class CleanPing extends JavaPlugin {

	private TextFile configTextFile;
	private TextFile messagesTextFile;
	private TextFile versionTextFile;
	public static CleanPing instance;

	public boolean updated = false;

	public static CleanPing getInstance() {
		return instance;
	}

	@SneakyThrows
	@Override
	public void onEnable() {

		instance = this;

		BukkitLibraryManager bukkitLibraryManager = new BukkitLibraryManager(this);

		Library yaml;
		yaml = Library.builder()
				.groupId("me{}carleslc{}Simple-YAML")
				.artifactId("Simple-Yaml")
				.version("1.8.4")
				.build();

		bukkitLibraryManager.addJitPack();

		try {
			bukkitLibraryManager.loadLibrary(yaml);
		} catch (RuntimeException ignored) {
			getLogger().severe("Failed to load Simple-YAML library. Trying to download it from GitHub...");
			yaml = Library.builder()
					.groupId("me{}carleslc{}Simple-YAML")
					.artifactId("Simple-Yaml")
					.version("1.8.4")
					.url("https://github.com/Carleslc/Simple-YAML/releases/download/1.8.4/Simple-Yaml-1.8.4.jar")
					.build();
		}

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
		versionTextFile = new TextFile(getDataFolder().toPath(), "version.yml");
		File configFile = new File(getDataFolder(), "config.yml");
		File messagesFile = new File(getDataFolder(), "messages.yml");

		if (!getDescription().getVersion().equals(SpigotVersion.VERSION.get(String.class))) {

			getLogger().info("Creating new configurations...");
			try {
				ConfigUpdater.update(this, "config.yml", configFile, Collections.emptyList());
				ConfigUpdater.update(this, "messages.yml", messagesFile, Collections.emptyList());
			} catch (IOException exception) {
				getLogger().severe("Unable to update configuration file, see the error below:");
				exception.printStackTrace();
			}

			versionTextFile.getConfig().set("version", getDescription().getVersion());
			versionTextFile.getConfig().save();
			configTextFile = new TextFile(getDataFolder().toPath(), "config.yml");
			messagesTextFile = new TextFile(getDataFolder().toPath(), "messages.yml");
			versionTextFile = new TextFile(getDataFolder().toPath(), "version.yml");

		}

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

	public YamlFile getVersionTextFile() {
		return getInstance().versionTextFile.getConfig();
	}

	@SneakyThrows
	public static int getPing(Player player) {
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

	public void autoUpdate() {
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