package com.justixdev.eazynick.updater;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;

public class SpigotUpdater {

	private final PluginDescriptionFile pluginDescription;
	private final String prefix;
	private final EazyNick eazyNick;
	private final SetupYamlFile setupYamlFile;
	private final Utils utils;
	
	public SpigotUpdater(EazyNick eazyNick) {
		this.pluginDescription = eazyNick.getDescription();
		this.prefix = "[" + pluginDescription.getName() + "-Updater] ";
		this.eazyNick = eazyNick;
		this.setupYamlFile = eazyNick.getSetupYamlFile();
		this.utils = eazyNick.getUtils();
	}

	public boolean checkForUpdates() {
		String currentVersion = pluginDescription.getVersion();

		if(currentVersion.equals("0.0.0"))
			return false;

		Bukkit.getLogger().log(Level.INFO, prefix + "Checking for updates...");

		// Fetch the latest version from spigotmc.org
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://raw.githubusercontent.com/JustixDevelopment/EazyNick/master/.github/.version").openStream()))) {
			final String latestVersion = reader.readLine();

			// Check if version is up-to-date
			if (latestVersion.equals(currentVersion)) {
				Bukkit.getLogger().log(Level.INFO, "No new version available");
				return false;
			}

			Bukkit.getLogger().log(Level.INFO, prefix + "Found a new version: " + latestVersion);

			if (!(setupYamlFile.getConfiguration().getBoolean("AutoUpdater"))) {
				Bukkit.getLogger().log(Level.INFO, prefix + "Download the update here: " + pluginDescription.getWebsite());
				return true;
			}

			Bukkit.getLogger().log(Level.INFO, prefix + "Starting download...");

			try {
				// Open connection to download server
				HttpsURLConnection downloadConnection = (HttpsURLConnection) new URL("https://github.com/JustixDevelopment/EazyNick/releases/latest/download/EazyNick.jar").openConnection();
				downloadConnection.setRequestProperty("User-Agent", "JustixDevelopment/Updater " + pluginDescription.getVersion());

				// Download file
				ReadableByteChannel fileChannel = Channels.newChannel(downloadConnection.getInputStream());

				// Save downloaded file
				try(FileOutputStream localFileStream = new FileOutputStream(eazyNick.getPluginFile())) {
					localFileStream.getChannel().transferFrom(fileChannel, 0L, Long.MAX_VALUE);
					localFileStream.flush();
				} catch (IOException ex) {
					Bukkit.getLogger().log(Level.SEVERE, "Could not save file: " + ex.getMessage());
				}

				Bukkit.getLogger().log(Level.INFO, prefix + "Successfully updated plugin to version '" + latestVersion + "', please restart/reload your server");

				return true;
			} catch (IOException ex) {
				Bukkit.getLogger().log(Level.SEVERE, "Could not download file: " + ex.getMessage());
			}
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not fetch latest version: " + ex.getMessage());
		}
		
		return false;
	}
	
	public void checkForUpdates(Player player) {
		final String prefix = utils.getPrefix();

		Bukkit.getLogger().log(Level.INFO, "§aUpdater §8» §7Checking for updates...");

		// Fetch the latest version from spigotmc.org
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://raw.githubusercontent.com/JustixDevelopment/EazyNick/master/.github/.version").openStream()))) {
			final String latestVersion = reader.readLine();

			// Check if version is up-to-date
			if (latestVersion.equals(pluginDescription.getVersion()))
				player.sendMessage(prefix + "§aUpdater §8» §cNo new version available");

			player.sendMessage(prefix + "§aUpdater §8» §7Found a new version: §d" + latestVersion);

			if (!(setupYamlFile.getConfiguration().getBoolean("AutoUpdater")))
				player.sendMessage(prefix + "§aUpdater §8» §7Download the update here§8: §d" + pluginDescription.getWebsite());

			player.sendMessage(prefix + "§aUpdater §8» §7Starting download...");

			try {
				// Open connection to download server
				HttpsURLConnection downloadConnection = (HttpsURLConnection) new URL("https://github.com/JustixDevelopment/EazyNick/releases/latest/download/EazyNick.jar").openConnection();
				downloadConnection.setRequestProperty("User-Agent", "JustixDevelopment/Updater " + pluginDescription.getVersion());

				// Download file
				ReadableByteChannel fileChannel = Channels.newChannel(downloadConnection.getInputStream());

				// Save downloaded file
				try(FileOutputStream localFileStream = new FileOutputStream(eazyNick.getPluginFile())) {
					localFileStream.getChannel().transferFrom(fileChannel, 0L, Long.MAX_VALUE);
					localFileStream.flush();
				} catch (IOException ex) {
					Bukkit.getLogger().log(Level.SEVERE, "Could not save file: " + ex.getMessage());
				}

				player.sendMessage(prefix + "§aUpdater §8» §7Successfully updated plugin to version §8'§d" + latestVersion + "§8'§7, please restart/reload your server");
			} catch (IOException ex) {
				Bukkit.getLogger().log(Level.SEVERE, "Could not download file: " + ex.getMessage());
			}
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not fetch latest version: " + ex.getMessage());
		}
	}

}
