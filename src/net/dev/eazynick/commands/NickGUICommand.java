package net.dev.eazynick.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utilities.ItemBuilder;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.GUIYamlFile;

public class NickGUICommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		GUIYamlFile guiYamlFile = eazyNick.getGUIYamlFile();
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(player.hasPermission("nick.gui")) {
				Inventory inv = Bukkit.createInventory(null, 27, guiYamlFile.getConfigString(player, "NickGUI.InventoryTitle"));
				
				for (int i = 0; i < inv.getSize(); i++)
					inv.setItem(i, new ItemBuilder(Material.getMaterial(utils.isNewVersion() ? "BLACK_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE"), 1, utils.isNewVersion() ? 0 : 15).setDisplayName("§r").build());
				
				inv.setItem(11, new ItemBuilder(Material.NAME_TAG).setDisplayName(guiYamlFile.getConfigString(player, "NickGUI.Nick.DisplayName")).build());
				inv.setItem(15, new ItemBuilder(Material.GLASS, 1, 14).setDisplayName(guiYamlFile.getConfigString(player, "NickGUI.Unnick.DisplayName")).build());
				
				player.openInventory(inv);
			} else
				eazyNick.getLanguageYamlFile().sendMessage(player, utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}

}
