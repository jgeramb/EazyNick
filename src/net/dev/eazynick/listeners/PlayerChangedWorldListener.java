package net.dev.eazynick.listeners;

import java.util.Timer;
import java.util.TimerTask;

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
			if (fileUtils.getConfig().getBoolean("NickOnWorldChange") && utils.getNickOnWorldChangePlayers().contains(p.getUniqueId()) && !(api.isNicked()) && p.hasPermission("nick.use"))
				utils.performReNick(p);
			else if(api.isNicked()) {
				new Timer().schedule(new TimerTask() {
					
					@Override
					public void run() {
						String name = api.getNickName();
						
						api.unnickPlayer();
						
						if(fileUtils.getConfig().getBoolean("KeepNickOnWorldChange")) {
							new Timer().schedule(new TimerTask() {
								
								@Override
								public void run() {
									utils.performReNick(p, name);
								}
							}, 500 + (fileUtils.getConfig().getBoolean("RandomDisguiseDelay") ? (1000 * 2) : 0));
						}
					}
				}, 500);
			}
		} else if(api.isNicked())
			api.unnickPlayer();
	}

}
