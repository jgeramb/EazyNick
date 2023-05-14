package com.justixdev.eazynick.utilities.configuration.yaml;

import com.justixdev.eazynick.EazyNick;
import org.bukkit.command.CommandSender;

public class LanguageYamlFile extends YamlFile {

    public LanguageYamlFile(EazyNick eazyNick) {
        super(eazyNick, "lang/", eazyNick.getSetupYamlFile().getConfigString("Language"));
    }
    @Override
    public void setDefaults() {
        super.setDefaults();

        if(this.eazyNick.getSetupYamlFile().getConfigString("Language").equalsIgnoreCase("de_DE")) {
            this.configuration.addDefault("NickActionBarMessage", "%prefix%&7Du spielst als&8: &a%nickPrefix%%nickName%%nickSuffix%");
            this.configuration.addDefault("NickActionBarMessageOther", "&7Du spielst als&8: &a%nickPrefix%%nickName%%nickSuffix%");

            this.configuration.addDefault("NickItem.ItemLore.Enabled", "&7Rechtsklick um den AutoNick zu &cdeaktivieren");
            this.configuration.addDefault("NickItem.ItemLore.Disabled", "&7Rechtsklick um den AutoNick zu &aaktivieren");

            this.configuration.addDefault("NickItem.DisplayName.Enabled", "&aAutoNick &7(Rechtsklick)");
            this.configuration.addDefault("NickItem.DisplayName.Disabled", "&cAutoNick &7(Rechtsklick)");
            this.configuration.addDefault("NickItem.WorldChange.DisplayName.Enabled", "&aAutomatischer Nickname &7(Rechtsklick)");
            this.configuration.addDefault("NickItem.WorldChange.DisplayName.Disabled", "&cAutomatischer Nickname &7(Rechtsklick)");
            this.configuration.addDefault("NickItem.BungeeCord.DisplayName.Enabled", "&aAutomatischer Nickname &7(Rechtsklick)");
            this.configuration.addDefault("NickItem.BungeeCord.DisplayName.Disabled", "&cAutomatischer Nickname &7(Rechtsklick)");

            this.configuration.addDefault("Messages.Prefix", "&5&lNICK&r &8┃ ");
            this.configuration.addDefault("Messages.Nick", "%prefix%&7Du spielst als&8: &a%name%");
            this.configuration.addDefault("Messages.Unnick", "%prefix%&7Dein Nickname wurde entfernt");
            this.configuration.addDefault("Messages.Name", "%prefix%&7Aktueller Nickname&8: &a%name%");
            this.configuration.addDefault("Messages.SkinChanged", "%prefix%&7Du hast einen neuen Skin erhalten&8: &a%skinName%");
            this.configuration.addDefault("Messages.NotNicked", "%prefix%&cDu bist nicht genickt");
            this.configuration.addDefault("Messages.NickTooLong", "%prefix%&cDer Nickname darf nicht laenger als &a16 Zeichen &csein");
            this.configuration.addDefault("Messages.NickTooShort", "%prefix%&cDer Nickname darf nicht kuerzer als &a3 Zeichen &csein");
            this.configuration.addDefault("Messages.NickContainsSpecialCharacters", "%prefix%&cDer Nickname darf nur die Zeichen &aa-z&c, &aA-Z&c, &a0-9 &cund &a_ &centhalten");
            this.configuration.addDefault("Messages.NickNameAlreadyInUse", "%prefix%&cDieser Nickname wird derzeit schon verwendet");
            this.configuration.addDefault("Messages.CanNotNickAsSelf", "%prefix%&cDu kannst dich nicht als du selbst nicken");
            this.configuration.addDefault("Messages.PlayerWithThisNameIsKnown", "%prefix%&cEin Spieler mit diesem Namen ist dem Server bekannt, weshalb du dich nicht in diesen Spieler verwandeln kannst");
            this.configuration.addDefault("Messages.NoPerm", "&cI'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.");
            this.configuration.addDefault("Messages.NotPlayer", "&cNur Spieler koennen diesen Befehl ausfuehren");
            this.configuration.addDefault("Messages.NickDelay", "%prefix%&cBitte warte einen Moment bevor du diese Aktion erneut ausfuehrst");
            this.configuration.addDefault("Messages.PlayerNotNicked", "%prefix%&cDer angegebene Spieler ist nicht genickt");
            this.configuration.addDefault("Messages.PlayerNotFound", "%prefix%&cDer Spieler wurde nicht gefunden");
            this.configuration.addDefault("Messages.NameNotAllowed", "%prefix%&cDieser Nickname ist verboten");
            this.configuration.addDefault("Messages.RealName", "%prefix%&7Der angegebene Spieler ist&8: &a%realName%");
            this.configuration.addDefault("Messages.Other.RandomNick", "%prefix%&7Du hast den Spieler &a%playerName% &7genickt");
            this.configuration.addDefault("Messages.Other.SelectedNick", "%prefix%&7Du hast den Nicknamen von dem Spieler &a%playerName% &7auf &d%nickName% &7gesetzt");
            this.configuration.addDefault("Messages.Other.Unnick", "%prefix%&7Du hast den Spieler &a%playerName% &7entnickt");
            this.configuration.addDefault("Messages.Other.DisabledWorld", "%prefix%&cDer Spieler &a%playerName% &ckann in dieser Welt nicht genickt werden");
            this.configuration.addDefault("Messages.Other.RandomSkin", "%prefix%&7Der Spieler &a%playerName% &7hat einen zufälligen Skin erhalten");
            this.configuration.addDefault("Messages.Other.SelectedSkin", "%prefix%&7Der Spieler &a%playerName% &7hat den Skin von &d%skinName% &7erhalten");
            this.configuration.addDefault("Messages.Other.ResetSkin", "%prefix%&7Der Skin von dem Spieler &a%playerName% &7wurde zurueckgesetzt");
            this.configuration.addDefault("Messages.ReloadConfig", "%prefix%&7Die Konfigurationsdatei wurde neu geladen");
            this.configuration.addDefault("Messages.FixSkin", "%prefix%&7Dein Skin wurde behoben");
            this.configuration.addDefault("Messages.ResetSkin", "%prefix%&7Dein Skin wurde zurueckgesetzt");
            this.configuration.addDefault("Messages.ResetName", "%prefix%&7Dein Name wurde zurueckgesetzt");
            this.configuration.addDefault("Messages.BungeeAutoNickEnabled", "%prefix%&7Der automatische Nickname wurde &aaktiviert");
            this.configuration.addDefault("Messages.BungeeAutoNickDisabled", "%prefix%&7Der automatische Nickname wurde &cdeaktiviert");
            this.configuration.addDefault("Messages.WorldChangeAutoNickEnabled", "%prefix%&7Der automatische Nickname wurde &aaktiviert");
            this.configuration.addDefault("Messages.WorldChangeAutoNickDisabled", "%prefix%&7Der automatische Nickname wurde &cdeaktiviert");
            this.configuration.addDefault("Messages.ActiveNick", "%prefix%&7Aktueller Nickname&8: &a%name%");
            this.configuration.addDefault("Messages.NickedPlayers.CurrentNickedPlayers", "%prefix%&7Hier ist eine Liste von allen genickten Spielern&8:");
            this.configuration.addDefault("Messages.NickedPlayers.PlayerInfo", "%prefix%&a%realName% &8➜ &d%nickName%");
            this.configuration.addDefault("Messages.NickedPlayers.NoPlayerIsNicked", "%prefix%&cDerzeit ist kein Spieler genickt");
            this.configuration.addDefault("Messages.TypeNameInChat", "%prefix%&7Bitte schreibe den Namen in den Chat&8:");
            this.configuration.addDefault("Messages.DisabledWorld", "%prefix%&cDu kannst dich in dieser Welt nicht nicken");
        } else {
            this.configuration.addDefault("NickActionBarMessage", "%prefix%&7You play as&8: &a%nickPrefix%%nickName%%nickSuffix%");
            this.configuration.addDefault("NickActionBarMessageOther", "&7You play as&8: &a%nickPrefix%%nickName%%nickSuffix%");

            this.configuration.addDefault("NickItem.ItemLore.Enabled", "&7Rightclick to &cdisable &7the automatic nickname");
            this.configuration.addDefault("NickItem.ItemLore.Disabled", "&7Rightclick to &aenable &7the automatic nickname");

            this.configuration.addDefault("NickItem.DisplayName.Enabled", "&aAutoNick &7(rightclick)");
            this.configuration.addDefault("NickItem.DisplayName.Disabled", "&cAutoNick &7(rightclick)");
            this.configuration.addDefault("NickItem.WorldChange.DisplayName.Enabled", "&aAutomatic Nickname &7(rightclick)");
            this.configuration.addDefault("NickItem.WorldChange.DisplayName.Disabled", "&cAutomatic Nickname &7(rightclick)");
            this.configuration.addDefault("NickItem.BungeeCord.DisplayName.Enabled", "&aAutomatic Nickname &7(rightclick)");
            this.configuration.addDefault("NickItem.BungeeCord.DisplayName.Disabled", "&cAutomatic Nickname &7(rightclick)");

            this.configuration.addDefault("Messages.Prefix", "&5&lNICK&r &8┃ ");
            this.configuration.addDefault("Messages.Nick", "%prefix%&7You play as&8: &a%name%");
            this.configuration.addDefault("Messages.Unnick", "%prefix%&7Your nickname was removed");
            this.configuration.addDefault("Messages.Name", "%prefix%&7Current nickname&8: &a%name%");
            this.configuration.addDefault("Messages.SkinChanged", "%prefix%&7You received a new skin&8: &a%skinName%");
            this.configuration.addDefault("Messages.NotNicked", "%prefix%&cYou are not nicked");
            this.configuration.addDefault("Messages.NickTooLong", "%prefix%&cThe nickname must not be longer than &a16 characters");
            this.configuration.addDefault("Messages.NickTooShort", "%prefix%&cThe nickname must not be shorter than &a3 characters");
            this.configuration.addDefault("Messages.NickContainsSpecialCharacters", "%prefix%&cThe nickname may only contain the characters &aa-z&c, &aA-Z&c, &a0-9 &cand &a_");
            this.configuration.addDefault("Messages.NickNameAlreadyInUse", "%prefix%&cThis nickname is currently already in use");
            this.configuration.addDefault("Messages.CanNotNickAsSelf", "%prefix%&cYou can not nick as yourself");
            this.configuration.addDefault("Messages.PlayerWithThisNameIsKnown", "%prefix%&cA player with this name is known to the server, which is why you cannot disguise as this player");
            this.configuration.addDefault("Messages.NoPerm", "&cI'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.");
            this.configuration.addDefault("Messages.NotPlayer", "&cOnly players can execute this command");
            this.configuration.addDefault("Messages.NickDelay", "%prefix%&cPlease wait a moment before performing this action again");
            this.configuration.addDefault("Messages.PlayerNotNicked", "%prefix%&cThe specified player is not nicked");
            this.configuration.addDefault("Messages.PlayerNotFound", "%prefix%&cThe player was not found");
            this.configuration.addDefault("Messages.NameNotAllowed", "%prefix%&cThis nickname is not allowed");
            this.configuration.addDefault("Messages.RealName", "%prefix%&7The specified player is&8: &a%realName%");
            this.configuration.addDefault("Messages.Other.RandomNick", "%prefix%&7You nicked the player &a%playerName%");
            this.configuration.addDefault("Messages.Other.SelectedNick", "%prefix%&7You set the name of the player &a%playerName% &7to &d%nickName%");
            this.configuration.addDefault("Messages.Other.Unnick", "%prefix%&7You unnicked the player &a%playerName%");
            this.configuration.addDefault("Messages.Other.DisabledWorld", "%prefix%&cThe player &a%playerName% &ccannot be nicked in this world");
            this.configuration.addDefault("Messages.Other.RandomSkin", "%prefix%&7The player &a%playerName% &7received a random skin");
            this.configuration.addDefault("Messages.Other.SelectedSkin", "%prefix%&7The player &a%playerName% &7received the skin of &d%skinName%");
            this.configuration.addDefault("Messages.Other.ResetSkin", "%prefix%&7The skin of the player &a%playerName% &7was reset");
            this.configuration.addDefault("Messages.ReloadConfig", "%prefix%&7The configuration file was reloaded");
            this.configuration.addDefault("Messages.FixSkin", "%prefix%&7Your skin was fixed");
            this.configuration.addDefault("Messages.ResetSkin", "%prefix%&7Your skin was reset");
            this.configuration.addDefault("Messages.ResetName", "%prefix%&7Your name was reset");
            this.configuration.addDefault("Messages.BungeeAutoNickEnabled", "%prefix%&7The automatic nickname was &aenabled");
            this.configuration.addDefault("Messages.BungeeAutoNickDisabled", "%prefix%&7The automatic nickname was &cdisabled");
            this.configuration.addDefault("Messages.WorldChangeAutoNickEnabled", "%prefix%&7The automatic nickname was &aenabled");
            this.configuration.addDefault("Messages.WorldChangeAutoNickDisabled", "%prefix%&7The automatic nickname was &cdisabled");
            this.configuration.addDefault("Messages.ActiveNick", "%prefix%&7Current nickname&8: &a%name%");
            this.configuration.addDefault("Messages.NickedPlayers.CurrentNickedPlayers", "%prefix%&7Here is a list of all nicked players&8:");
            this.configuration.addDefault("Messages.NickedPlayers.PlayerInfo", "%prefix%&a%realName% &8➜ &d%nickName%");
            this.configuration.addDefault("Messages.NickedPlayers.NoPlayerIsNicked", "%prefix%&cCurrently no player is nicked");
            this.configuration.addDefault("Messages.TypeNameInChat", "%prefix%&7Please write the name in the chat&8:");
            this.configuration.addDefault("Messages.DisabledWorld", "%prefix%&cYou can not nick yourself in this world");
        }
    }

    @Override
    public void reload() {
        super.reload();

        if(this.utils != null) {
            this.utils.setPrefix(this.getConfigString("Messages.Prefix"));
            this.utils.setNoPerm(this.getConfigString("Messages.NoPerm"));
            this.utils.setNotPlayer(this.getConfigString("Messages.NotPlayer"));
        }
    }

    public void sendMessage(CommandSender sender, String message) {
        if((message != null) && !message.trim().isEmpty())
            sender.sendMessage(message);
    }

}
