package net.dev.eazynick.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.LanguageYamlFile;

public class HelpCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		
		String prefix = utils.getPrefix();
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			PluginDescriptionFile desc = eazyNick.getDescription();

			if(player.hasPermission("nick.showhelp")) {
				if((args.length == 0) || args[0].equalsIgnoreCase("1")) {
					player.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
					player.sendMessage(prefix);
					player.sendMessage(prefix + "§7/nick §8» §aRandom nick");
					player.sendMessage(prefix + "§7/nick [name] §8» §aStated nick");
					player.sendMessage(prefix + "§7/unnick §8» §aReset nick");
					player.sendMessage(prefix + "§7/realname [player] §8» §aReal name of player");
					player.sendMessage(prefix + "§7/nickother [player] §8» §aRandom nick for stated player");
					player.sendMessage(prefix + "§7/nickother [player] [name] §8» §aStated nick for stated player");
					player.sendMessage(prefix);
					player.sendMessage(prefix + "§7More help§8: §a/eazynick 2");
					player.sendMessage(prefix);
					player.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
				} else {
					if(args[0].equalsIgnoreCase("2")) {
						player.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
						player.sendMessage(prefix);
						player.sendMessage(prefix + "§7/name §8» §aShows current nickname");
						player.sendMessage(prefix + "§7/fixskin §8» §aFixes your skin");
						player.sendMessage(prefix + "§7/nickedplayers §8» §aList of current nicked players");
						player.sendMessage(prefix + "§7/nickgui §8» §aGUI for (un)nicking");
						player.sendMessage(prefix + "§7/nicklist §8» §aGUI with skulls to nick");
						player.sendMessage(prefix + "§7/bookgui §8» §aNickGUI like Hypixel");
						player.sendMessage(prefix);
						player.sendMessage(prefix + "§7More help§8: §a/eazynick 3");
						player.sendMessage(prefix);
						player.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
					} else if(args[0].equalsIgnoreCase("3")) {
						player.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
						player.sendMessage(prefix);
						player.sendMessage(prefix + "§7/resetname §8» §aReset name");
						player.sendMessage(prefix + "§7/changeskin §8» §aRandom skin");
						player.sendMessage(prefix + "§7/changeskin [name] §8» §aStated skin");
						player.sendMessage(prefix + "§7/resetskin §8» §aReset skin");
						player.sendMessage(prefix + "§7/nickupdatecheck §8» §aUpdate check");
						player.sendMessage(prefix + "§7/reloadconfig §8» §aConfig reload");
						player.sendMessage(prefix);
						player.sendMessage(prefix + "§7/eazynick [1-3] §8» §aPlugin help");
						player.sendMessage(prefix);
						player.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
					} else if(args[0].equalsIgnoreCase("reload") && player.hasPermission("nick.reload")) {
						utils.reloadConfigs();
						
						languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.ReloadConfig").replace("%prefix%", prefix));
					} else
						player.sendMessage(prefix + "§cPage §a" + args[0] + " §ccould not be found (available: 1, 2, 3)");
				}
			} else {
				player.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
				player.sendMessage(prefix);
				player.sendMessage(prefix + "§7Version§8: §a" + desc.getVersion());
				player.sendMessage(prefix + "§7Authors§8: §a" + desc.getAuthors().toString().replace("[", "").replace("]", ""));
				player.sendMessage(prefix + "§7Resource page§8: §a" + desc.getWebsite() + " §7or §ahttps://www.justix-dev.de/go/project?id=1");
				player.sendMessage(prefix);
				player.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
			}
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}
	
}
