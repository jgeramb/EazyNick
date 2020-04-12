package net.dev.eazynick.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.api.PlayerNickEvent;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.LanguageFileUtils;
import net.dev.eazynick.utils.Utils;

public class PlayerNickListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerNick(PlayerNickEvent e) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
		
		if(!(e.isCancelled())) {
			Player p = e.getPlayer();
			NickManager api = new NickManager(p);
			
			api.updateLuckPerms(e.getTagPrefix(), e.getTagSuffix());
			api.nickPlayer(e.getNickName(), e.getSkinName());
			api.updatePrefixSuffix(e.getTagPrefix(), e.getTagSuffix(), e.getChatPrefix(), e.getChatSuffix(), e.getTabPrefix(), e.getTabSuffix(), e.getGroupName());
			
			utils.getCanUseNick().put(p.getUniqueId(), false);
			
			Bukkit.getScheduler().runTaskLater(eazyNick, new Runnable() {
				
				@Override
				public void run() {
					utils.getCanUseNick().put(p.getUniqueId(), true);
				}
			}, fileUtils.cfg.getLong("Settings.NickDelay") * 20);
			
			if(fileUtils.cfg.getBoolean("BungeeCord")) {
				String oldRank = e.getGroupName();
				
				if(!(e.isJoinNick())) {
					if(utils.ultraPermissionsStatus()) {
						if(utils.getOldUltraPermissionsGroups().containsKey(p.getUniqueId()))
							oldRank = utils.getOldUltraPermissionsGroups().get(p.getUniqueId()).toString();
					}
					
					if(utils.luckPermsStatus()) {
						if(utils.getOldLuckPermsGroups().containsKey(p.getUniqueId()))
							oldRank = utils.getOldLuckPermsGroups().get(p.getUniqueId());
					}
					
					if(utils.permissionsExStatus()) {
						if(utils.getOldPermissionsExGroups().containsKey(p.getUniqueId()))
							oldRank = utils.getOldPermissionsExGroups().get(p.getUniqueId()).toString();
					}
				}
				
				if(!(oldRank.equals("NONE")))
					eazyNick.getMySQLPlayerDataManager().insertData(p.getUniqueId(), oldRank, e.getChatPrefix(), e.getChatSuffix(), e.getTabPrefix(), e.getTabSuffix(), e.getTagPrefix(), e.getTagSuffix());
			}
			
			if(!(e.isRenick()))
				p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages." + (e.isJoinNick() ? "ActiveNick" : "Nick")).replace("%name%", e.getNickName()));
		}
	}

}
