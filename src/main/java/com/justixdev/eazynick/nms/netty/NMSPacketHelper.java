package com.justixdev.eazynick.nms.netty;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NMSPacketHelper {

    public static String[] replaceTabCompletions(Player player, String[] tabCompletions) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();

        List<String> newCompletions = new ArrayList<>(Arrays.asList(tabCompletions));
        String textToComplete = utils.getTextsToComplete().get(player);

        if(textToComplete.startsWith("/")) {
            if(!textToComplete.contains(" ")) {
                // add commands
                eazyNick.getCommandManager().getCommands().forEach((key, value) -> {
                    newCompletions.add("/" + key.getName());
                    value.getAliases().forEach(alias -> newCompletions.add("/" + alias));
                });

                List<String> matches = StringUtil.copyPartialMatches(textToComplete, newCompletions, new ArrayList<>());
                newCompletions.clear();
                newCompletions.addAll(matches);
            }
        }

        if(textToComplete.endsWith(" "))
            textToComplete = "";
        else
            textToComplete = textToComplete.substring(textToComplete.lastIndexOf(' ') + 1).trim();

        if(!(player.hasPermission("eazynick.bypass")
                && eazyNick.getSetupYamlFile().getConfiguration().getBoolean("EnableBypassPermission"))) {
            List<String> playerNames = new ArrayList<>();

            // Collect player-/nicknames

            Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(currentPlayer -> !new NickManager(currentPlayer).isNicked())
                    .forEach(currentPlayer -> playerNames.add(currentPlayer.getName()));

            utils.getNickedPlayers()
                    .values()
                    .forEach(currentNickedPlayerData -> playerNames.add(currentNickedPlayerData.getNickName()));

            // Process completions
            newCompletions.removeIf(currentCompletion -> Bukkit.getOnlinePlayers()
                    .stream()
                    .anyMatch(currentPlayer -> currentPlayer.getName().equalsIgnoreCase(currentCompletion))
            );
            newCompletions.addAll(StringUtil.copyPartialMatches(
                    textToComplete,
                    playerNames,
                    new ArrayList<>()
            ));
        }

        Collections.sort(newCompletions);

        return newCompletions.toArray(new String[0]);
    }

}
