package net.dev.eazynick.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.PlayerUnnickEvent;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.ItemBuilder;
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
			String displayName = e.getItem().getItemMeta().getDisplayName();
			
			if (fileUtils.getConfig().getBoolean("NickItem.getOnJoin")) {
				if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					if (displayName.equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.DisplayName.Disabled"))) {
						e.setCancelled(true);
						utils.performNick(p, "RANDOM");

						p.getInventory().setItem(p.getInventory().getHeldItemSlot(), new ItemBuilder(Material.getMaterial(fileUtils.getConfig().getString("NickItem.ItemType.Enabled")), fileUtils.getConfig().getInt("NickItem.ItemAmount.Enabled"), fileUtils.getConfig().getInt("NickItem.MetaData.Enabled")).setDisplayName(languageFileUtils.getConfigString("NickItem.DisplayName.Enabled")).setLore(languageFileUtils.getConfigString("NickItem.ItemLore.Enabled").split("&n")).setEnchanted(fileUtils.getConfig().getBoolean("NickItem.Enchanted.Enabled")).build());
					} else if (displayName.equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.DisplayName.Enabled"))) {
						e.setCancelled(true);
						Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(p));

						p.getInventory().setItem(p.getInventory().getHeldItemSlot(), new ItemBuilder(Material.getMaterial(fileUtils.getConfig().getString("NickItem.ItemType.Disabled")), fileUtils.getConfig().getInt("NickItem.ItemAmount.Disabled"), fileUtils.getConfig().getInt("NickItem.MetaData.Disabled")).setDisplayName(languageFileUtils.getConfigString("NickItem.DisplayName.Disabled")).setLore(languageFileUtils.getConfigString("NickItem.ItemLore.Disabled").split("&n")).setEnchanted(fileUtils.getConfig().getBoolean("NickItem.Enchanted.Disabled")).build());
					} else {
						if (fileUtils.getConfig().getBoolean("NickOnWorldChange")) {
							if (displayName.equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.WorldChange.DisplayName.Disabled"))) {
								e.setCancelled(true);

								utils.getNickOnWorldChangePlayers().add(p.getUniqueId());
								p.getInventory().setItem(p.getInventory().getHeldItemSlot(), new ItemBuilder(Material.getMaterial(fileUtils.getConfig().getString("NickItem.ItemType.Enabled")), fileUtils.getConfig().getInt("NickItem.ItemAmount.Enabled"), fileUtils.getConfig().getInt("NickItem.MetaData.Enabled")).setDisplayName(languageFileUtils.getConfigString("NickItem.WorldChange.DisplayName.Enabled")).setLore(languageFileUtils.getConfigString("NickItem.ItemLore.Enabled").split("&n")).setEnchanted(fileUtils.getConfig().getBoolean("NickItem.Enchanted.Enabled")).build());

								p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.WorldChangeAutoNickEnabled"));
							} else if (displayName.equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.WorldChange.DisplayName.Enabled"))) {
								e.setCancelled(true);

								utils.getNickOnWorldChangePlayers().remove(p.getUniqueId());
								p.getInventory().setItem(p.getInventory().getHeldItemSlot(), new ItemBuilder(Material.getMaterial(fileUtils.getConfig().getString("NickItem.ItemType.Disabled")), fileUtils.getConfig().getInt("NickItem.ItemAmount.Disabled"), fileUtils.getConfig().getInt("NickItem.MetaData.Disabled")).setDisplayName(languageFileUtils.getConfigString("NickItem.WorldChange.DisplayName.Disabled")).setLore(languageFileUtils.getConfigString("NickItem.ItemLore.Disabled").split("&n")).setEnchanted(fileUtils.getConfig().getBoolean("NickItem.Enchanted.Disabled")).build());

								p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.WorldChangeAutoNickDisabled"));
							}
						}

						if (fileUtils.getConfig().getBoolean("BungeeCord")) {
							if (!(utils.getNickedPlayers().contains(p.getUniqueId()))) {
								if (displayName.equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.BungeeCord.DisplayName.Disabled"))) {
									e.setCancelled(true);

									utils.toggleBungeeNick(p);
								} else if (displayName.equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.BungeeCord.DisplayName.Enabled"))) {
									e.setCancelled(true);

									utils.toggleBungeeNick(p);
								}
							}
						}
					}
				}
			}
		}
	}

}
