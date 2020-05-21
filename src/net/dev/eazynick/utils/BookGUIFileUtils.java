package net.dev.eazynick.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

import net.dev.eazynick.EazyNick;

public class BookGUIFileUtils {

	private File directory, file;
	public YamlConfiguration cfg;
	
	public BookGUIFileUtils() {
		PluginDescriptionFile desc = EazyNick.getInstance().getDescription();
		
		directory = new File("plugins/" + desc.getName() + "/");
		file = new File(directory, "bookgui.yml");
				
		if (!(directory.exists()))
			directory.mkdir();

		if (!(file.exists())) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		cfg = YamlConfiguration.loadConfiguration(file);

		cfg.options().header("This plugin was coded by " + desc.getAuthors().toString().replace("[", "").replace("]", "") +  " - YouTube: https://www.youtube.com/c/JustixDevelopment"
				+ "\n"
				+ "\nColorCodes can be found here: http://minecraft.tools/en/color-code.php"
				+ "\nSpigot-Materials: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html"
				+ "\nResource-Page: " + desc.getWebsite()
				+ "\n");
		
		String arrow = "&0\u27A4";
		
		cfg.addDefault("BookGUI.Page1.Title", "Info");
		cfg.addDefault("BookGUI.Page1.Text", "&0Nicknames allow you to play with a different\nusername to not get recognized.\n\nAll rules still apply. You can still be reported and all name history is stored.\n\n");
		cfg.addDefault("BookGUI.Page1.Enabled", true);
		cfg.addDefault("BookGUI.Page2.Title", "Ranks");
		cfg.addDefault("BookGUI.Page2.Text", "&0Let's get you set up with your nickname! First, you'll need to choose which &lRANK &0you would like to be shown as when nicked.\n\n");
		cfg.addDefault("BookGUI.Page3.Title", "Skin");
		cfg.addDefault("BookGUI.Page3.Text","&0Awesome! Now, which &lSKIN &0would you like to have while nicked?\n\n");
		cfg.addDefault("BookGUI.Page4.Title", "Name");
		cfg.addDefault("BookGUI.Page4.Text", "&0Alright, now you'll need to choose the &0&lNAME &0to use!\n\n");
		cfg.addDefault("BookGUI.Page5.Title", "RandomNick");
		cfg.addDefault("BookGUI.Page5.Text", "&0We've generated a random username for you:\n&l%name%\n\n");
		cfg.addDefault("BookGUI.Page5.Text2", "\n&0To go back to being your usual self, type:\n&l/nick reset");
		cfg.addDefault("BookGUI.Page6.Title", "Done");
		cfg.addDefault("BookGUI.Page6.Text.SingleServer", "&0You have finished setting up your nickname!\n\nYou are now nicked as %name%&0.\n\nTo go back to being your usual self, type:\n&l/nick reset");
		cfg.addDefault("BookGUI.Page6.Text.BungeeCord", "&0You have finished setting up your nickname!\n\nWhen you go into a game, you will be nicked as %name%&0.\n\nTo go back to being your usual self, type:\n&l/nick reset");
		cfg.addDefault("BookGUI.Page6.Enabled", true);
		
		cfg.addDefault("BookGUI.Accept.Text", arrow + " &nI understand, set\n&nup my nickname");
		cfg.addDefault("BookGUI.Accept.Hover", "&fClick here to proceed");
		cfg.addDefault("BookGUI.Rank.Text", arrow + " %rank%\n");
		cfg.addDefault("BookGUI.Rank.Hover", "&fClick here to be shown as %rank%");
		cfg.addDefault("BookGUI.NormalSkin.Text", arrow + " &0My normal skin\n");
		cfg.addDefault("BookGUI.NormalSkin.Hover", "&fClick here to use your normal skin");
		cfg.addDefault("BookGUI.SteveAlexSkin.Text", arrow + " &0Steve/Alex skin\n");
		cfg.addDefault("BookGUI.SteveAlexSkin.Hover", "&fClick here to use a Steve/Alex skin");
		cfg.addDefault("BookGUI.RandomSkin.Text", arrow + " &0Random skin\n");
		cfg.addDefault("BookGUI.RandomSkin.Hover", "&fClick here to use a random skin");
		cfg.addDefault("BookGUI.SkinFromName.Text", arrow + " &0Use skin of nickname\n");
		cfg.addDefault("BookGUI.SkinFromName.Hover", "&fClick here to use the skin of your nickname");
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
		cfg.addDefault("BookGUI.Rank2.Permission", "nick.rank.vip");
		cfg.addDefault("BookGUI.Rank3.Enabled", true);
		cfg.addDefault("BookGUI.Rank3.Rank", "&aVIP&6+");
		cfg.addDefault("BookGUI.Rank3.RankName", "VIPPLUS");
		cfg.addDefault("BookGUI.Rank3.Permission", "nick.rank.vipplus");
		cfg.addDefault("BookGUI.Rank4.Enabled", true);
		cfg.addDefault("BookGUI.Rank4.Rank", "&bMVP");
		cfg.addDefault("BookGUI.Rank4.RankName", "MVP");
		cfg.addDefault("BookGUI.Rank4.Permission", "nick.rank.mvp");
		cfg.addDefault("BookGUI.Rank5.Enabled", true);
		cfg.addDefault("BookGUI.Rank5.Rank", "&bMVP&c+");
		cfg.addDefault("BookGUI.Rank5.RankName", "MVPPLUS");
		cfg.addDefault("BookGUI.Rank5.Permission", "nick.rank.mvpplus");
		cfg.addDefault("BookGUI.Rank6.Enabled", true);
		cfg.addDefault("BookGUI.Rank6.Rank", "&6MVP&c++");
		cfg.addDefault("BookGUI.Rank6.RankName", "MVPPLUSPLUS");
		cfg.addDefault("BookGUI.Rank6.Permission", "nick.rank.mvpplusplus");
		
		for (int i = 7; i <= 18; i++) {
			cfg.addDefault("BookGUI.Rank" + i + ".Enabled", false);
			cfg.addDefault("BookGUI.Rank" + i + ".Rank", "&dRank" + i);
			cfg.addDefault("BookGUI.Rank" + i + ".RankName", "RANK" + i);
			cfg.addDefault("BookGUI.Rank" + i + ".Permission", "nick.rank." + i);
		}

		cfg.addDefault("Settings.NickFormat.Rank1.ChatPrefix", "&7");
		cfg.addDefault("Settings.NickFormat.Rank1.ChatSuffix", "&7");
		cfg.addDefault("Settings.NickFormat.Rank1.TabPrefix", "&7");
		cfg.addDefault("Settings.NickFormat.Rank1.TabSuffix", "&7");
		cfg.addDefault("Settings.NickFormat.Rank1.TagPrefix", "&7");
		cfg.addDefault("Settings.NickFormat.Rank1.TagSuffix", "&7");
		cfg.addDefault("Settings.NickFormat.Rank1.GroupName", "Default");
		
		cfg.addDefault("Settings.NickFormat.Rank2.ChatPrefix", "&a[VIP] ");
		cfg.addDefault("Settings.NickFormat.Rank2.ChatSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank2.TabPrefix", "&a[VIP] ");
		cfg.addDefault("Settings.NickFormat.Rank2.TabSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank2.TagPrefix", "&a[VIP] ");
		cfg.addDefault("Settings.NickFormat.Rank2.TagSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank2.GroupName", "VIP");
		
		cfg.addDefault("Settings.NickFormat.Rank3.ChatPrefix", "&a[VIP&6+&a] ");
		cfg.addDefault("Settings.NickFormat.Rank3.ChatSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank3.TabPrefix", "&a[VIP&6+&a] ");
		cfg.addDefault("Settings.NickFormat.Rank3.TabSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank3.TagPrefix", "&a[VIP&6+&a] ");
		cfg.addDefault("Settings.NickFormat.Rank3.TagSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank3.GroupName", "VIPPlus");
		
		cfg.addDefault("Settings.NickFormat.Rank4.ChatPrefix", "&b[MVP] ");
		cfg.addDefault("Settings.NickFormat.Rank4.ChatSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank4.TabPrefix", "&b[MVP] ");
		cfg.addDefault("Settings.NickFormat.Rank4.TabSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank4.TagPrefix", "&b[MVP] ");
		cfg.addDefault("Settings.NickFormat.Rank4.TagSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank4.GroupName", "MVP");
		
		cfg.addDefault("Settings.NickFormat.Rank5.ChatPrefix", "&b[MVP%randomColor%+&b] ");
		cfg.addDefault("Settings.NickFormat.Rank5.ChatSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank5.TabPrefix", "&b[MVP%randomColor%+&b] ");
		cfg.addDefault("Settings.NickFormat.Rank5.TabSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank5.TagPrefix", "&b[MVP%randomColor%+&b] ");
		cfg.addDefault("Settings.NickFormat.Rank5.TagSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank5.GroupName", "MVPPlus");
		
		cfg.addDefault("Settings.NickFormat.Rank6.ChatPrefix", "&6[MVP%randomColor%++&6] ");
		cfg.addDefault("Settings.NickFormat.Rank6.ChatSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank6.TabPrefix", "&6[MVP%randomColor%++&6] ");
		cfg.addDefault("Settings.NickFormat.Rank6.TabSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank6.TagPrefix", "&6[MVP%randomColor%++&6] ");
		cfg.addDefault("Settings.NickFormat.Rank6.TagSuffix", "&r");
		cfg.addDefault("Settings.NickFormat.Rank6.GroupName", "MVPPlusPlus");
		
		for (int i = 7; i <= 18; i++) {
			String prefix = "&d[Rank" + i + "] ", suffix = "&r";
			
			cfg.addDefault("Settings.NickFormat.Rank" + i + ".ChatPrefix", prefix);
			cfg.addDefault("Settings.NickFormat.Rank" + i + ".ChatSuffix", suffix);
			cfg.addDefault("Settings.NickFormat.Rank" + i + ".TabPrefix", prefix);
			cfg.addDefault("Settings.NickFormat.Rank" + i + ".TabSuffix", suffix);
			cfg.addDefault("Settings.NickFormat.Rank" + i + ".TagPrefix", prefix);
			cfg.addDefault("Settings.NickFormat.Rank" + i + ".TagSuffix", suffix);
			cfg.addDefault("Settings.NickFormat.Rank" + i + ".GroupName", "Rank" + i);
		}

		cfg.options().copyDefaults(true);
		cfg.options().copyHeader(true);
		saveFile();
	}
	
	public void saveFile() {
		try {
			cfg.save(file);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public String getConfigString(String path) {
		return ChatColor.translateAlternateColorCodes('&', cfg.getString(path));
	}
	
	public File getFile() {
		return file;
	}
	
}
