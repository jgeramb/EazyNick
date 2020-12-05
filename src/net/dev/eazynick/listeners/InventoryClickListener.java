package net.dev.eazynick.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.PlayerUnnickEvent;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.GUIFileUtils;
import net.dev.eazynick.utils.LanguageFileUtils;
import net.dev.eazynick.utils.Utils;

public class InventoryClickListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClick(InventoryClickEvent e) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
		GUIFileUtils guiFileUtils = eazyNick.getGUIFileUtils();
		
		if (e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			String title = e.getView().getTitle();
			
			if ((e.getCurrentItem() != null) && (e.getCurrentItem().getType() != Material.AIR) && (e.getCurrentItem().getItemMeta() != null) && (e.getCurrentItem().getItemMeta().getDisplayName() != null)) {
				String displayName = e.getCurrentItem().getItemMeta().getDisplayName();
				
				if (displayName.equalsIgnoreCase(languageFileUtils.getConfigString(p, "NickItem.DisplayName.Enabled")) || displayName.equalsIgnoreCase(languageFileUtils.getConfigString(p, "NickItem.DisplayName.Disabled")) || displayName.equalsIgnoreCase(languageFileUtils.getConfigString(p, "NickItem.WorldChange.DisplayName.Enabled")) || displayName.equalsIgnoreCase(languageFileUtils.getConfigString(p, "NickItem.WorldChange.DisplayName.Disabled")) || displayName.equalsIgnoreCase(languageFileUtils.getConfigString(p, "NickItem.BungeeCord.DisplayName.Enabled")) || displayName.equalsIgnoreCase(languageFileUtils.getConfigString(p, "NickItem.BungeeCord.DisplayName.Disabled"))) {
					if (!(fileUtils.getConfig().getBoolean("NickItem.InventorySettings.PlayersCanMoveItem")))
						e.setCancelled(true);
				}
				
				if (title.equalsIgnoreCase(guiFileUtils.getConfigString(p, "NickGUI.InventoryTitle"))) {
					e.setCancelled(true);

					if (displayName.equalsIgnoreCase(guiFileUtils.getConfigString(p, "NickGUI.Nick.DisplayName"))) {
						p.closeInventory();
						utils.performNick(p, "RANDOM");
					} else if (displayName.equalsIgnoreCase(guiFileUtils.getConfigString(p, "NickGUI.Unnick.DisplayName"))) {
						p.closeInventory();
						Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(p));
					}
				} else if (utils.getNickNameListPages().containsKey(p.getUniqueId())) {
					String currentPage = String.valueOf(utils.getNickNameListPages().get(p.getUniqueId()) + 1);
					
					if (title.equalsIgnoreCase(guiFileUtils.getConfigString(p, "NickNameGUI.InventoryTitle").replace("%currentPage%", currentPage).replace("%currentpage%", currentPage))) {
						e.setCancelled(true);

						if (displayName.equalsIgnoreCase(guiFileUtils.getConfigString(p, "NickNameGUI.Previous.DisplayName")))
							utils.openNickList(p, utils.getNickNameListPages().get(p.getUniqueId()) - 1);
						else if (displayName.equalsIgnoreCase(guiFileUtils.getConfigString(p, "NickNameGUI.Next.DisplayName")))
							utils.openNickList(p, utils.getNickNameListPages().get(p.getUniqueId()) + 1);
						else if(!(displayName.equals("§r"))) {
							p.closeInventory();
							utils.performNick(p, displayName.replace(guiFileUtils.getConfigString(p, "NickNameGUI.NickName.DisplayName").replace("%nickName%", "").replace("%nickname%", ""), ""));
						}
					}
				} else {
					String[] step4Parts = guiFileUtils.getConfigString(p, "RankedNickGUI.Step4.InventoryTitle").split("%nickName%");
					
					if(step4Parts.length < 2)
						step4Parts = guiFileUtils.getConfigString(p, "RankedNickGUI.Step4.InventoryTitle").split("%nickname%");

					if (title.equalsIgnoreCase(guiFileUtils.getConfigString(p, "RankedNickGUI.Step1.InventoryTitle"))) {
						e.setCancelled(true);
						
						if(!(displayName.equals("§r"))) {
							for (int i = 1; i <= 18; i++) {
								if(displayName.equals(guiFileUtils.getConfigString(p, "RankGUI.Rank" + i + ".Rank"))) {
									utils.openRankedNickGUI(p, guiFileUtils.getConfigString(p, "RankGUI.Rank" + i + ".RankName"));
									return;
								}
							}
						}
					} else if (title.equalsIgnoreCase(guiFileUtils.getConfigString(p, "RankedNickGUI.Step2.InventoryTitle"))) {
						e.setCancelled(true);
						
						if(!(displayName.equals("§r")))
							utils.openRankedNickGUI(p, utils.getLastGUITexts().get(p.getUniqueId()) + (displayName.equals(guiFileUtils.getConfigString(p, "RankedNickGUI.Step2.Default.DisplayName")) ? " DEFAULT" : (displayName.equals(guiFileUtils.getConfigString(p, "RankedNickGUI.Step2.Normal.DisplayName")) ? " NORMAL" : " RANDOM")));
					} else if (title.equalsIgnoreCase(guiFileUtils.getConfigString(p, "RankedNickGUI.Step3.InventoryTitle"))) {
						e.setCancelled(true);
						
						String lastText = utils.getLastGUITexts().get(p.getUniqueId());
						
						if(displayName.equals(guiFileUtils.getConfigString(p, "RankedNickGUI.Step3.Custom.DisplayName"))) {
							if(eazyNick.getVersion().equals("1_7_R4") || eazyNick.getVersion().equals("1_8_R1")) {
								utils.getPlayersTypingNameInChat().put(p.getUniqueId(), lastText);
								
								p.closeInventory();
								p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString(p, "Messages.TypeNameInChat"));
							} else {
								String[] args = lastText.split(" ");
								
								utils.openCustomGUI(p, args[0], args[1]);
							}
						} else if(displayName.equals(guiFileUtils.getConfigString(p, "RankedNickGUI.Step3.Random.DisplayName"))) {
							String name = utils.getNickNames().get((new Random().nextInt(utils.getNickNames().size())));
							boolean nickNameIsInUse = false;
							
							for (String nickName : utils.getPlayerNicknames().values()) {
								if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
									nickNameIsInUse = true;
							}
							
							while (nickNameIsInUse) {
								nickNameIsInUse = false;
								name = utils.getNickNames().get((new Random().nextInt(utils.getNickNames().size())));
								
								for (String nickName : utils.getPlayerNicknames().values()) {
									if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
										nickNameIsInUse = true;
								}
							}
							
							utils.openRankedNickGUI(p, lastText + " " + name);
						}
					} else if (title.startsWith(step4Parts[0]) && ((step4Parts.length == 1) || title.endsWith(step4Parts[1]))) {
						e.setCancelled(true);
						
						String lastText = utils.getLastGUITexts().get(p.getUniqueId());
						String[] args = lastText.split(" ");
						
						utils.getLastGUITexts().put(p.getUniqueId(), (lastText = args[0] + " " + args[1]));
						
						if(displayName.equals(guiFileUtils.getConfigString(p, "RankedNickGUI.Step4.Use.DisplayName"))) {
							p.closeInventory();
							
							utils.performRankedNick(p, args[0], args[1], title.replace(step4Parts[0], "").replace(step4Parts[1], ""));
						} else if(displayName.equals(guiFileUtils.getConfigString(p, "RankedNickGUI.Step4.Retry.DisplayName"))) {
							String name = utils.getNickNames().get((new Random().nextInt(utils.getNickNames().size())));
							boolean nickNameIsInUse = false;
							
							for (String nickName : utils.getPlayerNicknames().values()) {
								if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
									nickNameIsInUse = true;
							}
							
							while (nickNameIsInUse) {
								nickNameIsInUse = false;
								name = utils.getNickNames().get((new Random().nextInt(utils.getNickNames().size())));
								
								for (String nickName : utils.getPlayerNicknames().values()) {
									if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
										nickNameIsInUse = true;
								}
							}
							
							utils.openRankedNickGUI(p, lastText + " " + name);
						} else if(displayName.equals(guiFileUtils.getConfigString(p, "RankedNickGUI.Step4.Custom.DisplayName"))) {
							if(eazyNick.getVersion().equals("1_7_R4") || eazyNick.getVersion().equals("1_8_R1")) {
								utils.getPlayersTypingNameInChat().put(p.getUniqueId(), lastText);
								
								p.closeInventory();
								p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString(p, "Messages.TypeNameInChat"));
							} else
								utils.openCustomGUI(p, args[0], args[1]);
						}
					}
				}
			}
		}
	}

}
