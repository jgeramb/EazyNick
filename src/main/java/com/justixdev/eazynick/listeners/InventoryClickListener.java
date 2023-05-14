package com.justixdev.eazynick.listeners;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.events.PlayerUnnickEvent;
import com.justixdev.eazynick.utilities.GUIManager;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.GUIYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import static com.justixdev.eazynick.nms.ReflectionHelper.NMS_VERSION;

public class InventoryClickListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();
        GUIManager guiManager = eazyNick.getGuiManager();
        SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
        LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
        GUIYamlFile guiYamlFile = eazyNick.getGuiYamlFile();

        String prefix = utils.getPrefix();

        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            String title = event.getView().getTitle();

            if ((event.getCurrentItem() != null)
                    && (event.getCurrentItem().getType() != Material.AIR)
                    && (event.getCurrentItem().getItemMeta() != null)
                    && event.getCurrentItem().getItemMeta().hasDisplayName()) {
                String displayName = event.getCurrentItem().getItemMeta().getDisplayName();

                if (displayName.equalsIgnoreCase(languageYamlFile.getConfigString(player, "NickItem.DisplayName.Enabled"))
                        || displayName.equalsIgnoreCase(languageYamlFile.getConfigString(player, "NickItem.DisplayName.Disabled"))
                        || displayName.equalsIgnoreCase(languageYamlFile.getConfigString(player, "NickItem.WorldChange.DisplayName.Enabled"))
                        || displayName.equalsIgnoreCase(languageYamlFile.getConfigString(player, "NickItem.WorldChange.DisplayName.Disabled"))
                        || displayName.equalsIgnoreCase(languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Enabled"))
                        || displayName.equalsIgnoreCase(languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Disabled"))) {
                    if (!(setupYamlFile.getConfiguration().getBoolean("NickItem.InventorySettings.PlayersCanMoveItem")))
                        event.setCancelled(true);
                }

                if (title.equalsIgnoreCase(guiYamlFile.getConfigString(player, "NickGUI.InventoryTitle"))) {
                    event.setCancelled(true);

                    if (displayName.equalsIgnoreCase(guiYamlFile.getConfigString(player, "NickGUI.Nick.DisplayName"))) {
                        player.closeInventory();

                        if(!(setupYamlFile.getConfiguration().getStringList("DisabledNickWorlds").contains(player.getWorld().getName())))
                            utils.performNick(player, "RANDOM");
                        else
                            languageYamlFile.sendMessage(
                                    player,
                                    languageYamlFile.getConfigString(player, "Messages.DisabledWorld")
                                            .replace("%prefix%", prefix));
                    } else if (displayName.equalsIgnoreCase(guiYamlFile.getConfigString(player, "NickGUI.Unnick.DisplayName"))) {
                        player.closeInventory();

                        Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(player));
                    }
                } else if (utils.getNickNameListPages().containsKey(player.getUniqueId())) {
                    String currentPage = String.valueOf(utils.getNickNameListPages().get(player.getUniqueId()) + 1);

                    if (title.equalsIgnoreCase(guiYamlFile.getConfigString(player, "NickNameGUI.InventoryTitle")
                            .replace("%currentPage%", currentPage)
                            .replace("%currentpage%", currentPage))) {
                        event.setCancelled(true);

                        if (displayName.equalsIgnoreCase(guiYamlFile.getConfigString(player, "NickNameGUI.Previous.DisplayName")))
                            guiManager.openNickList(player, utils.getNickNameListPages().get(player.getUniqueId()) - 1);
                        else if (displayName.equalsIgnoreCase(guiYamlFile.getConfigString(player, "NickNameGUI.Next.DisplayName")))
                            guiManager.openNickList(player, utils.getNickNameListPages().get(player.getUniqueId()) + 1);
                        else if(!(displayName.equals("§r"))) {
                            player.closeInventory();

                            if(!(setupYamlFile.getConfiguration().getStringList("DisabledNickWorlds").contains(player.getWorld().getName())))
                                utils.performNick(
                                        player,
                                        displayName.replace(guiYamlFile.getConfigString(player, "NickNameGUI.NickName.DisplayName")
                                                .replace("%nickName%", "")
                                                .replace("%nickname%", ""), ""));
                            else
                                languageYamlFile.sendMessage(
                                        player,
                                        languageYamlFile.getConfigString(player, "Messages.DisabledWorld")
                                                .replace("%prefix%", prefix));
                        }
                    }
                } else {
                    String[] step4Parts = guiYamlFile.getConfigString(player, "RankedNickGUI.Step4.InventoryTitle").split("%nickName%");

                    if(step4Parts.length < 2)
                        step4Parts = guiYamlFile.getConfigString(player, "RankedNickGUI.Step4.InventoryTitle").split("%nickname%");

                    if (title.equalsIgnoreCase(guiYamlFile.getConfigString(player, "RankedNickGUI.Step1.InventoryTitle"))) {
                        event.setCancelled(true);

                        if(!(displayName.equals("§r"))) {
                            for (int i = 1; i <= 18; i++) {
                                if(displayName.equals(guiYamlFile.getConfigString(player, "RankGUI.Rank" + i + ".Rank"))) {
                                    guiManager.openRankedNickGUI(
                                            player,
                                            guiYamlFile.getConfigString(player, "RankGUI.Rank" + i + ".RankName"));
                                    return;
                                }
                            }
                        }
                    } else if (title.equalsIgnoreCase(guiYamlFile.getConfigString(player, "RankedNickGUI.Step2.InventoryTitle"))) {
                        event.setCancelled(true);

                        if(!(displayName.equals("§r")))
                            guiManager.openRankedNickGUI(
                                    player,
                                    utils.getLastGUITexts().get(player.getUniqueId())
                                            + (displayName.equals(guiYamlFile.getConfigString(player, "RankedNickGUI.Step2.Default.DisplayName"))
                                                    ? " DEFAULT"
                                                    : displayName.equals(guiYamlFile.getConfigString(player, "RankedNickGUI.Step2.Normal.DisplayName"))
                                                            ? " NORMAL"
                                                            : displayName.equals(guiYamlFile.getConfigString(player, "RankedNickGUI.Step2.Random.DisplayName"))
                                                                    ? " RANDOM"
                                                                    : " SKINFROMNAME"));
                    } else if (title.equalsIgnoreCase(guiYamlFile.getConfigString(player, "RankedNickGUI.Step3.InventoryTitle"))) {
                        event.setCancelled(true);

                        String lastText = utils.getLastGUITexts().get(player.getUniqueId());

                        if(displayName.equals(guiYamlFile.getConfigString(player, "RankedNickGUI.Step3.Custom.DisplayName"))) {
                            if(NMS_VERSION.equals("v1_7_R4")
                                    || NMS_VERSION.equals("v1_8_R1")) {
                                utils.getPlayersTypingNameInChat().put(player.getUniqueId(), lastText);

                                player.closeInventory();
                                languageYamlFile.sendMessage(
                                        player,
                                        languageYamlFile.getConfigString(player, "Messages.TypeNameInChat")
                                                .replace("%prefix%", prefix));
                            } else {
                                String[] args = lastText.split(" ");

                                guiManager.openCustomGUI(player, args[0], args[1]);
                            }
                        } else if(displayName.equals(guiYamlFile.getConfigString(player, "RankedNickGUI.Step3.Random.DisplayName"))) {
                            AtomicReference<String> currentName = new AtomicReference<>();

                            do {
                                currentName.set(utils.getNickNames().get((new Random().nextInt(utils.getNickNames().size()))));
                            } while (utils.getNickedPlayers()
                                    .values()
                                    .stream()
                                    .anyMatch(currentNickedPlayerData -> currentNickedPlayerData.getNickName().equalsIgnoreCase(currentName.get())));

                            guiManager.openRankedNickGUI(player, lastText + " " + currentName.get());
                        }
                    } else if (title.startsWith(step4Parts[0]) && ((step4Parts.length == 1) || title.endsWith(step4Parts[1]))) {
                        event.setCancelled(true);

                        String lastText = utils.getLastGUITexts().get(player.getUniqueId());
                        String[] args = lastText.split(" ");

                        utils.getLastGUITexts().put(player.getUniqueId(), (lastText = args[0] + " " + args[1]));

                        if(displayName.equals(guiYamlFile.getConfigString(player, "RankedNickGUI.Step4.Use.DisplayName"))) {
                            player.closeInventory();

                            if(!(setupYamlFile.getConfiguration().getStringList("DisabledNickWorlds")
                                    .contains(player.getWorld().getName())))
                                utils.performRankedNick(
                                        player,
                                        args[0],
                                        args[1],
                                        title
                                                .replace(step4Parts[0], "")
                                                .replace(step4Parts[1], ""));
                            else
                                languageYamlFile.sendMessage(
                                        player,
                                        languageYamlFile.getConfigString(player, "Messages.DisabledWorld")
                                                .replace("%prefix%", prefix));
                        } else if(displayName.equals(guiYamlFile.getConfigString(player, "RankedNickGUI.Step4.Retry.DisplayName"))) {
                            AtomicReference<String> currentName = new AtomicReference<>();

                            do {
                                currentName.set(utils.getNickNames().get((new Random().nextInt(utils.getNickNames().size()))));
                            } while (utils.getNickedPlayers()
                                    .values()
                                    .stream()
                                    .anyMatch(currentNickedPlayerData -> currentNickedPlayerData.getNickName().equalsIgnoreCase(currentName.get())));

                            guiManager.openRankedNickGUI(player, lastText + " " + currentName.get());
                        } else if(displayName.equals(guiYamlFile.getConfigString(player, "RankedNickGUI.Step4.Custom.DisplayName"))) {
                            if(NMS_VERSION.equals("v1_7_R4")
                                    || NMS_VERSION.equals("v1_8_R1")
                                    || !(setupYamlFile.getConfiguration().getBoolean("UseSignGUIForCustomName")
                                            || setupYamlFile.getConfiguration().getBoolean("UseAnvilGUIForCustomName"))) {
                                utils.getPlayersTypingNameInChat().put(player.getUniqueId(), lastText);

                                player.closeInventory();
                                languageYamlFile.sendMessage(
                                        player,
                                        languageYamlFile.getConfigString(player, "Messages.TypeNameInChat")
                                                .replace("%prefix%", prefix));
                            } else
                                guiManager.openCustomGUI(player, args[0], args[1]);
                        }
                    }
                }
            }
        }
    }

}
