package com.justixdev.eazynick.commands;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ChangeSkinCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();

		if(!(sender instanceof Player)) {
			utils.sendConsole(utils.getNotPlayer());
			return true;
		}

		String prefix = utils.getPrefix();
		Player player = (Player) sender;

		if(!(utils.getCanUseNick().getOrDefault(player.getUniqueId(), System.currentTimeMillis()) <= System.currentTimeMillis())) {
			languageYamlFile.sendMessage(
					player,
					languageYamlFile.getConfigString(player, "Messages.NickDelay")
							.replace("%prefix%", prefix)
			);
			return true;
		}

		NickManager api = new NickManager(player);

		if(args.length >= 1) {
			if(!(player.hasPermission("eazynick.skin.custom"))) {
				languageYamlFile.sendMessage(player, utils.getNoPerm());
				return true;
			}

			String name = args[0];

			api.changeSkin(name);

			languageYamlFile.sendMessage(
					player,
					languageYamlFile.getConfigString(player, "Messages.SkinChanged")
							.replace("%skinName%", name)
							.replace("%skinname%", name)
							.replace("%prefix%", prefix)
			);
		} else if(player.hasPermission("eazynick.skin.random")) {
			String name = setupYamlFile.getConfiguration().getBoolean("UseMineSkinAPI")
					? ("MINESKIN:" + utils.getRandomStringFromList(utils.getMineSkinUUIDs()))
					: utils.getNickNames().get((new Random().nextInt(utils.getNickNames().size())));

			api.changeSkin(name);

			languageYamlFile.sendMessage(
					player,
					languageYamlFile.getConfigString(player, "Messages.SkinChanged")
							.replace("%skinName%", name)
							.replace("%skinname%", name)
							.replace("%prefix%", prefix)
			);
		} else
			languageYamlFile.sendMessage(player, utils.getNoPerm());
		
		return true;
	}
	
}
