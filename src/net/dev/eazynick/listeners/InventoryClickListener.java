package net.dev.eazynick.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.SkullMeta;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.PlayerUnnickEvent;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.LanguageFileUtils;
import net.dev.eazynick.utils.Utils;

public class InventoryClickListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClick(InventoryClickEvent e) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
		
		if (e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();

			if ((e.getCurrentItem() != null) && (e.getCurrentItem().getType() != Material.AIR) && (e.getCurrentItem().getItemMeta() != null) && (e.getCurrentItem().getItemMeta().getDisplayName() != null)) {
				if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.DisplayName.Enabled")) || e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.DisplayName.Disabled")) || e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.WorldChange.DisplayName.Enabled")) || e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.WorldChange.DisplayName.Disabled")) || e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.BungeeCord.DisplayName.Enabled")) || e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.BungeeCord.DisplayName.Disabled"))) {
					if (fileUtils.cfg.getBoolean("NickItem.InventorySettings.PlayersCanMoveItem") == false)
						e.setCancelled(true);
				}

				if (e.getView().getTitle().equalsIgnoreCase(languageFileUtils.getConfigString("NickGUI.InventoryTitle"))) {
					e.setCancelled(true);

					if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickGUI.NickItem.DisplayName"))) {
						p.closeInventory();
						utils.performNick(p, "RANDOM");
					} else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickGUI.UnnickItem.DisplayName"))) {
						p.closeInventory();
						Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(p));
					}
				} else if (utils.getNickNameListPage().containsKey(p.getUniqueId())) {
					String shownPage = "" + (utils.getNickNameListPage().get(p.getUniqueId()) + 1);
					String inventoryName = languageFileUtils.getConfigString("NickNameGUI.InventoryTitle").replace("%currentPage%", shownPage);

					if (e.getView().getTitle().equalsIgnoreCase(inventoryName)) {
						Integer page = utils.getNickNameListPage().get(p.getUniqueId());
						e.setCancelled(true);

						if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickNameGUI.BackItem.DisplayName"))) {
							if (!(page == 0)) {
								utils.getNickNamesHandler().createPage(p, page - 1);
								utils.getNickNameListPage().put(p.getUniqueId(), page - 1);
							} else
								p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.NoMorePages"));
						} else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickNameGUI.NextItem.DisplayName"))) {
							if (page < (utils.getNickNamesHandler().getPages().size() - 1)) {
								if (page != 99) {
									utils.getNickNamesHandler().createPage(p, page + 1);
									utils.getNickNameListPage().put(p.getUniqueId(), page + 1);
								} else
									p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.NoMorePagesCanBeLoaded"));
							} else
								p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.NoMorePages"));
						} else {
							if (e.getCurrentItem().getType().equals(Material.getMaterial((eazyNick.getVersion().startsWith("1_13") || eazyNick.getVersion().startsWith("1_14") || eazyNick.getVersion().startsWith("1_15")) ? "PLAYER_HEAD" : "SKULL_ITEM"))) {
								String nickName = "";
								String skullOwner = ((SkullMeta) e.getCurrentItem().getItemMeta()).getOwner();

								for (String name : utils.getNickNames())
									if (skullOwner.equalsIgnoreCase(name))
										nickName = name;

								p.closeInventory();
								utils.performNick(p, nickName);
							}
						}
					}
				}
			}
		}
	}

}
