package com.justixdev.eazynick.listeners;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.api.NickedPlayerData;
import com.justixdev.eazynick.api.events.PlayerNickEvent;
import com.justixdev.eazynick.sql.MySQLNickManager;
import com.justixdev.eazynick.sql.MySQLPlayerDataManager;
import com.justixdev.eazynick.utilities.AsyncTask;
import com.justixdev.eazynick.utilities.AsyncTask.AsyncRunnable;
import com.justixdev.eazynick.utilities.ItemBuilder;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.SavedNickDataYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

import static com.justixdev.eazynick.nms.ReflectionHelper.*;

public class PlayerJoinListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFirstPlayerJoin(PlayerJoinEvent event) {
        EazyNick.getInstance().getPacketInjectorManager().inject(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLastPlayerJoin(PlayerJoinEvent event) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();
        SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
        SavedNickDataYamlFile savedNickDataYamlFile = eazyNick.getSavedNickDataYamlFile();
        LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
        MySQLNickManager mysqlNickManager = eazyNick.getMysqlNickManager();
        MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMysqlPlayerDataManager();

        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();
        String uniqueIdString = uniqueId.toString().replace("-", "");
        boolean isAPIMode = setupYamlFile.getConfiguration().getBoolean("APIMode");
        NickManager api = new NickManager(player);

        utils.getCanUseNick().putIfAbsent(uniqueId, 0L);

        if (!NMS_VERSION.equalsIgnoreCase("1_7_R4"))
            player.setCustomName(player.getName());

        if(!isAPIMode) {
            if(setupYamlFile.getConfiguration().getBoolean("OverwriteJoinQuitMessages")
                    && ((setupYamlFile.getConfiguration().getBoolean("BungeeCord") && mysqlNickManager.isNicked(uniqueId))
                            || utils.getLastNickData().containsKey(uniqueId))) {
                String message = setupYamlFile.getConfigString(player, "OverwrittenMessages.Join");

                if(setupYamlFile.getConfiguration().getBoolean("BungeeCord")
                        && (!setupYamlFile.getConfiguration().getBoolean("LobbyMode")
                                || (player.hasPermission("eazynick.bypasslobbymode")
                                        && setupYamlFile.getConfiguration().getBoolean("EnableBypassLobbyModePermission")))
                        && mysqlNickManager.isNicked(uniqueId))
                    message = message
                            .replace("%name%", mysqlNickManager.getNickName(uniqueId))
                            .replace(
                                    "%displayName%",
                                    mysqlPlayerDataManager.getChatPrefix(uniqueId)
                                            + mysqlNickManager.getNickName(uniqueId)
                                            + mysqlPlayerDataManager.getChatSuffix(uniqueId)
                            );
                else if(utils.getLastNickData().containsKey(uniqueId)) {
                    NickedPlayerData nickedPlayerData = utils.getLastNickData().get(uniqueId);

                    message = message.replace("%name%", nickedPlayerData.getNickName())
                            .replace(
                                    "%displayName%",
                                    nickedPlayerData.getChatPrefix()
                                            + nickedPlayerData.getNickName()
                                            + nickedPlayerData.getChatSuffix());
                } else if(setupYamlFile.getConfiguration().getBoolean("SaveLocalNickData")
                        && savedNickDataYamlFile.getConfiguration().contains(uniqueIdString))
                    message = message.replace(
                                    "%name%",
                                    savedNickDataYamlFile.getConfigString(uniqueIdString + ".nick_name"))
                            .replace(
                                    "%displayName%",
                                    savedNickDataYamlFile.getConfigString(uniqueIdString + ".chat_prefix")
                                            + savedNickDataYamlFile.getConfigString(player, uniqueIdString + ".nick_name")
                                            + savedNickDataYamlFile.getConfigString(player, uniqueIdString + ".chat_suffix")
                            );

                event.setJoinMessage(message);
            } else if (!((event.getJoinMessage() == null) || event.getJoinMessage().isEmpty())) {
                if (setupYamlFile.getConfiguration().getBoolean("BungeeCord")
                        && (!setupYamlFile.getConfiguration().getBoolean("LobbyMode")
                                || (player.hasPermission("eazynick.bypasslobbymode")
                                        && setupYamlFile.getConfiguration().getBoolean("EnableBypassLobbyModePermission")))
                        && mysqlNickManager.isNicked(uniqueId)) {
                    if (event.getJoinMessage().contains("formerly known as"))
                        event.setJoinMessage("§e" + player.getName() + " joined the game");

                    event.setJoinMessage(event.getJoinMessage()
                            .replace(player.getName(), mysqlNickManager.getNickName(uniqueId)));
                } else if (utils.getLastNickData().containsKey(uniqueId)) {
                    if (event.getJoinMessage().contains("formerly known as"))
                        event.setJoinMessage("§e" + player.getName() + " joined the game");

                    event.setJoinMessage(event.getJoinMessage()
                            .replace(player.getName(), utils.getLastNickData().get(uniqueId).getNickName()));
                } else if (setupYamlFile.getConfiguration().getBoolean("SaveLocalNickData")
                        && savedNickDataYamlFile.getConfiguration().contains(uniqueIdString)) {
                    if (event.getJoinMessage().contains("formerly known as"))
                        event.setJoinMessage("§e" + player.getName() + " joined the game");

                    event.setJoinMessage(event.getJoinMessage()
                            .replace(player.getName(), savedNickDataYamlFile.getConfigString(uniqueIdString + ".nick_name")));
                }
            }
        }

        new AsyncTask(new AsyncRunnable() {

            @SuppressWarnings("ConstantConditions")
            @Override
            public void run() {
                if(NMS_VERSION.equals("v1_19_R3")
                        && !(player.hasPermission("eazynick.bypass")
                                && setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission"))) {
                    try {
                        for(UUID uuid : utils.getNickedPlayers().keySet()) {
                            Player nickedPlayer = Bukkit.getPlayer(uuid);

                            if(nickedPlayer == null)
                                continue;

                            Object entityPlayer = invoke(nickedPlayer, "getHandle");
                            Location playerLocation = nickedPlayer.getLocation();

                            sendPacketNMS(
                                    player,
                                    newInstance(
                                            getNMSClass("network.protocol.game.PacketPlayOutNamedEntitySpawn"),
                                            types(getNMSClass("world.entity.player.EntityHuman")),
                                            entityPlayer
                                    )
                            );
                            sendPacketNMS(
                                    player,
                                    newInstance(
                                            getSubClass(
                                                    getNMSClass("network.protocol.game.PacketPlayOutEntity"),
                                                    "PacketPlayOutEntityLook"
                                            ),
                                            types(
                                                    int.class,
                                                    byte.class,
                                                    byte.class,
                                                    boolean.class
                                            ),
                                            nickedPlayer.getEntityId(),
                                            (byte) ((int) (playerLocation.getYaw() * 256.0F / 360.0F)),
                                            (byte) ((int) (playerLocation.getPitch() * 256.0F / 360.0F)),
                                            true
                                    )
                            );
                            sendPacketNMS(
                                    player,
                                    newInstance(
                                            getNMSClass("network.protocol.game.PacketPlayOutEntityHeadRotation"),
                                            types(
                                                    getNMSClass("world.entity.Entity"),
                                                    byte.class
                                            ),
                                            entityPlayer,
                                            (byte) ((int) (playerLocation.getYaw() * 256.0F / 360.0F))
                                    )
                            );
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                if(!isAPIMode) {
                    Bukkit.getScheduler().runTask(eazyNick, () -> {
                        if (setupYamlFile.getConfiguration().getBoolean("BungeeCord")) {
                            if (!setupYamlFile.getConfiguration().getBoolean("LobbyMode")
                                    || (player.hasPermission("eazynick.bypasslobbymode")
                                            && setupYamlFile.getConfiguration().getBoolean("EnableBypassLobbyModePermission"))) {
                                if (mysqlNickManager.isNicked(uniqueId)) {
                                    String nickName = mysqlNickManager.getNickName(uniqueId);

                                    if((nickName != null) && !(nickName.equals(player.getName())))
                                        utils.performReNick(player);
                                    else
                                        api.changeSkin(nickName);
                                }
                            } else if (mysqlNickManager.isNicked(uniqueId)
                                    && setupYamlFile.getConfiguration().getBoolean("GetNewNickOnEveryServerSwitch")) {
                                String nickName = mysqlNickManager.getNickName(uniqueId);

                                if((nickName == null) || !nickName.equals(player.getName())) {
                                    String name = api.getRandomName();

                                    mysqlNickManager.removePlayer(uniqueId);
                                    mysqlNickManager.addPlayer(uniqueId, name, name);
                                }
                            }

                            if (setupYamlFile.getConfiguration().getBoolean("NickItem.getOnJoin")) {
                                if (player.hasPermission("eazynick.item")) {
                                    if (!mysqlNickManager.isNicked(uniqueId))
                                        player.getInventory().setItem(
                                                setupYamlFile.getConfiguration().getInt("NickItem.Slot") - 1,
                                                new ItemBuilder(Material.getMaterial(setupYamlFile.getConfiguration().getString("NickItem.ItemType.Disabled")),
                                                        setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Disabled"),
                                                        setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Disabled"))
                                                        .setDisplayName(languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Disabled"))
                                                        .setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Disabled").split("&n"))
                                                        .setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Disabled"))
                                                        .build()
                                        );
                                    else
                                        player.getInventory().setItem(
                                                setupYamlFile.getConfiguration().getInt("NickItem.Slot") - 1,
                                                new ItemBuilder(Material.getMaterial(setupYamlFile.getConfiguration().getString("NickItem.ItemType.Enabled")),
                                                        setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Enabled"),
                                                        setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Enabled"))
                                                        .setDisplayName(languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Enabled"))
                                                        .setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Enabled").split("&n"))
                                                        .setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Enabled"))
                                                        .build()
                                        );
                                }
                            }
                        } else {
                            if (setupYamlFile.getConfiguration().getBoolean("NickItem.getOnJoin")) {
                                if (player.hasPermission("eazynick.item")) {
                                    if (setupYamlFile.getConfiguration().getBoolean("NickOnWorldChange"))
                                        player.getInventory().setItem(
                                                setupYamlFile.getConfiguration().getInt("NickItem.Slot") - 1,
                                                new ItemBuilder(Material.getMaterial(setupYamlFile.getConfiguration().getString("NickItem.ItemType.Disabled")),
                                                        setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Disabled"),
                                                        setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Disabled"))
                                                        .setDisplayName(languageYamlFile.getConfigString(player, "NickItem.WorldChange.DisplayName.Disabled"))
                                                        .setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Disabled").split("&n"))
                                                        .setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Disabled"))
                                                        .build()
                                        );
                                    else
                                        player.getInventory().setItem(
                                                setupYamlFile.getConfiguration().getInt("NickItem.Slot") - 1,
                                                new ItemBuilder(Material.getMaterial(setupYamlFile.getConfiguration().getString("NickItem.ItemType.Disabled")),
                                                        setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Disabled"),
                                                        setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Disabled"))
                                                        .setDisplayName(languageYamlFile.getConfigString(player, "NickItem.DisplayName.Disabled"))
                                                        .setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Disabled").split("&n"))
                                                        .setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Disabled"))
                                                        .build()
                                        );
                                }
                            }

                            if (!setupYamlFile.getConfiguration().getBoolean("DisconnectUnnick")) {
                                if(utils.getLastNickData().containsKey(uniqueId)) {
                                    NickedPlayerData nickedPlayerData = utils.getLastNickData().get(uniqueId);

                                    Bukkit.getPluginManager().callEvent(new PlayerNickEvent(
                                            player,
                                            nickedPlayerData.getNickName(),
                                            nickedPlayerData.getSkinName(),
                                            nickedPlayerData.getSpoofedUniqueId(),
                                            nickedPlayerData.getChatPrefix(),
                                            nickedPlayerData.getChatSuffix(),
                                            nickedPlayerData.getTabPrefix(),
                                            nickedPlayerData.getTabSuffix(),
                                            nickedPlayerData.getTagPrefix(),
                                            nickedPlayerData.getTagSuffix(),
                                            false,
                                            true,
                                            nickedPlayerData.getSortID(),
                                            nickedPlayerData.getGroupName()
                                    ));
                                } else if(setupYamlFile.getConfiguration().getBoolean("SaveLocalNickData")
                                        && savedNickDataYamlFile.getConfiguration().contains(uniqueIdString))
                                    Bukkit.getPluginManager().callEvent(new PlayerNickEvent(
                                            player,
                                            savedNickDataYamlFile.getConfigString(uniqueIdString + ".nick_name"),
                                            savedNickDataYamlFile.getConfigString(uniqueIdString + ".skin_name"),
                                            UUID.fromString(savedNickDataYamlFile.getConfigString(uniqueIdString + ".spoofed_uniqueid")),
                                            savedNickDataYamlFile.getConfigString(uniqueIdString + ".chat_prefix"),
                                            savedNickDataYamlFile.getConfigString(uniqueIdString + ".chat_suffix"),
                                            savedNickDataYamlFile.getConfigString(uniqueIdString + ".tablist_prefix"),
                                            savedNickDataYamlFile.getConfigString(uniqueIdString + ".tablist_suffix"),
                                            savedNickDataYamlFile.getConfigString(uniqueIdString + ".nametag_prefix"),
                                            savedNickDataYamlFile.getConfigString(uniqueIdString + ".nametag_suffix"),
                                            false,
                                            true,
                                            savedNickDataYamlFile.getConfiguration().getInt(uniqueIdString + ".sort_id"),
                                            savedNickDataYamlFile.getConfigString(uniqueIdString + ".group_name")
                                    ));
                            }
                        }

                        if (setupYamlFile.getConfiguration().getBoolean("JoinNick")) {
                            if (!api.isNicked() && player.hasPermission("eazynick.nick.random"))
                                utils.performNick(player, "RANDOM");
                        }
                    });
                }
            }
        }, 350L).run();
    }

}
