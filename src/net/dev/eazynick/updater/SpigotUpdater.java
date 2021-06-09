package net.dev.eazynick.updater;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utilities.Utils;

public class SpigotUpdater {

	public boolean checkForUpdates() {
		EazyNick eazyNick = EazyNick.getInstance();
		
		PluginDescriptionFile desc = eazyNick.getDescription();
		
		System.out.println("[Updater] Checking for updates...");
		
		ReadableByteChannel channel = null;
		double newVersion = 0.0;
		
		File file = eazyNick.getPluginFile();

		//Parse latest version from spigotmc.org as double
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://api.spigotmc.org/legacy/update.php?resource=51398").openStream()))) {
			newVersion = Double.valueOf(reader.readLine()).doubleValue();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		//Check if version is outdated
		if (newVersion > Double.valueOf(desc.getVersion()).doubleValue()) {
			System.out.println("[Updater] Found a new version (" + newVersion + ")");

			if (eazyNick.getSetupYamlFile().getConfiguration().getBoolean("AutoUpdater")) {
				System.out.println("[Updater] Starting download...");

				try {
					//Open connection
					HttpURLConnection downloadURL = (HttpURLConnection) new URL("https://www.justix-dev.de/go/dl?id=1&ver=v" + newVersion).openConnection();
					downloadURL.setRequestProperty("User-Agent", "JustixDevelopment/Updater");
					
					//Open channel for downloading new file
					channel = Channels.newChannel(downloadURL.getInputStream());
				} catch (IOException ex) {
					throw new RuntimeException("Download failed", ex);
				}

				//Write downloaded file to file system
				try(FileOutputStream output = new FileOutputStream(file)) {
					output.getChannel().transferFrom(channel, 0L, Long.MAX_VALUE);
					output.flush();
				} catch (IOException ex) {
					throw new RuntimeException("File could not be saved", ex);
				}

				System.out.println("[Updater] Successfully updated plugin to version " + newVersion + ". Please reload/restart your server now.");
				
				return true;
			} else
				System.out.println("[Updater] Download the update here: " + desc.getWebsite());
		} else
			System.out.println("[Updater] No new version available");
		
		return false;
	}
	
	public void checkForUpdates(Player player) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		
		String prefix = utils.getPrefix();
		
		PluginDescriptionFile desc = eazyNick.getDescription();
		
		player.sendMessage(prefix + "§3Updater §8» §fChecking for updates...");
		
		ReadableByteChannel channel = null;
		double newVersion = 0.0;
		
		File file = eazyNick.getPluginFile();

		//Parse latest version from spigotmc.org as double
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://api.spigotmc.org/legacy/update.php?resource=51398").openStream()))) {
			newVersion = Double.valueOf(reader.readLine()).doubleValue();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		//Check if version is outdated
		if (newVersion > Double.valueOf(desc.getVersion()).doubleValue()) {
			player.sendMessage(prefix + "§3Updater §8» §fFound a new version (" + newVersion + ")");

			if (eazyNick.getSetupYamlFile().getConfiguration().getBoolean("AutoUpdater")) {
				player.sendMessage(prefix + "§3Updater §8» §fStarting download...");

				try {
					//Open connection
					HttpURLConnection downloadURL = (HttpURLConnection) new URL("https://www.justix-dev.de/go/dl?id=1&ver=v" + newVersion).openConnection();
					downloadURL.setRequestProperty("User-Agent", "JustixDevelopment/Updater");
					
					//Open channel for downloading new file
					channel = Channels.newChannel(downloadURL.getInputStream());
				} catch (IOException ex) {
					throw new RuntimeException("Download failed", ex);
				}

				//Write downloaded file to file system
				try(FileOutputStream output = new FileOutputStream(file)) {
					output.getChannel().transferFrom(channel, 0L, Long.MAX_VALUE);
					output.flush();
				} catch (IOException ex) {
					throw new RuntimeException("File could not be saved", ex);
				}

				player.sendMessage(prefix + "§3Updater §8» §fSuccessfully updated plugin to version " + newVersion + ". Please restart/reload your server");
			} else
				player.sendMessage(prefix + "§3Updater §8» §fDownload the update here: " + desc.getWebsite());
		} else
			player.sendMessage(prefix + "§3Updater §8» §fNo new version available");
	}

}
