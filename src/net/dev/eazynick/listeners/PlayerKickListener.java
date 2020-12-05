package net.dev.eazynick.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.sql.MySQLNickManager;
import net.dev.eazynick.sql.MySQLPlayerDataManager;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.Utils;

public class PlayerKickListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerKick(PlayerKickEvent e) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
		MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMySQLPlayerDataManager();
		
		Player p = e.getPlayer();
		NickManager api = new NickManager(p);

		if(utils.getNameCache().containsKey(p.getUniqueId()))
			utils.getNameCache().remove(p.getUniqueId());
		
		if (api.isNicked()) {
			if (eazyNick.getFileUtils().getConfig().getBoolean("DisconnectUnnick"))
				api.unnickPlayerWithoutRemovingMySQL(true);
			
			if(fileUtils.getConfig().getBoolean("OverwriteJoinQuitMessages")) {
				String message = fileUtils.getConfigString(p, "OverwrittenMessages.Quit");
				
				if(fileUtils.getConfig().getBoolean("BungeeCord") && mysqlNickManager.isPlayerNicked(p.getUniqueId()))
					message = message.replace("%name%", mysqlNickManager.getNickName(p.getUniqueId())).replace("%displayName%", mysqlPlayerDataManager.getChatPrefix(p.getUniqueId()) + mysqlNickManager.getNickName(p.getUniqueId()) + mysqlPlayerDataManager.getChatSuffix(p.getUniqueId()));
				else if(utils.getPlayerNicknames().containsKey(p.getUniqueId()))
					message = message.replace("%name%", utils.getPlayerNicknames().get(p.getUniqueId()).replace("%displayName%", utils.getChatPrefixes().get(p.getUniqueId()) + utils.getPlayerNicknames().get(p.getUniqueId()) + utils.getChatSuffixes().get(p.getUniqueId())));
				else
					message = message.replace("%name%", p.getName()).replace("%displayName%", p.getDisplayName());
				
				e.setLeaveMessage(message);
			}
		}
	}

}
