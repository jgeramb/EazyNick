package com.justixdev.eazynick.listeners;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.events.PlayerUnnickEvent;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Objects;

import static com.justixdev.eazynick.nms.ReflectionHelper.NMS_VERSION;

public class PlayerCommandPreprocessListener implements Listener {

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();
        SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();

        Player player = event.getPlayer();
        String message = event.getMessage().trim();

        // Name replacements
        String[] args = message.split("\\s+");
        StringBuilder msg = new StringBuilder(message);
        boolean allowRealName = setupYamlFile.getConfiguration().getBoolean("AllowRealNamesInCommands");

        if(utils.getReplaceNameInCommandBlackList()
                .stream()
                .noneMatch(command -> args[0].equalsIgnoreCase("/" + command))
                && (EazyNick.getInstance().getCommand(args[0].substring(1)) == null)) {
            utils.getNickedPlayers().values().forEach(nickedPlayerData -> {
                for (int i = 0; i < args.length; i++) {
                    if(args[i].equalsIgnoreCase(nickedPlayerData.getNickName()))
                        args[i] = nickedPlayerData.getRealName();
                    else if(args[i].equalsIgnoreCase(nickedPlayerData.getRealName()) && !(allowRealName))
                        args[i] = nickedPlayerData.getRealName() + "§r";
                }
            });

            msg = new StringBuilder();

            for (String arg : args)
                msg.append(arg).append(" ");

            event.setMessage(msg.toString().trim());
        }

        if(message.toLowerCase().startsWith("/me ")
                && (message.length() > 4)
                && !event.isCancelled()
                && (NMS_VERSION.startsWith("v1_19") || NMS_VERSION.startsWith("v1_20"))
        ) {
            event.setCancelled(true);
            Bukkit.getOnlinePlayers().forEach(currentPlayer ->
                    currentPlayer.sendMessage("* " + player.getName() + " " + message.substring(4))
            );
            return;
        }

        // Command system
        if(eazyNick.getCommandManager().execute(player, event.getMessage().substring(1))) {
            event.setCancelled(true);
            return;
        }

        String rawCommand = msg.toString().toLowerCase().replace("bukkit:", "");

        if(rawCommand.startsWith("/rl") || rawCommand.startsWith("/reload")) {
            event.setCancelled(true);

            utils.getNickedPlayers()
                    .keySet()
                    .stream()
                    .map(Bukkit::getPlayer)
                    .filter(Objects::nonNull)
                    .forEach(currentPlayer -> Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(currentPlayer)));

            Bukkit.getScheduler().runTaskLater(eazyNick, Bukkit::reload, 20);
            return;
        }

        if (
                !(rawCommand.startsWith("/help nick")
                        || rawCommand.startsWith("/help eazynick")
                        || rawCommand.startsWith("/? nick")
                        || rawCommand.startsWith("/? eazynick"))
                || !player.hasPermission("bukkit.command.help"))
            return;

        event.setCancelled(true);

        player.sendMessage("§e--------- §fHelp: " + eazyNick.getDescription().getName() + " §e----------------------");
        player.sendMessage("§7Below is a list of all " + eazyNick.getDescription().getName() + " commands:");

        PluginCommand command = eazyNick.getCommand("eazynick");

        if(command != null)
            player.sendMessage("§6/eazynick: §f" + command.getDescription());
    }

}
