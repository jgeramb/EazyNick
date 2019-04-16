package net.dev.nickplugin.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.dev.nickplugin.main.Main;

public class BookGUIFileUtils {

	public static File folder = new File("plugins/" + Main.getPlugin(Main.class).getDescription().getName() + "/");
	public static File file = new File("plugins/" + Main.getPlugin(Main.class).getDescription().getName() + "/bookgui.yml");
	public static FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
	
	public static void saveFile() {
		try {
			cfg.save(file);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void setupFiles() {
		if (!(folder.exists())) {
			folder.mkdir();
		}

		if (!(file.exists())) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		cfg.options().header("This plugin was coded by Justix - YouTube: https://www.youtube.com/c/JustixDevelopment "
				+ "\n" + "\nColorCodes can be found here: http://minecraft.tools/en/color-code.php "
				+ "\nSpigot-Materials: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html "
				+ "\nResource-Page: https://www.spigotmc.org/resources/eazynick-nicksystem-api-src-bungeecord-multiworld-1-7-10-1-12-2.51398/ "
				+ "\n");

		cfg.addDefault("BookGUI.Rank1.Enabled", true);
		cfg.addDefault("BookGUI.Rank1.Rank", "&8Player");
		cfg.addDefault("BookGUI.Rank1.RankName", "PLAYER");
		cfg.addDefault("BookGUI.Rank2.Enabled", true);
		cfg.addDefault("BookGUI.Rank2.Rank", "&aVIP");
		cfg.addDefault("BookGUI.Rank2.RankName", "VIP");
		cfg.addDefault("BookGUI.Rank3.Enabled", true);
		cfg.addDefault("BookGUI.Rank3.Rank", "&aVIP&6+");
		cfg.addDefault("BookGUI.Rank3.RankName", "VIPPLUS");
		cfg.addDefault("BookGUI.Rank4.Enabled", true);
		cfg.addDefault("BookGUI.Rank4.Rank", "&aVIP&6++");
		cfg.addDefault("BookGUI.Rank4.RankName", "VIPPLUSPLUS");
		cfg.addDefault("BookGUI.Rank5.Enabled", true);
		cfg.addDefault("BookGUI.Rank5.Rank", "&bMVP");
		cfg.addDefault("BookGUI.Rank5.RankName", "MVP");
		cfg.addDefault("BookGUI.Rank6.Enabled", true);
		cfg.addDefault("BookGUI.Rank6.Rank", "&bMVP&c+");
		cfg.addDefault("BookGUI.Rank6.RankName", "MVPPLUS");
		cfg.addDefault("BookGUI.Rank7.Enabled", true);
		cfg.addDefault("BookGUI.Rank7.Rank", "&bMVP&c++");
		cfg.addDefault("BookGUI.Rank7.RankName", "MVPPLUSPLUS");

		cfg.addDefault("Settings.NickFormat.Rank1.Prefix", "&7");
		cfg.addDefault("Settings.NickFormat.Rank1.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank1.TagPrefix", "&7");
		cfg.addDefault("Settings.NickFormat.Rank1.TagSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank1.PermissionsEx.GroupName", "Default");
		
		cfg.addDefault("Settings.NickFormat.Rank2.Prefix", "&a[VIP] ");
		cfg.addDefault("Settings.NickFormat.Rank2.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank2.TagPrefix", "&a[VIP] ");
		cfg.addDefault("Settings.NickFormat.Rank2.TagSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank2.PermissionsEx.GroupName", "VIP");
		
		cfg.addDefault("Settings.NickFormat.Rank3.Prefix", "&a[VIP&6+&a] ");
		cfg.addDefault("Settings.NickFormat.Rank3.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank3.TagPrefix", "&a[VIP&6+&a] ");
		cfg.addDefault("Settings.NickFormat.Rank3.TagSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank3.PermissionsEx.GroupName", "VIPPlus");
		
		cfg.addDefault("Settings.NickFormat.Rank4.Prefix", "&a[VIP&6+&a] ");
		cfg.addDefault("Settings.NickFormat.Rank4.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank4.TagPrefix", "&a[VIP&6+&a] ");
		cfg.addDefault("Settings.NickFormat.Rank4.TagSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank4.PermissionsEx.GroupName", "VIPPlusPlus");
		
		cfg.addDefault("Settings.NickFormat.Rank5.Prefix", "&b[MVP] ");
		cfg.addDefault("Settings.NickFormat.Rank5.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank5.TagPrefix", "&b[MVP] ");
		cfg.addDefault("Settings.NickFormat.Rank5.TagSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank5.PermissionsEx.GroupName", "MVP");
		
		cfg.addDefault("Settings.NickFormat.Rank6.Prefix", "&b[MVP&c+&b] ");
		cfg.addDefault("Settings.NickFormat.Rank6.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank6.TagPrefix", "&b[MVP&c+&b] ");
		cfg.addDefault("Settings.NickFormat.Rank6.TagSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank6.PermissionsEx.GroupName", "MVPPlus");
		
		cfg.addDefault("Settings.NickFormat.Rank7.Prefix", "&b[MVP&c++&b] ");
		cfg.addDefault("Settings.NickFormat.Rank7.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank7.TagPrefix", "&b[MVP&c++&b] ");
		cfg.addDefault("Settings.NickFormat.Rank7.TagSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank7.PermissionsEx.GroupName", "MVPPlusPlus");

		cfg.options().copyDefaults(true);
		cfg.options().copyHeader(true);
		saveFile();
	}
	
}
