package net.dev.eazynick.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.sql.MySQLNickManager;
import net.dev.eazynick.sql.MySQLPlayerDataManager;
import net.dev.eazynick.utils.LanguageFileUtils;
import net.dev.eazynick.utils.Utils;

public class ResetNameCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.name")) {
				NickManager api = new NickManager(p);
				api.setName(api.getRealName());
				api.updatePlayer();

				MySQLPlayerDataManager.removeData(p.getUniqueId());
				MySQLNickManager.removePlayer(p.getUniqueId());
				
				p.sendMessage(Utils.prefix + LanguageFileUtils.getConfigString("Messages.ResetName"));
			} else
				p.sendMessage(Utils.noPerm);
		} else
			Utils.sendConsole(Utils.notPlayer);
		
		return true;
	}
	
}
