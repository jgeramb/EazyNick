package net.dev.eazynick.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerRespawnEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.*;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.SetupYamlFile;

public class PlayerRespawnListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		
		Player player = event.getPlayer();
		NickManager api = new NickManager(player);
		
		if(utils.getNickedPlayers().containsKey(player.getUniqueId())) {
			NickedPlayerData nickedPlayerData = utils.getNickedPlayers().get(player.getUniqueId()).clone();
			
			Bukkit.getScheduler().runTaskLater(eazyNick, () -> {
				api.unnickPlayerWithoutRemovingMySQL(false, true);
				
				Bukkit.getScheduler().runTaskLater(eazyNick, () -> {
					if(player.isOnline())
						Bukkit.getPluginManager().callEvent(new PlayerNickEvent(player, nickedPlayerData.getNickName(), nickedPlayerData.getSkinName(), nickedPlayerData.getChatPrefix(), nickedPlayerData.getChatSuffix(), nickedPlayerData.getTabPrefix(), nickedPlayerData.getTabSuffix(), nickedPlayerData.getTagPrefix(), nickedPlayerData.getTagSuffix(), false, true, nickedPlayerData.getSortID(), nickedPlayerData.getGroupName()));
				}, 21 + (setupYamlFile.getConfiguration().getBoolean("RandomDisguiseDelay") ? (20 * 2) : 0));
			}, 1);
		}
	}

}
