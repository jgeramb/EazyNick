package net.dev.eazynick.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

import net.dev.eazynick.EazyNick;

public class FileUtils {

	private File directory, file;
	public YamlConfiguration cfg;
	
	public FileUtils() {
		EazyNick eazyNick = EazyNick.getInstance();
		PluginDescriptionFile desc = eazyNick.getDescription();
		
		directory = new File("plugins/" + desc.getName() + "/");
		file = new File(directory, "setup.yml");
		
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
				+ "\nLanguages: de_DE, en_US"
				+ "\n");

		cfg.addDefault("Language", "en_US");
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
		cfg.addDefault("GetNewNickOnEveryServerSwitch", false);
		cfg.addDefault("NickOnWorldChange", false);
		cfg.addDefault("JoinNick", false);
		cfg.addDefault("DisconnectUnnick", true);
		cfg.addDefault("SeeNickSelf", true);
		cfg.addDefault("ReplaceNickedChatFormat", true);
		cfg.addDefault("AllowPlayersToNickAsOnlinePlayers", false);
		cfg.addDefault("AllowPlayersToNickAsKnownPlayers", false);
		cfg.addDefault("SwitchUltraPermissionsGroupByNicking", false);
		cfg.addDefault("SwitchLuckPermsGroupByNicking", false);
		cfg.addDefault("SwitchPermissionsExGroupByNicking", false);
		cfg.addDefault("ServerIsUsingCloudNETPrefixesAndSuffixes", false);
		cfg.addDefault("ServerIsUsingVaultPrefixesAndSuffixes", false);
		cfg.addDefault("ChangeNameAndPrefixandSuffixInDeluxeChatFormat", false);
		cfg.addDefault("ChangeLuckPermsPrefixAndSufix", false);
		cfg.addDefault("RandomDisguiseDelay", false);
		cfg.addDefault("OpenNicknameGUIInsteadOfRandomNick", false);
		cfg.addDefault("OpenBookGUIOnNickCommand", false);
		cfg.addDefault("UseSignGUIForCustomName", true);
		cfg.addDefault("AllowBookGUICustomName", true);

		cfg.addDefault("NickActionBarMessage", false);
		
		cfg.addDefault("BypassFormat.Show", true);
		cfg.addDefault("BypassFormat.NameTagPrefix", "&dBYPASS &7| &d");
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
		cfg.addDefault("Settings.ChatFormat", "%prefix%%playerName%%suffix%&7: &r%message%");
		cfg.addDefault("Settings.ChangeOptions.UUID", false);
		cfg.addDefault("Settings.ChangeOptions.DisplayName", true);
		cfg.addDefault("Settings.ChangeOptions.PlayerListName", true);
		cfg.addDefault("Settings.ChangeOptions.NameTag", true);
		cfg.addDefault("Settings.NickFormat.Chat.Prefix", "&8");
		cfg.addDefault("Settings.NickFormat.Chat.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.PlayerList.Prefix", "&8");
		cfg.addDefault("Settings.NickFormat.PlayerList.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.NameTag.Prefix", "&8");
		cfg.addDefault("Settings.NickFormat.NameTag.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.GroupName", "Spieler");
		cfg.addDefault("Settings.NickFormat.ServerFullRank.Chat.Prefix", "&6");
		cfg.addDefault("Settings.NickFormat.ServerFullRank.Chat.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.ServerFullRank.PlayerList.Prefix", "&6");
		cfg.addDefault("Settings.NickFormat.ServerFullRank.PlayerList.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.ServerFullRank.NameTag.Prefix", "&6");
		cfg.addDefault("Settings.NickFormat.ServerFullRank.NameTag.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.ServerFullRank.GroupName", "Premium");
		cfg.addDefault("AutoNickWorldBlackList", Arrays.asList("world"));
		cfg.addDefault("BlackList", Arrays.asList("ExampleName"));
		
		cfg.options().copyDefaults(true);
		cfg.options().copyHeader(true);
		saveFile();
		
		eazyNick.setLanguageFileUtils(new LanguageFileUtils(cfg.getString("Language")));
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
