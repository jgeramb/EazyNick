package com.justixdev.eazynick.updater;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;

public class Updater {

    private final File file;
    private final String prefix;

    private final PluginDescriptionFile pluginDescription;
    private final SetupYamlFile setupYamlFile;
    private final Utils utils;

    public Updater(EazyNick eazyNick, File file) {
        this.file = file;
        this.prefix = "[" + eazyNick.getName() + "-Updater] ";

        this.pluginDescription = eazyNick.getDescription();
        this.setupYamlFile = eazyNick.getSetupYamlFile();
        this.utils = eazyNick.getUtils();
    }

    public boolean checkForUpdates() {
        Bukkit.getLogger().log(Level.INFO, this.prefix + "Checking for updates...");

        // Fetch the latest version from GitHub repository
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(
                "https://raw.githubusercontent.com/JustixDevelopment/EazyNick/master/.github/.version"
        ).openStream()))) {
            final String currentVersion = this.pluginDescription.getVersion(), publishedVersion = reader.readLine();
            final VersionComparisonResult versionComparisonResult = compareVersions(currentVersion, publishedVersion);

            // don't update if jar was built incorrectly or if it is a dev release
            if(currentVersion.equals("0.0.0"))
                return false;

            // Check if version is up-to-date
            if (versionComparisonResult.equals(VersionComparisonResult.UP_TO_DATE)
                    || versionComparisonResult.equals(VersionComparisonResult.DEV)) {
                Bukkit.getLogger().log(Level.INFO, this.prefix + "No new version available");
                return false;
            }

            Bukkit.getLogger().log(Level.INFO, this.prefix + "Found a new version: " + publishedVersion);

            if (
                    !this.setupYamlFile.getConfiguration().getBoolean("AutoUpdater")
                    || (!this.setupYamlFile.getConfiguration().getBoolean("AutoUpdatePreReleases")
                            && versionComparisonResult.equals(VersionComparisonResult.OUTDATED_PRE_RELEASE)
                    )
            ) {
                Bukkit.getLogger().log(Level.INFO, this.prefix + "Download the update here: " + this.pluginDescription.getWebsite());
                return true;
            }

            Bukkit.getLogger().log(Level.INFO, this.prefix + "Starting download...");

            try {
                // Open connection to download server
                HttpsURLConnection downloadConnection = (HttpsURLConnection) new URL(
                        "https://github.com/JustixDevelopment/EazyNick/releases/latest/download/EazyNick.jar"
                ).openConnection();
                downloadConnection.setRequestProperty("User-Agent", "JustixDevelopment/Updater " + this.pluginDescription.getVersion());
                downloadConnection.setInstanceFollowRedirects(true);

                // Download file
                ReadableByteChannel fileChannel = Channels.newChannel(downloadConnection.getInputStream());

                // Save downloaded file
                try(FileOutputStream localFileStream = new FileOutputStream(this.file)) {
                    localFileStream.getChannel().transferFrom(fileChannel, 0L, Long.MAX_VALUE);
                    localFileStream.flush();
                } catch (IOException ex) {
                    Bukkit.getLogger().log(Level.SEVERE, this.prefix + "Could not save file: " + ex.getMessage());
                    return false;
                }

                Bukkit.getLogger().log(Level.INFO, this.prefix + "Successfully updated plugin to version '" + publishedVersion + "', please restart/reload your server");

                return true;
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.SEVERE, this.prefix + "Could not download file: " + ex.getMessage());
            }
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, this.prefix + "Could not fetch latest version: " + ex.getMessage());
        }

        return false;
    }

    public void checkForUpdates(Player player) {
        final String prefix = this.utils.getPrefix();

        player.sendMessage(prefix + "§aUpdater §8» §7Checking for updates...");

        // Fetch the latest version from GitHub repository
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(
                "https://raw.githubusercontent.com/JustixDevelopment/EazyNick/master/.github/.version"
        ).openStream()))) {
            final String currentVersion = this.pluginDescription.getVersion(), publishedVersion = reader.readLine();
            final VersionComparisonResult versionComparisonResult = compareVersions(currentVersion, publishedVersion);

            // don't update if jar was built incorrectly or if it is a dev release
            if(currentVersion.equals("0.0.0"))
                return;

            // Check if version is up-to-date
            if (versionComparisonResult.equals(VersionComparisonResult.UP_TO_DATE) || versionComparisonResult.equals(VersionComparisonResult.DEV)) {
                player.sendMessage(prefix + "§aUpdater §8» §cNo new version available");
                return;
            }

            player.sendMessage(prefix + "§aUpdater §8» §7Found a new version: §d" + publishedVersion);

            Bukkit.getLogger().log(Level.INFO, prefix + "Found a new version: " + publishedVersion);

            if (
                    !this.setupYamlFile.getConfiguration().getBoolean("AutoUpdater")
                    || (!this.setupYamlFile.getConfiguration().getBoolean("AutoUpdatePreReleases")
                            && versionComparisonResult.equals(VersionComparisonResult.OUTDATED_PRE_RELEASE)
                    )
            ) {
                player.sendMessage(prefix + "§aUpdater §8» §7Download the update here§8: §d" + this.pluginDescription.getWebsite());
                return;
            }

            if (
                    !(this.setupYamlFile.getConfiguration().getBoolean("AutoUpdater"))
                    || (!this.setupYamlFile.getConfiguration().getBoolean("AutoUpdatePreReleases")
                            && versionComparisonResult.equals(VersionComparisonResult.OUTDATED_PRE_RELEASE)
                    )
            ) {
                return;
            }

            player.sendMessage(prefix + "§aUpdater §8» §7Starting download...");

            try {
                // Open connection to download server
                HttpsURLConnection downloadConnection = (HttpsURLConnection) new URL(
                        "https://github.com/JustixDevelopment/EazyNick/releases/latest/download/EazyNick.jar"
                ).openConnection();
                downloadConnection.setRequestProperty("User-Agent", "JustixDevelopment/Updater " + this.pluginDescription.getVersion());
                downloadConnection.setInstanceFollowRedirects(true);

                // Download file
                ReadableByteChannel fileChannel = Channels.newChannel(downloadConnection.getInputStream());

                // Save downloaded file
                try(FileOutputStream localFileStream = new FileOutputStream(this.file)) {
                    localFileStream.getChannel().transferFrom(fileChannel, 0L, Long.MAX_VALUE);
                    localFileStream.flush();
                } catch (IOException ex) {
                    Bukkit.getLogger().log(Level.SEVERE, prefix + "Could not save file: " + ex.getMessage());
                    return;
                }

                player.sendMessage(prefix + "§aUpdater §8» §7Successfully updated plugin to version §8'§d" + publishedVersion + "§8'§7, please restart/reload your server");
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.SEVERE, prefix + "Could not download file: " + ex.getMessage());
            }
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, prefix + "Could not fetch latest version: " + ex.getMessage());
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
