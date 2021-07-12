package net.dev.eazynick.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.*;
import net.dev.eazynick.utilities.*;
import net.dev.eazynick.utilities.AsyncTask.AsyncRunnable;
import net.dev.eazynick.utilities.configuration.yaml.SetupYamlFile;

public class PlayerChangedWorldListener implements Listener {

	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		
		Player player = event.getPlayer();
		NickManager api = new NickManager(player);
		
		if (!(utils.getWorldBlackList().contains(player.getWorld().getName().toUpperCase()))) {
			if (setupYamlFile.getConfiguration().getBoolean("NickOnWorldChange") && utils.getNickOnWorldChangePlayers().contains(player.getUniqueId()) && !(api.isNicked()) && player.hasPermission("nick.use"))
				utils.performReNick(player);
			else if(utils.getNickedPlayers().containsKey(player.getUniqueId()) && setupYamlFile.getConfiguration().getBoolean("KeepNickOnWorldChange") && !(setupYamlFile.getConfiguration().getStringList("DisabledNickWorlds").contains(player.getWorld().getName()))) {
				NickedPlayerData nickedPlayerData = utils.getNickedPlayers().get(player.getUniqueId()).clone();
				
				utils.getSoonNickedPlayers().put(player.getUniqueId(), NickReason.WORLDCHANGE);
				
				new AsyncTask(new AsyncRunnable() {
					
					@Override
					public void run() {
						if(player.isOnline()) {
							api.unnickPlayerWithoutRemovingMySQL(false, true);
							
							new AsyncTask(new AsyncRunnable() {
								
								@Override
								public void run() {
									if(player.isOnline())
										Bukkit.getPluginManager().callEvent(new PlayerNickEvent(player, nickedPlayerData.getNickName(), nickedPlayerData.getSkinName(), nickedPlayerData.getChatPrefix(), nickedPlayerData.getChatSuffix(), nickedPlayerData.getTabPrefix(), nickedPlayerData.getTabSuffix(), nickedPlayerData.getTagPrefix(), nickedPlayerData.getTagSuffix(), false, true, nickedPlayerData.getSortID(), nickedPlayerData.getGroupName()));
								}
							}, 50L * (21 + (setupYamlFile.getConfiguration().getBoolean("RandomDisguiseDelay") ? (20 * 2) : 0)));
						}
					}
				}, 1000L);
			}
			
			if(!(player.hasPermission("nick.bypass") && setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission")) && setupYamlFile.getConfiguration().getBoolean("ReNickAllOnNewPlayerJoinWorld")) {
				for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
					if(currentPlayer != player) {
						NickManager apiCurrentPlayer = new NickManager(currentPlayer);
						
						if (apiCurrentPlayer.isNicked() && currentPlayer.getWorld().getName().equals(player.getWorld().getName())) {
							NickedPlayerData nickedPlayerData = utils.getNickedPlayers().get(currentPlayer.getUniqueId()).clone();
							
							new AsyncTask(new AsyncRunnable() {
								
								@Override
								public void run() {
									if(player.isOnline()) {
										apiCurrentPlayer.unnickPlayerWithoutRemovingMySQL(false, true);
										
										new AsyncTask(new AsyncRunnable() {
											
											@Override
											public void run() {
												if(currentPlayer.isOnline())
													Bukkit.getPluginManager().callEvent(new PlayerNickEvent(currentPlayer, nickedPlayerData.getNickName(), nickedPlayerData.getSkinName(), nickedPlayerData.getChatPrefix(), nickedPlayerData.getChatSuffix(), nickedPlayerData.getTabPrefix(), nickedPlayerData.getTabSuffix(), nickedPlayerData.getTagPrefix(), nickedPlayerData.getTagSuffix(), false, true, nickedPlayerData.getSortID(), nickedPlayerData.getGroupName()));
											}
										}, 50L * (21 + (setupYamlFile.getConfiguration().getBoolean("RandomDisguiseDelay") ? (20 * 2) : 0)));
									}
								}
							}, 1000L);
						}
					}
				}
			}
		} else if(api.isNicked())
			api.unnickPlayer();
	}

}
