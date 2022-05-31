package com.justixdev.eazynick.listeners;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.api.NickedPlayerData;
import com.justixdev.eazynick.api.PlayerNickEvent;
import com.justixdev.eazynick.hooks.LuckPermsHook;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.SavedNickDataYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

public class PlayerNickListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerNick(PlayerNickEvent event) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		SavedNickDataYamlFile savedNickDataYamlFile = eazyNick.getsavedNickDataYamlFile();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		
		if(event.isCancelled()) return;

		Player player = event.getPlayer();
		NickManager api = new NickManager(player);
		boolean changePrefixAndSuffix = !(utils.getWorldsWithDisabledPrefixAndSuffix().contains(player.getWorld().getName().toUpperCase()));
		String realName = player.getName(),
				nickName = event.getNickName(),
				skinName = event.getSkinName(),
				tagPrefix = event.getTagPrefix(),
				tagSuffix = event.getTagSuffix(),
				chatPrefix = event.getChatPrefix(),
				chatSuffix = event.getChatSuffix(),
				tabPrefix = event.getTabPrefix(),
				tabSuffix = event.getTabSuffix(),
				groupName = event.getGroupName(),
				oldDisplayName = player.getDisplayName(),
				oldPlayerListName = player.getPlayerListName(),
				uniqueIdString = player.getUniqueId().toString().replace("-", "");
		UUID spoofedUniqueId = event.getSpoofedUniqueId();
		int sortID = event.getSortID();

		// Disallow nicking for 'Settings.NickDelay' seconds
		utils.getCanUseNick().put(
				player.getUniqueId(),
				System.currentTimeMillis() + (setupYamlFile.getConfiguration().getLong("Settings.NickDelay") * 1000L)
		);

		if(setupYamlFile.getConfiguration().getBoolean("BungeeCord")) {
			// Store nicked user data in MySQL database
			if(!(groupName.equals("NONE")))
				eazyNick.getMySQLPlayerDataManager().insertData(
						player.getUniqueId(),
						groupName,
						chatPrefix,
						chatSuffix,
						tabPrefix,
						tabSuffix,
						tagPrefix,
						tagSuffix
				);
		} else if(setupYamlFile.getConfiguration().getBoolean("SaveLocalNickData")
				&& !(setupYamlFile.getConfiguration().getBoolean("DisconnectUnnick"))) {
			// Store nicked user data in savedNickData.yml file
			savedNickDataYamlFile.getConfiguration().set(uniqueIdString + ".nick_name", nickName);
			savedNickDataYamlFile.getConfiguration().set(uniqueIdString + ".skin_name", skinName);
			savedNickDataYamlFile.getConfiguration().set(uniqueIdString + ".spoofed_uniqueid", spoofedUniqueId.toString());
			savedNickDataYamlFile.getConfiguration().set(uniqueIdString + ".chat_prefix", event.getChatPrefix());
			savedNickDataYamlFile.getConfiguration().set(uniqueIdString + ".chat_suffix", event.getChatSuffix());
			savedNickDataYamlFile.getConfiguration().set(uniqueIdString + ".tablist_prefix", event.getTabPrefix());
			savedNickDataYamlFile.getConfiguration().set(uniqueIdString + ".tablist_suffix", event.getTabSuffix());
			savedNickDataYamlFile.getConfiguration().set(uniqueIdString + ".nametag_prefix", event.getTagPrefix());
			savedNickDataYamlFile.getConfiguration().set(uniqueIdString + ".nametag_suffix", event.getTagSuffix());
			savedNickDataYamlFile.getConfiguration().set(uniqueIdString + ".group_name", groupName);
			savedNickDataYamlFile.getConfiguration().set(uniqueIdString + ".sort_id", sortID);
		}

		// Replace PlaceholderAPI placeholders;
		if(utils.isPluginInstalled("PlaceholderAPI")) {
			chatPrefix = PlaceholderAPI.setPlaceholders(player, chatPrefix);
			chatSuffix = PlaceholderAPI.setPlaceholders(player, chatSuffix);
		}

		// Update LuckPerms prefix, suffix and group
		if(changePrefixAndSuffix && utils.isPluginInstalled("LuckPerms"))
			new LuckPermsHook(player).updateNodes(chatPrefix, chatSuffix, groupName);

		if(setupYamlFile.getConfiguration().getBoolean("LogNicknames"))
			utils.sendConsole("§a" + realName + " §8(" + player.getUniqueId() + ") §7set his nickname to §d" + nickName);

		if(setupYamlFile.getConfiguration().getBoolean("NickMessage.OnNick"))
			Bukkit.getOnlinePlayers().forEach(currentPlayer -> languageYamlFile.sendMessage(
					currentPlayer,
					setupYamlFile.getConfigString(player, "NickMessage.Nick.Quit")
							.replace("%displayName%", oldDisplayName)
							.replace("%displayname%", oldDisplayName).replace("%name%", api.getRealName())
			));

		api.nickPlayer(nickName, skinName);

		if(changePrefixAndSuffix)
			api.updatePrefixSuffix(
					nickName,
					realName,
					event.getTagPrefix(),
					event.getTagSuffix(),
					event.getChatPrefix(),
					event.getChatSuffix(),
					event.getTabPrefix(),
					event.getTabSuffix(),
					sortID,
					groupName
			);
		else {
			if(setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.PlayerListName")) {
				String playerListName = player.getPlayerListName();

				api.setPlayerListName(playerListName.replace(realName, nickName));
			}

			if(setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.DisplayName")) {
				String displayName = player.getDisplayName();

				player.setDisplayName(displayName.replace(realName, nickName));
			}
		}

		utils.getNickedPlayers().put(
				player.getUniqueId(),
				new NickedPlayerData(
						player.getUniqueId(),
						(utils.isPluginInstalled("TAB", "NEZNAMY") && setupYamlFile.getConfiguration().getBoolean("ChangeGroupAndPrefixAndSuffixInTAB"))
								? player.getUniqueId()
								: spoofedUniqueId,
						changePrefixAndSuffix
								? oldDisplayName
								: "NONE",
						changePrefixAndSuffix
								? oldPlayerListName
								: "NONE",
						realName,
						nickName,
						skinName,
						event.getChatPrefix(),
						event.getChatSuffix(),
						event.getTabPrefix(),
						event.getTabSuffix(),
						event.getTagPrefix(),
						event.getTagSuffix(),
						groupName,
						sortID
				)
		);

		if(!(event.isRenick()))
			languageYamlFile.sendMessage(
					player,
					languageYamlFile.getConfigString(
							player,
							"Messages." + (
									event.isJoinNick()
											? "ActiveNick"
											: "Nick"
							)
					)
							.replace("%name%", nickName)
							.replace("%prefix%", utils.getPrefix())
			);

		if(setupYamlFile.getConfiguration().getBoolean("NickCommands.OnNick")) {
			if(utils.isPluginInstalled("PlaceholderAPI"))
				setupYamlFile.getConfiguration().getStringList("NickCommands.Nick")
						.forEach(command -> Bukkit.dispatchCommand(
								setupYamlFile.getConfiguration().getBoolean("NickCommands.SendAsConsole")
										? Bukkit.getConsoleSender()
										: player,
								PlaceholderAPI.setPlaceholders(
										player,
										command.replace("%player%", player.getName())
												.replace("%nickName%", nickName)
								)
						));
			else
				setupYamlFile.getConfiguration().getStringList("NickCommands.Nick")
						.forEach(command -> Bukkit.dispatchCommand(
								setupYamlFile.getConfiguration().getBoolean("NickCommands.SendAsConsole")
										? Bukkit.getConsoleSender()
										: player,
								command.replace("%player%", player.getName())
										.replace("%nickName%", nickName)
						));
		}

		if(setupYamlFile.getConfiguration().getBoolean("NickMessage.OnNick"))
			Bukkit.getOnlinePlayers().forEach(currentPlayer -> languageYamlFile.sendMessage(
					currentPlayer,
					setupYamlFile.getConfigString(player, "NickMessage.Nick.Join")
							.replace("%displayName%", player.getDisplayName())
							.replace("%displayname%", player.getDisplayName())
							.replace("%name%", nickName)
			));
	}

}
