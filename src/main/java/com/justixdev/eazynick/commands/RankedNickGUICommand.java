package com.justixdev.eazynick.commands;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.api.PlayerUnnickEvent;
import com.justixdev.eazynick.sql.MySQLNickManager;
import com.justixdev.eazynick.sql.MySQLPlayerDataManager;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RankedNickGUICommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
		MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMySQLPlayerDataManager();

		if(!(sender instanceof Player)) {
			utils.sendConsole(utils.getNotPlayer());
			return true;
		}

		Player player = (Player) sender;

		if(new NickManager(player).isNicked()) {
			if(player.hasPermission("eazynick.nick.reset"))
				Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(player));
		} else if((mysqlNickManager != null)
				&& mysqlNickManager.isPlayerNicked(player.getUniqueId())
				&& setupYamlFile.getConfiguration().getBoolean("LobbyMode")
				&& setupYamlFile.getConfiguration().getBoolean("RemoveMySQLNickOnUnnickWhenLobbyModeEnabled")) {
			if(player.hasPermission("eazynick.nick.reset")) {
				mysqlNickManager.removePlayer(player.getUniqueId());
				mysqlPlayerDataManager.removeData(player.getUniqueId());

				languageYamlFile.sendMessage(
						player,
						languageYamlFile.getConfigString(player, "Messages.Unnick")
								.replace("%prefix%", utils.getPrefix())
				);
			}
		} else if(player.hasPermission("eazynick.gui.book"))
			eazyNick.getGUIManager().openRankedNickGUI(player, "");
		else
			languageYamlFile.sendMessage(player, utils.getNoPerm());
		
		return true;
	}

}
