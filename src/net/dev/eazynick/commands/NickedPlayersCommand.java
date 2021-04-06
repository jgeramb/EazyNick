package net.dev.eazynick.commands;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.LanguageYamlFile;

public class NickedPlayersCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		
		String prefix = utils.getPrefix();
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(player.hasPermission("nick.nickedplayers")) {
				List<? extends Player> nickedPlayers = Bukkit.getOnlinePlayers().stream().filter(currentPlayer -> utils.getNickedPlayers().containsKey(currentPlayer.getUniqueId())).collect(Collectors.toList());
				
				if(!(nickedPlayers.isEmpty())) {
					languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.NickedPlayers.CurrentNickedPlayers").replace("%prefix%", prefix));
					
					for (Player currentNickedPlayer : nickedPlayers) {
						NickManager api = new NickManager(currentNickedPlayer);
						
						languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.NickedPlayers.PlayerInfo").replace("%realName%", api.getRealName()).replace("%realname%", api.getRealName()).replace("%nickName%", api.getNickName()).replace("%nickname%", api.getNickName()).replace("%prefix%", prefix));
					}
				} else
					languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.NickedPlayers.NoPlayerIsNicked").replace("%prefix%", prefix));
			} else
				languageYamlFile.sendMessage(player, utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}

}
