package net.dev.eazynick.listeners;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerLoginEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utilities.NickReason;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.SetupYamlFile;

public class PlayerLoginListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		
		Player player = event.getPlayer();
		UUID uniqueId = player.getUniqueId();

		if ((setupYamlFile.getConfiguration().getBoolean("BungeeCord") && (!(setupYamlFile.getConfiguration().getBoolean("LobbyMode")) || (player.hasPermission("nick.bypasslobbymode") && setupYamlFile.getConfiguration().getBoolean("EnableBypassLobbyModePermission"))) && eazyNick.getMySQLNickManager().isPlayerNicked(uniqueId)) || utils.getLastNickDatas().containsKey(uniqueId) || setupYamlFile.getConfiguration().getBoolean("JoinNick") || (setupYamlFile.getConfiguration().getBoolean("SaveLocalNickDatas") && eazyNick.getSavedNickDatasYamlFile().getConfiguration().contains(player.getUniqueId().toString().replace("-", ""))))
			utils.getSoonNickedPlayers().put(uniqueId, NickReason.JOIN);
	}

}
