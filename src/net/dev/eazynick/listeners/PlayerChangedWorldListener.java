package net.dev.eazynick.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.Utils;

public class PlayerChangedWorldListener implements Listener {

	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		
		Player p = e.getPlayer();
		NickManager api = new NickManager(p);
		
		if (!(utils.getWorldBlackList().contains(p.getWorld().getName().toUpperCase()))) {
			if (fileUtils.getConfig().getBoolean("NickOnWorldChange") && utils.getNickOnWorldChangePlayers().contains(p.getUniqueId()) && !(api.isNicked()))
				utils.performReNick(p);
			else {
				Bukkit.getScheduler().runTaskLater(eazyNick, new Runnable() {

					@Override
					public void run() {
						api.unnickPlayer();
						
						Bukkit.getScheduler().runTaskLater(eazyNick, new Runnable() {

							@Override
							public void run() {
								utils.performReNick(p);
							}
						}, 10 + (fileUtils.getConfig().getBoolean("RandomDisguiseDelay") ? (20 * 2) : 0));
					}
				}, 10);
			}
		} else if(api.isNicked())
			api.unnickPlayer();
	}

}
