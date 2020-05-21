package net.dev.eazynick.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.api.PlayerNickEvent;
import net.dev.eazynick.hooks.LuckPermsHook;
import net.dev.eazynick.hooks.TABHook;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.LanguageFileUtils;
import net.dev.eazynick.utils.Utils;

import me.clip.placeholderapi.PlaceholderAPI;

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
			boolean changePrefixAndSuffix = fileUtils.getConfig().getBoolean("WorldsWithDisabledPrefixAndSuffix") || !(utils.getWorldsWithDisabledPrefixAndSuffix().contains(p.getWorld().getName().toUpperCase()));
			String nickName = e.getNickName(), tagPrefix = e.getTagPrefix(), tagSuffix = e.getTagSuffix(), chatPrefix = e.getChatPrefix(), chatSuffix = e.getChatSuffix(), tabPrefix = e.getTabPrefix(), tabSuffix = e.getTabSuffix();
			
			utils.getCanUseNick().put(p.getUniqueId(), false);
			
			Bukkit.getScheduler().runTaskLater(eazyNick, new Runnable() {
				
				@Override
				public void run() {
					utils.getCanUseNick().put(p.getUniqueId(), true);
				}
			}, fileUtils.getConfig().getLong("Settings.NickDelay") * 20);
			
			if(fileUtils.getConfig().getBoolean("BungeeCord")) {
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
					eazyNick.getMySQLPlayerDataManager().insertData(p.getUniqueId(), oldRank, chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix);
			}
			
			if(utils.placeholderAPIStatus()) {
				tagPrefix = PlaceholderAPI.setPlaceholders(p, tagPrefix);
				tagSuffix = PlaceholderAPI.setPlaceholders(p, tagSuffix);
				chatPrefix = PlaceholderAPI.setPlaceholders(p, chatPrefix);
				chatSuffix = PlaceholderAPI.setPlaceholders(p, chatSuffix);
				tabPrefix = PlaceholderAPI.setPlaceholders(p, tabPrefix);
				tabSuffix = PlaceholderAPI.setPlaceholders(p, tabSuffix);
			}
			
			if(changePrefixAndSuffix && utils.luckPermsStatus())
				new LuckPermsHook(p).updateNodes(tagPrefix, tagSuffix, e.getGroupName());
			
			if(changePrefixAndSuffix && utils.tabStatus())
				new TABHook(p).update(nickName, tabPrefix, tabSuffix, tagPrefix, tagSuffix);
			
			api.nickPlayer(nickName, e.getSkinName());
			
			if(changePrefixAndSuffix)
				api.updatePrefixSuffix(e.getTagPrefix(), e.getTagSuffix(), chatPrefix, chatSuffix, e.getTabPrefix(), e.getTabSuffix(), e.getGroupName());
			
			if(!(e.isRenick()))
				p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages." + (e.isJoinNick() ? "ActiveNick" : "Nick")).replace("%name%", nickName));
		}
	}

}
