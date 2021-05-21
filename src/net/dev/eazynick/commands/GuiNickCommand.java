package net.dev.eazynick.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.utilities.Utils;

public class GuiNickCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(player.hasPermission("nick.use")) {
				NickManager api = new NickManager(player);
				
				if(!(api.isNicked())) {
					if(args.length >= 3)
						utils.performRankedNick(player, args[0], args[1], args[2]);
				}
			} else
				eazyNick.getLanguageYamlFile().sendMessage(player, utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}

}