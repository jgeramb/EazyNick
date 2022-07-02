package com.justixdev.eazynick.utilities.configuration.yaml;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Arrays;
import java.util.Collections;

public class SetupYamlFile extends YamlFile {

	public SetupYamlFile(EazyNick eazyNick) {
		super(eazyNick, "", "setup");
	}

	@Override
	public void setDefaults() {
		configuration.addDefault("Language", "en_US");
		configuration.addDefault("BungeeCord", false);
		configuration.addDefault("LobbyMode", false);
		configuration.addDefault("BungeeMySQL.hostname", "localhost");
		configuration.addDefault("BungeeMySQL.port", "3306");
		configuration.addDefault("BungeeMySQL.database", "private");
		configuration.addDefault("BungeeMySQL.username", "root");
		configuration.addDefault("BungeeMySQL.password", "password");

		configuration.addDefault("APIMode", false);
		configuration.addDefault("AutoUpdater", true);
		configuration.addDefault("AutoUpdatePreReleases", false);
		configuration.addDefault("AllowBookGUICustomName", true);
		configuration.addDefault("AllowBookGUISkinFromName", false);
		configuration.addDefault("AllowPlayersToNickAsKnownPlayers", false);
		configuration.addDefault("AllowPlayersToNickAsOnlinePlayers", false);
		configuration.addDefault("AllowPlayersToUseSameNickName", false);
		configuration.addDefault("AllowRealNamesInCommands", false);
		configuration.addDefault("AllowSpecialCharactersInCustomName", false);
		configuration.addDefault("ChangeLuckPermsPrefixAndSufix", false);
		configuration.addDefault("ChangeGroupAndPrefixAndSuffixInTAB", eazyNick.getUtils().isPluginInstalled("TAB", "NEZNAMY"));
		configuration.addDefault("DisconnectUnnick", true);
		configuration.addDefault("EnableBypassLobbyModePermission", true);
		configuration.addDefault("EnableBypassPermission", false);
		configuration.addDefault("FakeExperienceLevel", -1);
		configuration.addDefault("GetNewNickOnEveryServerSwitch", false);
		configuration.addDefault("JoinNick", false);
		configuration.addDefault("KeepNickOnDeath", true);
		configuration.addDefault("KeepNickOnWorldChange", true);
		configuration.addDefault("LogNicknames", false);
		configuration.addDefault("NeedItemToToggleNick", true);
		configuration.addDefault("NickActionBarMessage", false);
		configuration.addDefault("NickOnWorldChange", false);
		configuration.addDefault("OpenBookGUIOnNickCommand", false);
		configuration.addDefault("OpenNickListGUIOnNickCommand", false);
		configuration.addDefault("OpenRankedNickGUIOnNickCommand", false);
		configuration.addDefault("OpenRankedNickGUIOnNickGUICommand", false);
		configuration.addDefault("OverwriteJoinQuitMessages", false);
		configuration.addDefault("OverwriteMessagePackets", true);
		configuration.addDefault("OverwriteNamesInCommands", true);
		configuration.addDefault("PrefixSuffixUpdateDelay", 100);
		configuration.addDefault("RandomDisguiseDelay", false);
		configuration.addDefault("RemoveMySQLNickOnUnnickWhenLobbyModeEnabled", true);
		configuration.addDefault("ReplaceNickedChatFormat", true);
		configuration.addDefault("SaveLocalNickData", false);
		configuration.addDefault("SeeNickSelf", true);
		configuration.addDefault("ServerIsUsingCloudNETPrefixesAndSuffixes", false);
		configuration.addDefault("ServerIsUsingVaultPrefixesAndSuffixes", false);
		configuration.addDefault("ShowNickActionBarWhenMySQLNicked", true);
		configuration.addDefault("ShowProfileErrorMessages", true);
		configuration.addDefault("SwitchLuckPermsGroupByNicking", false);
		configuration.addDefault("SwitchPermissionsExGroupByNicking", false);
		configuration.addDefault("SwitchUltraPermissionsGroupByNicking", false);
		configuration.addDefault("UpdatePlayerStats", false);
		configuration.addDefault("UseAnvilGUIForCustomName", false);
		configuration.addDefault("UseLocalRankPrefixes", true);
		configuration.addDefault("UseMineSkinAPI", false);
		configuration.addDefault("UseSignGUIForCustomName", true);
		
		configuration.addDefault("CustomPlaceholders.Local.Default", "NOT NICKED");
		configuration.addDefault("CustomPlaceholders.Local.Nicked", "NICKED");
		configuration.addDefault("CustomPlaceholders.Global.Default", "NOT GLOBALLY NICKED");
		configuration.addDefault("CustomPlaceholders.Global.Nicked", "GLOBALLY NICKED");
		
		configuration.addDefault("BypassFormat.Show", true);
		configuration.addDefault("BypassFormat.NameTagPrefix", "&dBYPASS &7| &d");
		configuration.addDefault("BypassFormat.NameTagSuffix", "&r");
		
		configuration.addDefault("OverwrittenMessages.Join", "&8[&a+&8] &7%name%");
		configuration.addDefault("OverwrittenMessages.Quit", "&8[&c-&8] &7%name%");
		
		configuration.addDefault("NickMessage.OnNick", false);
		configuration.addDefault("NickMessage.OnUnnick", false);
		configuration.addDefault("NickMessage.Nick.Join", "&8[&a+&8] &7%name%");
		configuration.addDefault("NickMessage.Nick.Quit", "&8[&c-&8] &7%name%");
		configuration.addDefault("NickMessage.Unnick.Join", "&8[&a+&8] &7%displayName%");
		configuration.addDefault("NickMessage.Unnick.Quit", "&8[&c-&8] &7%displayName%");
		
		configuration.addDefault("NickCommands.SendAsConsole", false);
		configuration.addDefault("NickCommands.OnNick", false);
		configuration.addDefault("NickCommands.OnUnnick", false);
		configuration.addDefault("NickCommands.Nick", Collections.singletonList("/yourCommandOnNick"));
		configuration.addDefault("NickCommands.Unnick", Collections.singletonList("/yourCommandOnUnnick"));
		
		configuration.addDefault("NickItem.getOnJoin", false);
		configuration.addDefault("NickItem.InventorySettings.PlayersCanDropItem", false);
		configuration.addDefault("NickItem.InventorySettings.PlayersCanMoveItem", true);
		configuration.addDefault("NickItem.Slot", 5);

		configuration.addDefault("NickItem.ItemType.Enabled", "NAME_TAG");
		configuration.addDefault("NickItem.ItemAmount.Enabled", 1);
		configuration.addDefault("NickItem.MetaData.Enabled", 0);
		configuration.addDefault("NickItem.Enchanted.Enabled", true);

		configuration.addDefault("NickItem.ItemType.Disabled", "NAME_TAG");
		configuration.addDefault("NickItem.ItemAmount.Disabled", 1);
		configuration.addDefault("NickItem.MetaData.Disabled", 0);
		configuration.addDefault("NickItem.Enchanted.Disabled", false);

		configuration.addDefault("Settings.NickDelay", 1L);
		configuration.addDefault("Settings.ChatFormat", "%prefix%%nickName%%suffix%&7: &f%message%");
		configuration.addDefault("Settings.NameLength.Min", 3);
		configuration.addDefault("Settings.NameLength.Max", 16);
		configuration.addDefault("Settings.ChangeOptions.UUID", true);
		configuration.addDefault("Settings.ChangeOptions.DisplayName", true);
		configuration.addDefault("Settings.ChangeOptions.PlayerListName", true);
		configuration.addDefault("Settings.ChangeOptions.NameTag", true);
		configuration.addDefault("Settings.ChangeOptions.Skin", true);
		configuration.addDefault("Settings.NickFormat.Chat.Prefix", "&7");
		configuration.addDefault("Settings.NickFormat.Chat.Suffix", "&r");
		configuration.addDefault("Settings.NickFormat.PlayerList.Prefix", "&7");
		configuration.addDefault("Settings.NickFormat.PlayerList.Suffix", "&r");
		configuration.addDefault("Settings.NickFormat.NameTag.Prefix", "&7");
		configuration.addDefault("Settings.NickFormat.NameTag.Suffix", "&r");
		configuration.addDefault("Settings.NickFormat.SortID", 9999);
		configuration.addDefault("Settings.NickFormat.GroupName", "DefaultGroup");
		configuration.addDefault("Settings.NickFormat.ServerFullRank.Chat.Prefix", "&a[VIP] ");
		configuration.addDefault("Settings.NickFormat.ServerFullRank.Chat.Suffix", "&r");
		configuration.addDefault("Settings.NickFormat.ServerFullRank.PlayerList.Prefix", "&a[VIP] ");
		configuration.addDefault("Settings.NickFormat.ServerFullRank.PlayerList.Suffix", "&r");
		configuration.addDefault("Settings.NickFormat.ServerFullRank.NameTag.Prefix", "&a[VIP] ");
		configuration.addDefault("Settings.NickFormat.ServerFullRank.NameTag.Suffix", "&r");
		configuration.addDefault("Settings.NickFormat.ServerFullRank.SortID", 9998);
		configuration.addDefault("Settings.NickFormat.ServerFullRank.GroupName", "VIPGroup");
		configuration.addDefault("WorldsWithDisabledLobbyMode", Collections.singletonList("example_world"));
		configuration.addDefault("WorldsWithDisabledPrefixAndSuffix", Collections.singletonList("example_world"));
		configuration.addDefault("WorldsWithDisabledActionBar", Collections.singletonList("example_world"));
		configuration.addDefault("ReplaceNameInCommandBlackList", Arrays.asList("msg", "r"));
		configuration.addDefault("AutoNickWorldBlackList", Collections.singletonList("example_world"));
		configuration.addDefault("BlackList", Collections.singletonList("ExampleName"));
		configuration.addDefault("DisabledNickWorlds", Collections.singletonList("ExampleName"));
		configuration.addDefault(
				"MineSkinUUIDs",
				Arrays.asList(
						"f6268b5d64044876bdb13d9dc1c808aa",
						"37c4366f257049b482a052ca26cf1acc",
						"f8011cd2c70447b49ed2fd6ca5fb47f4"
				)
		);
	}
	
	@Override
	public void reload() {
		this.configuration = YamlConfiguration.loadConfiguration(getFile());
		save();
		
		if(!(configuration.getBoolean("BungeeCord")) && configuration.getBoolean("BungeeCord")) {
			Utils utils = eazyNick.getUtils();
			utils.sendConsole("§cBungeeCord-Mode enabled§8, §cplease reload/restart your server now");
			
			Bukkit.getOnlinePlayers()
					.stream()
					.filter(currentPlayer -> currentPlayer.hasPermission("eazynick.notify"))
					.forEach(currentPlayer -> currentPlayer.sendMessage("§cBungeeCord-Mode enabled§8, §cplease reload/restart your server now"));
			Bukkit.getPluginManager().disablePlugin(eazyNick);
		}
	}
	
}
