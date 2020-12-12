package net.dev.eazynick.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.PlayerUnnickEvent;
import net.dev.eazynick.sql.MySQLNickManager;
import net.dev.eazynick.utils.Utils;

public class ReNickCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
		
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(utils.getNickOnWorldChangePlayers().contains(p.getUniqueId()) || ((mysqlNickManager != null) && mysqlNickManager.isPlayerNicked(p.getUniqueId()))) {
				if(utils.getNickedPlayers().contains(p.getUniqueId()))
					Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(p));
				else if(!(eazyNick.getFileUtils().getConfig().getStringList("DisabledNickWorlds").contains(p.getWorld().getName())))
					utils.performReNick(p);
				else
					p.sendMessage(utils.getPrefix() + eazyNick.getLanguageFileUtils().getConfigString(p, "Messages.DisabledWorld"));
			}
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}
	
}
