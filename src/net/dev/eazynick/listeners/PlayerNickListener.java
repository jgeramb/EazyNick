package net.dev.eazynick.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.*;
import net.dev.eazynick.hooks.LuckPermsHook;
import net.dev.eazynick.hooks.TABHook;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import net.dev.eazynick.utilities.configuration.yaml.SetupYamlFile;

import me.clip.placeholderapi.PlaceholderAPI;

public class PlayerNickListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerNick(PlayerNickEvent event) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		
		if(!(event.isCancelled())) {
			Player player = event.getPlayer();
			NickManager api = new NickManager(player);
			boolean changePrefixAndSuffix = !(utils.getWorldsWithDisabledPrefixAndSuffix().contains(player.getWorld().getName().toUpperCase()));
			String realName = player.getName(), nickName = event.getNickName(), skinName = event.getSkinName(), tagPrefix = event.getTagPrefix(), tagSuffix = event.getTagSuffix(), chatPrefix = event.getChatPrefix(), chatSuffix = event.getChatSuffix(), tabPrefix = event.getTabPrefix(), tabSuffix = event.getTabSuffix(), groupName = event.getGroupName(), oldDisplayName = player.getDisplayName(), oldPlayerListName = player.getPlayerListName();
			int sortID = event.getSortID();
			
			utils.getCanUseNick().put(player.getUniqueId(), false);
			
			Bukkit.getScheduler().runTaskLater(eazyNick, () -> utils.getCanUseNick().put(player.getUniqueId(), true), setupYamlFile.getConfiguration().getLong("Settings.NickDelay") * 20);
			
			if(setupYamlFile.getConfiguration().getBoolean("BungeeCord")) {
				if(!(groupName.equals("NONE")))
					eazyNick.getMySQLPlayerDataManager().insertData(player.getUniqueId(), groupName, chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix);
			}
			
			if(utils.placeholderAPIStatus()) {
				tagPrefix = PlaceholderAPI.setPlaceholders(player, tagPrefix);
				tagSuffix = PlaceholderAPI.setPlaceholders(player, tagSuffix);
				chatPrefix = PlaceholderAPI.setPlaceholders(player, chatPrefix);
				chatSuffix = PlaceholderAPI.setPlaceholders(player, chatSuffix);
				tabPrefix = PlaceholderAPI.setPlaceholders(player, tabPrefix);
				tabSuffix = PlaceholderAPI.setPlaceholders(player, tabSuffix);
			}
			
			if(changePrefixAndSuffix && utils.luckPermsStatus())
				new LuckPermsHook(player).updateNodes(chatPrefix, chatSuffix, groupName);
			
			final String finalTabPrefix = tabPrefix, finalTabSuffix = tabSuffix, finalTagPrefix = tagPrefix, finalTagSuffix = tagSuffix;
			
			if(changePrefixAndSuffix && utils.tabStatus() && setupYamlFile.getConfiguration().getBoolean("ChangeNameAndPrefixAndSuffixInTAB"))
				Bukkit.getScheduler().runTaskLater(eazyNick, () -> new TABHook(player).update(nickName, finalTabPrefix, finalTabSuffix, finalTagPrefix, finalTagSuffix, sortID), 8 + (setupYamlFile.getConfiguration().getBoolean("RandomDisguiseDelay") ? (20 * new Random().nextInt(3)) : 0));

			if(setupYamlFile.getConfiguration().getBoolean("LogNicknames"))
				eazyNick.getUtils().sendConsole("§a" + player.getName() + " §7(" + player.getUniqueId().toString() + ") §4set his nickname to §6" + nickName);

			if(changePrefixAndSuffix)
				api.updatePrefixSuffix(nickName, realName, event.getTagPrefix(), event.getTagSuffix(), event.getChatPrefix(), event.getChatSuffix(), event.getTabPrefix(), event.getTabSuffix(), sortID, groupName);
			
			api.nickPlayer(nickName, skinName);

			utils.getNickedPlayers().put(player.getUniqueId(), new NickedPlayerData(player.getUniqueId(), eazyNick.getVersion().startsWith("1_7") ? eazyNick.getUUIDFetcher_1_7().getUUID(nickName) : (eazyNick.getVersion().equals("1_8_R1") ? eazyNick.getUUIDFetcher_1_8_R1().getUUID(nickName) : eazyNick.getUUIDFetcher().getUUID(nickName)), oldDisplayName, oldPlayerListName, realName, nickName, skinName, chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix, groupName, sortID));
			
			if(setupYamlFile.getConfiguration().getBoolean("NickMessage.OnNnick")) {
				for(Player currentPlayer : Bukkit.getOnlinePlayers())
					languageYamlFile.sendMessage(currentPlayer, setupYamlFile.getConfigString(player, "NickMessage.Nick.Quit").replace("%displayName%", oldDisplayName).replace("%displayname%", oldDisplayName).replace("%name%", api.getRealName()));
				
				for(Player currentPlayer : Bukkit.getOnlinePlayers())
					languageYamlFile.sendMessage(currentPlayer, setupYamlFile.getConfigString(player, "NickMessage.Nick.Join").replace("%displayName%", player.getDisplayName()).replace("%displayname%", player.getDisplayName()).replace("%name%", nickName));
			}
			
			if(!(event.isRenick()))
				languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages." + (event.isJoinNick() ? "ActiveNick" : "Nick")).replace("%name%", nickName).replace("%prefix%", utils.getPrefix()));
			
			if(setupYamlFile.getConfiguration().getBoolean("NickCommands.OnNick")) {
				if(utils.placeholderAPIStatus())
					setupYamlFile.getConfiguration().getStringList("NickCommands.Nick").forEach(cmd -> Bukkit.dispatchCommand(setupYamlFile.getConfiguration().getBoolean("NickCommands.SendAsConsole") ? Bukkit.getConsoleSender() : player, PlaceholderAPI.setPlaceholders(player, cmd)));
				else
					setupYamlFile.getConfiguration().getStringList("NickCommands.Nick").forEach(cmd -> Bukkit.dispatchCommand(setupYamlFile.getConfiguration().getBoolean("NickCommands.SendAsConsole") ? Bukkit.getConsoleSender() : player, cmd));
			}
		}
	}

}
