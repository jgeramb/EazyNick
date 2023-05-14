package com.justixdev.eazynick.utilities.configuration.yaml;

import com.justixdev.eazynick.EazyNick;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.Collections;

public class SetupYamlFile extends YamlFile {

    public SetupYamlFile(EazyNick eazyNick) {
        super(eazyNick, "", "setup");
    }

    @Override
    public void setDefaults() {
        super.setDefaults();

        this.configuration.addDefault("Language", "en_US");
        this.configuration.addDefault("BungeeCord", false);
        this.configuration.addDefault("LobbyMode", false);
        this.configuration.addDefault("BungeeMySQL.hostname", "localhost");
        this.configuration.addDefault("BungeeMySQL.port", "3306");
        this.configuration.addDefault("BungeeMySQL.database", "private");
        this.configuration.addDefault("BungeeMySQL.username", "root");
        this.configuration.addDefault("BungeeMySQL.password", "password");

        this.configuration.addDefault("APIMode", false);
        this.configuration.addDefault("AutoUpdater", true);
        this.configuration.addDefault("AutoUpdatePreReleases", false);
        this.configuration.addDefault("AllowBookGUICustomName", true);
        this.configuration.addDefault("AllowBookGUISkinFromName", false);
        this.configuration.addDefault("AllowPlayersToNickAsKnownPlayers", false);
        this.configuration.addDefault("AllowPlayersToNickAsOnlinePlayers", false);
        this.configuration.addDefault("AllowPlayersToUseSameNickName", false);
        this.configuration.addDefault("AllowRealNamesInCommands", false);
        this.configuration.addDefault("AllowSpecialCharactersInCustomName", false);
        this.configuration.addDefault("ChangeLuckPermsPrefixAndSufix", false);
        this.configuration.addDefault("ChangeGroupAndPrefixAndSuffixInTAB", this.eazyNick.getUtils().isPluginInstalled("TAB", "NEZNAMY"));
        this.configuration.addDefault("ChangeSkinsRestorerSkin", this.eazyNick.getUtils().isPluginInstalled("SkinsRestorer"));
        this.configuration.addDefault("DisconnectUnnick", true);
        this.configuration.addDefault("EnableBypassLobbyModePermission", true);
        this.configuration.addDefault("EnableBypassPermission", false);
        this.configuration.addDefault("FakeExperienceLevel", -1);
        this.configuration.addDefault("GetNewNickOnEveryServerSwitch", false);
        this.configuration.addDefault("JoinNick", false);
        this.configuration.addDefault("KeepNickOnDeath", true);
        this.configuration.addDefault("KeepNickOnWorldChange", true);
        this.configuration.addDefault("LogNicknames", false);
        this.configuration.addDefault("NeedItemToToggleNick", true);
        this.configuration.addDefault("NickActionBarMessage", false);
        this.configuration.addDefault("NickOnWorldChange", false);
        this.configuration.addDefault("OpenBookGUIOnNickCommand", false);
        this.configuration.addDefault("OpenNickListGUIOnNickCommand", false);
        this.configuration.addDefault("OpenRankedNickGUIOnNickCommand", false);
        this.configuration.addDefault("OpenRankedNickGUIOnNickGUICommand", false);
        this.configuration.addDefault("OverwriteJoinQuitMessages", false);
        this.configuration.addDefault("OverwriteMessagePackets", true);
        this.configuration.addDefault("OverwriteNamesInCommands", true);
        this.configuration.addDefault("PrefixSuffixUpdateDelay", 100);
        this.configuration.addDefault("RandomDisguiseDelay", false);
        this.configuration.addDefault("RemoveMySQLNickOnUnnickWhenLobbyModeEnabled", true);
        this.configuration.addDefault("ReplaceNickedChatFormat", true);
        this.configuration.addDefault("SaveLocalNickData", false);
        this.configuration.addDefault("SeeNickSelf", true);
        this.configuration.addDefault("ServerIsUsingCloudNETPrefixesAndSuffixes", false);
        this.configuration.addDefault("ServerIsUsingVaultPrefixesAndSuffixes", false);
        this.configuration.addDefault("ShowNickActionBarWhenMySQLNicked", true);
        this.configuration.addDefault("ShowProfileErrorMessages", true);
        this.configuration.addDefault("SwitchLuckPermsGroupByNicking", false);
        this.configuration.addDefault("SwitchPermissionsExGroupByNicking", false);
        this.configuration.addDefault("SwitchUltraPermissionsGroupByNicking", false);
        this.configuration.addDefault("UpdatePlayerStats", false);
        this.configuration.addDefault("UseAnvilGUIForCustomName", false);
        this.configuration.addDefault("UseLocalRankPrefixes", true);
        this.configuration.addDefault("UseMineSkinAPI", false);
        this.configuration.addDefault("UseSignGUIForCustomName", true);

        this.configuration.addDefault("CustomPlaceholders.Local.Default", "NOT NICKED");
        this.configuration.addDefault("CustomPlaceholders.Local.Nicked", "NICKED");
        this.configuration.addDefault("CustomPlaceholders.Global.Default", "NOT GLOBALLY NICKED");
        this.configuration.addDefault("CustomPlaceholders.Global.Nicked", "GLOBALLY NICKED");

        this.configuration.addDefault("BypassFormat.Show", true);
        this.configuration.addDefault("BypassFormat.NameTagPrefix", "&dBYPASS &7| &d");
        this.configuration.addDefault("BypassFormat.NameTagSuffix", "&r");

        this.configuration.addDefault("OverwrittenMessages.Join", "&8[&a+&8] &7%name%");
        this.configuration.addDefault("OverwrittenMessages.Quit", "&8[&c-&8] &7%name%");

        this.configuration.addDefault("NickMessage.OnNick", false);
        this.configuration.addDefault("NickMessage.OnUnnick", false);
        this.configuration.addDefault("NickMessage.Nick.Join", "&8[&a+&8] &7%name%");
        this.configuration.addDefault("NickMessage.Nick.Quit", "&8[&c-&8] &7%name%");
        this.configuration.addDefault("NickMessage.Unnick.Join", "&8[&a+&8] &7%displayName%");
        this.configuration.addDefault("NickMessage.Unnick.Quit", "&8[&c-&8] &7%displayName%");

        this.configuration.addDefault("NickCommands.SendAsConsole", false);
        this.configuration.addDefault("NickCommands.OnNick", false);
        this.configuration.addDefault("NickCommands.OnUnnick", false);
        this.configuration.addDefault("NickCommands.Nick", Collections.singletonList("/yourCommandOnNick"));
        this.configuration.addDefault("NickCommands.Unnick", Collections.singletonList("/yourCommandOnUnnick"));

        this.configuration.addDefault("NickItem.getOnJoin", false);
        this.configuration.addDefault("NickItem.InventorySettings.PlayersCanDropItem", false);
        this.configuration.addDefault("NickItem.InventorySettings.PlayersCanMoveItem", true);
        this.configuration.addDefault("NickItem.Slot", 5);

        this.configuration.addDefault("NickItem.ItemType.Enabled", "NAME_TAG");
        this.configuration.addDefault("NickItem.ItemAmount.Enabled", 1);
        this.configuration.addDefault("NickItem.MetaData.Enabled", 0);
        this.configuration.addDefault("NickItem.Enchanted.Enabled", true);

        this.configuration.addDefault("NickItem.ItemType.Disabled", "NAME_TAG");
        this.configuration.addDefault("NickItem.ItemAmount.Disabled", 1);
        this.configuration.addDefault("NickItem.MetaData.Disabled", 0);
        this.configuration.addDefault("NickItem.Enchanted.Disabled", false);

        this.configuration.addDefault("Settings.NickDelay", 1L);
        this.configuration.addDefault("Settings.ChatFormat", "%prefix%%nickName%%suffix%&7: &f%message%");
        this.configuration.addDefault("Settings.NameLength.Min", 3);
        this.configuration.addDefault("Settings.NameLength.Max", 16);
        this.configuration.addDefault("Settings.ChangeOptions.UUID", true);
        this.configuration.addDefault("Settings.ChangeOptions.DisplayName", true);
        this.configuration.addDefault("Settings.ChangeOptions.PlayerListName", true);
        this.configuration.addDefault("Settings.ChangeOptions.NameTag", true);
        this.configuration.addDefault("Settings.ChangeOptions.Skin", true);
        this.configuration.addDefault("Settings.NickFormat.Chat.Prefix", "&7");
        this.configuration.addDefault("Settings.NickFormat.Chat.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.PlayerList.Prefix", "&7");
        this.configuration.addDefault("Settings.NickFormat.PlayerList.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.NameTag.Prefix", "&7");
        this.configuration.addDefault("Settings.NickFormat.NameTag.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.SortID", 9999);
        this.configuration.addDefault("Settings.NickFormat.GroupName", "DefaultGroup");
        this.configuration.addDefault("Settings.NickFormat.ServerFullRank.Chat.Prefix", "&a[VIP] ");
        this.configuration.addDefault("Settings.NickFormat.ServerFullRank.Chat.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.ServerFullRank.PlayerList.Prefix", "&a[VIP] ");
        this.configuration.addDefault("Settings.NickFormat.ServerFullRank.PlayerList.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.ServerFullRank.NameTag.Prefix", "&a[VIP] ");
        this.configuration.addDefault("Settings.NickFormat.ServerFullRank.NameTag.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.ServerFullRank.SortID", 9998);
        this.configuration.addDefault("Settings.NickFormat.ServerFullRank.GroupName", "VIPGroup");
        this.configuration.addDefault("WorldsWithDisabledLobbyMode", Collections.singletonList("example_world"));
        this.configuration.addDefault("WorldsWithDisabledPrefixAndSuffix", Collections.singletonList("example_world"));
        this.configuration.addDefault("WorldsWithDisabledActionBar", Collections.singletonList("example_world"));
        this.configuration.addDefault("ReplaceNameInCommandBlackList", Arrays.asList("msg", "r"));
        this.configuration.addDefault("AutoNickWorldBlackList", Collections.singletonList("example_world"));
        this.configuration.addDefault("BlackList", Collections.singletonList("ExampleName"));
        this.configuration.addDefault("DisabledNickWorlds", Collections.singletonList("ExampleName"));
        this.configuration.addDefault(
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
        super.reload();

        if(this.utils != null) {
            boolean bungeecord = this.configuration.getBoolean("BungeeCord");

            if (!bungeecord && this.configuration.getBoolean("BungeeCord")) {
                this.utils.sendConsole("§cBungeeCord-Mode enabled§8, §cplease reload/restart your server now");

                Bukkit.getOnlinePlayers()
                        .stream()
                        .filter(currentPlayer -> currentPlayer.hasPermission("eazynick.notify"))
                        .forEach(currentPlayer -> currentPlayer.sendMessage("§cBungeeCord-Mode enabled§8, §cplease reload/restart your server now"));
                Bukkit.getPluginManager().disablePlugin(this.eazyNick);
                return;
            }

            this.utils.getReplaceNameInCommandBlackList().clear();
            this.utils.getReplaceNameInCommandBlackList().addAll(this.configuration.getStringList("ReplaceNameInCommandBlackList"));

            this.utils.getBlackList().clear();
            this.utils.getBlackList().addAll(this.configuration.getStringList("BlackList"));

            this.utils.getWorldsWithDisabledLobbyMode().clear();
            this.utils.getWorldsWithDisabledLobbyMode().addAll(this.configuration.getStringList("WorldsWithDisabledLobbyMode"));

            this.utils.getWorldsWithDisabledPrefixAndSuffix().clear();
            this.utils.getWorldsWithDisabledPrefixAndSuffix().addAll(this.configuration.getStringList("WorldsWithDisabledPrefixAndSuffix"));

            this.utils.getWorldsWithDisabledActionBar().clear();
            this.utils.getWorldsWithDisabledActionBar().addAll(this.configuration.getStringList("WorldsWithDisabledActionBar"));

            this.utils.getWorldBlackList().clear();
            this.utils.getWorldBlackList().addAll(this.configuration.getStringList("AutoNickWorldBlackList"));

            this.utils.getMineSkinUUIDs().clear();
            this.utils.getMineSkinUUIDs().addAll(this.configuration.getStringList("MineSkinUUIDs"));
        }
    }

}
