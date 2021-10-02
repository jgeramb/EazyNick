package net.dev.eazynick.utilities.configuration.yaml;

import org.bukkit.command.CommandSender;

import net.dev.eazynick.EazyNick;

public class LanguageYamlFile extends YamlFile {
	
	public LanguageYamlFile(EazyNick eazyNick, String subdirectoryName, String fileName) {
		super(eazyNick, subdirectoryName, fileName);
	}

	private String language;

	@Override
	public void setDefaults() {
		if(language.equalsIgnoreCase("de_DE")) {
			configuration.addDefault("NickActionBarMessage", "%prefix%&7Du spielst als&8: &a%nickPrefix%%nickName%%nickSuffix%");
			configuration.addDefault("NickActionBarMessageOther", "&7Du spielst als&8: &a%nickPrefix%%nickName%%nickSuffix%");
			
			configuration.addDefault("NickItem.ItemLore.Enabled", "&7Rechtsklick um den AutoNick zu &cdeaktivieren");
			configuration.addDefault("NickItem.ItemLore.Disabled", "&7Rechtsklick um den AutoNick zu &aaktivieren");
	
			configuration.addDefault("NickItem.DisplayName.Enabled", "&aAutoNick &7(Rechtsklick)");
			configuration.addDefault("NickItem.DisplayName.Disabled", "&cAutoNick &7(Rechtsklick)");
			configuration.addDefault("NickItem.WorldChange.DisplayName.Enabled", "&aAutomatischer Nickname &7(Rechtsklick)");
			configuration.addDefault("NickItem.WorldChange.DisplayName.Disabled", "&cAutomatischer Nickname &7(Rechtsklick)");
			configuration.addDefault("NickItem.BungeeCord.DisplayName.Enabled", "&aAutomatischer Nickname &7(Rechtsklick)");
			configuration.addDefault("NickItem.BungeeCord.DisplayName.Disabled", "&cAutomatischer Nickname &7(Rechtsklick)");
	
			configuration.addDefault("Messages.Prefix", "&5&lNICK &8┃ ");
			configuration.addDefault("Messages.Nick", "%prefix%&7Du spielst als&8: &a%name%");
			configuration.addDefault("Messages.Unnick", "%prefix%&7Dein Nickname wurde entfernt");
			configuration.addDefault("Messages.Name", "%prefix%&7Aktueller Nickname&8: &a%name%");
			configuration.addDefault("Messages.SkinChanged", "%prefix%&7Du hast einen neuen Skin erhalten&8: &a%skinName%");
			configuration.addDefault("Messages.NotNicked", "%prefix%&cDu bist nicht genickt");
			configuration.addDefault("Messages.NickTooLong", "%prefix%&cDer Nickname darf nicht laenger als &a16 Zeichen &csein");
			configuration.addDefault("Messages.NickTooShort", "%prefix%&cDer Nickname darf nicht kuerzer als &a3 Zeichen &csein");
			configuration.addDefault("Messages.NickContainsSpecialCharacters", "%prefix%&cDer Nickname darf nur die Zeichen &aa-z&c, &aA-Z&c, &a0-9 &cund &a_ &centhalten");
			configuration.addDefault("Messages.NickNameAlreadyInUse", "%prefix%&cDieser Nickname wird derzeit schon verwendet");
			configuration.addDefault("Messages.CanNotNickAsSelf", "%prefix%&cDu kannst dich nicht als du selbst nicken");
			configuration.addDefault("Messages.PlayerWithThisNameIsKnown", "%prefix%&cEin Spieler mit diesem Namen ist dem Server bekannt, weshalb du dich nicht in diesen Spieler verwandeln kannst");
			configuration.addDefault("Messages.NoPerm", "&cI'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.");
			configuration.addDefault("Messages.NotPlayer", "&cNur Spieler koennen diesen Befehl ausfuehren");
			configuration.addDefault("Messages.NickDelay", "%prefix%&cBitte warte einen Moment bevor du diese Aktion erneut ausfuehrst");
			configuration.addDefault("Messages.PlayerNotNicked", "%prefix%&cDer angegebene Spieler ist nicht genickt");
			configuration.addDefault("Messages.PlayerNotFound", "%prefix%&cDer Spieler wurde nicht gefunden");
			configuration.addDefault("Messages.NameNotAllowed", "%prefix%&cDieser Nickname ist verboten");
			configuration.addDefault("Messages.RealName", "%prefix%&7Der angegebene Spieler ist&8: &a%realName%");
			configuration.addDefault("Messages.Other.RandomNick", "%prefix%&7Du hast den Spieler &a%playerName% &7genickt");
			configuration.addDefault("Messages.Other.SelectedNick", "%prefix%&7Du hast den Nicknamen von dem Spieler &a%playerName% &7auf &d%nickName% &7gesetzt");
			configuration.addDefault("Messages.Other.Unnick", "%prefix%&7Du hast den Spieler &a%playerName% &7entnickt");
			configuration.addDefault("Messages.Other.DisabledWorld", "%prefix%&cDer Spieler &a%playerName% &ckann in dieser Welt nicht genickt werden");
			configuration.addDefault("Messages.Other.RandomSkin", "%prefix%&7Der Spieler &a%playerName% &7hat einen zufälligen Skin erhalten");
			configuration.addDefault("Messages.Other.SelectedSkin", "%prefix%&7Der Spieler &a%playerName% &7hat den Skin von &d%skinName% &7erhalten");
			configuration.addDefault("Messages.Other.ResetSkin", "%prefix%&7Der Skin von dem Spieler &a%playerName% &7wurde zurueckgesetzt");
			configuration.addDefault("Messages.ReloadConfig", "%prefix%&7Die Konfigurationsdatei wurde neu geladen");
			configuration.addDefault("Messages.FixSkin", "%prefix%&7Dein Skin wurde behoben");
			configuration.addDefault("Messages.ResetSkin", "%prefix%&7Dein Skin wurde zurueckgesetzt");
			configuration.addDefault("Messages.ResetName", "%prefix%&7Dein Name wurde zurueckgesetzt");
			configuration.addDefault("Messages.BungeeAutoNickEnabled", "%prefix%&7Der automatische Nickname wurde &aaktiviert");
			configuration.addDefault("Messages.BungeeAutoNickDisabled", "%prefix%&7Der automatische Nickname wurde &cdeaktiviert");
			configuration.addDefault("Messages.WorldChangeAutoNickEnabled", "%prefix%&7Der automatische Nickname wurde &aaktiviert");
			configuration.addDefault("Messages.WorldChangeAutoNickDisabled", "%prefix%&7Der automatische Nickname wurde &cdeaktiviert");
			configuration.addDefault("Messages.ActiveNick", "%prefix%&7Aktueller Nickname&8: &a%name%");
			configuration.addDefault("Messages.NickedPlayers.CurrentNickedPlayers", "%prefix%&7Hier ist eine Liste von allen genickten Spielern&8:");
			configuration.addDefault("Messages.NickedPlayers.PlayerInfo", "%prefix%&a%realName% &8➜ &d%nickName%");
			configuration.addDefault("Messages.NickedPlayers.NoPlayerIsNicked", "%prefix%&cDerzeit ist kein Spieler genickt");
			configuration.addDefault("Messages.TypeNameInChat", "%prefix%&7Bitte schreibe den Namen in den Chat&8:");
			configuration.addDefault("Messages.DisabledWorld", "%prefix%&cDu kannst dich in dieser Welt nicht nicken");
		} else {
			configuration.addDefault("NickActionBarMessage", "%prefix%&7You play as&8: &a%nickPrefix%%nickName%%nickSuffix%");
			configuration.addDefault("NickActionBarMessageOther", "&7You play as&8: &a%nickPrefix%%nickName%%nickSuffix%");
			
			configuration.addDefault("NickItem.ItemLore.Enabled", "&7Rightclick to &cdisable &7the automatic nickname");
			configuration.addDefault("NickItem.ItemLore.Disabled", "&7Rightclick to &aenable &7the automatic nickname");
	
			configuration.addDefault("NickItem.DisplayName.Enabled", "&aAutoNick &7(rightclick)");
			configuration.addDefault("NickItem.DisplayName.Disabled", "&cAutoNick &7(rightclick)");
			configuration.addDefault("NickItem.WorldChange.DisplayName.Enabled", "&aAutomatic Nickname &7(rightclick)");
			configuration.addDefault("NickItem.WorldChange.DisplayName.Disabled", "&cAutomatic Nickname &7(rightclick)");
			configuration.addDefault("NickItem.BungeeCord.DisplayName.Enabled", "&aAutomatic Nickname &7(rightclick)");
			configuration.addDefault("NickItem.BungeeCord.DisplayName.Disabled", "&cAutomatic Nickname &7(rightclick)");
			
			configuration.addDefault("Messages.Prefix", "&5&lNICK &8┃ ");
			configuration.addDefault("Messages.Nick", "%prefix%&7You play as&8: &a%name%");
			configuration.addDefault("Messages.Unnick", "%prefix%&7Your nickname was removed");
			configuration.addDefault("Messages.Name", "%prefix%&7Current nickname&8: &a%name%");
			configuration.addDefault("Messages.SkinChanged", "%prefix%&7You received a new skin&8: &a%skinName%");
			configuration.addDefault("Messages.NotNicked", "%prefix%&cYou are not nicked");
			configuration.addDefault("Messages.NickTooLong", "%prefix%&cThe nickname must not be longer than &a16 characters");
			configuration.addDefault("Messages.NickTooShort", "%prefix%&cThe nickname must not be shorter than &a3 characters");
			configuration.addDefault("Messages.NickContainsSpecialCharacters", "%prefix%&cThe nickname may only contain the characters &aa-z&c, &aA-Z&c, &a0-9 &cand &a_");
			configuration.addDefault("Messages.NickNameAlreadyInUse", "%prefix%&cThis nickname is currently already in use");
			configuration.addDefault("Messages.CanNotNickAsSelf", "%prefix%&cYou can not nick as yourself");
			configuration.addDefault("Messages.PlayerWithThisNameIsKnown", "%prefix%&cA player with this name is known to the server, which is why you cannot disguise as this player");
			configuration.addDefault("Messages.NoPerm", "&cI'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.");
			configuration.addDefault("Messages.NotPlayer", "&cOnly players can execute this command");
			configuration.addDefault("Messages.NickDelay", "%prefix%&cPlease wait a moment before performing this action again");
			configuration.addDefault("Messages.PlayerNotNicked", "%prefix%&cThe specified player is not nicked");
			configuration.addDefault("Messages.PlayerNotFound", "%prefix%&cThe player was not found");
			configuration.addDefault("Messages.NameNotAllowed", "%prefix%&cThis nickname is not allowed");
			configuration.addDefault("Messages.RealName", "%prefix%&7The specified player is&8: &a%realName%");
			configuration.addDefault("Messages.Other.RandomNick", "%prefix%&7You nicked the player &a%playerName%");
			configuration.addDefault("Messages.Other.SelectedNick", "%prefix%&7You set the name of the player &a%playerName% &7to &d%nickName%");
			configuration.addDefault("Messages.Other.Unnick", "%prefix%&7You unnicked the player &a%playerName%");
			configuration.addDefault("Messages.Other.DisabledWorld", "%prefix%&cThe player &a%playerName% &ccannot be nicked in this world");
			configuration.addDefault("Messages.Other.RandomSkin", "%prefix%&7The player &a%playerName% &7received a random skin");
			configuration.addDefault("Messages.Other.SelectedSkin", "%prefix%&7The player &a%playerName% &7received the skin of &d%skinName%");
			configuration.addDefault("Messages.Other.ResetSkin", "%prefix%&7The skin of the player &a%playerName% &7was reset");
			configuration.addDefault("Messages.ReloadConfig", "%prefix%&7The configuration file was reloaded");
			configuration.addDefault("Messages.FixSkin", "%prefix%&7Your skin was fixed");
			configuration.addDefault("Messages.ResetSkin", "%prefix%&7Your skin was reset");
			configuration.addDefault("Messages.ResetName", "%prefix%&7Your name was reset");
			configuration.addDefault("Messages.BungeeAutoNickEnabled", "%prefix%&7The automatic nickname was &aenabled");
			configuration.addDefault("Messages.BungeeAutoNickDisabled", "%prefix%&7The automatic nickname was &cdisabled");
			configuration.addDefault("Messages.WorldChangeAutoNickEnabled", "%prefix%&7The automatic nickname was &aenabled");
			configuration.addDefault("Messages.WorldChangeAutoNickDisabled", "%prefix%&7The automatic nickname was &cdisabled");
			configuration.addDefault("Messages.ActiveNick", "%prefix%&7Current nickname&8: &a%name%");
			configuration.addDefault("Messages.NickedPlayers.CurrentNickedPlayers", "%prefix%&7Here is a list of all nicked players&8:");
			configuration.addDefault("Messages.NickedPlayers.PlayerInfo", "%prefix%&a%realName% &8➜ &d%nickName%");
			configuration.addDefault("Messages.NickedPlayers.NoPlayerIsNicked", "%prefix%&cCurrently no player is nicked");
			configuration.addDefault("Messages.TypeNameInChat", "%prefix%&7Please write the name in the chat&8:");
			configuration.addDefault("Messages.DisabledWorld", "%prefix%&cYou can not nick yourself in this world");
		}
	}
	
	public void sendMessage(CommandSender sender, String message) {
		if((message != null) && !(message.trim().isEmpty()))
			sender.sendMessage(message);
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String getLanguage() {
		return language;
	}
	
}
