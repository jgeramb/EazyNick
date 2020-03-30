package net.dev.eazynick.listeners;

import org.bukkit.Material;
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
		
		if ((e.getItemDrop() != null) && (e.getItemDrop().getItemStack().getType() != Material.AIR) && (e.getItemDrop().getItemStack().getItemMeta() != null) && (e.getItemDrop().getItemStack().getItemMeta().getDisplayName() != null)) {
			if (e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.DisplayName.Enabled")) || e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.DisplayName.Disabled")) || e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.WorldChange.DisplayName.Enabled")) || e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.WorldChange.DisplayName.Disabled")) || e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.BungeeCord.DisplayName.Enabled")) || e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.BungeeCord.DisplayName.Disabled"))) {
				if (eazyNick.getFileUtils().cfg.getBoolean("NickItem.InventorySettings.PlayersCanDropItem") == false)
					e.setCancelled(true);
			}
		}
	}

}
