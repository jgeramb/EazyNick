package net.dev.nickplugin.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

import net.dev.nickplugin.main.Main;

public class BookGUIFileUtils {

	public static File folder = new File("plugins/" + Main.getInstance().getDescription().getName() + "/");
	public static File file = new File("plugins/" + Main.getInstance().getDescription().getName() + "/bookgui.yml");
	public static FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
	
	public static void saveFile() {
		try {
			cfg.save(file);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void setupFiles() {
		if (!(folder.exists()))
			folder.mkdir();

		if (!(file.exists())) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		PluginDescriptionFile desc = Main.getInstance().getDescription();
		
		cfg.options().header("This plugin was coded by " + desc.getAuthors().toString().replace("[", "").replace("]", "") +  " - YouTube: https://www.youtube.com/c/JustixDevelopment"
				+ "\n"
				+ "\nColorCodes can be found here: http://minecraft.tools/en/color-code.php"
				+ "\nSpigot-Materials: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html"
				+ "\nResource-Page: " + desc.getWebsite()
				+ "\n");

		cfg.addDefault("BookGUI.Rank1.Enabled", true);
		cfg.addDefault("BookGUI.Rank1.Rank", "&8DEFAULT");
		cfg.addDefault("BookGUI.Rank1.RankName", "DEFAULT");
		cfg.addDefault("BookGUI.Rank1.Permission", "NONE");
		cfg.addDefault("BookGUI.Rank2.Enabled", true);
		cfg.addDefault("BookGUI.Rank2.Rank", "&aVIP");
		cfg.addDefault("BookGUI.Rank2.RankName", "VIP");
		cfg.addDefault("BookGUI.Rank2.Permission", "nick.rank.2");
		cfg.addDefault("BookGUI.Rank3.Enabled", true);
		cfg.addDefault("BookGUI.Rank3.Rank", "&aVIP&6+");
		cfg.addDefault("BookGUI.Rank3.RankName", "VIPPLUS");
		cfg.addDefault("BookGUI.Rank3.Permission", "nick.rank.3");
		cfg.addDefault("BookGUI.Rank4.Enabled", true);
		cfg.addDefault("BookGUI.Rank4.Rank", "&aVIP&6++");
		cfg.addDefault("BookGUI.Rank4.RankName", "VIPPLUSPLUS");
		cfg.addDefault("BookGUI.Rank4.Permission", "nick.rank.4");
		cfg.addDefault("BookGUI.Rank5.Enabled", true);
		cfg.addDefault("BookGUI.Rank5.Rank", "&bMVP");
		cfg.addDefault("BookGUI.Rank5.RankName", "MVP");
		cfg.addDefault("BookGUI.Rank5.Permission", "nick.rank.5");
		cfg.addDefault("BookGUI.Rank6.Enabled", true);
		cfg.addDefault("BookGUI.Rank6.Rank", "&bMVP&c+");
		cfg.addDefault("BookGUI.Rank6.RankName", "MVPPLUS");
		cfg.addDefault("BookGUI.Rank6.Permission", "nick.rank.6");

		cfg.addDefault("Settings.NickFormat.Rank1.ChatPrefix", "&7");
		cfg.addDefault("Settings.NickFormat.Rank1.ChatSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank1.TabPrefix", "&7");
		cfg.addDefault("Settings.NickFormat.Rank1.TabSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank1.TagPrefix", "&7");
		cfg.addDefault("Settings.NickFormat.Rank1.TagSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank1.PermissionsEx.GroupName", "Default");
		
		cfg.addDefault("Settings.NickFormat.Rank2.ChatPrefix", "&a[VIP] ");
		cfg.addDefault("Settings.NickFormat.Rank2.ChatSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank2.TabPrefix", "&aVIP &7| &a");
		cfg.addDefault("Settings.NickFormat.Rank2.TabSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank2.TagPrefix", "&a[VIP] ");
		cfg.addDefault("Settings.NickFormat.Rank2.TagSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank2.PermissionsEx.GroupName", "VIP");
		
		cfg.addDefault("Settings.NickFormat.Rank3.ChatPrefix", "&a[VIP&6+&a] ");
		cfg.addDefault("Settings.NickFormat.Rank3.ChatSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank3.TabPrefix", "&aVIP&6+ &7| &a");
		cfg.addDefault("Settings.NickFormat.Rank3.TabSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank3.TagPrefix", "&a[VIP&6+&a] ");
		cfg.addDefault("Settings.NickFormat.Rank3.TagSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank3.PermissionsEx.GroupName", "VIPPlus");
		
		cfg.addDefault("Settings.NickFormat.Rank4.ChatPrefix", "&a[VIP&6+&a] ");
		cfg.addDefault("Settings.NickFormat.Rank4.ChatSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank4.TabPrefix", "&aVIP&6+ &7| &a");
		cfg.addDefault("Settings.NickFormat.Rank4.TabSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank4.TagPrefix", "&a[VIP&6+&a] ");
		cfg.addDefault("Settings.NickFormat.Rank4.TagSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank4.PermissionsEx.GroupName", "VIPPlusPlus");
		
		cfg.addDefault("Settings.NickFormat.Rank5.ChatPrefix", "&b[MVP] ");
		cfg.addDefault("Settings.NickFormat.Rank5.ChatSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank5.TabPrefix", "&bMVP &7| &b");
		cfg.addDefault("Settings.NickFormat.Rank5.TabSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank5.TagPrefix", "&b[MVP] ");
		cfg.addDefault("Settings.NickFormat.Rank5.TagSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank5.PermissionsEx.GroupName", "MVP");
		
		cfg.addDefault("Settings.NickFormat.Rank6.ChatPrefix", "&b[MVP&c+&b] ");
		cfg.addDefault("Settings.NickFormat.Rank6.ChatSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank6.TabPrefix", "&bMVP&c+ &7| &b");
		cfg.addDefault("Settings.NickFormat.Rank6.TabSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank6.TagPrefix", "&b[MVP&c+&b] ");
		cfg.addDefault("Settings.NickFormat.Rank6.TagSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank6.PermissionsEx.GroupName", "MVPPlus");

		cfg.options().copyDefaults(true);
		cfg.options().copyHeader(true);
		saveFile();
	}
	
}
