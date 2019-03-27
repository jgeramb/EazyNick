package net.dev.nickplugin.commands;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.nickplugin.sql.MySQLNickManager;
import net.dev.nickplugin.utils.FileUtils;
import net.dev.nickplugin.utils.LanguageFileUtils;
import net.dev.nickplugin.utils.Utils;

public class ToggleBungeeNickCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.use") && p.hasPermission("nick.item")) {
				if (FileUtils.cfg.getBoolean("BungeeCord")) {
					boolean hasItem = false;
					
					if(FileUtils.cfg.getBoolean("NeedItemToToggleNick")) {
						 if(!((p.getItemInHand() != null)
							&& (p.getItemInHand().getType() != Material.AIR && p.getItemInHand().getItemMeta() != null && p.getItemInHand().getItemMeta().getDisplayName() != null)
							&& (p.getItemInHand().getItemMeta().getDisplayName() .equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.BungeeCord.DisplayName.Disabled")))
							   || p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.BungeeCord.DisplayName.Enabled")))))) {
							 return true;
						 } else {
							 hasItem = true;
						 }
					}
					
					if(MySQLNickManager.isPlayerNicked(p.getUniqueId())) {
						MySQLNickManager.removePlayer(p.getUniqueId());
						
						if(hasItem) {
							p.getInventory().setItem(p.getInventory().getHeldItemSlot(),
									Utils.createItem(
											Material.getMaterial(
													FileUtils.cfg.getString("NickItem.ItemType.Disabled")),
											FileUtils.cfg.getInt("NickItem.ItemAmount.Disabled"),
											FileUtils.cfg.getInt("NickItem.MetaData.Disabled"),
											ChatColor.translateAlternateColorCodes('&',
													FileUtils.cfg.getString(
															"NickItem.BungeeCord.DisplayName.Disabled")),
											ChatColor.translateAlternateColorCodes('&',
													LanguageFileUtils.cfg.getString("NickItem.ItemLore.Disabled")
															.replace("&n", "\n")),
											FileUtils.cfg.getBoolean("NickItem.Enchanted.Disabled")));
						}
	
						p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&',
								LanguageFileUtils.cfg.getString("Messages.BungeeAutoNickDisabled")));
					} else {
						String name = Utils.nickNames.get((new Random().nextInt(Utils.nickNames.size())));
	
						MySQLNickManager.addPlayer(p.getUniqueId(), name);
						
						if(hasItem) {
							p.getInventory().setItem(p.getInventory().getHeldItemSlot(), Utils.createItem(
									Material.getMaterial(FileUtils.cfg.getString("NickItem.ItemType.Enabled")),
									FileUtils.cfg.getInt("NickItem.ItemAmount.Enabled"),
									FileUtils.cfg.getInt("NickItem.MetaData.Enabled"),
									ChatColor.translateAlternateColorCodes('&',
											LanguageFileUtils.cfg.getString("NickItem.BungeeCord.DisplayName.Enabled")),
									ChatColor.translateAlternateColorCodes('&',
											LanguageFileUtils.cfg.getString("NickItem.ItemLore.Enabled").replace("&n",
													"\n")),
									FileUtils.cfg.getBoolean("NickItem.Enchanted.Enabled")));
						}
	
						p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&',
								LanguageFileUtils.cfg.getString("Messages.BungeeAutoNickEnabled")));
					}
				}
			} else {
				p.sendMessage(Utils.NO_PERM);
			}
		} else {
			Utils.sendConsole(Utils.NOT_PLAYER);
		}
		
		return true;
	}
	
}
