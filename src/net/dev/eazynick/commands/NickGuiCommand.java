package net.dev.eazynick.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utils.LanguageFileUtils;
import net.dev.eazynick.utils.Utils;

public class NickGuiCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
		
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.gui")) {
				Inventory inv = Bukkit.createInventory(null, 27, languageFileUtils.getConfigString("NickGUI.InventoryTitle"));
				
				for (int i = 0; i < inv.getSize(); i++)
					inv.setItem(i, utils.createItem(Material.getMaterial((eazyNick.getVersion().startsWith("1_13") || eazyNick.getVersion().startsWith("1_14") || eazyNick.getVersion().startsWith("1_15")) ? "BLACK_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE"), 1, 0, "§8"));
				
				inv.setItem(11, utils.createItem(Material.NAME_TAG, 1, 0, languageFileUtils.getConfigString("NickGUI.NickItem.DisplayName")));
				inv.setItem(15, utils.createItem(Material.getMaterial(eazyNick.getVersion().startsWith("1_7") ? "GLASS" : "BARRIER"), 1, 0, languageFileUtils.getConfigString("NickGUI.UnnickItem.DisplayName")));
				
				p.openInventory(inv);
			} else
				p.sendMessage(utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}

}
