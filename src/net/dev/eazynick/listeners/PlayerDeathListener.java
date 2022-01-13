package net.dev.eazynick.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.utilities.configuration.yaml.SetupYamlFile;

public class PlayerDeathListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		EazyNick eazyNick = EazyNick.getInstance();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		
		Player player = event.getEntity();
		String deathMessage = ((event.getDeathMessage() == null) || event.getDeathMessage().isEmpty()) ? null : event.getDeathMessage();
		NickManager api = new NickManager(player);
			
		if(api.isNicked() && (deathMessage != null) && !(setupYamlFile.getConfiguration().getBoolean("SeeNickSelf"))) {
			event.setDeathMessage(null);

			for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
				if (currentPlayer != player)
					currentPlayer.sendMessage(deathMessage);
				else
					currentPlayer.sendMessage(deathMessage.replace(api.getNickFormat(), api.getOldDisplayName()));
			}
		}
	}

}
