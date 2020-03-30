package net.dev.eazynick.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.LanguageFileUtils;
import net.dev.eazynick.utils.Utils;

public class PlayerInteractListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent e) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
		
		Player p = e.getPlayer();

		if ((e.getItem() != null) && (e.getItem().getType() != Material.AIR && (e.getItem().getItemMeta() != null) && (e.getItem().getItemMeta().getDisplayName() != null))) {
			if (fileUtils.cfg.getBoolean("NickItem.getOnJoin")) {
				if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					if (e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.DisplayName.Disabled"))) {
						e.setCancelled(true);
						p.chat("/nick");

						p.getInventory().setItem(p.getInventory().getHeldItemSlot(), utils.createItem(Material.getMaterial(fileUtils.cfg.getString("NickItem.ItemType.Enabled")), fileUtils.cfg.getInt("NickItem.ItemAmount.Enabled"), fileUtils.cfg.getInt("NickItem.MetaData.Enabled"), languageFileUtils.getConfigString("NickItem.DisplayName.Enabled"), languageFileUtils.getConfigString("NickItem.ItemLore.Enabled").replace("&n", "\n"), fileUtils.cfg.getBoolean("NickItem.Enchanted.Enabled")));
					} else if (e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.DisplayName.Enabled"))) {
						e.setCancelled(true);
						p.chat("/unnick");

						p.getInventory().setItem(p.getInventory().getHeldItemSlot(), utils.createItem(Material.getMaterial(fileUtils.cfg.getString("NickItem.ItemType.Disabled")), fileUtils.cfg.getInt("NickItem.ItemAmount.Disabled"), fileUtils.cfg.getInt("NickItem.MetaData.Disabled"), languageFileUtils.getConfigString("NickItem.DisplayName.Disabled"), languageFileUtils.getConfigString("NickItem.ItemLore.Disabled").replace("&n", "\n"), fileUtils.cfg.getBoolean("NickItem.Enchanted.Disabled")));
					} else {
						if (fileUtils.cfg.getBoolean("NickOnWorldChange")) {
							if (e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.WorldChange.DisplayName.Disabled"))) {
								e.setCancelled(true);

								utils.getNickOnWorldChangePlayers().add(p.getUniqueId());
								p.getInventory().setItem(p.getInventory().getHeldItemSlot(), utils.createItem(Material.getMaterial(fileUtils.cfg.getString("NickItem.ItemType.Enabled")), fileUtils.cfg.getInt("NickItem.ItemAmount.Enabled"), fileUtils.cfg.getInt("NickItem.MetaData.Enabled"), languageFileUtils.getConfigString("NickItem.WorldChange.DisplayName.Enabled"), languageFileUtils.getConfigString("NickItem.ItemLore.Enabled").replace("&n", "\n"), fileUtils.cfg.getBoolean("NickItem.Enchanted.Enabled")));

								p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.WorldChangeAutoNickEnabled"));
							} else if (e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.WorldChange.DisplayName.Enabled"))) {
								e.setCancelled(true);

								utils.getNickOnWorldChangePlayers().remove(p.getUniqueId());
								p.getInventory().setItem(p.getInventory().getHeldItemSlot(), utils.createItem(Material.getMaterial(fileUtils.cfg.getString("NickItem.ItemType.Disabled")), fileUtils.cfg.getInt("NickItem.ItemAmount.Disabled"), fileUtils.cfg.getInt("NickItem.MetaData.Disabled"), languageFileUtils.getConfigString("NickItem.WorldChange.DisplayName.Disabled"), languageFileUtils.getConfigString("NickItem.ItemLore.Disabled").replace("&n", "\n"), fileUtils.cfg.getBoolean("NickItem.Enchanted.Disabled")));

								p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.WorldChangeAutoNickDisabled"));
							}
						}

						if (fileUtils.cfg.getBoolean("BungeeCord")) {
							if (!(utils.getNickedPlayers().contains(p.getUniqueId()))) {
								if (e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.BungeeCord.DisplayName.Disabled"))) {
									e.setCancelled(true);

									p.chat("/togglenick");
								} else if (e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.BungeeCord.DisplayName.Enabled"))) {
									e.setCancelled(true);

									p.chat("/togglenick");
								}
							}
						}
					}
				}
			}
		}
	}

}
