package net.dev.eazynick.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.utilities.Utils;
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
			if (setupYamlFile.getConfiguration().getBoolean("NickOnWorldChange") && utils.getNickOnWorldChangePlayers().contains(player.getUniqueId()) && !(api.isNicked()))
				utils.performReNick(player);
			else if(!(setupYamlFile.getConfiguration().getBoolean("KeepNickOnWorldChange")))
				api.unnickPlayerWithoutRemovingMySQL(false, true);
		} else if(api.isNicked())
			api.unnickPlayerWithoutRemovingMySQL(false, true);
	}

}
