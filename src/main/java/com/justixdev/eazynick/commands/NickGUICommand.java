package com.justixdev.eazynick.commands;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.utilities.ItemBuilder;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.GUIYamlFile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class NickGUICommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		GUIYamlFile guiYamlFile = eazyNick.getGUIYamlFile();

		if(!(sender instanceof Player)) {
			utils.sendConsole(utils.getNotPlayer());
			return true;
		}

		Player player = (Player) sender;

		if(player.hasPermission("eazynick.gui.classic")) {
			Inventory inv = Bukkit.createInventory(
					null,
					27,
					guiYamlFile.getConfigString(player, "NickGUI.InventoryTitle")
			);

			for (int i = 0; i < inv.getSize(); i++)
				inv.setItem(
						i,
						new ItemBuilder(
							Material.getMaterial(utils.isVersion13OrLater()
									? "BLACK_STAINED_GLASS_PANE"
									: "STAINED_GLASS_PANE"
							),
							1,
							utils.isVersion13OrLater()
									? 0
									: 15
						).setDisplayName("§r").build()
				);

			inv.setItem(
					11,
					new ItemBuilder(Material.NAME_TAG)
							.setDisplayName(guiYamlFile.getConfigString(player, "NickGUI.Nick.DisplayName"))
							.build());
			inv.setItem(
					15,
					new ItemBuilder(Material.GLASS, 1, 14)
							.setDisplayName(guiYamlFile.getConfigString(player, "NickGUI.Unnick.DisplayName"))
							.build());

			player.openInventory(inv);
		} else
			eazyNick.getLanguageYamlFile().sendMessage(player, utils.getNoPerm());
		
		return true;
	}

}
