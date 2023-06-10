package com.justixdev.eazynick.listeners;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static com.justixdev.eazynick.nms.ReflectionHelper.NMS_VERSION;

public class AsyncPlayerChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFirstAsyncPlayerChat(AsyncPlayerChatEvent event) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();

        Player player = event.getPlayer();

        if(utils.getPlayersTypingNameInChat().containsKey(player.getUniqueId())) {
            String[] args = (utils.getPlayersTypingNameInChat().get(player.getUniqueId())
                    + " "
                    + event.getMessage().trim()).split("\\s+");

            utils.getPlayersTypingNameInChat().remove(player.getUniqueId());
            utils.performRankedNick(player, args[0], args[1], args[2]);

            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLastAsyncPlayerChat(AsyncPlayerChatEvent event) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();
        SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();

        Player player = event.getPlayer();
        boolean replaceChat =
                setupYamlFile.getConfiguration().getBoolean("ReplaceNickedChatFormat")
                && utils.getWorldsWithDisabledPrefixAndSuffix()
                        .stream()
                        .anyMatch(world -> world.equalsIgnoreCase(player.getWorld().getName()));

        utils.setLastChatMessage(event.getMessage());

        NickManager api = new NickManager(player);

        if(event.getFormat().equals("<%1$s> %2$s") && !replaceChat && (NMS_VERSION.startsWith("v1_19") || NMS_VERSION.startsWith("v1_20"))) {
            event.setCancelled(true);
            Bukkit.getOnlinePlayers().forEach(currentPlayer -> currentPlayer.sendMessage("<" + api.getNickName() + "> " + event.getMessage()));
            return;
        }

        if (!replaceChat || event.isCancelled())
            return;

        if (api.isNicked()) {
            event.setCancelled(true);

            String format = setupYamlFile.getConfigString(player, "Settings.ChatFormat")
                    .replace("%displayName%", player.getDisplayName())
                    .replace("%nickName%", api.getNickName())
                    .replace("%playerName%", api.getNickName())
                    .replace("%displayname%", player.getDisplayName())
                    .replace("%nickname%", api.getNickName())
                    .replace("%playername%", api.getNickName())
                    .replace("%prefix%", api.getChatPrefix())
                    .replace("%suffix%", api.getChatSuffix());

            if(utils.isPluginInstalled("PlaceholderAPI"))
                format = PlaceholderAPI.setPlaceholders(player, format);

            String finalFormat = format
                    .replace("%message%", event.getMessage())
                    .replaceAll("%", "%%");

            event.setFormat(finalFormat);

            Bukkit.getConsoleSender().sendMessage(finalFormat);

            Bukkit.getOnlinePlayers().forEach(currentPlayer -> {
                if (currentPlayer.getName().equalsIgnoreCase(player.getName())) {
                    if (setupYamlFile.getConfiguration().getBoolean("SeeNickSelf"))
                        currentPlayer.sendMessage(finalFormat);
                    else
                        currentPlayer.sendMessage(finalFormat.replace(player.getDisplayName(), api.getOldDisplayName()));
                } else if (currentPlayer.hasPermission("eazynick.bypass")
                        && setupYamlFile.getConfiguration().getBoolean("EnableBypassPermission"))
                    currentPlayer.sendMessage(finalFormat.replace(player.getDisplayName(), api.getOldDisplayName()));
                else
                    currentPlayer.sendMessage(finalFormat);
            });
        }
    }

}
