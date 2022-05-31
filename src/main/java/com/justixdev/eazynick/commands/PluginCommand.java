package com.justixdev.eazynick.commands;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickedPlayerData;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class PluginCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();

		String prefix = utils.getPrefix();
		PluginDescriptionFile desc = eazyNick.getDescription();

		if(!(sender.hasPermission("eazynick.help"))) {
			sender.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
			sender.sendMessage(prefix);
			sender.sendMessage(prefix + "§7Version§8: §a" + desc.getVersion());
			sender.sendMessage(prefix + "§7Authors§8: §a" + desc.getAuthors().toString().replace("[", "").replace("]", ""));
			sender.sendMessage(prefix + "§7Resource page§8: §a" + desc.getWebsite() + " §7or §ahttps://www.justix-dev.de/go/project?id=1");
			sender.sendMessage(prefix);
			sender.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
			return true;
		}

		if((args.length == 0) || args[0].equalsIgnoreCase("1")) {
			sender.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
			sender.sendMessage(prefix);
			sender.sendMessage(prefix + "§7/eazynick support §8» §aSupport tutorial");
			sender.sendMessage(prefix + "§7/nickupdatecheck §8» §aUpdate check");
			sender.sendMessage(prefix + "§7/reloadconfig §8» §aConfig reload");
			sender.sendMessage(prefix + "§7/nick §8» §aRandom nick");
			sender.sendMessage(prefix + "§7/nick [name] §8» §aStated nick");
			sender.sendMessage(prefix + "§7/unnick §8» §aReset nick");
			sender.sendMessage(prefix + "§7/realname [player] §8» §aReal name of player");
			sender.sendMessage(prefix + "§7/name §8» §aShows current nickname");
			sender.sendMessage(prefix);
			sender.sendMessage(prefix + "§7More help§8: §a/eazynick 2");
			sender.sendMessage(prefix);
			sender.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
		} else {
			if(args[0].equalsIgnoreCase("2")) {
				sender.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
				sender.sendMessage(prefix);
				sender.sendMessage(prefix + "§7/nickother [player] §8» §aRandom nick for stated player");
				sender.sendMessage(prefix + "§7/nickother [player] [name] §8» §aStated nick for stated player");
				sender.sendMessage(prefix + "§7/fixskin §8» §aFixes your skin");
				sender.sendMessage(prefix + "§7/nickedplayers §8» §aList of current nicked players");
				sender.sendMessage(prefix + "§7/nickgui §8» §aGUI for (un)nicking");
				sender.sendMessage(prefix + "§7/nicklist §8» §aGUI with skulls to nick");
				sender.sendMessage(prefix + "§7/bookgui §8» §aNickGUI like Hypixel");
				sender.sendMessage(prefix + "§7/resetname §8» §aReset name");
				sender.sendMessage(prefix);
				sender.sendMessage(prefix + "§7More help§8: §a/eazynick 3");
				sender.sendMessage(prefix);
				sender.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
			} else if(args[0].equalsIgnoreCase("3")) {
				sender.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
				sender.sendMessage(prefix);
				sender.sendMessage(prefix + "§7/changeskin §8» §aRandom skin");
				sender.sendMessage(prefix + "§7/changeskin [name] §8» §aStated skin");
				sender.sendMessage(prefix + "§7/changeskinother [player] §8» §aRandom skin for stated player");
				sender.sendMessage(prefix + "§7/changeskinother [player] [name] §8» §aStated skin for stated player");
				sender.sendMessage(prefix + "§7/resetskin §8» §aReset skin");
				sender.sendMessage(prefix + "§7/resetskinother §8» §aReset skin of stated player");
				sender.sendMessage(prefix + "§7/eazynick debug [player] §8» §aGet information about a player");
				sender.sendMessage(prefix);
				sender.sendMessage(prefix + "§7/eazynick [1-3] §8» §aPlugin help");
				sender.sendMessage(prefix);
				sender.sendMessage(prefix + "§7§m-----§8 [ §5" + desc.getName() + " §8] §7§m-----");
			} else if(args[0].equalsIgnoreCase("debug") && sender.hasPermission("eazynick.debug")) {
				if(args.length >= 2) {
					Player targetPlayer = Bukkit.getPlayer(args[1]);

					if(targetPlayer != null) {
						sender.sendMessage(prefix + "§7§m-----§8 [ §5Debug Info §8] §7§m-----");
						sender.sendMessage(prefix);
						sender.sendMessage(prefix + "§5§lPlayer details");

						NickedPlayerData nickedPlayerData = utils.getNickedPlayers().get(targetPlayer.getUniqueId());

						if(nickedPlayerData != null) {
							sender.sendMessage(prefix + "§8┣ §7Nicked §8» §aYes");
							sender.sendMessage(prefix + "§8┣ §7Real name §8» §a" + nickedPlayerData.getRealName());
							sender.sendMessage(prefix + "§8┣ §7Nickname §8» §a" + nickedPlayerData.getNickName());
							sender.sendMessage(prefix + "§8┣ §7Skin §8» §a" + nickedPlayerData.getSkinName());
							sender.sendMessage(prefix + "§8┣ §7UUID §8» §8'§f" + nickedPlayerData.getUniqueId() + "§8'");
							sender.sendMessage(prefix + "§8┣ §7Spoofed UUID §8» §8'§f" + nickedPlayerData.getSpoofedUniqueId() + "§8'");
							sender.sendMessage(prefix + "§8┣ §7Chat prefix §8» §8'§f" + nickedPlayerData.getChatPrefix().replace("§", "&") + "§8'");
							sender.sendMessage(prefix + "§8┣ §7Chat suffix §8» §8'§f" + nickedPlayerData.getChatSuffix().replace("§", "&") + "§8'");
							sender.sendMessage(prefix + "§8┣ §7Tab prefix §8» §8'§f" + nickedPlayerData.getTabPrefix().replace("§", "&") + "§8'");
							sender.sendMessage(prefix + "§8┣ §7Tab suffix §8» §8'§f" + nickedPlayerData.getTabSuffix().replace("§", "&") + "§8'");
							sender.sendMessage(prefix + "§8┣ §7Tag prefix §8» §8'§f" + nickedPlayerData.getTagPrefix().replace("§", "&") + "§8'");
							sender.sendMessage(prefix + "§8┣ §7Tag suffix §8» §8'§f" + nickedPlayerData.getTagSuffix().replace("§", "&") + "§8'");
							sender.sendMessage(prefix + "§8┣ §7Group name §8» §8'§f" + nickedPlayerData.getGroupName() + "§8'");
							sender.sendMessage(prefix + "§8┣ §7SortID §8» §a" + nickedPlayerData.getSortID());
							sender.sendMessage(prefix + "§8┣ §7Old chat name §8» §8'§f" + nickedPlayerData.getOldDisplayName().replace("§", "&") + "§8'");
							sender.sendMessage(prefix + "§8┗ §7Old tab name §8» §8'§f" + nickedPlayerData.getOldPlayerListName().replace("§", "&") + "§8'");
						} else
							sender.sendMessage(prefix + "§8┗  §7Nicked §8» §cNo");

						sender.sendMessage(prefix);
						sender.sendMessage(prefix + "§7§m-----§8 [ §5Debug Info §8] §7§m-----");
					} else
						languageYamlFile.sendMessage(sender, languageYamlFile.getConfigString(null, "Messages.PlayerNotFound").replace("%prefix%", prefix));
				} else
					sender.sendMessage(prefix + "§cYou need to enter a player name");
			} else if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("eazynick.reload")) {
				utils.reloadConfigs();

				languageYamlFile.sendMessage(sender, languageYamlFile.getConfigString("Messages.ReloadConfig").replace("%prefix%", prefix));
			} else if(args[0].equalsIgnoreCase("support") && sender.hasPermission("eazynick.support")) {
				if(utils.isSupportMode()) {
					utils.setSupportMode(false);

					sender.sendMessage(prefix + "§cSupport mode was disabled");
				} else {
					utils.setSupportMode(true);

					sender.sendMessage(prefix + "§7§m-----§8 [ §5EazyNick Support §8] §7§m-----");
					sender.sendMessage(prefix);

					if(sender instanceof Player) {
						if(eazyNick.getVersion().startsWith("1_17") || eazyNick.getVersion().startsWith("1_18"))
							((Player) sender).spigot().sendMessage(new ComponentBuilder(prefix + "§71. Join the discord server §8(discord.justix-dev.com)").event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.justix-dev.com/")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Collections.singletonList(new net.md_5.bungee.api.chat.hover.content.Text("§7Click here to open the discord invitation link")))).create());
						else
							//noinspection deprecation
							((Player) sender).spigot().sendMessage(new ComponentBuilder(prefix + "§71. Join the discord server §8(discord.justix-dev.com)").event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.justix-dev.com/")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Click here to open the discord invitation link"))).create());
					} else
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
					sender.sendMessage(prefix + "§8┣ §7BungeeCord §8» §a" + setupYamlFile.getConfiguration().getBoolean("BungeeCord"));
					sender.sendMessage(prefix + "§8┣ §7LobbyMode §8» §a" + setupYamlFile.getConfiguration().getBoolean("LobbyMode"));
					sender.sendMessage(prefix + "§8┣ §7Spawn protection §8» §a" + Bukkit.getSpawnRadius());

					StringBuilder plugins = new StringBuilder();

					for(Plugin plugin : Bukkit.getPluginManager().getPlugins())
						plugins.append(plugin.isEnabled() ? "§a" : "§c").append(plugin.getName()).append(" v").append(plugin.getDescription().getVersion()).append("§8, ");

					if(plugins.length() > 0)
						plugins = new StringBuilder(plugins.substring(0, plugins.length() - 4));

					sender.sendMessage(prefix + "§8┗ §7Plugins §8» §a" + plugins);
					sender.sendMessage(prefix);
					sender.sendMessage(prefix + "§7§m-----§8 [ §5EazyNick Support §8] §7§m-----");
				}
			} else
				sender.sendMessage(prefix + "§cPage §a" + args[0] + " §ccould not be found (available: 1, 2, 3)");
		}
		
		return true;
	}
	
}
