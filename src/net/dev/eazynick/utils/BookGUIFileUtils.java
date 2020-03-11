package net.dev.eazynick.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

import net.dev.eazynick.EazyNick;

public class BookGUIFileUtils {

	public static File folder = new File("plugins/" + EazyNick.getInstance().getDescription().getName() + "/");
	public static File file = new File("plugins/" + EazyNick.getInstance().getDescription().getName() + "/bookgui.yml");
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

		PluginDescriptionFile desc = EazyNick.getInstance().getDescription();
		
		cfg.options().header("This plugin was coded by " + desc.getAuthors().toString().replace("[", "").replace("]", "") +  " - YouTube: https://www.youtube.com/c/JustixDevelopment"
				+ "\n"
				+ "\nColorCodes can be found here: http://minecraft.tools/en/color-code.php"
				+ "\nSpigot-Materials: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html"
				+ "\nResource-Page: " + desc.getWebsite()
				+ "\n");
		
		String arrow = "&0\u27A4";
		
		cfg.addDefault("BookGUI.Page1.Title", "Info");
		cfg.addDefault("BookGUI.Page1.Text", "&0Nicknames allow you to play with a different\nusername to not get recognized.\n\nAll rules still apply. You can still be reported and all name history is stored.\n\n");
		cfg.addDefault("BookGUI.Page2.Title", "Ranks");
		cfg.addDefault("BookGUI.Page2.Text", "&0Let's get you set up with your nickname! First, you'll need to choose which &lRANK &0you would like to be shown as when nicked.\n\n");
		cfg.addDefault("BookGUI.Page3.Title", "Skin");
		cfg.addDefault("BookGUI.Page3.Text","&0Awesome! Now, wich &lSKIN &0would you like to have while nicked?\n\n");
		cfg.addDefault("BookGUI.Page4.Title", "Name");
		cfg.addDefault("BookGUI.Page4.Text", "&0Alright, now you'll need to choose the &0&lNAME &0to use!\n\n");
		cfg.addDefault("BookGUI.Page5.Title", "RandomNick");
		cfg.addDefault("BookGUI.Page5.Text", "&0We've generated a random username for you:\n&l%name%\n\n");
		cfg.addDefault("BookGUI.Page5.Text2", "\n&0To go back to being your usual self, type:\n&l/nick reset");
		cfg.addDefault("BookGUI.Page6.Title", "Done");
		cfg.addDefault("BookGUI.Page6.Text.SingleServer", "&0You have finished setting up your nickname!\n\nYou are now nicked as %name%&0.\n\nTo go back to being your usual self, type:\n&l/nick reset");
		cfg.addDefault("BookGUI.Page6.Text.BungeeCord", "&0You have finished setting up your nickname!\n\nWhen you go into a game, you will be nicked as %name%&0.\n\nTo go back to being your usual self, type:\n&l/nick reset");
		
		cfg.addDefault("BookGUI.Accept.Text", "&0&n" + arrow + " I understand, set\n&nup my nickname");
		cfg.addDefault("BookGUI.Accept.Hover", "&fClick here to proceed");
		cfg.addDefault("BookGUI.Rank.Text", arrow + " %rank%\n");
		cfg.addDefault("BookGUI.Rank.Hover", "&fClick here to be shown as %rank%");
		cfg.addDefault("BookGUI.NormalSkin.Text", arrow + " &0My normal skin\n");
		cfg.addDefault("BookGUI.NormalSkin.Hover", "&fClick here to use your normal skin");
		cfg.addDefault("BookGUI.StevenAlexSkin.Text", arrow + " &0Steven/Alex skin\n");
		cfg.addDefault("BookGUI.StevenAlexSkin.Hover", "&fClick here to use a Steven/Alex skin");
		cfg.addDefault("BookGUI.RandomSkin.Text", arrow + " &0Random skin\n");
		cfg.addDefault("BookGUI.RandomSkin.Hover", "&fClick here to use a random skin");
		cfg.addDefault("BookGUI.ReuseSkin.Text", arrow + " &0Reuse %skin%\n");
		cfg.addDefault("BookGUI.ReuseSkin.Hover", "&fClick here to reuse your previous skin");
		cfg.addDefault("BookGUI.EnterName.Text", arrow + " &0Enter a name\n");
		cfg.addDefault("BookGUI.EnterName.Hover", "&fClick here to enter a name");
		cfg.addDefault("BookGUI.RandomName.Text", arrow + " &0Use random name\n");
		cfg.addDefault("BookGUI.RandomName.Hover", "&fClick here to use a random name");
		cfg.addDefault("BookGUI.ReuseName.Text", arrow + " &0Reuse '%name%'\n");
		cfg.addDefault("BookGUI.ReuseName.Hover", "&fClick here to reuse '%name%'");
		cfg.addDefault("BookGUI.OptionUseName.Text", "     &a&nUSE NAME\n");
		cfg.addDefault("BookGUI.OptionUseName.Hover", "&fClick here to use this name.");
		cfg.addDefault("BookGUI.OptionTryAgain.Text", "     &c&nTRY AGAIN\n\n");
		cfg.addDefault("BookGUI.OptionTryAgain.Hover", "&fClick here to generate another name.");
		cfg.addDefault("BookGUI.OptionEnterName.Text", "&0&nOr enter a name to\n&nuse.");
		cfg.addDefault("BookGUI.OptionEnterName.Hover", "&fClick here to enter a name");
		
		cfg.addDefault("SignGUI.Line1", "");
		cfg.addDefault("SignGUI.Line2", "^^^^^^^^^^^^^^^");
		cfg.addDefault("SignGUI.Line3", "Enter your");
		cfg.addDefault("SignGUI.Line4", "username here");
		
		cfg.addDefault("AnvilGUI.Title", "Enter name here");

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
	
	public static String getConfigString(String path) {
		return ChatColor.translateAlternateColorCodes('&', cfg.getString(path));
	}
	
}
