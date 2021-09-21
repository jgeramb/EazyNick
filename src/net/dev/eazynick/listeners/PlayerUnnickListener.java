package net.dev.eazynick.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.api.PlayerUnnickEvent;
import net.dev.eazynick.hooks.LuckPermsHook;
import net.dev.eazynick.hooks.TABHook;
import net.dev.eazynick.utilities.AsyncTask;
import net.dev.eazynick.utilities.AsyncTask.AsyncRunnable;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.*;

import me.clip.placeholderapi.PlaceholderAPI;

public class PlayerUnnickListener implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerUnnick(PlayerUnnickEvent event) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		SavedNickDatasYamlFile savedNickDatasYamlFile = eazyNick.getSavedNickDatasYamlFile();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		
		if(!(event.isCancelled())) {
			Player player = event.getPlayer();
			NickManager api = new NickManager(player);
			String nickName = api.getNickName(), name = api.getRealName(), uniqueIdString = player.getUniqueId().toString().replace("-", "");
	
			if(setupYamlFile.getConfiguration().getBoolean("NickCommands.OnUnnick")) {
				if(utils.isPluginInstalled("PlaceholderAPI"))
					setupYamlFile.getConfiguration().getStringList("NickCommands.Unnick").forEach(command -> Bukkit.dispatchCommand(setupYamlFile.getConfiguration().getBoolean("NickCommands.SendAsConsole") ? Bukkit.getConsoleSender() : player, PlaceholderAPI.setPlaceholders(player, command.replace("%player%", player.getName()).replace("%nickName%", nickName))));
				else
					setupYamlFile.getConfiguration().getStringList("NickCommands.Unnick").forEach(command -> Bukkit.dispatchCommand(setupYamlFile.getConfiguration().getBoolean("NickCommands.SendAsConsole") ? Bukkit.getConsoleSender() : player, command.replace("%player%", player.getName()).replace("%nickName%", nickName)));
			}
			
			if(savedNickDatasYamlFile.getConfiguration().contains(uniqueIdString))
				savedNickDatasYamlFile.getConfiguration().set(uniqueIdString, null);
			
			api.unnickPlayer();
			
			if(utils.isPluginInstalled("LuckPerms"))
				new LuckPermsHook(player).resetNodes();
			
			if(utils.isPluginInstalled("TAB", "NEZNAMY") && setupYamlFile.getConfiguration().getBoolean("ChangeNameAndPrefixAndSuffixInTAB")) {
				new AsyncTask(new AsyncRunnable() {

					@Override
					public void run() {
						new TABHook(player).reset();
					}
				}, 400 + (setupYamlFile.getConfiguration().getBoolean("RandomDisguiseDelay") ? 2000 : 0)).run();
			}
			
			if(setupYamlFile.getConfiguration().getBoolean("LogNicknames"))
				utils.sendConsole("§a" + nickName + " §8(" + player.getUniqueId().toString() + ") §7reset his nickname to §d" + name);
			
			languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.Unnick").replace("%prefix%", utils.getPrefix()));
			
			if(setupYamlFile.getConfiguration().getBoolean("NickMessage.OnUnnick")) {
				Bukkit.getOnlinePlayers().forEach(currentPlayer -> {
					languageYamlFile.sendMessage(currentPlayer, setupYamlFile.getConfigString(player, "NickMessage.Unnick.Quit").replace("%displayName%", player.getDisplayName()).replace("%displayname%", player.getDisplayName()).replace("%name%", nickName));
					languageYamlFile.sendMessage(currentPlayer, setupYamlFile.getConfigString(player, "NickMessage.Unnick.Join").replace("%displayName%", player.getDisplayName()).replace("%displayname%", player.getDisplayName()).replace("%name%", name));
				});
			}
		}
	}

}
