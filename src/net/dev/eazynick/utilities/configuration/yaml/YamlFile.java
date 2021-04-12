package net.dev.eazynick.utilities.configuration.yaml;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utilities.configuration.ConfigurationFile;

import me.clip.placeholderapi.PlaceholderAPI;

public abstract class YamlFile implements ConfigurationFile<YamlConfiguration> {

	private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");
	
	protected EazyNick eazyNick;
	private File directory, file;
	protected YamlConfiguration configuration;
	
	public YamlFile(EazyNick eazyNick, String subdirectoryName, String fileName) {
		this.eazyNick = eazyNick;
		this.directory = new File("plugins/" + eazyNick.getDescription().getName() + "/" + subdirectoryName);
		this.file = new File(directory, fileName);
				
		if (!(directory.exists()))
			directory.mkdir();

		if (!(file.exists())) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		this.configuration = YamlConfiguration.loadConfiguration(file);
	}
	
	@Override
	public void initConfiguration() {
		PluginDescriptionFile pluginDescriptionFile = eazyNick.getDescription();
		
		configuration.options().header("-------------------------------------------------------------------\n"
				+ "\n"
				+ " _____                _   _ _      _    \n"
				+ "|  ___|              | \\ | (_)    | |   \n"
				+ "| |__  __ _ _____   _|  \\| |_  ___| | __\n"
				+ "|  __|/ _` |_  / | | | . ` | |/ __| |/ /\n"
				+ "| |__| (_| |/ /| |_| | |\\  | | (__|   < \n"
				+ "\\____/\\__,_/___|\\__, \\_| \\_/_|\\___|_|\\_\\\n"
				+ "                 __/ |                  \n"
				+ "                |___/                   \n"
				+ "by " + pluginDescriptionFile.getAuthors().toString().replace("[", "").replace("]", "") + "\n"
				+ "\n"
				+ "Color codes: https://cdpn.io/mattrowen/fullpage/MWYJYQq\n"
				+ "Bukkit materials: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html\n"
				+ "Resource page: " + pluginDescriptionFile.getWebsite() + "\n"
				+ "Languages: de_DE, en_US\n"
				+ "\n"
				+ "-------------------------------------------------------------------\n");
		
		setDefaults();
	
		configuration.options().copyDefaults(true);
		configuration.options().copyHeader(true);
		save();
	}
	
	@Override
	public abstract void setDefaults();
	
	@Override
	public void save() {
		try {
			configuration.save(file);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void reload() {
		this.configuration = YamlConfiguration.loadConfiguration(file);
		save();
	}
	
	public String getConfigString(Player player, String path) {
		if(configuration.contains(path)) {
			String string = ChatColor.translateAlternateColorCodes('&', configuration.getString(path));
			
			if(eazyNick.getVersion().startsWith("1_16")) {
				try {
					Matcher match = HEX_COLOR_PATTERN.matcher(string);
					
					while (match.find()) {
						String color = string.substring(match.start(), match.end());
						string = string.replace(color, ChatColor.class.getMethod("of", String.class).invoke(null, color) + "");
						match = HEX_COLOR_PATTERN.matcher(string);
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
					ex.printStackTrace();
				}
			}
			
			if(EazyNick.getInstance().getUtils().placeholderAPIStatus() && (player != null))
				string = PlaceholderAPI.setPlaceholders(player, string);
			
			return string;
		} else
			return "";
	}
	
	@Override
	public File getDirectory() {
		return directory;
	}
	
	@Override
	public File getFile() {
		return file;
	}
	
	@Override
	public YamlConfiguration getConfiguration() {
		return configuration;
	}

}
