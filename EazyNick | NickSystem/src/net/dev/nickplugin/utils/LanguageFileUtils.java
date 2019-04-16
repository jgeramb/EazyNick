package net.dev.nickplugin.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.dev.nickplugin.main.Main;

public class LanguageFileUtils {

	public static File folder = new File("plugins/" + Main.getPlugin(Main.class).getDescription().getName() + "/lang/");
	public static File file = new File("plugins/" + Main.getPlugin(Main.class).getDescription().getName() + "/lang/de_DE.yml");
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
		
		if(file.getName().contains("de_DE")) {
			cfg.addDefault("NickItem.ItemLore.Enabled", "&7Rechtsklick um den AutoNick zu &cdeaktivieren");
			cfg.addDefault("NickItem.ItemLore.Disabled", "&7Rechtsklick um den AutoNick zu &aaktivieren");
	
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
			cfg.addDefault("Messages.BungeeAutoNickEnabled", "&4Der automatische Nickname wurde &aaktiviert");
			cfg.addDefault("Messages.BungeeAutoNickDisabled", "&4Der automatische Nickname wurde &cdeaktiviert");
			cfg.addDefault("Messages.WorldChangeAutoNickEnabled", "&4Der automatische Nickname wurde &aaktiviert");
			cfg.addDefault("Messages.WorldChangeAutoNickDisabled", "&4Der automatische Nickname wurde &cdeaktiviert");
			cfg.addDefault("Messages.ActiveNick", "&4Aktueller Nickname&8: &6%name%");
			cfg.addDefault("Messages.NickedPlayers.CurrentNickedPlayers", "&4Hier ist eine Liste von &eallen genickten Spielern&8:");
			cfg.addDefault("Messages.NickedPlayers.PlayerINFO", "&5%realName% &7-> &3%nickName%");
			cfg.addDefault("Messages.NickedPlayers.NoPlayerIsNicked", "&cDerzeit ist &4&lkein Spieler genickt");
		} else {
			cfg.addDefault("NickItem.ItemLore.Enabled", "&7Rightclick to &cdeactivate &7the automatic nickname");
			cfg.addDefault("NickItem.ItemLore.Disabled", "&7Rightclick to &aactivate &7the automatic nickname");
	
			cfg.addDefault("NickItem.DisplayName.Enabled", "&a&lAutoNick &7(rightclick)");
			cfg.addDefault("NickItem.DisplayName.Disabled", "&c&lAutoNick &7(rightclick)");
			cfg.addDefault("NickItem.WorldChange.DisplayName.Enabled", "&a&lAutomatic Nickname &7(rightclick)");
			cfg.addDefault("NickItem.WorldChange.DisplayName.Disabled", "&c&lAutomatic Nickname &7(rightclick)");
			cfg.addDefault("NickItem.BungeeCord.DisplayName.Enabled", "&a&lAutomatic Nickname &7(rightclick)");
			cfg.addDefault("NickItem.BungeeCord.DisplayName.Disabled", "&c&lAutomatic Nickname &7(rightclick)");
	
			cfg.addDefault("NickGUI.InventoryTitle", "&5Nick&8:");
			cfg.addDefault("NickGUI.NickItem.DisplayName", "&aNick");
			cfg.addDefault("NickGUI.UnnickItem.DisplayName", "&cUnnick");
	
			cfg.addDefault("NickNameGUI.InventoryTitle", "&5Nick &7[&aPage %currentPage%&7]&8: ");
			cfg.addDefault("NickNameGUI.BackItem.DisplayName", "&8[&e<--&8] &7Back");
			cfg.addDefault("NickNameGUI.NextItem.DisplayName", "&7Next &8[&e-->&8]");
			cfg.addDefault("NickNameGUI.NickNameSkull.DisplayName", "&e&l%nickName%");
			
			cfg.addDefault("Messages.Prefix", "&8[&5NICK&8]");
			cfg.addDefault("Messages.Nick", "&4You are now playing as&8: &6%name%");
			cfg.addDefault("Messages.Unnick", "&4Your nickname has been reset");
			cfg.addDefault("Messages.Name", "&4Current nickname&8: &6%name%");
			cfg.addDefault("Messages.SkinChanged", "&4You have received a new skin&7: &6%skinName%");
			cfg.addDefault("Messages.NameChanged", "&4You have received a new name&7: &6%nickName%");
			cfg.addDefault("Messages.NotNicked", "&cYou are &4&lnot nicked");
			cfg.addDefault("Messages.NickTooLong", "&cYour nickname can not be longer than &4&l16 characters");
			cfg.addDefault("Messages.NickNameAlreadyInUse", "&cYour nickname is &4&lcurrently in use");
			cfg.addDefault("Messages.CanNotNickAsSelf", "&cYou can't disguise as &4&lyourself");
			cfg.addDefault("Messages.PlayerWithThisNameIsKnown", "&cYou can't disguise as this player, because a player with that name is &4&lknown &con this Server");
			cfg.addDefault("Messages.NoPerm", "&cI'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.");
			cfg.addDefault("Messages.NotPlayer", "&4&lOnly players &ccan perform this command");
			cfg.addDefault("Messages.NickDelay", "&cPlease wait &4&la moment &cbefore you perform this action again");
			cfg.addDefault("Messages.PlayerNotNicked", "&cThe player is &4&lnot nicked");
			cfg.addDefault("Messages.PlayerNotFound", "&cThe player &4&lcould not be found");
			cfg.addDefault("Messages.NameNotAllowed", "&cThis nickname is &4&lFORBIDDEN");
			cfg.addDefault("Messages.RealName", "&4This player is&7: &5%realName%");
			cfg.addDefault("Messages.Other.RandomNick", "&4You nicked the player &e%playerName%");
			cfg.addDefault("Messages.Other.SelectedNick", "&4You set the nickname of &e%playerName% &4to &e%nickName%");
			cfg.addDefault("Messages.Other.Unnick", "&4You removed the nickname of player &e%playerName%");
			cfg.addDefault("Messages.NoMorePages", "&cThere are &4&lno more pages");
			cfg.addDefault("Messages.NoMorePagesCanBeLoaded", "&4&lNo more pages &can be loaded.");
			cfg.addDefault("Messages.CommandNotAvaiableForThatVersion", "&cThis command &4&lcan't be used &cin this version");
			cfg.addDefault("Messages.ReloadConfig", "&4The configuration file was &ereloaded&4!");
			cfg.addDefault("Messages.FixSkin", "&4Your skin has been fixed &asuccessfully &4!");
			cfg.addDefault("Messages.ResetSkin", "&4Your skin has been reset &asuccessfully&4!");
			cfg.addDefault("Messages.ResetName", "&4Your name has been reset &asuccessfully&4!");
			cfg.addDefault("Messages.BungeeAutoNickEnabled", "&4The automatic nickname was &aactivated");
			cfg.addDefault("Messages.BungeeAutoNickDisabled", "&4The automatic nickname was &cdeactivated");
			cfg.addDefault("Messages.WorldChangeAutoNickEnabled", "&4The automatic nickname was &aactivated");
			cfg.addDefault("Messages.WorldChangeAutoNickDisabled", "&4The automatic nickname was &cdeactivated");
			cfg.addDefault("Messages.ActiveNick", "&4Current nickname&8: &6%name%");
			cfg.addDefault("Messages.NickedPlayers.CurrentNickedPlayers", "&4Here is a list of &eall nicked players&8:");
			cfg.addDefault("Messages.NickedPlayers.PlayerINFO", "&5%realName% &7-> &3%nickName%");
			cfg.addDefault("Messages.NickedPlayers.NoPlayerIsNicked", "&cCurrently &4&lno player is nicked");
		}
		
		cfg.options().copyDefaults(true);
		saveFile();
	}
	
}
