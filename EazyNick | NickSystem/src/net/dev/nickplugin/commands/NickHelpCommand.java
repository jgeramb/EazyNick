package net.dev.nickplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import net.dev.nickplugin.main.Main;
import net.dev.nickplugin.utils.Utils;

public class NickHelpCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			PluginDescriptionFile desc = Main.getPlugin(Main.class).getDescription();
			
			if(p.hasPermission("nick.showHelp") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.showHelp")) {
				p.sendMessage(Utils.PREFIX + "§7===== §8[ §5" + desc.getName() + " §8] §7=====");
				
				if(args.length == 0) {
					p.sendMessage(Utils.PREFIX + "§7/nick §8» §eRandom nick");
					p.sendMessage(Utils.PREFIX + "§7/nick [name] §8» §eStated nick");
					p.sendMessage(Utils.PREFIX + "§7/unnick §8» §eReset nick");
					p.sendMessage(Utils.PREFIX + "§7/realname [player] §8» §eReal name of player");
					p.sendMessage(Utils.PREFIX + "§7/nickother [player] §8» §eRandom nick for stated player");
					p.sendMessage(Utils.PREFIX + "§7/nickother [player] [name] §8» §eStated nick for stated player");
					p.sendMessage(Utils.PREFIX + "§7");
					p.sendMessage(Utils.PREFIX + "§aMore help§8: §7/eazynick 2");
				} else {
					if(args[0].equalsIgnoreCase("1")) {
						p.sendMessage(Utils.PREFIX + "§7/nick §8» §eRandom nickname");
						p.sendMessage(Utils.PREFIX + "§7/nick [name] §8» §eStated nickname");
						p.sendMessage(Utils.PREFIX + "§7/nickgui §8» §eGUI for (un)nicking");
						p.sendMessage(Utils.PREFIX + "§7/nicklist §8» §eGUI with skulls to nick");
						p.sendMessage(Utils.PREFIX + "§7/nickedplayers §8» §eList of current nicked players");
						p.sendMessage(Utils.PREFIX + "§7/bookgui §8» §eNickGUI like HyPixel");
						p.sendMessage(Utils.PREFIX + "§7");
						p.sendMessage(Utils.PREFIX + "§aMore help§8: §7/eazynick 2");
					} else if(args[0].equalsIgnoreCase("2")) {
						p.sendMessage(Utils.PREFIX + "§7/name §8» §eShows current nickname");
						p.sendMessage(Utils.PREFIX + "§7/fixskin §8» §eFixes your skin");
						p.sendMessage(Utils.PREFIX + "§7/nickedplayers §8» §eList of current nicked players");
						p.sendMessage(Utils.PREFIX + "§7/nickgui §8» §eGUI for (un)nicking");
						p.sendMessage(Utils.PREFIX + "§7/nicklist §8» §eGUI with skulls to nick");
						p.sendMessage(Utils.PREFIX + "§7/bookgui §8» §eNickGUI like HyPixel");
						p.sendMessage(Utils.PREFIX + "§7");
						p.sendMessage(Utils.PREFIX + "§aMore help§8: §7/eazynick 3");
					} else if(args[0].equalsIgnoreCase("3")) {
						p.sendMessage(Utils.PREFIX + "§7/changename §8» §eRandom name");
						p.sendMessage(Utils.PREFIX + "§7/changename [name] §8» §eStated name");
						p.sendMessage(Utils.PREFIX + "§7/resetname §8» §eReset name");
						p.sendMessage(Utils.PREFIX + "§7/changeskin §8» §eRandom skin");
						p.sendMessage(Utils.PREFIX + "§7/changeskin [name] §8» §eStated skin");
						p.sendMessage(Utils.PREFIX + "§7/resetskin §8» §eReset skin");
						p.sendMessage(Utils.PREFIX + "§7");
						p.sendMessage(Utils.PREFIX + "§aMore help§8: §7/eazynick 4");
					} else if(args[0].equalsIgnoreCase("4")) {
						p.sendMessage(Utils.PREFIX + "§7/nickupdatecheck §8» §eUpdate check");
						p.sendMessage(Utils.PREFIX + "§7/reloadconfig §8» §eConfig reload");
						p.sendMessage(Utils.PREFIX + "§7/eazynick [1-4] §8» §ePlugin help");
					} else {
						p.sendMessage(Utils.PREFIX + "§cPage not found!");
					}
				}
			} else {
				p.sendMessage(Utils.PREFIX + "§7===== §8[ §5" + desc.getName() + " §8] §7=====");
				p.sendMessage(Utils.PREFIX + "§7");
				p.sendMessage(Utils.PREFIX + "§5" + desc.getName() + " §7Version§8: §e" + desc.getVersion());
				p.sendMessage(Utils.PREFIX + "§7Plugin by§8: §e" + desc.getAuthors());
				p.sendMessage(Utils.PREFIX + "§7Resource page§8: §e" + desc.getWebsite());
			}
		} else {
			Utils.sendConsole(Utils.NOT_PLAYER);
		}
		
		return true;
	}
	
}
