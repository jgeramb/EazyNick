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

public class Updater {

	private final PluginDescriptionFile pluginDescription;
	private final String prefix;
	private final EazyNick eazyNick;
	private final SetupYamlFile setupYamlFile;
	private final Utils utils;
	
	public Updater(EazyNick eazyNick) {
		this.pluginDescription = eazyNick.getDescription();
		this.prefix = "[" + pluginDescription.getName() + "-Updater] ";
		this.eazyNick = eazyNick;
		this.setupYamlFile = eazyNick.getSetupYamlFile();
		this.utils = eazyNick.getUtils();
	}

	public boolean checkForUpdates() {
		Bukkit.getLogger().log(Level.INFO, prefix + "Checking for updates...");

		// Fetch the latest version from GitHub repository
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(
				"https://raw.githubusercontent.com/JustixDevelopment/EazyNick/master/.github/.version"
		).openStream()))) {
			final String publishedVersion = reader.readLine();
			final VersionComparisonResult versionComparisonResult = compareVersions(pluginDescription.getVersion(), publishedVersion);

			// Check if version is up-to-date
			if (versionComparisonResult.equals(VersionComparisonResult.UP_TO_DATE) || versionComparisonResult.equals(VersionComparisonResult.DEV)) {
				Bukkit.getLogger().log(Level.INFO, "No new version available");
				return false;
			}

			Bukkit.getLogger().log(Level.INFO, prefix + "Found a new version: " + publishedVersion);

			if (
					!(setupYamlFile.getConfiguration().getBoolean("AutoUpdater"))
					|| (
							!(setupYamlFile.getConfiguration().getBoolean("AutoUpdatePreReleases"))
							&& versionComparisonResult.equals(VersionComparisonResult.OUTDATED_PRE_RELEASE)
					)
			) {
				Bukkit.getLogger().log(Level.INFO, prefix + "Download the update here: " + pluginDescription.getWebsite());
				return true;
			}

			Bukkit.getLogger().log(Level.INFO, prefix + "Starting download...");

			try {
				// Open connection to download server
				HttpsURLConnection downloadConnection = (HttpsURLConnection) new URL(
						"https://github.com/JustixDevelopment/EazyNick/releases/latest/download/EazyNick.jar"
				).openConnection();
				downloadConnection.setRequestProperty("User-Agent", "JustixDevelopment/Updater " + pluginDescription.getVersion());
				downloadConnection.setInstanceFollowRedirects(true);

				// Download file
				ReadableByteChannel fileChannel = Channels.newChannel(downloadConnection.getInputStream());

				// Save downloaded file
				try(FileOutputStream localFileStream = new FileOutputStream(eazyNick.getPluginFile())) {
					localFileStream.getChannel().transferFrom(fileChannel, 0L, Long.MAX_VALUE);
					localFileStream.flush();
				} catch (IOException ex) {
					Bukkit.getLogger().log(Level.SEVERE, "Could not save file: " + ex.getMessage());
					return false;
				}

				Bukkit.getLogger().log(Level.INFO, prefix + "Successfully updated plugin to version '" + publishedVersion + "', please restart/reload your server");

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

		player.sendMessage(prefix + "§aUpdater §8» §7Checking for updates...");

		// Fetch the latest version from GitHub repository
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(
				"https://raw.githubusercontent.com/JustixDevelopment/EazyNick/master/.github/.version"
		).openStream()))) {
			final String publishedVersion = reader.readLine();
			final VersionComparisonResult versionComparisonResult = compareVersions(pluginDescription.getVersion(), publishedVersion);

			// Check if version is up-to-date
			if (versionComparisonResult.equals(VersionComparisonResult.UP_TO_DATE) || versionComparisonResult.equals(VersionComparisonResult.DEV)) {
				player.sendMessage(prefix + "§aUpdater §8» §cNo new version available");
				return;
			}

			player.sendMessage(prefix + "§aUpdater §8» §7Found a new version: §d" + publishedVersion);

			Bukkit.getLogger().log(Level.INFO, prefix + "Found a new version: " + publishedVersion);

			if (
					!(setupYamlFile.getConfiguration().getBoolean("AutoUpdater"))
					|| (
							!(setupYamlFile.getConfiguration().getBoolean("AutoUpdatePreReleases"))
							&& versionComparisonResult.equals(VersionComparisonResult.OUTDATED_PRE_RELEASE)
					)
			) {
				player.sendMessage(prefix + "§aUpdater §8» §7Download the update here§8: §d" + pluginDescription.getWebsite());
				return;
			}

			player.sendMessage(prefix + "§aUpdater §8» §7Starting download...");

			try {
				// Open connection to download server
				HttpsURLConnection downloadConnection = (HttpsURLConnection) new URL(
						"https://github.com/JustixDevelopment/EazyNick/releases/latest/download/EazyNick.jar"
				).openConnection();
				downloadConnection.setRequestProperty("User-Agent", "JustixDevelopment/Updater " + pluginDescription.getVersion());
				downloadConnection.setInstanceFollowRedirects(true);

				// Download file
				ReadableByteChannel fileChannel = Channels.newChannel(downloadConnection.getInputStream());

				// Save downloaded file
				try(FileOutputStream localFileStream = new FileOutputStream(eazyNick.getPluginFile())) {
					localFileStream.getChannel().transferFrom(fileChannel, 0L, Long.MAX_VALUE);
					localFileStream.flush();
				} catch (IOException ex) {
					Bukkit.getLogger().log(Level.SEVERE, "Could not save file: " + ex.getMessage());
					return;
				}

				player.sendMessage(prefix + "§aUpdater §8» §7Successfully updated plugin to version §8'§d" + publishedVersion + "§8'§7, please restart/reload your server");
			} catch (IOException ex) {
				Bukkit.getLogger().log(Level.SEVERE, "Could not download file: " + ex.getMessage());
			}
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not fetch latest version: " + ex.getMessage());
		}
	}

	public VersionComparisonResult compareVersions(String current, String published) {
		String[] versionPartsCurrent = current.split("\\."), versionPartsPublished = published.split("\\.");

		if((versionPartsCurrent.length == 3) && (versionPartsPublished.length == 3)) {
			int[] currentVersion = new int[] {
					Integer.parseInt(versionPartsCurrent[0]),
					Integer.parseInt(versionPartsCurrent[1]),
					Integer.parseInt(versionPartsCurrent[2])
			}, publishedVersion = new int[] {
					Integer.parseInt(versionPartsPublished[0]),
					Integer.parseInt(versionPartsPublished[1]),
					Integer.parseInt(versionPartsPublished[2])
			};

			if(
					(publishedVersion[0] == currentVersion[0])
					&& (publishedVersion[1] == currentVersion[1])
					&& (publishedVersion[2] == currentVersion[2])
			)
				return VersionComparisonResult.UP_TO_DATE;

			if(
					(currentVersion[0] > publishedVersion[0])
					|| (currentVersion[1] > publishedVersion[1])
					|| (currentVersion[2] > publishedVersion[2])
			)
				return VersionComparisonResult.DEV;

			if(
					(publishedVersion[0] == currentVersion[0])
					&& (publishedVersion[1] == currentVersion[1])
					&& (publishedVersion[2] > currentVersion[2])
			)
				return VersionComparisonResult.OUTDATED_PRE_RELEASE;
		}

		return VersionComparisonResult.OUTDATED_RELEASE;
	}

}
