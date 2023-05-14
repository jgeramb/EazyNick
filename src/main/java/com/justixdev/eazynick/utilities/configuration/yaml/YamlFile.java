package com.justixdev.eazynick.utilities.configuration.yaml;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.utilities.StringUtils;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.ConfigurationFile;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public abstract class YamlFile implements ConfigurationFile<YamlConfiguration> {

    protected EazyNick eazyNick;
    protected Utils utils;
    private final File file;
    protected YamlConfiguration configuration;

    public YamlFile(EazyNick eazyNick, String subdirectoryName, String fileName) {
        this.eazyNick = eazyNick;
        this.utils = eazyNick.getUtils();

        File directory = new File("plugins/" + eazyNick.getName() + "/" + subdirectoryName);
        this.file = new File(directory, fileName + ".yml");

        // Create directory and file
        if (!directory.exists()) {
            if(!directory.mkdir())
                Bukkit.getLogger().log(Level.WARNING, "[" + eazyNick.getName() + "] Could not create directory '" + directory.getAbsolutePath() + "'");
        }

        if (!this.file.exists()) {
            try {
                if(!this.file.createNewFile())
                    throw new IOException("unknown");
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.WARNING, "[" + eazyNick.getName() + "] Could not create file '" + this.file.getAbsolutePath() + "': " + ex.getMessage());
            }
        }

        // Initialize configuration
        initConfiguration();
    }

    @Override
    public void initConfiguration() {
        this.reloadFile();
        this.setDefaults();
        this.save();
    }

    @Override
    public void save() {
        try {
            // Save configuration to file
            this.configuration.save(this.file);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.WARNING, "[" + this.eazyNick.getName() + "] Could not save configuration '" + this.file.getAbsolutePath() + "': " + ex.getMessage());
        }
    }

    @Override
    public void setDefaults() {
        // Set configuration header & defaults
        PluginDescriptionFile pluginDescriptionFile = this.eazyNick.getDescription();

        StringBuilder bottomLine = new StringBuilder();

        for (int i = 0; i < 80; i++)
            bottomLine.append('_');

        this.configuration
                .options()
                .header("\n"
                                + " _____                _   _ _      _    \n"
                                + "|  ___|              | \\ | (_)    | |   \n"
                                + "| |__  __ _ _____   _|  \\| |_  ___| | __\n"
                                + "|  __|/ _` |_  / | | | . ` | |/ __| |/ /\n"
                                + "| |__| (_| |/ /| |_| | |\\  | | (__|   < \n"
                                + "\\____/\\__,_/___|\\__, \\_| \\_/_|\\___|_|\\_\\\n"
                                + "                 __/ |                  \n"
                                + "                |___/                   \n"
                                + "by " + pluginDescriptionFile.getAuthors().toString().substring(1).replace("]", "") + "\n"
                                + "\n"
                                + "Color codes: https://cdpn.io/mattrowen/fullpage/MWYJYQq\n"
                                + "Bukkit materials (1.13+): https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html\n"
                                + "Bukkit materials: https://bit.ly/old-bukkit-materials\n"
                                + "GitHub: " + pluginDescriptionFile.getWebsite() + "\n"
                                + "Supported languages: de_DE, en_US\n"
                                + "\n"
                                + bottomLine + "\n")
                .copyHeader(true)
                .copyDefaults(true);
    }

    @Override
    public void reload() {
        this.reloadFile();
    }

    protected void reloadFile() {
        // Load configuration from file
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    // Get a string from the configuration and apply 'setPlaceholders' from PlaceholderAPI if it is installed
    public String getConfigString(Player player, String path) {
        String string = getConfigString(path);

        if(EazyNick.getInstance().getUtils().isPluginInstalled("PlaceholderAPI")
                && (player != null))
            string = PlaceholderAPI.setPlaceholders(player, string);

        return string;
    }

    // Get a string from the configuration and convert the color codes and new lines
    public String getConfigString(String path) {
        String string;

        return ((this.configuration.contains(path) && ((string = this.configuration.getString(path)) != null)))
                ? new StringUtils(string.replace("%nl%", "%nl%&0")).getColoredString()
                : "";
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public YamlConfiguration getConfiguration() {
        return this.configuration;
    }

}
