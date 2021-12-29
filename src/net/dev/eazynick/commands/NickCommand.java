package net.dev.eazynick.commands;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.*;
import net.dev.eazynick.sql.MySQLNickManager;
import net.dev.eazynick.sql.MySQLPlayerDataManager;
import net.dev.eazynick.utilities.*;
import net.dev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import net.dev.eazynick.utilities.configuration.yaml.SetupYamlFile;

public class NickCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
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
				if(utils.getCanUseNick().get(player.getUniqueId())) {
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
						String name = args[0].replace("\"", ""), nameWithoutColors = new StringUtils(name).removeColorCodes().getString();
						boolean isCancelled = false;
						int nameLengthMin = setupYamlFile.getConfiguration().getInt("Settings.NameLength.Min"), nameLengthMax = setupYamlFile.getConfiguration().getInt("Settings.NameLength.Max");
						
						if(nameLengthMin > 16)
							nameLengthMin = 16;
						
						if(nameLengthMin < 1)
							nameLengthMin = 1;
						
						if(nameLengthMax > 16)
							nameLengthMax = 16;
						
						if(nameLengthMax < 1)
							nameLengthMax = 1;
						
						if(nameWithoutColors.length() <= nameLengthMax) {
							if(nameWithoutColors.length() >= nameLengthMin) {
								if(!(utils.containsSpecialChars(nameWithoutColors)) || setupYamlFile.getConfiguration().getBoolean("AllowSpecialCharactersInCustomName")) {
									if(!(utils.containsBlackListEntry(args[0]))) {
										boolean nickNameIsInUse = false;
										
										for (NickedPlayerData nickedPlayerData : utils.getNickedPlayers().values()) {
											if(nickedPlayerData.getNickName().equalsIgnoreCase(name))
												nickNameIsInUse = true;
										}
	
										if(!(nickNameIsInUse) || setupYamlFile.getConfiguration().getBoolean("AllowPlayersToUseSameNickName")) {
											boolean playerWithNameIsKnown = false;
											
											for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
												if(currentPlayer.getName().equalsIgnoreCase(name))
													playerWithNameIsKnown = true;
											}
												
											try {
												for (OfflinePlayer currentOfflinePlayer : Bukkit.getOfflinePlayers()) {
													if((currentOfflinePlayer != null) && (currentOfflinePlayer.getName() != null) && currentOfflinePlayer.getName().equalsIgnoreCase(name))
														playerWithNameIsKnown = true;
												}
											} catch (NullPointerException ignore) {
											}
											
											if(!(setupYamlFile.getConfiguration().getBoolean("AllowPlayersToNickAsKnownPlayers")) && playerWithNameIsKnown)
												isCancelled = true;
											
											if(!(isCancelled)) {
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
