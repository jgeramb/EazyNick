package net.dev.eazynick.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

import net.dev.eazynick.EazyNick;

public class LanguageFileUtils {

	private File directory, file;
	private YamlConfiguration cfg;
	
	public LanguageFileUtils(String language) {
		PluginDescriptionFile desc = EazyNick.getInstance().getDescription();
		
		directory = new File("plugins/" + desc.getName() + "/lang/");
		file = new File(directory, language + ".yml");
		
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
		
		if(language.equalsIgnoreCase("de_DE")) {
			cfg.addDefault("NickActionBarMessage", "%prefix%&4Du spielst als&8: &6%nickName%");
			
			cfg.addDefault("NickItem.ItemLore.Enabled", "&7Rechtsklick um den AutoNick zu &cdeaktivieren");
			cfg.addDefault("NickItem.ItemLore.Disabled", "&7Rechtsklick um den AutoNick zu &aaktivieren");
	
			cfg.addDefault("NickItem.DisplayName.Enabled", "&a&lAutoNick &7(Rechtsklick)");
			cfg.addDefault("NickItem.DisplayName.Disabled", "&c&lAutoNick &7(Rechtsklick)");
			cfg.addDefault("NickItem.WorldChange.DisplayName.Enabled", "&a&lAutomatischer Nickname &7(Rechtsklick)");
			cfg.addDefault("NickItem.WorldChange.DisplayName.Disabled", "&c&lAutomatischer Nickname &7(Rechtsklick)");
			cfg.addDefault("NickItem.BungeeCord.DisplayName.Enabled", "&a&lAutomatischer Nickname &7(Rechtsklick)");
			cfg.addDefault("NickItem.BungeeCord.DisplayName.Disabled", "&c&lAutomatischer Nickname &7(Rechtsklick)");
	
			cfg.addDefault("Messages.Prefix", "&8[&5NICK&8] ");
			cfg.addDefault("Messages.Nick", "&4Du spielst als&8: &6%name%");
			cfg.addDefault("Messages.Unnick", "&4Dein Nickname wurde entfernt");
			cfg.addDefault("Messages.Name", "&4Aktueller Nickname&8: &6%name%");
			cfg.addDefault("Messages.SkinChanged", "&4Du hast einen neuen Skin erhalten&7: &6%skinName%");
			cfg.addDefault("Messages.NameChanged", "&4Du hast einen neuen Namen erhalten&7: &6%nickName%");
			cfg.addDefault("Messages.NotNicked", "&cDu bist nicht genickt");
			cfg.addDefault("Messages.NickTooLong", "&cDer Nickname darf nicht laenger als &616 Zeichen &csein");
			cfg.addDefault("Messages.NickTooShort", "&cDer Nickname darf nicht kuerzer als &63 Zeichen &csein");
			cfg.addDefault("Messages.NickContainsSpecialCharacters", "&cDer Nickname darf nur die Zeichen &6a-z, A-Z, 0-9 &cund &6_ &centhalten");
			cfg.addDefault("Messages.NickNameAlreadyInUse", "&cDieser Nickname wird derzeit schon verwendet");
			cfg.addDefault("Messages.CanNotNickAsSelf", "&cDu kannst dich nicht als &6du selbst &cnicken");
			cfg.addDefault("Messages.PlayerWithThisNameIsKnown", "&cEin Spieler mit diesem Namen ist dem Server bekannt, weshalb du dich nicht in diesen Spieler verwandeln kannst");
			cfg.addDefault("Messages.NoPerm", "&cI'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.");
			cfg.addDefault("Messages.NotPlayer", "&cNur Spieler koennen diesen Befehl ausfuehren");
			cfg.addDefault("Messages.NickDelay", "&cBitte warte &6einen Moment &cbevor du diese Aktion erneut ausfuehrst");
			cfg.addDefault("Messages.PlayerNotNicked", "&cDer angegebene Spieler ist nicht genickt");
			cfg.addDefault("Messages.PlayerNotFound", "&cDer Spieler wurde nicht gefunden");
			cfg.addDefault("Messages.NameNotAllowed", "&cDieser Nickname ist verboten");
			cfg.addDefault("Messages.RealName", "&4Der angegebene Spieler ist&7: &5%realName%");
			cfg.addDefault("Messages.Other.RandomNick", "&4Du hast den Spieler &e%playerName% &4genickt");
			cfg.addDefault("Messages.Other.SelectedNick", "&4Du hast den Nicknamen von dem Spieler &e%playerName% &4auf &e%nickName% &4gesetzt");
			cfg.addDefault("Messages.Other.Unnick", "&4Du hast den Spieler &e%playerName% &4entnickt");
			cfg.addDefault("Messages.ReloadConfig", "&4Die Konfigurationsdatei wurde &eneu geladen&4!");
			cfg.addDefault("Messages.FixSkin", "&4Dein Skin wurde &aerfolgreich &4gefixxt!");
			cfg.addDefault("Messages.ResetSkin", "&4Dein Skin wurde &aerfolgreich &4zurueckgesetzt!");
			cfg.addDefault("Messages.ResetName", "&4Dein Name wurde &aerfolgreich &4zurueckgesetzt!");
			cfg.addDefault("Messages.BungeeAutoNickEnabled", "&4Der automatische Nickname wurde &aAktiviert");
			cfg.addDefault("Messages.BungeeAutoNickDisabled", "&4Der automatische Nickname wurde &cDeaktiviert");
			cfg.addDefault("Messages.WorldChangeAutoNickEnabled", "&4Der automatische Nickname wurde &aAktiviert");
			cfg.addDefault("Messages.WorldChangeAutoNickDisabled", "&4Der automatische Nickname wurde &cDeaktiviert");
			cfg.addDefault("Messages.ActiveNick", "&4Aktueller Nickname&8: &6%name%");
			cfg.addDefault("Messages.NickedPlayers.CurrentNickedPlayers", "&4Hier ist eine Liste von allen genickten Spielern&8:");
			cfg.addDefault("Messages.NickedPlayers.PlayerINFO", "&5%realName% &7-> &6%nickName%");
			cfg.addDefault("Messages.NickedPlayers.NoPlayerIsNicked", "&cDerzeit ist kein Spieler genickt");
			cfg.addDefault("Messages.TypeNameInChat", "&4Bitte schreibe den Namen in den Chat&8:");
		} else {
			cfg.addDefault("NickActionBarMessage", "%prefix%&4You are playing as&8: &6%nickName%");
			
			cfg.addDefault("NickItem.ItemLore.Enabled", "&7Rightclick to &cdeactivate &7the automatic nickname");
			cfg.addDefault("NickItem.ItemLore.Disabled", "&7Rightclick to &aactivate &7the automatic nickname");
	
			cfg.addDefault("NickItem.DisplayName.Enabled", "&a&lAutoNick &7(rightclick)");
			cfg.addDefault("NickItem.DisplayName.Disabled", "&c&lAutoNick &7(rightclick)");
			cfg.addDefault("NickItem.WorldChange.DisplayName.Enabled", "&a&lAutomatic Nickname &7(rightclick)");
			cfg.addDefault("NickItem.WorldChange.DisplayName.Disabled", "&c&lAutomatic Nickname &7(rightclick)");
			cfg.addDefault("NickItem.BungeeCord.DisplayName.Enabled", "&a&lAutomatic Nickname &7(rightclick)");
			cfg.addDefault("NickItem.BungeeCord.DisplayName.Disabled", "&c&lAutomatic Nickname &7(rightclick)");
			
			cfg.addDefault("Messages.Prefix", "&8[&5NICK&8] ");
			cfg.addDefault("Messages.Nick", "&4You are playing as&8: &6%name%");
			cfg.addDefault("Messages.Unnick", "&4Your nickname has been reset");
			cfg.addDefault("Messages.Name", "&4Current nickname&8: &6%name%");
			cfg.addDefault("Messages.SkinChanged", "&4You have received a new skin&7: &6%skinName%");
			cfg.addDefault("Messages.NameChanged", "&4You have received a new name&7: &6%nickName%");
			cfg.addDefault("Messages.NotNicked", "&cYou are not nicked");
			cfg.addDefault("Messages.NickTooLong", "&cThe nickname may not be longer than &616 characters");
			cfg.addDefault("Messages.NickTooShort", "&cThe nickname may not be shorter than &63 characters");
			cfg.addDefault("Messages.NickContainsSpecialCharacters", "&cThe nickname may not contain any other characters than &6a-z, A-Z, 0-9 &cand &6_");
			cfg.addDefault("Messages.NickNameAlreadyInUse", "&cThis nickname is currently in use");
			cfg.addDefault("Messages.CanNotNickAsSelf", "&cYou can't disguise as &6yourself");
			cfg.addDefault("Messages.PlayerWithThisNameIsKnown", "&cYou can't disguise as this player, because a player with that name is known on this Server");
			cfg.addDefault("Messages.NoPerm", "&cI'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.");
			cfg.addDefault("Messages.NotPlayer", "&cOnly players can perform this command");
			cfg.addDefault("Messages.NickDelay", "&cPlease wait &6a moment &cbefore you perform this action again");
			cfg.addDefault("Messages.PlayerNotNicked", "&cThe player is not nicked");
			cfg.addDefault("Messages.PlayerNotFound", "&cThe player could not be found");
			cfg.addDefault("Messages.NameNotAllowed", "&cThis nickname is forbidden");
			cfg.addDefault("Messages.RealName", "&4This player is&7: &5%realName%");
			cfg.addDefault("Messages.Other.RandomNick", "&4You nicked the player &e%playerName%");
			cfg.addDefault("Messages.Other.SelectedNick", "&4You set the nickname of &e%playerName% &4to &e%nickName%");
			cfg.addDefault("Messages.Other.Unnick", "&4You removed the nickname of player &e%playerName%");
			cfg.addDefault("Messages.CommandNotAvaiableForThatVersion", "&cThis command can't be used in this version");
			cfg.addDefault("Messages.ReloadConfig", "&4The configuration file was &ereloaded&4!");
			cfg.addDefault("Messages.FixSkin", "&4Your skin has been fixed &asuccessfully &4!");
			cfg.addDefault("Messages.ResetSkin", "&4Your skin has been reset &asuccessfully&4!");
			cfg.addDefault("Messages.ResetName", "&4Your name has been reset &asuccessfully&4!");
			cfg.addDefault("Messages.BungeeAutoNickEnabled", "&4The automatic nickname was &aactivated");
			cfg.addDefault("Messages.BungeeAutoNickDisabled", "&4The automatic nickname was &cdeactivated");
			cfg.addDefault("Messages.WorldChangeAutoNickEnabled", "&4The automatic nickname was &aactivated");
			cfg.addDefault("Messages.WorldChangeAutoNickDisabled", "&4The automatic nickname was &cdeactivated");
			cfg.addDefault("Messages.ActiveNick", "&4Current nickname&8: &6%name%");
			cfg.addDefault("Messages.NickedPlayers.CurrentNickedPlayers", "&4Here is a list of all nicked players&8:");
			cfg.addDefault("Messages.NickedPlayers.PlayerINFO", "&5%realName% &7-> &6%nickName%");
			cfg.addDefault("Messages.NickedPlayers.NoPlayerIsNicked", "&cCurrently no player is nicked");
			cfg.addDefault("Messages.TypeNameInChat", "&4Please type the name you want to use in chat&8:");
		}
		
		cfg.options().copyDefaults(true);
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
	
	public YamlConfiguration getConfig() {
		return cfg;
	}
	
	public void setConfig(YamlConfiguration cfg) {
		this.cfg = cfg;
	}
	
}
