package com.justixdev.eazynick.commands.impl.gui;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.commands.Command;
import com.justixdev.eazynick.commands.CommandResult;
import com.justixdev.eazynick.commands.CustomCommand;
import com.justixdev.eazynick.commands.parameters.ParameterCombination;
import com.justixdev.eazynick.utilities.ItemBuilder;
import com.justixdev.eazynick.utilities.configuration.yaml.GUIYamlFile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import static com.justixdev.eazynick.nms.ReflectionHelper.VERSION_13_OR_LATER;

@CustomCommand(name = "nickgui", description = "Opens a GUI to change/reset your identity")
public class NickGUICommand extends Command {

	@Override
	protected void initAliases() {
		super.initAliases();

		this.aliases.add("dgui");
		this.aliases.add("disguisegui");
	}

	@Override
	public CommandResult execute(CommandSender sender, ParameterCombination args) {
		EazyNick eazyNick = EazyNick.getInstance();
		GUIYamlFile guiYamlFile = eazyNick.getGuiYamlFile();

		Player player = (Player) sender;

		if(!player.hasPermission("eazynick.gui.classic"))
			return CommandResult.FAILURE_NO_PERMISSION;

		Inventory inventory = Bukkit.createInventory(
				null,
				27,
				guiYamlFile.getConfigString(player, "NickGUI.InventoryTitle")
		);

		for (int i = 0; i < inventory.getSize(); i++)
			inventory.setItem(
					i,
					new ItemBuilder(
							Material.getMaterial(VERSION_13_OR_LATER
									? "BLACK_STAINED_GLASS_PANE"
									: "STAINED_GLASS_PANE"),
							1,
							VERSION_13_OR_LATER ? 0 : 15
					)
							.setDisplayName("§r")
							.build()
			);

		inventory.setItem(
				11,
				new ItemBuilder(Material.NAME_TAG)
						.setDisplayName(guiYamlFile.getConfigString(player, "NickGUI.Nick.DisplayName"))
						.build()
		);
		inventory.setItem(
				15,
				new ItemBuilder(Material.GLASS, 1, 14)
						.setDisplayName(guiYamlFile.getConfigString(player, "NickGUI.Unnick.DisplayName"))
						.build()
		);

		player.openInventory(inventory);

		return CommandResult.SUCCESS;
	}

}
