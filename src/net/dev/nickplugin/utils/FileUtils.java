package net.dev.nickplugin.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

import net.dev.nickplugin.main.Main;

public class FileUtils {

	public static File folder = new File("plugins/" + Main.getPlugin(Main.class).getDescription().getName() + "/");
	public static File file = new File("plugins/" + Main.getPlugin(Main.class).getDescription().getName() + "/setup.yml");
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

		PluginDescriptionFile desc = Main.getPlugin(Main.class).getDescription();
		
		cfg.options().header("This plugin was coded by " + desc.getAuthors().toString().replace("[", "").replace("]", "") +  " - YouTube: https://www.youtube.com/c/JustixDevelopment"
				+ "\n"
				+ "\nColorCodes can be found here: http://minecraft.tools/en/color-code.php"
				+ "\nSpigot-Materials: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html"
				+ "\nResource-Page: " + desc.getWebsite()
				+ "\nLanguages: de_DE, en_US"
				+ "\n");

		cfg.addDefault("Language", "de_DE");
		cfg.addDefault("BungeeCord", false);
		cfg.addDefault("LobbyMode", false);
		cfg.addDefault("BungeeMySQL.hostname", "localhost");
		cfg.addDefault("BungeeMySQL.port", "3306");
		cfg.addDefault("BungeeMySQL.database", "private");
		cfg.addDefault("BungeeMySQL.username", "root");
		cfg.addDefault("BungeeMySQL.password", "password");

		cfg.addDefault("AutoUpdater", true);
		cfg.addDefault("APIMode", false);

		cfg.addDefault("NeedItemToToggleNick", false);
		cfg.addDefault("GetNewNickOnEveryServerSwitch", true);
		cfg.addDefault("NickOnWorldChange", false);
		cfg.addDefault("JoinNick", false);
		cfg.addDefault("DisconnectUnnick", true);
		cfg.addDefault("SeeNickSelf", true);
		cfg.addDefault("ReplaceNickedChatFormat", true);
		cfg.addDefault("AllowPlayersToNickAsOnlinePlayers", false);
		cfg.addDefault("AllowPlayersToNickAsKnownPlayers", false);
		cfg.addDefault("SwitchPermissionsExGroupByNicking", false);
		cfg.addDefault("ServerIsUsingCloudNETPrefixes", false);
		cfg.addDefault("RandomDisguiseDelay", false);
		cfg.addDefault("OpenNicknameGUIInsteadOfRandomNick", false);
		cfg.addDefault("OpenBookGUIOnNickCommand", false);

		cfg.addDefault("BypassFormat.Show", true);
		cfg.addDefault("BypassFormat.NameTagPrefix", "&dNICKED &7| &d");
		cfg.addDefault("BypassFormat.NameTagSuffix", "&r");
		
		cfg.addDefault("NickMessage.OnNnick", false);
		cfg.addDefault("NickMessage.OnUnnick", false);
		cfg.addDefault("NickMessage.Nick.Quit", "&8[&c-&8] &7%name%");
		cfg.addDefault("NickMessage.Nick.Join", "&8[&a+&8] &7%name%");
		cfg.addDefault("NickMessage.Unnick.Quit", "&8[&c-&8] &7%displayName%");
		cfg.addDefault("NickMessage.Unnick.Join", "&8[&a+&8] &7%displayName%");
		
		cfg.addDefault("NickCommands.SendAsConsole", false);
		cfg.addDefault("NickCommands.OnNick", false);
		cfg.addDefault("NickCommands.OnUnnick", false);
		cfg.addDefault("NickCommands.Nick", Arrays.asList("/yourCommandOnNick"));
		cfg.addDefault("NickCommands.Unnick", Arrays.asList("/yourCommandOnUnnick"));
		
		cfg.addDefault("NickItem.getOnJoin", false);
		cfg.addDefault("NickItem.InventorySettings.PlayersCanDropItem", false);
		cfg.addDefault("NickItem.InventorySettings.PlayersCanMoveItem", true);
		cfg.addDefault("NickItem.Slot", 5);

		cfg.addDefault("NickItem.ItemType.Enabled", "NAME_TAG");
		cfg.addDefault("NickItem.ItemAmount.Enabled", 1);
		cfg.addDefault("NickItem.MetaData.Enabled", 0);
		cfg.addDefault("NickItem.Enchanted.Enabled", true);

		cfg.addDefault("NickItem.ItemType.Disabled", "NAME_TAG");
		cfg.addDefault("NickItem.ItemAmount.Disabled", 1);
		cfg.addDefault("NickItem.MetaData.Disabled", 0);
		cfg.addDefault("NickItem.Enchanted.Disabled", false);

		cfg.addDefault("Settings.NickDelay", (long) 0.5);
		cfg.addDefault("Settings.ChatFormat", "%prefix%%displayName%%suffix%&7: &r%message%");
		cfg.addDefault("Settings.NameChangeOptions.RefreshPlayer", true);
		cfg.addDefault("Settings.NameChangeOptions.DisplayNameColored", true);
		cfg.addDefault("Settings.NameChangeOptions.PlayerListNameColored", true);
		cfg.addDefault("Settings.NameChangeOptions.NameTagColored", true);
		cfg.addDefault("Settings.NickFormat.Chat.Prefix", "&8");
		cfg.addDefault("Settings.NickFormat.Chat.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.PlayerList.Prefix", "&8");
		cfg.addDefault("Settings.NickFormat.PlayerList.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.NameTag.Prefix", "&8");
		cfg.addDefault("Settings.NickFormat.NameTag.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.PermissionsEx.GroupName", "Spieler");
		cfg.addDefault("Settings.NickFormat.ServerFullRank.Chat.Prefix", "&6");
		cfg.addDefault("Settings.NickFormat.ServerFullRank.Chat.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.ServerFullRank.PlayerList.Prefix", "&6");
		cfg.addDefault("Settings.NickFormat.ServerFullRank.PlayerList.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.ServerFullRank.NameTag.Prefix", "&6");
		cfg.addDefault("Settings.NickFormat.ServerFullRank.NameTag.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.ServerFullRank.PermissionsEx.GroupName", "Premium");

		List<String> worldBlackList = new ArrayList<>();
		worldBlackList.add("world");

		cfg.addDefault("AutoNickWorldBlackList", worldBlackList);

		List<String> blackList = new ArrayList<>();
		blackList.add("NAME_IN_CAPS_HERE");

		cfg.addDefault("BlackList", blackList);

		cfg.options().copyDefaults(true);
		cfg.options().copyHeader(true);
		saveFile();
		
		LanguageFileUtils.file = new File("plugins/" + Main.getPlugin(Main.class).getDescription().getName() + "/lang/" + cfg.getString("Language") + ".yml");
		LanguageFileUtils.cfg = YamlConfiguration.loadConfiguration(LanguageFileUtils.file);
		LanguageFileUtils.setupFiles();
	}
	
}
