package net.dev.nickplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import net.dev.nickplugin.utils.LanguageFileUtils;
import net.dev.nickplugin.utils.Utils;

public class NickGuiCommand_1_7_R4 implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.gui") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.gui")) {
				Inventory inv = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickGUI.InventoryTitle")));
				
				for (int i = 0; i < inv.getSize(); i++) {
					inv.setItem(i, Utils.createItem(Material.getMaterial("STAINED_GLASS_PANE"), 1, 0, "§8"));
				}
				
				inv.setItem(11, Utils.createItem(Material.NAME_TAG, 1, 0, ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickGUI.NickItem.DisplayName"))));
				inv.setItem(15, Utils.createItem(Material.GLASS, 1, 0, ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickGUI.UnnickItem.DisplayName"))));
				
				p.openInventory(inv);
			} else {
				p.sendMessage(Utils.NO_PERM);
			}
		} else {
			Utils.sendConsole(Utils.NOT_PLAYER);
		}
		
		return true;
	}

}
