package net.dev.eazynick.utilities.configuration.yaml;

import java.io.File;
import java.io.IOException;
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

	private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
	private static final char COLOR_CHAR = ChatColor.COLOR_CHAR;
	
	protected EazyNick eazyNick;
	private File directory, file;
	protected YamlConfiguration configuration;
	
	public YamlFile(EazyNick eazyNick, String subdirectoryName, String fileName) {
		this.eazyNick = eazyNick;
		this.directory = new File("plugins/" + eazyNick.getDescription().getName() + "/" + subdirectoryName);
		this.file = new File(directory, fileName);
		
		//Create directory and file
		if (!(directory.exists()))
			directory.mkdir();

		if (!(file.exists())) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		//Load configuration from file
		this.configuration = YamlConfiguration.loadConfiguration(file);
	}
	
	@Override
	public void initConfiguration() {
		//Set configuration header & defaults
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
				+ "Bukkit materials (1.13+): https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html\n"
				+ "Bukkit materials: https://bit.ly/old-bukkit-materials\n"
				+ "Resource page: " + pluginDescriptionFile.getWebsite() + "\n"
				+ "Languages: de_DE, en_US\n"
				+ "\n"
				+ "-------------------------------------------------------------------\n");
		
		setDefaults();
	
		configuration.options().copyDefaults(true);
		configuration.options().copyHeader(true);
		
		//Save configuration
		save();
	}
	
	@Override
	public abstract void setDefaults();
	
	@Override
	public void save() {
		try {
			//Save configuration to file
			configuration.save(file);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void reload() {
		//Load configuration from file and save it to the file
		this.configuration = YamlConfiguration.loadConfiguration(file);
		save();
	}
	
	//Get a string from the configuration and apply 'setPlaceholders' from PlaceholderAPI if it is installed
	public String getConfigString(Player player, String path) {
		String string = getConfigString(path);
		
		if(EazyNick.getInstance().getUtils().isPluginInstalled("PlaceholderAPI") && (player != null))
			string = PlaceholderAPI.setPlaceholders(player, string);
		
		return string;
	}
	
	//Get a string from the configuration and convert the color codes and new lines
	public String getConfigString(String path) {
		if(configuration.contains(path)) {
			String version = eazyNick.getVersion(), string = ChatColor.translateAlternateColorCodes('&', configuration.getString(path).replace("%nl%", "%nl%&0"));
			
			//HEX-Color-Support
			if(version.startsWith("1_16") || version.startsWith("1_17") || version.startsWith("1_18")) {
				Matcher matcher = HEX_COLOR_PATTERN.matcher(string);
				StringBuffer buffer = new StringBuffer(string.length() + 4 * 8);
				
				while (matcher.find()) {
					String group = matcher.group(1);
					
					matcher.appendReplacement(buffer, COLOR_CHAR + "x" + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1) + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3) + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5));
				}
				
				string = matcher.appendTail(buffer).toString();
			}
			
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
