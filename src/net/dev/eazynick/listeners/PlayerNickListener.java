package net.dev.eazynick.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.*;
import net.dev.eazynick.hooks.LuckPermsHook;
import net.dev.eazynick.utilities.AsyncTask;
import net.dev.eazynick.utilities.AsyncTask.AsyncRunnable;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.*;

import me.clip.placeholderapi.PlaceholderAPI;

public class PlayerNickListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerNick(PlayerNickEvent event) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		SavedNickDatasYamlFile savedNickDatasYamlFile = eazyNick.getSavedNickDatasYamlFile();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		
		if(!(event.isCancelled())) {
			Player player = event.getPlayer();
			NickManager api = new NickManager(player);
			boolean changePrefixAndSuffix = !(utils.getWorldsWithDisabledPrefixAndSuffix().contains(player.getWorld().getName().toUpperCase()));
			String realName = player.getName(), nickName = event.getNickName(), skinName = event.getSkinName(), tagPrefix = event.getTagPrefix(), tagSuffix = event.getTagSuffix(), chatPrefix = event.getChatPrefix(), chatSuffix = event.getChatSuffix(), tabPrefix = event.getTabPrefix(), tabSuffix = event.getTabSuffix(), groupName = event.getGroupName(), oldDisplayName = player.getDisplayName(), oldPlayerListName = player.getPlayerListName(), uniqueIdString = player.getUniqueId().toString().replace("-", "");
			UUID spoofedUniqueId = event.getSpoofedUniqueId();
			int sortID = event.getSortID();
			
			utils.getCanUseNick().put(player.getUniqueId(), false);
			
			new AsyncTask(new AsyncRunnable() {
				
				@Override
				public void run() {
					utils.getCanUseNick().put(player.getUniqueId(), true);
				}
			}, setupYamlFile.getConfiguration().getLong("Settings.NickDelay") * 1000L).run();
			
			if(setupYamlFile.getConfiguration().getBoolean("BungeeCord")) {
				if(!(groupName.equals("NONE")))
					eazyNick.getMySQLPlayerDataManager().insertData(player.getUniqueId(), groupName, chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix);
			} else if(setupYamlFile.getConfiguration().getBoolean("SaveLocalNickDatas") && !(setupYamlFile.getConfiguration().getBoolean("DisconnectUnnick"))) {
				savedNickDatasYamlFile.getConfiguration().set(uniqueIdString + ".nick_name", nickName);
				savedNickDatasYamlFile.getConfiguration().set(uniqueIdString + ".skin_name", skinName);
				savedNickDatasYamlFile.getConfiguration().set(uniqueIdString + ".spoofed_uniqueid", spoofedUniqueId.toString());
				savedNickDatasYamlFile.getConfiguration().set(uniqueIdString + ".chat_prefix", event.getChatPrefix());
				savedNickDatasYamlFile.getConfiguration().set(uniqueIdString + ".chat_suffix", event.getChatSuffix());
				savedNickDatasYamlFile.getConfiguration().set(uniqueIdString + ".tablist_prefix", event.getTabPrefix());
				savedNickDatasYamlFile.getConfiguration().set(uniqueIdString + ".tablist_suffix", event.getTabSuffix());
				savedNickDatasYamlFile.getConfiguration().set(uniqueIdString + ".nametag_prefix", event.getTagPrefix());
				savedNickDatasYamlFile.getConfiguration().set(uniqueIdString + ".nametag_suffix", event.getTagSuffix());
				savedNickDatasYamlFile.getConfiguration().set(uniqueIdString + ".group_name", groupName);
				savedNickDatasYamlFile.getConfiguration().set(uniqueIdString + ".sort_id", sortID);
			}
			
			if(utils.isPluginInstalled("PlaceholderAPI")) {
				chatPrefix = PlaceholderAPI.setPlaceholders(player, chatPrefix);
				chatSuffix = PlaceholderAPI.setPlaceholders(player, chatSuffix);
			}
			
			if(changePrefixAndSuffix && utils.isPluginInstalled("LuckPerms"))
				new LuckPermsHook(player).updateNodes(chatPrefix, chatSuffix, groupName);
			
			if(setupYamlFile.getConfiguration().getBoolean("LogNicknames"))
				utils.sendConsole("§a" + realName + " §8(" + player.getUniqueId().toString() + ") §7set his nickname to §d" + nickName);
			
			api.nickPlayer(nickName, skinName);
			
			if(changePrefixAndSuffix)
				api.updatePrefixSuffix(nickName, realName, event.getTagPrefix(), event.getTagSuffix(), event.getChatPrefix(), event.getChatSuffix(), event.getTabPrefix(), event.getTabSuffix(), sortID, groupName);
			
			utils.getNickedPlayers().put(player.getUniqueId(), new NickedPlayerData(player.getUniqueId(), spoofedUniqueId, oldDisplayName, oldPlayerListName, realName, nickName, skinName, event.getChatPrefix(), event.getChatSuffix(), event.getTabPrefix(), event.getTabSuffix(), event.getTagPrefix(), event.getTagSuffix(), groupName, sortID));
			
			if(setupYamlFile.getConfiguration().getBoolean("NickMessage.OnNnick")) {
				Bukkit.getOnlinePlayers().forEach(currentPlayer -> {
					languageYamlFile.sendMessage(currentPlayer, setupYamlFile.getConfigString(player, "NickMessage.Nick.Quit").replace("%displayName%", oldDisplayName).replace("%displayname%", oldDisplayName).replace("%name%", api.getRealName()));
					languageYamlFile.sendMessage(currentPlayer, setupYamlFile.getConfigString(player, "NickMessage.Nick.Join").replace("%displayName%", player.getDisplayName()).replace("%displayname%", player.getDisplayName()).replace("%name%", nickName));
				});
			}
			
			if(!(event.isRenick()))
				languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages." + (event.isJoinNick() ? "ActiveNick" : "Nick")).replace("%name%", nickName).replace("%prefix%", utils.getPrefix()));
			
			if(setupYamlFile.getConfiguration().getBoolean("NickCommands.OnNick")) {
				if(utils.isPluginInstalled("PlaceholderAPI"))
					setupYamlFile.getConfiguration().getStringList("NickCommands.Nick").forEach(command -> Bukkit.dispatchCommand(setupYamlFile.getConfiguration().getBoolean("NickCommands.SendAsConsole") ? Bukkit.getConsoleSender() : player, PlaceholderAPI.setPlaceholders(player, command.replace("%player%", player.getName()).replace("%nickName%", nickName))));
				else
					setupYamlFile.getConfiguration().getStringList("NickCommands.Nick").forEach(command -> Bukkit.dispatchCommand(setupYamlFile.getConfiguration().getBoolean("NickCommands.SendAsConsole") ? Bukkit.getConsoleSender() : player, command.replace("%player%", player.getName()).replace("%nickName%", nickName)));
			}
		}
	}

}
