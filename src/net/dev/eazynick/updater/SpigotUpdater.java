package net.dev.eazynick.updater;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utils.Utils;

public class SpigotUpdater {

	public boolean checkForUpdates() {
		EazyNick eazyNick = EazyNick.getInstance();
		
		PluginDescriptionFile desc = eazyNick.getDescription();
		
		System.out.println("[Updater] Checking for updates...");
		
		ReadableByteChannel channel = null;
		double newVersion = 0.0;
		
		File file = eazyNick.getPluginFile();

		try {
			URL versionURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=51398");
			BufferedReader reader = new BufferedReader(new InputStreamReader(versionURL.openStream()));
			newVersion = Double.valueOf(reader.readLine()).doubleValue();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (newVersion > Double.valueOf(desc.getVersion()).doubleValue()) {
			System.out.println("[Updater] Found a new version (" + newVersion + ")");

			if (eazyNick.getFileUtils().getConfig().getBoolean("AutoUpdater")) {
				System.out.println("[Updater] Starting download...");

				try {
					HttpURLConnection downloadURL = (HttpURLConnection) new URL("https://www.justix-dev.de/go/download_file?id=1&version=v" + newVersion).openConnection();
					downloadURL.setRequestProperty("User-Agent", "JustixDevelopment/Updater");
					channel = Channels.newChannel(downloadURL.getInputStream());
				} catch (IOException e) {
					throw new RuntimeException("Download failed", e);
				}

				try {
					FileOutputStream output = new FileOutputStream(file);
					output.getChannel().transferFrom(channel, 0L, Long.MAX_VALUE);
					output.flush();
					output.close();
				} catch (IOException e) {
					throw new RuntimeException("File could not be saved", e);
				}

				System.out.println("[Updater] Successfully updated plugin to version " + newVersion + ". Please reload/restart your server now.");
				
				return true;
			} else
				System.out.println("[Updater] Download the update here: " + desc.getWebsite());
		} else
			System.out.println("[Updater] No new version available");
		
		return false;
	}
	
	public void checkForUpdates(Player p) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		
		PluginDescriptionFile desc = eazyNick.getDescription();
		
		p.sendMessage(utils.getPrefix() + "§3Updater §8» §fChecking for updates...");
		
		ReadableByteChannel channel = null;
		double newVersion = 0.0;
		
		File file = eazyNick.getPluginFile();

		try {
			URL versionURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=51398");
			BufferedReader reader = new BufferedReader(new InputStreamReader(versionURL.openStream()));
			newVersion = Double.valueOf(reader.readLine()).doubleValue();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (newVersion > Double.valueOf(desc.getVersion()).doubleValue()) {
			p.sendMessage(utils.getPrefix() + "§3Updater §8» §fFound a new version (" + newVersion + ")");

			if (eazyNick.getFileUtils().getConfig().getBoolean("AutoUpdater")) {
				p.sendMessage(utils.getPrefix() + "§3Updater §8» §fStarting download...");

				try {
					HttpURLConnection downloadURL = (HttpURLConnection) new URL("https://www.justix-dev.de/go/download_file?id=1&version=v" + newVersion).openConnection();
					downloadURL.setRequestProperty("User-Agent", "JustixDevelopment/Updater");
					channel = Channels.newChannel(downloadURL.getInputStream());
				} catch (IOException e) {
					throw new RuntimeException("Download failed", e);
				}

				try {
					FileOutputStream output = new FileOutputStream(file);
					output.getChannel().transferFrom(channel, 0L, Long.MAX_VALUE);
					output.flush();
					output.close();
				} catch (IOException e) {
					throw new RuntimeException("File could not be saved", e);
				}

				p.sendMessage(utils.getPrefix() + "§3Updater §8» §fSuccessfully updated plugin to version " + newVersion + ". Please restart/reload your server");
			} else
				p.sendMessage(utils.getPrefix() + "§3Updater §8» §fDownload the update here: " + desc.getWebsite());
		} else
			p.sendMessage(utils.getPrefix() + "§3Updater §8» §fNo new version available");
	}

}
