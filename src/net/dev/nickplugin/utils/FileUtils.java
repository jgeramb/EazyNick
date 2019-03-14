package net.dev.nickplugin.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

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
		cfg.addDefault("SwitchPermissionsExGroupByNicking", false);
		cfg.addDefault("ServerIsUsingCloudNETPrefixes", false);

		cfg.addDefault("NickItem.getOnJoin", true);
		cfg.addDefault("NickItem.InventorySettings.PlayersCanDropItem", false);
		cfg.addDefault("NickItem.InventorySettings.PlayersCanMoveItem", true);
		cfg.addDefault("NickItem.Slot", 5);

		cfg.addDefault("NickItem.ItemType.Enabled", "NAME_TAG");
		cfg.addDefault("NickItem.ItemLore.Enabled", "&7Rechtsklick um den AutoNick zu &cdeaktivieren");
		cfg.addDefault("NickItem.ItemAmount.Enabled", 1);
		cfg.addDefault("NickItem.MetaData.Enabled", 0);
		cfg.addDefault("NickItem.Enchanted.Enabled", true);

		cfg.addDefault("NickItem.ItemType.Disabled", "NAME_TAG");
		cfg.addDefault("NickItem.ItemLore.Disabled", "&7Rechtsklick um den AutoNick zu &aaktivieren");
		cfg.addDefault("NickItem.ItemAmount.Disabled", 1);
		cfg.addDefault("NickItem.MetaData.Disabled", 0);
		cfg.addDefault("NickItem.Enchanted.Disabled", false);

		cfg.addDefault("NickItem.DisplayName.Enabled", "&a&lAutoNick &7(Rechtsklick)");
		cfg.addDefault("NickItem.DisplayName.Disabled", "&c&lAutoNick &7(Rechtsklick)");
		cfg.addDefault("NickItem.WorldChange.DisplayName.Enabled", "&a&lAutomatischer Nickname &7(Rechtsklick)");
		cfg.addDefault("NickItem.WorldChange.DisplayName.Disabled", "&c&lAutomatischer Nickname &7(Rechtsklick)");
		cfg.addDefault("NickItem.BungeeCord.DisplayName.Enabled", "&a&lAutomatischer Nickname &7(Rechtsklick)");
		cfg.addDefault("NickItem.BungeeCord.DisplayName.Disabled", "&c&lAutomatischer Nickname &7(Rechtsklick)");

		cfg.addDefault("NickGUI.InventoryTitle", "&5Nick&8:");
		cfg.addDefault("NickGUI.NickItem.DisplayName", "&aNick");
		cfg.addDefault("NickGUI.UnnickItem.DisplayName", "&cUnnick");

		cfg.addDefault("NickNameGUI.InventoryTitle", "&5Nick &7[&aSeite %currentPage%&7]&8: ");
		cfg.addDefault("NickNameGUI.BackItem.DisplayName", "&8[&e<--&8] &7Zurueck");
		cfg.addDefault("NickNameGUI.NextItem.DisplayName", "&7Weiter &8[&e-->&8]");
		cfg.addDefault("NickNameGUI.NickNameSkull.DisplayName", "&e&l%nickName%");

		cfg.addDefault("Messages.Prefix", "&8[&5NICK&8]");
		cfg.addDefault("Messages.Nick", "&4Du spielst als&8: &6%name%");
		cfg.addDefault("Messages.Unnick", "&4Dein Nickname wurde entfernt");
		cfg.addDefault("Messages.Name", "&4Aktueller Nickname&8: &6%name%");
		cfg.addDefault("Messages.SkinChanged", "&4Du hast einen neuen Skin erhalten&7: &6%skinName%");
		cfg.addDefault("Messages.NameChanged", "&4Du hast einen neuen Namen erhalten&7: &6%nickName%");
		cfg.addDefault("Messages.NotNicked", "&cDu bist &4&lnicht genickt");
		cfg.addDefault("Messages.NickTooLong", "&cDieser Nickname darf nicht laenger als &4&l16 Zeichen &csein");
		cfg.addDefault("Messages.NickNameAlreadyInUse", "&cDein Nickname wird &4&lderzeit schon benutzt");
		cfg.addDefault("Messages.CanNotNickAsSelf", "&cDu kannst dich &4&lnicht als du selbst &cnicken");
		cfg.addDefault("Messages.PlayerWithThisNameIsKnown", "&cEin Spieler mit diesem Namen ist dem Server &4&lbekannt&c, weshalb du dich nicht in diesen Spieler verwandeln kannst");
		cfg.addDefault("Messages.NoPerm", "&cI'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.");
		cfg.addDefault("Messages.NotPlayer", "&4&lNur Spieler &ckoennen diesen Befehl ausfuehren");
		cfg.addDefault("Messages.NickDelay", "&cBitte warte &4&leinen Moment &cbevor du diese Aktion erneut ausfuehrst");
		cfg.addDefault("Messages.PlayerNotNicked", "&cDer angegebene Spieler ist &4&lnicht genickt");
		cfg.addDefault("Messages.PlayerNotFound", "&cDer Spieler wurde &4&lnicht gefunden");
		cfg.addDefault("Messages.NameNotAllowed", "&cDieser Nickname ist &4&lVERBOTEN");
		cfg.addDefault("Messages.RealName", "&4Der angegebene Spieler ist&7: &5%realName%");
		cfg.addDefault("Messages.Other.RandomNick", "&4Du hast den Spieler &e%playerName% &4genickt");
		cfg.addDefault("Messages.Other.SelectedNick", "&4Du hast den Nicknamen von dem Spieler &e%playerName% &4auf &e%nickName% &4gesetzt");
		cfg.addDefault("Messages.Other.Unnick", "&4Du hast den Spieler &e%playerName% &4entnickt");
		cfg.addDefault("Messages.NoMorePages", "&cEs sind &4&lkeine weiteren Seiten &cverfuegbar");
		cfg.addDefault("Messages.NoMorePagesCanBeLoaded", "&cEs koennen &4&lkeine weiteren Seiten &cgeladen werden!");
		cfg.addDefault("Messages.CommandNotAvaiableForThatVersion", "&cDieser Befehl kann in dieser Minecraft-Version &4&lnicht benutzt &cwerden!");
		cfg.addDefault("Messages.ReloadConfig", "&4Die Konfigurationsdatei wurde &eneu geladen&4!");
		cfg.addDefault("Messages.FixSkin", "&4Dein Skin wurde &aerfolgreich &4gefixxt!");
		cfg.addDefault("Messages.ResetSkin", "&4Dein Skin wurde &aerfolgreich &4zurueckgesetzt!");
		cfg.addDefault("Messages.ResetName", "&4Dein Name wurde &aerfolgreich &4zurueckgesetzt!");
		cfg.addDefault("Messages.BungeeAutoNickEnabled", "&4Der automatische Nickname wurde &aAktiviert");
		cfg.addDefault("Messages.BungeeAutoNickDisabled", "&4Der automatische Nickname wurde &cDeaktiviert");
		cfg.addDefault("Messages.WorldChangeAutoNickEnabled", "&4Der automatische Nickname wurde &aAktiviert");
		cfg.addDefault("Messages.WorldChangeAutoNickDisabled", "&4Der automatische Nickname wurde &cDeaktiviert");
		cfg.addDefault("Messages.ActiveNick", "&4Aktueller Nickname&8: &6%name%");
		cfg.addDefault("Messages.NickedPlayers.CurrentNickedPlayers", "&4Hier ist eine Liste von &eallen genickten Spielern&8:");
		cfg.addDefault("Messages.NickedPlayers.PlayerINFO", "&5%realName% &7-> &3%nickName%");
		cfg.addDefault("Messages.NickedPlayers.NoPlayerIsNicked", "&cDerzeit ist &4&lkein Spieler genickt");

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
	}
	
}
