package net.dev.eazynick.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utils.LanguageFileUtils;

public class PlayerDropItemListener implements Listener {

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		EazyNick eazyNick = EazyNick.getInstance();
		LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
		
		Player p = e.getPlayer();
		
		if ((e.getItemDrop() != null) && (e.getItemDrop().getItemStack().getType() != Material.AIR) && (e.getItemDrop().getItemStack().getItemMeta() != null) && (e.getItemDrop().getItemStack().getItemMeta().getDisplayName() != null)) {
			if (e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString(p, "NickItem.DisplayName.Enabled")) || e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString(p, "NickItem.DisplayName.Disabled")) || e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString(p, "NickItem.WorldChange.DisplayName.Enabled")) || e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString(p, "NickItem.WorldChange.DisplayName.Disabled")) || e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString(p, "NickItem.BungeeCord.DisplayName.Enabled")) || e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString(p, "NickItem.BungeeCord.DisplayName.Disabled"))) {
				if (!(eazyNick.getFileUtils().getConfig().getBoolean("NickItem.InventorySettings.PlayersCanDropItem")))
					e.setCancelled(true);
			}
		}
	}

}
