package net.dev.eazynick.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import net.dev.eazynick.EazyNick;

import me.clip.placeholderapi.PlaceholderAPI;

public class FileUtils {

	private File directory, file;
	private YamlConfiguration cfg;
	
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

		cfg.addDefault("NeedItemToToggleNick", true);
		cfg.addDefault("GetNewNickOnEveryServerSwitch", false);
		cfg.addDefault("NickOnWorldChange", false);
		cfg.addDefault("KeepNickOnWorldChange", true);
		cfg.addDefault("JoinNick", false);
		cfg.addDefault("DisconnectUnnick", true);
		cfg.addDefault("SeeNickSelf", true);
		cfg.addDefault("ReplaceNickedChatFormat", true);
		cfg.addDefault("OverwriteJoinQuitMessages", false);
		cfg.addDefault("AllowPlayersToUseSameNickName", false);
		cfg.addDefault("AllowPlayersToNickAsOnlinePlayers", false);
		cfg.addDefault("AllowPlayersToNickAsKnownPlayers", false);
		cfg.addDefault("SwitchUltraPermissionsGroupByNicking", false);
		cfg.addDefault("SwitchLuckPermsGroupByNicking", false);
		cfg.addDefault("SwitchPermissionsExGroupByNicking", false);
		cfg.addDefault("ServerIsUsingCloudNETPrefixesAndSuffixes", false);
		cfg.addDefault("ServerIsUsingVaultPrefixesAndSuffixes", false);
		cfg.addDefault("ChangeNameAndPrefixAndSuffixInDeluxeChatFormat", false);
		cfg.addDefault("ChangeNameAndPrefixAndSuffixInTAB", false);
		cfg.addDefault("ChangeLuckPermsPrefixAndSufix", false);
		cfg.addDefault("RandomDisguiseDelay", false);
		cfg.addDefault("UseSignGUIForCustomName", true);
		cfg.addDefault("UseAnvilGUIForCustomName", false);
		cfg.addDefault("AllowBookGUICustomName", true);
		cfg.addDefault("AllowBookGUISkinFromName", false);
		cfg.addDefault("UseMineSkinAPI", false);
		cfg.addDefault("OverwriteMessagePackets", false);
		cfg.addDefault("OpenNickListGUIOnNickCommand", false);
		cfg.addDefault("OpenBookGUIOnNickCommand", false);
		cfg.addDefault("OpenRankedNickGUIOnNickCommand", false);
		cfg.addDefault("OpenRankedNickGUIOnNickGUICommand", false);
		cfg.addDefault("EnableBypassPermission", false);
		cfg.addDefault("ShowProfileErrorMessages", true);
		cfg.addDefault("AllowSpecialCharactersInCustomName", false);
		cfg.addDefault("AllowCustomNamesShorterThanThreeCharacters", false);
		cfg.addDefault("LogNicknames", false);

		cfg.addDefault("NickActionBarMessage", false);
		
		cfg.addDefault("BypassFormat.Show", true);
		cfg.addDefault("BypassFormat.NameTagPrefix", "&dBYPASS &7| &d");
		cfg.addDefault("BypassFormat.NameTagSuffix", "&r");
		
		cfg.addDefault("OverwrittenMessages.Join", "&8[&a+&8] &7%name%");
		cfg.addDefault("OverwrittenMessages.Quit", "&8[&c-&8] &7%name%");
		
		cfg.addDefault("NickMessage.OnNnick", false);
		cfg.addDefault("NickMessage.OnUnnick", false);
		cfg.addDefault("NickMessage.Nick.Join", "&8[&a+&8] &7%name%");
		cfg.addDefault("NickMessage.Nick.Quit", "&8[&c-&8] &7%name%");
		cfg.addDefault("NickMessage.Unnick.Join", "&8[&a+&8] &7%displayName%");
		cfg.addDefault("NickMessage.Unnick.Quit", "&8[&c-&8] &7%displayName%");
		
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
		cfg.addDefault("Settings.ChatFormat", "%prefix%%playerName%%suffix%: %message%");
		cfg.addDefault("Settings.ChangeOptions.UUID", false);
		cfg.addDefault("Settings.ChangeOptions.DisplayName", true);
		cfg.addDefault("Settings.ChangeOptions.PlayerListName", true);
		cfg.addDefault("Settings.ChangeOptions.NameTag", true);
		cfg.addDefault("Settings.NickFormat.Chat.Prefix", "&7");
		cfg.addDefault("Settings.NickFormat.Chat.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.PlayerList.Prefix", "&7");
		cfg.addDefault("Settings.NickFormat.PlayerList.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.NameTag.Prefix", "&7");
		cfg.addDefault("Settings.NickFormat.NameTag.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.SortID", 9999);
		cfg.addDefault("Settings.NickFormat.GroupName", "Default");
		cfg.addDefault("Settings.NickFormat.ServerFullRank.Chat.Prefix", "&a[VIP] ");
		cfg.addDefault("Settings.NickFormat.ServerFullRank.Chat.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.ServerFullRank.PlayerList.Prefix", "&a[VIP] ");
		cfg.addDefault("Settings.NickFormat.ServerFullRank.PlayerList.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.ServerFullRank.NameTag.Prefix", "&a[VIP] ");
		cfg.addDefault("Settings.NickFormat.ServerFullRank.NameTag.Suffix", "&r");
		cfg.addDefault("Settings.NickFormat.ServerFullRank.SortID", 9998);
		cfg.addDefault("Settings.NickFormat.ServerFullRank.GroupName", "VIP");
		cfg.addDefault("WorldsWithDisabledPrefixAndSuffix", Arrays.asList("example_world"));
		cfg.addDefault("AutoNickWorldBlackList", Arrays.asList("example_world"));
		cfg.addDefault("BlackList", Arrays.asList("ExampleName"));
		cfg.addDefault("DisabledNickWorlds", Arrays.asList("ExampleName"));
		cfg.addDefault("MineSkinIds", Arrays.asList("1416741364", "898982494", "186233253"));
		
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
	
	public String getConfigString(Player p, String path) {
		String string = ChatColor.translateAlternateColorCodes('&', cfg.getString(path));
		
		if(EazyNick.getInstance().getUtils().placeholderAPIStatus() && (p != null))
			string = PlaceholderAPI.setPlaceholders(p, string);
		
		return string;
	}
	
	public File getFile() {
		return file;
	}
	
	public YamlConfiguration getConfig() {
		return cfg;
	}
	
	public void setConfig(YamlConfiguration cfg) {
		this.cfg = cfg;
	}
	
}
