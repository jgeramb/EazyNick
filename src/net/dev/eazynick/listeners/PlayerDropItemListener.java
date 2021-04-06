package net.dev.eazynick.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utilities.configuration.yaml.LanguageYamlFile;

public class PlayerDropItemListener implements Listener {

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		EazyNick eazyNick = EazyNick.getInstance();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		
		Player player = event.getPlayer();
		
		if ((event.getItemDrop() != null) && (event.getItemDrop().getItemStack().getType() != Material.AIR) && (event.getItemDrop().getItemStack().getItemMeta() != null) && (event.getItemDrop().getItemStack().getItemMeta().getDisplayName() != null)) {
			if (event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(languageYamlFile.getConfigString(player, "NickItem.DisplayName.Enabled")) || event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(languageYamlFile.getConfigString(player, "NickItem.DisplayName.Disabled")) || event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(languageYamlFile.getConfigString(player, "NickItem.WorldChange.DisplayName.Enabled")) || event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(languageYamlFile.getConfigString(player, "NickItem.WorldChange.DisplayName.Disabled")) || event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Enabled")) || event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Disabled"))) {
				if (!(eazyNick.getSetupYamlFile().getConfiguration().getBoolean("NickItem.InventorySettings.PlayersCanDropItem")))
					event.setCancelled(true);
			}
		}
	}

}
