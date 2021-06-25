package net.dev.eazynick.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.PlayerUnnickEvent;
import net.dev.eazynick.utilities.ItemBuilder;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import net.dev.eazynick.utilities.configuration.yaml.SetupYamlFile;

public class PlayerInteractListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		
		String prefix = utils.getPrefix();
		Player player = event.getPlayer();

		if ((event.getItem() != null) && (event.getItem().getType() != Material.AIR && (event.getItem().getItemMeta() != null) && (event.getItem().getItemMeta().getDisplayName() != null))) {
			String displayName = event.getItem().getItemMeta().getDisplayName();
			
			if (setupYamlFile.getConfiguration().getBoolean("NickItem.getOnJoin") && player.hasPermission("nick.item")) {
				if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					if (displayName.equalsIgnoreCase(languageYamlFile.getConfigString(player, "NickItem.DisplayName.Disabled"))) {
						event.setCancelled(true);
						utils.performNick(player, "RANDOM");

						player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemBuilder(Material.getMaterial(setupYamlFile.getConfiguration().getString("NickItem.ItemType.Enabled")), setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Enabled"), setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Enabled")).setDisplayName(languageYamlFile.getConfigString(player, "NickItem.DisplayName.Enabled")).setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Enabled").split("&n")).setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Enabled")).build());
					} else if (displayName.equalsIgnoreCase(languageYamlFile.getConfigString(player, "NickItem.DisplayName.Enabled"))) {
						event.setCancelled(true);
						Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(player));

						player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemBuilder(Material.getMaterial(setupYamlFile.getConfiguration().getString("NickItem.ItemType.Disabled")), setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Disabled"), setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Disabled")).setDisplayName(languageYamlFile.getConfigString(player, "NickItem.DisplayName.Disabled")).setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Disabled").split("&n")).setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Disabled")).build());
					} else {
						if (setupYamlFile.getConfiguration().getBoolean("NickOnWorldChange")) {
							if (displayName.equalsIgnoreCase(languageYamlFile.getConfigString(player, "NickItem.WorldChange.DisplayName.Disabled"))) {
								event.setCancelled(true);

								utils.getNickOnWorldChangePlayers().add(player.getUniqueId());
								player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemBuilder(Material.getMaterial(setupYamlFile.getConfiguration().getString("NickItem.ItemType.Enabled")), setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Enabled"), setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Enabled")).setDisplayName(languageYamlFile.getConfigString(player, "NickItem.WorldChange.DisplayName.Enabled")).setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Enabled").split("&n")).setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Enabled")).build());

								languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.WorldChangeAutoNickEnabled").replace("%prefix%", prefix));
							} else if (displayName.equalsIgnoreCase(languageYamlFile.getConfigString(player, "NickItem.WorldChange.DisplayName.Enabled"))) {
								event.setCancelled(true);

								utils.getNickOnWorldChangePlayers().remove(player.getUniqueId());
								player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemBuilder(Material.getMaterial(setupYamlFile.getConfiguration().getString("NickItem.ItemType.Disabled")), setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Disabled"), setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Disabled")).setDisplayName(languageYamlFile.getConfigString(player, "NickItem.WorldChange.DisplayName.Disabled")).setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Disabled").split("&n")).setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Disabled")).build());

								languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.WorldChangeAutoNickDisabled").replace("%prefix%", prefix));
							}
						}

						if (setupYamlFile.getConfiguration().getBoolean("BungeeCord")) {
							if (!(utils.getNickedPlayers().containsKey(player.getUniqueId()))) {
								if (displayName.equalsIgnoreCase(languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Disabled"))) {
									event.setCancelled(true);

									utils.toggleBungeeNick(player);
								} else if (displayName.equalsIgnoreCase(languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Enabled"))) {
									event.setCancelled(true);

									utils.toggleBungeeNick(player);
								}
							}
						}
					}
				}
			}
		}
	}

}
