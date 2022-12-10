package com.justixdev.eazynick.listeners;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandPreprocessListener implements Listener {

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();
        SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();

        Player player = event.getPlayer();
        StringBuilder msg = new StringBuilder(event.getMessage());
        String[] args = msg.toString().trim().split(" ");
        boolean allowRealName = setupYamlFile.getConfiguration().getBoolean("AllowRealNamesInCommands");

        if(utils.getReplaceNameInCommandBlackList().stream().noneMatch(command -> args[0].equalsIgnoreCase("/" + command)) && (EazyNick.getInstance().getCommand(args[0].substring(1)) == null)) {
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

        String msg2 = msg.toString().toLowerCase().replace("bukkit:", "");

        if (!(msg2.startsWith("/help nick") || msg2.startsWith("/help eazynick") || msg2.startsWith("/? nick") || msg2.startsWith("/? eazynick")) || !(player.hasPermission("bukkit.command.help"))) return;

        event.setCancelled(true);

        player.sendMessage("§e--------- §fHelp: " + eazyNick.getDescription().getName() + " §e----------------------");
        player.sendMessage("§7Below is a list of all " + eazyNick.getDescription().getName() + " commands:");

        PluginCommand command = eazyNick.getCommand("eazynick");

        if(command != null)
            player.sendMessage("§6/eazynick: §f" + command.getDescription());
    }

}
