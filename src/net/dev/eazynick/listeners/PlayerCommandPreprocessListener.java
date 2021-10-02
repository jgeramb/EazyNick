package net.dev.eazynick.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.SetupYamlFile;

public class PlayerCommandPreprocessListener implements Listener {

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		
		Player player = event.getPlayer();
		String msg = event.getMessage();
		String[] args = msg.trim().split(" ");
		boolean doReplace = setupYamlFile.getConfiguration().getBoolean("OverwriteNamesInCommands"), allowRealName = setupYamlFile.getConfiguration().getBoolean("AllowRealNamesInCommands");
		
		for(String command : utils.getReplaceNameInCommandBlackList()) {
			if(args[0].equalsIgnoreCase("/" + command))
				doReplace = false;
		}
		
		if(doReplace) {
			utils.getNickedPlayers().values().forEach(nickedPlayerData -> {
				for (int i = 0; i < args.length; i++) {
					if(args[i].equalsIgnoreCase(nickedPlayerData.getNickName()))
						args[i] = nickedPlayerData.getRealName();
					else if(args[i].equalsIgnoreCase(nickedPlayerData.getRealName()) && !(allowRealName))
						args[i] = nickedPlayerData.getRealName() + "§r";
				}
			});
			
			msg = "";
			
			for (int i = 0; i < args.length; i++)
				msg += args[i] + " ";
			
			event.setMessage(msg.trim());
		}
		
		String msg2 = msg.toLowerCase().replace("bukkit:", "");
		
		if (msg2.startsWith("/help nick") || msg2.startsWith("/help eazynick") || msg2.startsWith("/? nick") || msg2.startsWith("/? eazynick")) {
			if (player.hasPermission("bukkit.command.help")) {
				event.setCancelled(true);

				player.sendMessage("§e--------- §fHelp: " + eazyNick.getDescription().getName() + " §e----------------------");
				player.sendMessage("§7Below is a list of all " + eazyNick.getDescription().getName() + " commands:");
				player.sendMessage("§6/eazynick: §f" + eazyNick.getCommand("eazynick").getDescription());
			}
		}
	}

}
