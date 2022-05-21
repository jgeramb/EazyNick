package com.justixdev.eazynick.commands;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.api.PlayerUnnickEvent;
import com.justixdev.eazynick.sql.MySQLNickManager;
import com.justixdev.eazynick.sql.MySQLPlayerDataManager;
import com.justixdev.eazynick.utilities.GUIManager;
import com.justixdev.eazynick.utilities.StringUtils;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class NickCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		GUIManager guiManager = eazyNick.getGUIManager();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
		MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMySQLPlayerDataManager();
		
		String prefix = utils.getPrefix();
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			
			if(new NickManager(player).isNicked()) {
				if(player.hasPermission("eazynick.nick.reset"))
					Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(player));
			} else if((mysqlNickManager != null) && mysqlNickManager.isPlayerNicked(player.getUniqueId()) && setupYamlFile.getConfiguration().getBoolean("LobbyMode") && setupYamlFile.getConfiguration().getBoolean("RemoveMySQLNickOnUnnickWhenLobbyModeEnabled")) {
				if(player.hasPermission("eazynick.nick.reset")) {
					mysqlNickManager.removePlayer(player.getUniqueId());
					mysqlPlayerDataManager.removeData(player.getUniqueId());
					
					languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.Unnick").replace("%prefix%", prefix));
				}
			} else if(player.hasPermission("eazynick.nick.random") || player.hasPermission("eazynick.nick.custom")) {
				if(utils.getCanUseNick().getOrDefault(player.getUniqueId(), System.currentTimeMillis()) <= System.currentTimeMillis()) {
					if(setupYamlFile.getConfiguration().getBoolean("OpenBookGUIOnNickCommand") && !(eazyNick.getVersion().startsWith("1_7"))) {
						if(!(player.hasPermission("eazynick.gui.book"))) {
							PermissionAttachment pa = player.addAttachment(eazyNick);
							pa.setPermission("eazynick.gui.book", true);
							player.recalculatePermissions();
							
							player.chat("/bookgui");
							
							player.removeAttachment(pa);
							player.recalculatePermissions();
						} else
							player.chat("/bookgui");
					} else if(setupYamlFile.getConfiguration().getBoolean("OpenNickListGUIOnNickCommand"))
						guiManager.openNickList(player, 0);
					else if(setupYamlFile.getConfiguration().getBoolean("OpenRankedNickGUIOnNickCommand"))
						guiManager.openRankedNickGUI(player, "");
					else if(args.length == 0) {
						if(player.hasPermission("eazynick.nick.random")) {
							if(!(setupYamlFile.getConfiguration().getStringList("DisabledNickWorlds").contains(player.getWorld().getName())))
								utils.performNick(player, "RANDOM");
							else
								languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.DisabledWorld").replace("%prefix%", prefix));
						} else
							languageYamlFile.sendMessage(player, utils.getNoPerm());
					} else if(player.hasPermission("eazynick.nick.custom")) {
						String name = args[0].replace("\"", ""), nameWithoutColors = new StringUtils(name).getPureString();
						int nameLengthMin = Math.max(Math.min(setupYamlFile.getConfiguration().getInt("Settings.NameLength.Min"), 16), 1), nameLengthMax = Math.max(Math.min(setupYamlFile.getConfiguration().getInt("Settings.NameLength.Max"), 16), 1);
						
						if(nameWithoutColors.length() <= nameLengthMax) {
							if(nameWithoutColors.length() >= nameLengthMin) {
								if(!(utils.containsSpecialChars(nameWithoutColors)) || setupYamlFile.getConfiguration().getBoolean("AllowSpecialCharactersInCustomName")) {
									if(!(utils.containsBlackListEntry(args[0]))) {
										if(utils.getNickedPlayers().values().stream().noneMatch(currentNickedPlayerData -> currentNickedPlayerData.getNickName().equalsIgnoreCase(name)) || setupYamlFile.getConfiguration().getBoolean("AllowPlayersToUseSameNickName")) {
											if(!(setupYamlFile.getConfiguration().getBoolean("AllowPlayersToNickAsKnownPlayers")) && (Bukkit.getOnlinePlayers().stream().anyMatch(currentPlayer -> currentPlayer.getName().equalsIgnoreCase(name)) || Stream.of(Bukkit.getOfflinePlayers()).anyMatch(currentOfflinePlayer -> name.equalsIgnoreCase(currentOfflinePlayer.getName())))) {
												if(!(name.equalsIgnoreCase(player.getName()))) {
													if(!(setupYamlFile.getConfiguration().getStringList("DisabledNickWorlds").contains(player.getWorld().getName())))
														utils.performNick(player, ChatColor.translateAlternateColorCodes('&', eazyNick.getVersion().equals("1_7_R4") ? eazyNick.getUUIDFetcher_1_7().getName(name, eazyNick.getUUIDFetcher_1_7().getUUID(name)) : (eazyNick.getVersion().equals("1_8_R1") ? eazyNick.getUUIDFetcher_1_8_R1().getName(name, eazyNick.getUUIDFetcher_1_8_R1().getUUID(name)) : eazyNick.getUUIDFetcher().getName(name, eazyNick.getUUIDFetcher().getUUID(name)))));
													else
														languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.DisabledWorld").replace("%prefix%", prefix));
												} else
													languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.CanNotNickAsSelf").replace("%prefix%", prefix));
											} else
												languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.PlayerWithThisNameIsKnown").replace("%prefix%", prefix));
										} else
											languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.NickNameAlreadyInUse").replace("%prefix%", prefix));
									} else
										languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.NameNotAllowed").replace("%prefix%", prefix));
								} else
									languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.NickContainsSpecialCharacters").replace("%prefix%", prefix));
							} else
								languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.NickTooShort").replace("%prefix%", prefix));
						} else
							languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.NickTooLong").replace("%prefix%", prefix));
					} else
						languageYamlFile.sendMessage(player, utils.getNoPerm());
				} else
					languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.NickDelay").replace("%prefix%", prefix));
			} else
				languageYamlFile.sendMessage(player, utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}
	
}
