package com.justixdev.eazynick.listeners;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.api.PlayerUnnickEvent;
import com.justixdev.eazynick.hooks.LuckPermsHook;
import com.justixdev.eazynick.hooks.TABHook;
import com.justixdev.eazynick.utilities.AsyncTask;
import com.justixdev.eazynick.utilities.AsyncTask.AsyncRunnable;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.SavedNickDataYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerUnnickListener implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerUnnick(PlayerUnnickEvent event) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		SavedNickDataYamlFile savedNickDataYamlFile = eazyNick.getsavedNickDataYamlFile();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		
		if(!(event.isCancelled())) {
			Player player = event.getPlayer();
			NickManager api = new NickManager(player);
			String nickName = api.getNickName(), name = api.getRealName(), oldDisplayName = api.getOldDisplayName(), uniqueIdString = player.getUniqueId().toString().replace("-", "");
	
			if(setupYamlFile.getConfiguration().getBoolean("NickCommands.OnUnnick")) {
				if(utils.isPluginInstalled("PlaceholderAPI"))
					setupYamlFile.getConfiguration().getStringList("NickCommands.Unnick").forEach(command -> Bukkit.dispatchCommand(setupYamlFile.getConfiguration().getBoolean("NickCommands.SendAsConsole") ? Bukkit.getConsoleSender() : player, PlaceholderAPI.setPlaceholders(player, command.replace("%player%", player.getName()).replace("%nickName%", nickName))));
				else
					setupYamlFile.getConfiguration().getStringList("NickCommands.Unnick").forEach(command -> Bukkit.dispatchCommand(setupYamlFile.getConfiguration().getBoolean("NickCommands.SendAsConsole") ? Bukkit.getConsoleSender() : player, command.replace("%player%", player.getName()).replace("%nickName%", nickName)));
			}
			
			if(savedNickDataYamlFile.getConfiguration().contains(uniqueIdString))
				savedNickDataYamlFile.getConfiguration().set(uniqueIdString, null);
			
			api.unnickPlayer();
			
			if(utils.isPluginInstalled("LuckPerms"))
				new LuckPermsHook(player).resetNodes();
			
			if(utils.isPluginInstalled("TAB", "NEZNAMY") && setupYamlFile.getConfiguration().getBoolean("ChangeGroupAndPrefixAndSuffixInTAB")) {
				new AsyncTask(new AsyncRunnable() {

					@Override
					public void run() {
						new TABHook(player).reset();
					}
				}, 400 + (setupYamlFile.getConfiguration().getBoolean("RandomDisguiseDelay") ? 2000 : 0)).run();
			}
			
			if(setupYamlFile.getConfiguration().getBoolean("LogNicknames"))
				utils.sendConsole("§a" + nickName + " §8(" + player.getUniqueId() + ") §7reset his nickname to §d" + name);

			if(setupYamlFile.getConfiguration().getBoolean("NickMessage.OnUnnick"))
				Bukkit.getOnlinePlayers().forEach(currentPlayer -> languageYamlFile.sendMessage(currentPlayer, setupYamlFile.getConfigString(player, "NickMessage.Unnick.Quit").replace("%displayName%", player.getDisplayName()).replace("%displayname%", player.getDisplayName()).replace("%name%", nickName)));
			
			languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.Unnick").replace("%prefix%", utils.getPrefix()));
			
			new AsyncTask(new AsyncRunnable() {
				
				@Override
				public void run() {
					if(setupYamlFile.getConfiguration().getBoolean("NickMessage.OnUnnick"))
						Bukkit.getOnlinePlayers().forEach(currentPlayer -> languageYamlFile.sendMessage(currentPlayer, setupYamlFile.getConfigString(player, "NickMessage.Unnick.Join").replace("%displayName%", oldDisplayName).replace("%displayname%", oldDisplayName).replace("%name%", name)));
				}
			}, 50L * 3).run();
		}
	}

}
