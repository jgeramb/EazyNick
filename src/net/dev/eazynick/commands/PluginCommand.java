package net.dev.eazynick.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import net.md_5.bungee.api.chat.*;

public class PluginCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		
		String prefix = utils.getPrefix();
		
		PluginDescriptionFile desc = eazyNick.getDescription();

		if(sender.hasPermission("nick.showhelp")) {
			if((args.length == 0) || args[0].equalsIgnoreCase("1")) {
				sender.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
				sender.sendMessage(prefix);
				sender.sendMessage(prefix + "§7/eazynick support §8» §aSupport tutorial");
				sender.sendMessage(prefix + "§7/nickupdatecheck §8» §aUpdate check");
				sender.sendMessage(prefix + "§7/reloadconfig §8» §aConfig reload");
				sender.sendMessage(prefix + "§7/nick §8» §aRandom nick");
				sender.sendMessage(prefix + "§7/nick [name] §8» §aStated nick");
				sender.sendMessage(prefix + "§7/unnick §8» §aReset nick");
				sender.sendMessage(prefix + "§7/realname [sender] §8» §aReal name of sender");
				sender.sendMessage(prefix);
				sender.sendMessage(prefix + "§7More help§8: §a/eazynick 2");
				sender.sendMessage(prefix);
				sender.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
			} else {
				if(args[0].equalsIgnoreCase("2")) {
					sender.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
					sender.sendMessage(prefix);
					sender.sendMessage(prefix + "§7/name §8» §aShows current nickname");
					sender.sendMessage(prefix + "§7/nickother [sender] §8» §aRandom nick for stated sender");
					sender.sendMessage(prefix + "§7/nickother [sender] [name] §8» §aStated nick for stated sender");
					sender.sendMessage(prefix + "§7/fixskin §8» §aFixes your skin");
					sender.sendMessage(prefix + "§7/nickedsenders §8» §aList of current nicked senders");
					sender.sendMessage(prefix + "§7/nickgui §8» §aGUI for (un)nicking");
					sender.sendMessage(prefix + "§7/nicklist §8» §aGUI with skulls to nick");
					sender.sendMessage(prefix);
					sender.sendMessage(prefix + "§7More help§8: §a/eazynick 3");
					sender.sendMessage(prefix);
					sender.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
				} else if(args[0].equalsIgnoreCase("3")) {
					sender.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
					sender.sendMessage(prefix);
					sender.sendMessage(prefix + "§7/bookgui §8» §aNickGUI like Hypixel");
					sender.sendMessage(prefix + "§7/resetname §8» §aReset name");
					sender.sendMessage(prefix + "§7/changeskin §8» §aRandom skin");
					sender.sendMessage(prefix + "§7/changeskin [name] §8» §aStated skin");
					sender.sendMessage(prefix + "§7/resetskin §8» §aReset skin");
					sender.sendMessage(prefix);
					sender.sendMessage(prefix + "§7/eazynick [1-3] §8» §aPlugin help");
					sender.sendMessage(prefix);
					sender.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
				} else if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("nick.reload")) {
					utils.reloadConfigs();
					
					languageYamlFile.sendMessage(sender, languageYamlFile.getConfigString("Messages.ReloadConfig").replace("%prefix%", prefix));
				} else if(args[0].equalsIgnoreCase("support") && sender.hasPermission("nick.support")) {
					if(utils.isSupportMode()) {
						utils.setSupportMode(false);
						
						sender.sendMessage(prefix + "§cSupport mode was disabled");
					} else {
						utils.setSupportMode(true);
						
						sender.sendMessage(prefix + "§7§m-----§8 [ §5EazyNick Support §8] §7§m-----");
						sender.sendMessage(prefix);
						
						if(sender instanceof Player)
							((Player) sender).spigot().sendMessage(new ComponentBuilder(prefix + "§71. Join the discord server §8(discord.justix-dev.com)").event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.justix-dev.com/")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Click here to open the discord invitation link"))).create());
						else
							sender.sendMessage(prefix + "§71. Join the discord server §a(discord.justix-dev.com)");
						
						sender.sendMessage(prefix + "§72. Create a support ticket in the §a#create-tickets §7channel");
						sender.sendMessage(prefix + "§73. Fill out the format specified in the bot message");
						sender.sendMessage(prefix + "§74. Send a screenshot of the details below into the newly created ticket channel");
						sender.sendMessage(prefix);
						sender.sendMessage(prefix + "§5§lServer details");
						sender.sendMessage(prefix + "§8┣ §7Operating system §8» §a" + System.getProperty("os.name") + " (" + System.getProperty("os.version") + ")");
						sender.sendMessage(prefix + "§8┣ §7Java version §8» §a" + System.getProperty("java.version"));
						sender.sendMessage(prefix + "§8┣ §7Server version §8» §a" + Bukkit.getVersion());
						sender.sendMessage(prefix + "§8┣ §7Online mode §8» §a" + Bukkit.getOnlineMode());
						sender.sendMessage(prefix + "§8┣ §7BungeeCord §8» §a" + Bukkit.spigot().getConfig().getBoolean("settings.bungeecord"));
						sender.sendMessage(prefix + "§8┣ §7Spawn protection §8» §a" + Bukkit.getSpawnRadius());
						
						String plugins = "";
						
						for(Plugin plugin : Bukkit.getPluginManager().getPlugins())
							plugins += (plugin.isEnabled() ? "§a" : "§c") + plugin.getName() + " v" + plugin.getDescription().getVersion() + "§8, ";
						
						if(!(plugins.isEmpty()))
							plugins = plugins.substring(0, plugins.length() - 4);
						
						sender.sendMessage(prefix + "§8┗ §7Plugins §8» §a" + plugins);
						sender.sendMessage(prefix);
						sender.sendMessage(prefix + "§7§m-----§8 [ §5EazyNick Support §8] §7§m-----");
					}
				} else
					sender.sendMessage(prefix + "§cPage §a" + args[0] + " §ccould not be found (available: 1, 2, 3)");
			}
		} else {
			sender.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
			sender.sendMessage(prefix);
			sender.sendMessage(prefix + "§7Version§8: §a" + desc.getVersion());
			sender.sendMessage(prefix + "§7Authors§8: §a" + desc.getAuthors().toString().replace("[", "").replace("]", ""));
			sender.sendMessage(prefix + "§7Resource page§8: §a" + desc.getWebsite() + " §7or §ahttps://www.justix-dev.de/go/project?id=1");
			sender.sendMessage(prefix);
			sender.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
		}
		
		return true;
	}
	
}
