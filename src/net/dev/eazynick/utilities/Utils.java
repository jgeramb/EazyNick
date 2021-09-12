package net.dev.eazynick.utilities;

import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.google.common.primitives.Chars;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.*;
import net.dev.eazynick.nms.ScoreboardTeamHandler;
import net.dev.eazynick.nms.fakegui.book.*;
import net.dev.eazynick.sql.MySQLNickManager;
import net.dev.eazynick.sql.MySQLPlayerDataManager;
import net.dev.eazynick.utilities.AsyncTask.AsyncRunnable;
import net.dev.eazynick.utilities.configuration.yaml.*;
import net.md_5.bungee.api.chat.TextComponent;

public class Utils {

	//Support mode status
	private boolean supportMode = false;
	
	//OutgoingPacketInjector -> ignore chat message
	private String lastChatMessage = "NONE";
	
	//Imported from setup.yml
	private String prefix, noPerm, notPlayer;
	
	private List<String> nickNames = new ArrayList<>();
	private List<String> blackList = new ArrayList<>();
	private List<String> worldsWithDisabledLobbyMode = new ArrayList<>();
	private List<String> worldsWithDisabledPrefixAndSuffix = new ArrayList<>();
	private List<String> worldsWithDisabledActionBar = new ArrayList<>();
	private List<String> replaceNameInCommandBlackList = new ArrayList<>();
	private List<String> worldBlackList = new ArrayList<>();
	private List<String> mineSkinIds = new ArrayList<>();
	
	//NickOnWorldChange -> nick item -> enabled
	private ArrayList<UUID> nickOnWorldChangePlayers = new ArrayList<>();
	
	//Nicked player data handling
	private HashMap<UUID, NickedPlayerData> nickedPlayers = new HashMap<>();
	private HashMap<UUID, NickedPlayerData> lastNickDatas = new HashMap<>();
	private HashMap<UUID, ScoreboardTeamHandler> scoreboardTeamHandlers = new HashMap<>();
	
	//PermissionsEx old data
	private HashMap<UUID, String[]> oldPermissionsExGroups = new HashMap<>();
	private HashMap<UUID, String> oldPermissionsExPrefixes = new HashMap<>();
	private HashMap<UUID, String> oldPermissionsExSuffixes = new HashMap<>();
	
	//CloudNet old data
	private HashMap<UUID, String> oldCloudNETPrefixes = new HashMap<>();
	private HashMap<UUID, String> oldCloudNETSuffixes = new HashMap<>();
	private HashMap<UUID, Integer> oldCloudNETTagIDs = new HashMap<>();
	
	//LuckPerms old data
	private HashMap<UUID, Object> oldLuckPermsGroups = new HashMap<>();
	private HashMap<UUID, Object> luckPermsPrefixes = new HashMap<>();
	private HashMap<UUID, Object> luckPermsSuffixes = new HashMap<>();
	
	//UltraPermissions old data
	private HashMap<UUID, HashMap<String, Long>> oldUltraPermissionsGroups = new HashMap<>();
	private HashMap<UUID, String> ultraPermissionsPrefixes = new HashMap<>();
	private HashMap<UUID, String> ultraPermissionsSuffixes = new HashMap<>();
	
	//NameTagEdit old data
	private HashMap<UUID, String> nametagEditPrefixes = new HashMap<>();
	private HashMap<UUID, String> nametagEditSuffixes = new HashMap<>();
	
	//Last selected options
	private HashMap<UUID, String> lastNickNames = new HashMap<>();
	private HashMap<UUID, String> lastSkinNames = new HashMap<>();
	
	//FakeExperienceLevel -> real data
	private HashMap<UUID, Integer> oldExperienceLevels = new HashMap<>();
	
	//NickList
	private HashMap<UUID, Integer> nickNameListPages = new HashMap<>();
	
	//NickDelay
	private HashMap<UUID, Boolean> canUseNick = new HashMap<>();
	
	//RankedNickGUI
	private HashMap<UUID, String> lastGUITexts = new HashMap<>();
	
	//Enter name -> Chat
	private HashMap<UUID, String> playersTypingNameInChat = new HashMap<>();
	
	//IncomingPacketInjector
	private HashMap<UUID, Object> incomingPacketInjectors = new HashMap<>();
	
	//TextsToComplete
	private HashMap<Player, String> textsToComplete = new HashMap<>();
	
	//Soon nicked
	private HashMap<UUID, NickReason> soonNickedPlayers = new HashMap<>();
	
	//Loaded plugins
	private HashMap<String, List<String>> loadedPlugins = new HashMap<>();
	
	//Check plugin status
	public boolean isPluginInstalled(String name) {
		return loadedPlugins.containsKey(name);
	}
	
	public boolean isPluginInstalled(String name, String author) {
		if(loadedPlugins.containsKey(name))
			return loadedPlugins.get(name).contains(author);
		
		return false;
	}
	
	//Send message to console
	public void sendConsole(String msg) {
		if((msg != null) && !(msg.isEmpty()))
			Bukkit.getConsoleSender().sendMessage(prefix + msg);
	}

	//Version is newer than 1.12.2
	public boolean isVersion13OrLater() {
		return (Integer.parseInt(EazyNick.getInstance().getVersion().split("_")[1]) > 12);
	}
	
	//Online player count
	public int getOnlinePlayerCount() {
		return Bukkit.getOnlinePlayers().size();
	}
	
	//Command 'reloadconfig', 'eazynick reload' & plugin startup
	public void reloadConfigs() {
		EazyNick eazyNick = EazyNick.getInstance();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		GUIYamlFile guiYamlFile = eazyNick.getGUIYamlFile();
		NickNameYamlFile nickNameYamlFile = eazyNick.getNickNameYamlFile();
		
		//Reload configurations
		setupYamlFile.reload();
		nickNameYamlFile.reload();
		guiYamlFile.reload();
		languageYamlFile.reload();
		
		//Import from nickNames.yml
		this.nickNames = nickNameYamlFile.getConfiguration().getStringList("NickNames");
		
		//Import from setup.yml
		List<String> replaceNameInCommandBlackList = setupYamlFile.getConfiguration().getStringList("ReplaceNameInCommandBlackList");
		List<String> blackList = setupYamlFile.getConfiguration().getStringList("BlackList");
		List<String> worldsWithDisabledLobbyMode = setupYamlFile.getConfiguration().getStringList("WorldsWithDisabledLobbyMode");
		List<String> worldsWithDisabledPrefixAndSuffix = setupYamlFile.getConfiguration().getStringList("WorldsWithDisabledPrefixAndSuffix");
		List<String> worldsWithDisabledActionBar = setupYamlFile.getConfiguration().getStringList("WorldsWithDisabledActionBar");
		List<String> worldBlackList = setupYamlFile.getConfiguration().getStringList("AutoNickWorldBlackList");
		
		this.mineSkinIds = setupYamlFile.getConfiguration().getStringList("MineSkinIds");

		this.prefix = languageYamlFile.getConfigString("Messages.Prefix");
		this.noPerm = languageYamlFile.getConfigString("Messages.NoPerm");
		this.notPlayer = languageYamlFile.getConfigString("Messages.NotPlayer");
		
		//Optimize data
		if (replaceNameInCommandBlackList.size() >= 1) {
			ArrayList<String> toAdd = new ArrayList<>();
			
			for (String blackListCommand : replaceNameInCommandBlackList)
				toAdd.add(blackListCommand.toUpperCase());
			
			this.replaceNameInCommandBlackList = toAdd;
		}
		
		if (blackList.size() >= 1) {
			ArrayList<String> toAdd = new ArrayList<>();
			
			for (String blackListName : blackList)
				toAdd.add(blackListName.toUpperCase());
			
			this.blackList = toAdd;
		}
		
		if (worldsWithDisabledLobbyMode.size() >= 1) {
			ArrayList<String> toAdd = new ArrayList<>();
			
			for (String worldWithDisabledLobbyMode : worldsWithDisabledLobbyMode)
				toAdd.add(worldWithDisabledLobbyMode.toUpperCase());
			
			this.worldsWithDisabledLobbyMode = toAdd;
		}

		if (worldsWithDisabledPrefixAndSuffix.size() >= 1) {
			ArrayList<String> toAdd = new ArrayList<>();
			
			for (String worldWithDisabledPrefixAndSuffix : worldsWithDisabledPrefixAndSuffix)
				toAdd.add(worldWithDisabledPrefixAndSuffix.toUpperCase());
			
			this.worldsWithDisabledPrefixAndSuffix = toAdd;
		}
		
		if (worldsWithDisabledActionBar.size() >= 1) {
			ArrayList<String> toAdd = new ArrayList<>();
			
			for (String worldWithDisabledActionBar : worldsWithDisabledActionBar)
				toAdd.add(worldWithDisabledActionBar.toUpperCase());
			
			this.worldsWithDisabledActionBar = toAdd;
		}
		
		if (worldBlackList.size() >= 1) {
			ArrayList<String> toAdd = new ArrayList<>();
			
			for (String blackListWorld : worldBlackList)
				toAdd.add(blackListWorld.toUpperCase());
			
			this.worldBlackList = toAdd;
		}
	}

	//BookGUI & RankedNickGUI
	public void performRankedNick(Player player, String rankName, String skinType, String name) {
		EazyNick eazyNick = EazyNick.getInstance();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		GUIYamlFile guiYamlFile = eazyNick.getGUIYamlFile();
		NMSBookUtils nmsBookUtils = eazyNick.getNMSBookUtils();
		NMSBookBuilder nmsBookBuilder = eazyNick.getNMSBookBuilder();
		
		//Initialize variables
		String chatPrefix = "", chatSuffix = "", tabPrefix = "", tabSuffix = "", tagPrefix = "", tagSuffix = "", nameWithoutColors = new StringUtils(name).removeColorCodes().getString();
		String skinName = "";
		boolean isCancelled = false;
		int sortID = 9999;
		
		//Check if name length is valid
		if(nameWithoutColors.length() <= 16) {
			if(!(setupYamlFile.getConfiguration().getBoolean("AllowCustomNamesShorterThanThreeCharacters")) || (nameWithoutColors.length() > 2)) {
				if(!(containsSpecialChars(nameWithoutColors)) || setupYamlFile.getConfiguration().getBoolean("AllowSpecialCharactersInCustomName")) {
					//Check if name is allowed
					if(!(blackList.contains(name.toUpperCase()))) {
						//Check if nickname is in use
						boolean nickNameIsInUse = false;
						
						for (NickedPlayerData nickedPlayerData : nickedPlayers.values()) {
							if(nickedPlayerData.getNickName().toUpperCase().equalsIgnoreCase(name.toUpperCase()))
								nickNameIsInUse = true;
						}
		
						if(!(nickNameIsInUse) || setupYamlFile.getConfiguration().getBoolean("AllowPlayersToUseSameNickName")) {
							//Check if player is known on the server (in usercache.json)
							boolean playerWithNameIsKnown = false;
							
							for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
								if(currentPlayer.getName().toUpperCase().equalsIgnoreCase(name.toUpperCase()))
									playerWithNameIsKnown = true;
							}
							
							try {
								for (OfflinePlayer currentOfflinePlayer : Bukkit.getOfflinePlayers()) {
									if((currentOfflinePlayer != null) && (currentOfflinePlayer.getName() != null) && currentOfflinePlayer.getName().toUpperCase().equalsIgnoreCase(name.toUpperCase()))
										playerWithNameIsKnown = true;
								}
							} catch (NullPointerException ignore) {
							}
							
							if(!(setupYamlFile.getConfiguration().getBoolean("AllowPlayersToNickAsKnownPlayers")) && playerWithNameIsKnown)
								isCancelled = true;
							
							if(!(isCancelled)) {
								//Import rank data from guis.yml
								String groupName = "";
								
								for (int i = 1; i <= 18; i++) {
									String permission = guiYamlFile.getConfigString("RankGUI.Rank" + i + ".Permission");
									
									if(rankName.equalsIgnoreCase(guiYamlFile.getConfiguration().getString("RankGUI.Rank" + i + ".RankName")) && guiYamlFile.getConfiguration().getBoolean("RankGUI.Rank" + i + ".Enabled") && (permission.equalsIgnoreCase("NONE") || player.hasPermission(permission))) {
										chatPrefix = guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".ChatPrefix");
										chatSuffix = guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".ChatSuffix");
										tabPrefix = guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".TabPrefix");
										tabSuffix = guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".TabSuffix");
										tagPrefix = guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".TagPrefix");
										tagSuffix = guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".TagSuffix");
										groupName = guiYamlFile.getConfiguration().getString("Settings.NickFormat.Rank" + i + ".GroupName");
										sortID = setupYamlFile.getConfiguration().getInt("Settings.NickFormat.Rank" + i + ".SortID");
									}
								}
								
								if(groupName.isEmpty())
									return;
								
								//Add support for %randomColor% placeholder
								String randomColor = "§" + ("0123456789abcdef".charAt(new Random().nextInt(16)));
								
								chatPrefix = chatPrefix.replaceAll("%randomColor%", randomColor).replaceAll("%randomcolor%", randomColor);
								chatSuffix = chatSuffix.replaceAll("%randomColor%", randomColor).replaceAll("%randomcolor%", randomColor);
								tabPrefix = tabPrefix.replaceAll("%randomColor%", randomColor).replaceAll("%randomcolor%", randomColor);
								tabSuffix = tabSuffix.replaceAll("%randomColor%", randomColor).replaceAll("%randomcolor%", randomColor);
								tagPrefix = tagPrefix.replaceAll("%randomColor%", randomColor).replaceAll("%randomcolor%", randomColor);
								tagSuffix = tagSuffix.replaceAll("%randomColor%", randomColor).replaceAll("%randomcolor%", randomColor);
								
								//Set skin name depending on skin type
								if(skinType.equalsIgnoreCase("DEFAULT"))
									skinName = player.getName();
								else if(skinType.equalsIgnoreCase("NORMAL"))
									skinName = new Random().nextBoolean() ? "Steve" : "Alex";
								else if(skinType.equalsIgnoreCase("RANDOM"))
									skinName = setupYamlFile.getConfiguration().getBoolean("UseMineSkinAPI") ? ("MINESKIN:" + getRandomStringFromList(setupYamlFile.getConfiguration().getStringList("MineSkinIds"))) : nickNames.get(new Random().nextInt(getNickNames().size()));
								else if(skinType.equalsIgnoreCase("SKINFROMNAME"))
									skinName = name;
								else
									skinName = skinType;
								
								//Update last name & skin cache
								if(lastSkinNames.containsKey(player.getUniqueId()))
									lastSkinNames.remove(player.getUniqueId());
								
								if(lastNickNames.containsKey(player.getUniqueId()))
									lastNickNames.remove(player.getUniqueId());
								
								lastSkinNames.put(player.getUniqueId(), skinName.startsWith("MINESKIN:") ? "RANDOM" : skinName);
								lastNickNames.put(player.getUniqueId(), name);
								
								//Fix book still in inventory bug
								for (ItemStack item : player.getInventory().getContents()) {
									if((item != null) && item.getType().equals(Material.WRITTEN_BOOK)) {
										if(((BookMeta) item.getItemMeta()).getAuthor().equals(eazyNick.getDescription().getName()))
											player.getInventory().remove(item);
									}
								}
								
								//Add nick data to mysql or nick player
								new NickManager(player).setGroupName(groupName);
								
								if(setupYamlFile.getConfiguration().getBoolean("BungeeCord") && !(player.hasPermission("nick.bypasslobbymode") && setupYamlFile.getConfiguration().getBoolean("EnableBypassLobbyModePermission")) && setupYamlFile.getConfiguration().getBoolean("LobbyMode") && !(worldsWithDisabledLobbyMode.contains(player.getWorld().getName().toUpperCase()))) {
									eazyNick.getMySQLNickManager().addPlayer(player.getUniqueId(), name, skinName);
									eazyNick.getMySQLPlayerDataManager().insertData(player.getUniqueId(), "NONE", chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix);
									
									if(guiYamlFile.getConfiguration().getBoolean("BookGUI.Page6.Enabled") && !(eazyNick.getVersion().equals("1_7_R4"))) {
										ArrayList<TextComponent> textComponents = new ArrayList<>();
										
										for(String s : guiYamlFile.getConfigString(player, "BookGUI.Page6.Text.BungeeCord").replace("%name%", chatPrefix + name + chatSuffix).split("%nl%"))
											textComponents.add(new TextComponent(s + "\n"));
										
										nmsBookUtils.open(player, nmsBookBuilder.create("Done", new BookPage(textComponents)));
									}
								} else {
									PlayerNickEvent playerNickEvent = new PlayerNickEvent(player, name, skinName, setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.UUID") ? (eazyNick.getVersion().startsWith("1_7") ? eazyNick.getUUIDFetcher_1_7().getUUID(name) : (eazyNick.getVersion().equals("1_8_R1") ? eazyNick.getUUIDFetcher_1_8_R1().getUUID(name) : eazyNick.getUUIDFetcher().getUUID(name))) : player.getUniqueId(), chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix, false, false, sortID, groupName);
									
									Bukkit.getPluginManager().callEvent(playerNickEvent);
									
									if(!(playerNickEvent.isCancelled())) {
										String finalChatPrefix = chatPrefix, finalChatSuffix = chatSuffix;
										
										new AsyncTask(new AsyncRunnable() {
											
											@Override
											public void run() {
												Bukkit.getScheduler().runTask(eazyNick, () -> {
													if(guiYamlFile.getConfiguration().getBoolean("BookGUI.Page6.Enabled") && !(eazyNick.getVersion().equals("1_7_R4"))) {
														ArrayList<TextComponent> textComponents = new ArrayList<>();
														
														for(String s : guiYamlFile.getConfigString(player, "BookGUI.Page6.Text.SingleServer").replace("%name%", finalChatPrefix + name + finalChatSuffix).split("%nl%"))
															textComponents.add(new TextComponent(s + "\n"));
														
														nmsBookUtils.open(player, nmsBookBuilder.create("Done", new BookPage(textComponents)));
													}
												});
											}
										}, 400 + (setupYamlFile.getConfiguration().getBoolean("RandomDisguiseDelay") ? 2000 : 0)).run();
									}
								}
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
	}

	//Command 'nick', 'nickother', 'nickgui' -> Nick & nick item
	public void performNick(Player player, String customNickName) {
		EazyNick eazyNick = EazyNick.getInstance();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		
		//Process name
		String name = customNickName.equals("RANDOM") ? nickNames.get((new Random().nextInt(nickNames.size()))) : customNickName;
		
		//Make sure that random nick is not being used
		if(customNickName.equals("RANDOM") && !(setupYamlFile.getConfiguration().getBoolean("AllowPlayersToUseSameNickName"))) {
			boolean nickNameIsInUse = false;
			
			for (NickedPlayerData nickedPlayerData : nickedPlayers.values()) {
				if(nickedPlayerData.getNickName().toUpperCase().equalsIgnoreCase(name.toUpperCase()))
					nickNameIsInUse = true;
			}
			
			while (nickNameIsInUse) {
				nickNameIsInUse = false;
				name = nickNames.get((new Random().nextInt(nickNames.size())));
				
				for (NickedPlayerData nickedPlayerData : nickedPlayers.values()) {
					if(nickedPlayerData.getNickName().toUpperCase().equalsIgnoreCase(name.toUpperCase()))
						nickNameIsInUse = true;
				}
			}
		}
		
		//Collect nick data
		boolean serverFull = getOnlinePlayerCount() >= Bukkit.getMaxPlayers();
		String nameWhithoutColors = ChatColor.stripColor(name);
		String[] prefixSuffix = new String[] {};
		String chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix;
		
		try {
			prefixSuffix = name.split(nameWhithoutColors);
		} catch (Exception ignore) {
		}
		
		if(prefixSuffix.length >= 1) {
			chatPrefix = ChatColor.translateAlternateColorCodes('&', prefixSuffix[0]);
			
			if(chatPrefix.length() > 16)
				chatPrefix = chatPrefix.substring(0, 16);
			
			if(prefixSuffix.length >= 2) {
				chatSuffix = ChatColor.translateAlternateColorCodes('&', prefixSuffix[1]);
				
				if(chatSuffix.length() > 16)
					chatSuffix = chatSuffix.substring(0, 16);
			} else
				chatSuffix = "§r";
			
			tabPrefix = chatPrefix;
			tabSuffix = chatSuffix;
			tagPrefix = chatPrefix;
			tagSuffix = chatSuffix;
		} else {
			chatPrefix = (serverFull ? setupYamlFile.getConfigString("Settings.NickFormat.ServerFullRank.Chat.Prefix") : setupYamlFile.getConfigString("Settings.NickFormat.Chat.Prefix"));
			chatSuffix = (serverFull ? setupYamlFile.getConfigString("Settings.NickFormat.ServerFullRank.Chat.Suffix") : setupYamlFile.getConfigString("Settings.NickFormat.Chat.Suffix"));
			tabPrefix = (serverFull ? setupYamlFile.getConfigString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix") : setupYamlFile.getConfigString("Settings.NickFormat.PlayerList.Prefix"));
			tabSuffix = (serverFull ? setupYamlFile.getConfigString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix") : setupYamlFile.getConfigString("Settings.NickFormat.PlayerList.Suffix"));
			tagPrefix = (serverFull ? setupYamlFile.getConfigString("Settings.NickFormat.ServerFullRank.NameTag.Prefix") : setupYamlFile.getConfigString("Settings.NickFormat.NameTag.Prefix"));
			tagSuffix = (serverFull ? setupYamlFile.getConfigString("Settings.NickFormat.ServerFullRank.NameTag.Suffix") : setupYamlFile.getConfigString("Settings.NickFormat.NameTag.Suffix"));
		}

		//Nick player
		new NickManager(player).setGroupName(serverFull ? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.GroupName") : setupYamlFile.getConfigString(player, "Settings.NickFormat.GroupName"));
		
		Bukkit.getPluginManager().callEvent(new PlayerNickEvent(player, nameWhithoutColors, setupYamlFile.getConfiguration().getBoolean("UseMineSkinAPI") ? ("MINESKIN:" + getRandomStringFromList(setupYamlFile.getConfiguration().getStringList("MineSkinIds"))) : nameWhithoutColors, setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.UUID") ? (eazyNick.getVersion().startsWith("1_7") ? eazyNick.getUUIDFetcher_1_7().getUUID(name) : (eazyNick.getVersion().equals("1_8_R1") ? eazyNick.getUUIDFetcher_1_8_R1().getUUID(name) : eazyNick.getUUIDFetcher().getUUID(name))) : player.getUniqueId(), chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix, false, false, serverFull ? setupYamlFile.getConfiguration().getInt("Settings.NickFormat.ServerFullRank.SortID") : setupYamlFile.getConfiguration().getInt("Settings.NickFormat.SortID"), serverFull ? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.GroupName") : setupYamlFile.getConfigString(player, "Settings.NickFormat.GroupName")));
	}
	
	public void performReNick(Player player) {
		//Renick player by fitting nick method 
		performReNick(player, nickOnWorldChangePlayers.contains(player.getUniqueId()) ? nickNames.get((new Random().nextInt(nickNames.size()))) : EazyNick.getInstance().getMySQLNickManager().getNickName(player.getUniqueId()));
	}
	
	public void performReNick(Player player, String name) {
		if(!(new NickManager(player).isNicked())) {
			EazyNick eazyNick = EazyNick.getInstance();
			SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
			GUIYamlFile guiYamlFile = eazyNick.getGUIYamlFile();
			LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
			MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
			MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMySQLPlayerDataManager();
			
			boolean isCancelled = false;
			
			//Check if nickname is in use
			boolean nickNameIsInUse = false;
			
			for (NickedPlayerData nickedPlayerData : nickedPlayers.values()) {
				if(nickedPlayerData.getNickName().toUpperCase().equalsIgnoreCase(name.toUpperCase()))
					nickNameIsInUse = true;
			}
			
			if(!(nickNameIsInUse) || setupYamlFile.getConfiguration().getBoolean("AllowPlayersToUseSameNickName")) {
				//Check if player is known on the server (in usercache.json)
				boolean playerWithNameIsKnown = false;
				
				for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
					if(currentPlayer.getName().toUpperCase().equalsIgnoreCase(name.toUpperCase()))
						playerWithNameIsKnown = true;
				}
				
				try {
					for (OfflinePlayer currentOfflinePlayer : Bukkit.getOfflinePlayers()) {
						if((currentOfflinePlayer != null) && (currentOfflinePlayer.getName() != null) && currentOfflinePlayer.getName().toUpperCase().equalsIgnoreCase(name.toUpperCase()))
							playerWithNameIsKnown = true;
					}
				} catch (NullPointerException ignore) {
				}
				
				if(!(setupYamlFile.getConfiguration().getBoolean("AllowPlayersToNickAsKnownPlayers")) && playerWithNameIsKnown)
					isCancelled = true;
				
				if(!(isCancelled)) {
					UUID spoofedUniqueId = setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.UUID") ? (eazyNick.getVersion().startsWith("1_7") ? eazyNick.getUUIDFetcher_1_7().getUUID(name) : (eazyNick.getVersion().equals("1_8_R1") ? eazyNick.getUUIDFetcher_1_8_R1().getUUID(name) : eazyNick.getUUIDFetcher().getUUID(name))) : player.getUniqueId();
					
					//Check if the nickname is different than the real name
					if(!(name.equalsIgnoreCase(player.getName()))) {
						if((mysqlPlayerDataManager != null) && mysqlPlayerDataManager.isRegistered(player.getUniqueId())) {
							new NickManager(player).setGroupName("Default");
							
							if(setupYamlFile.getConfiguration().contains("UseLocalRankPrefixes")) {
								//Use mysql data as fallback data
								String groupName = mysqlPlayerDataManager.getGroupName(player.getUniqueId()), chatPrefix = mysqlPlayerDataManager.getChatPrefix(player.getUniqueId()), chatSuffix = mysqlPlayerDataManager.getChatSuffix(player.getUniqueId()), tabPrefix = mysqlPlayerDataManager.getTabPrefix(player.getUniqueId()), tabSuffix = mysqlPlayerDataManager.getTabSuffix(player.getUniqueId()), tagPrefix = mysqlPlayerDataManager.getTagPrefix(player.getUniqueId()), tagSuffix = mysqlPlayerDataManager.getTagSuffix(player.getUniqueId());
								int sortID = 9999;
								
								//Import from setup.yml
								if(groupName.equalsIgnoreCase(setupYamlFile.getConfigString("Settings.NickFormat.GroupName")) || groupName.equalsIgnoreCase(setupYamlFile.getConfigString("Settings.NickFormat.ServerFullRank.GroupName"))) {
									String serverFullRank = (getOnlinePlayerCount() >= Bukkit.getMaxPlayers()) ? ".ServerFullRank" : "";
									
									chatPrefix = setupYamlFile.getConfigString("Settings.NickFormat" + serverFullRank + ".Chat.Prefix");
									chatSuffix = setupYamlFile.getConfigString("Settings.NickFormat" + serverFullRank + ".Chat.Suffix");
									tabPrefix = setupYamlFile.getConfigString("Settings.NickFormat" + serverFullRank + ".PlayerList.Prefix");
									tabSuffix = setupYamlFile.getConfigString("Settings.NickFormat" + serverFullRank + ".PlayerList.Suffix");
									tagPrefix = setupYamlFile.getConfigString("Settings.NickFormat" + serverFullRank + ".NameTag.Prefix");
									tagSuffix = setupYamlFile.getConfigString("Settings.NickFormat" + serverFullRank + ".NameTag.Suffix");
									sortID = setupYamlFile.getConfiguration().getInt("Settings.NickFormat" + serverFullRank + ".SortID");
								} else {
									for (int i = 1; i <= 18; i++) {
										if(groupName.equalsIgnoreCase(guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".GroupName"))) {
											chatPrefix = guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".ChatPrefix");
											chatSuffix = guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".ChatSuffix");
											tabPrefix = guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".TabPrefix");
											tabSuffix = guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".TabSuffix");
											tagPrefix = guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".TagPrefix");
											tagSuffix = guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".TagSuffix");
											sortID = guiYamlFile.getConfiguration().getInt("Settings.NickFormat.Rank" + i + ".SortID");
											
											String randomColor = "§" + ("0123456789abcdef".charAt(new Random().nextInt(16)));
											
											chatPrefix = chatPrefix.replaceAll("%randomColor%", randomColor).replaceAll("%randomcolor%", randomColor);
											chatSuffix = chatSuffix.replaceAll("%randomColor%", randomColor).replaceAll("%randomcolor%", randomColor);
											tabPrefix = tabPrefix.replaceAll("%randomColor%", randomColor).replaceAll("%randomcolor%", randomColor);
											tabSuffix = tabSuffix.replaceAll("%randomColor%", randomColor).replaceAll("%randomcolor%", randomColor);
											tagPrefix = tagPrefix.replaceAll("%randomColor%", randomColor).replaceAll("%randomcolor%", randomColor);
											tagSuffix = tagSuffix.replaceAll("%randomColor%", randomColor).replaceAll("%randomcolor%", randomColor);
											
											break;
										}
									}
								}
								
								//Nick player
								Bukkit.getPluginManager().callEvent(new PlayerNickEvent(player, name, mysqlNickManager.getSkinName(player.getUniqueId()), spoofedUniqueId, chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix, true, false, sortID, mysqlPlayerDataManager.getGroupName(player.getUniqueId())));
							} else
								//Nick player
								Bukkit.getPluginManager().callEvent(new PlayerNickEvent(player, name, mysqlNickManager.getSkinName(player.getUniqueId()), spoofedUniqueId, mysqlPlayerDataManager.getChatPrefix(player.getUniqueId()), mysqlPlayerDataManager.getChatSuffix(player.getUniqueId()), mysqlPlayerDataManager.getTabPrefix(player.getUniqueId()), mysqlPlayerDataManager.getTabSuffix(player.getUniqueId()), mysqlPlayerDataManager.getTagPrefix(player.getUniqueId()), mysqlPlayerDataManager.getTagSuffix(player.getUniqueId()), true, false, 9999, mysqlPlayerDataManager.getGroupName(player.getUniqueId())));
						} else {
							//Import data from setup.yml
							boolean serverFull = getOnlinePlayerCount() >= Bukkit.getMaxPlayers();
							String prefix = serverFull ? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.NameTag.Prefix") : setupYamlFile.getConfigString(player, "Settings.NickFormat.NameTag.Prefix");
							String suffix = serverFull ? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.NameTag.Suffix") : setupYamlFile.getConfigString(player, "Settings.NickFormat.NameTag.Suffix");
						
							//Nick player
							new NickManager(player).setGroupName(serverFull ? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.GroupName") : setupYamlFile.getConfigString(player, "Settings.NickFormat.GroupName"));
							
							Bukkit.getPluginManager().callEvent(new PlayerNickEvent(player, name, mysqlNickManager.getSkinName(player.getUniqueId()), spoofedUniqueId, serverFull ? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.Chat.Prefix") : setupYamlFile.getConfigString(player, "Settings.NickFormat.Chat.Prefix"), serverFull ? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.Chat.Suffix") : setupYamlFile.getConfigString(player, "Settings.NickFormat.Chat.Suffix"), serverFull ? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.PlayerList.Prefix") : setupYamlFile.getConfigString(player, "Settings.NickFormat.PlayerList.Prefix"), serverFull ? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.PlayerList.Suffix") : setupYamlFile.getConfigString(player, "Settings.NickFormat.PlayerList.Suffix"), prefix, suffix, true, false, (getOnlinePlayerCount() >= Bukkit.getMaxPlayers()) ? setupYamlFile.getConfiguration().getInt("Settings.NickFormat.ServerFullRank.SortID") : setupYamlFile.getConfiguration().getInt("Settings.NickFormat.GrSortIDoupName"), (getOnlinePlayerCount() >= Bukkit.getMaxPlayers()) ? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.GroupName") : setupYamlFile.getConfigString(player, "Settings.NickFormat.GroupName")));
						}
					} else
						languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.CanNotNickAsSelf").replace("%prefix%", prefix));
				} else
					languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.PlayerWithThisNameIsKnown").replace("%prefix%", prefix));
			} else
				languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.NickNameAlreadyInUse").replace("%prefix%", prefix));
		}
	}
	
	//Command 'togglebungeenick', nick item
	public void toggleBungeeNick(Player player) {
		EazyNick eazyNick = EazyNick.getInstance();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
		MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMySQLPlayerDataManager();
		
		//Check if player has nick item
		boolean hasItem = (player.getItemInHand() != null) && (player.getItemInHand().getType() != Material.AIR && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null) && (player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Disabled")) || player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Enabled")));
		
		if(setupYamlFile.getConfiguration().getBoolean("NeedItemToToggleNick") && !(hasItem))
			return;
		
		if(mysqlNickManager.isPlayerNicked(player.getUniqueId())) {
			//Remove player from mysql
			mysqlNickManager.removePlayer(player.getUniqueId());
			mysqlPlayerDataManager.removeData(player.getUniqueId());
			
			//Update nick item
			if(hasItem)
				player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemBuilder(Material.getMaterial(setupYamlFile.getConfiguration().getString("NickItem.ItemType.Disabled")), setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Disabled"), setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Disabled")).setDisplayName(languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Disabled")).setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Disabled").split("&n")).setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Disabled")).build());

			languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.BungeeAutoNickDisabled").replace("%prefix%", prefix));
		} else {
			String name = nickNames.get((new Random().nextInt(nickNames.size())));

			//Add player to mysql
			mysqlNickManager.addPlayer(player.getUniqueId(), name, name);
			
			//Update nick item
			if(hasItem)
				player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemBuilder(Material.getMaterial(setupYamlFile.getConfiguration().getString("NickItem.ItemType.Enabled")), setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Enabled"), setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Enabled")).setDisplayName(languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Enabled")).setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Enabled").split("&n")).setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Enabled")).build());

			languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.BungeeAutoNickEnabled").replace("%prefix%", prefix));
		}
	}
	
	//Get a random string from a list
	public String getRandomStringFromList(List<String> list) {
		return (list.isEmpty() ? "" : list.get((new Random()).nextInt(list.size())));
	}
	
	//String contains characters that are not allowed in minecraft usernames (allowed: a-z, A-Z, 0-9, _)
	public boolean containsSpecialChars(String s) {
		List<Character> allowCharacters = Chars.asList("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_".toCharArray());
		
		for (char c : s.toCharArray()) {
			if(!(allowCharacters.contains(c)))
				return true;
		}
		
		return false;
	}

	//Default game profile (Steve)
	public GameProfile getDefaultGameProfile() {
		GameProfile gameProfile = new GameProfile(getDefaultUniqueId(), getDefaultName());
		gameProfile.getProperties().put("textures", new Property("textures", getDefaultSkinValue(), getDefaultSkinSignature()));
		
		return gameProfile;
	}
	
	//Default 1.7 game profile ("Steve")
	public net.minecraft.util.com.mojang.authlib.GameProfile getDefaultGameProfile_1_7() {
		net.minecraft.util.com.mojang.authlib.GameProfile gameProfile = new net.minecraft.util.com.mojang.authlib.GameProfile(getDefaultUniqueId(), getDefaultName());
		gameProfile.getProperties().put("textures", new net.minecraft.util.com.mojang.authlib.properties.Property("textures", getDefaultSkinValue(), getDefaultSkinSignature()));
		
		return gameProfile;
	}
	
	//"Steve" unique id
	public UUID getDefaultUniqueId() {
		return UUID.fromString("8667ba71-b85a-4004-af54-457a9734eed7");
	}
	
	//"Steve" name
	public String getDefaultName() {
		return "Steve";
	}
	
	//"Steve" skin value
	public String getDefaultSkinValue() {
		return "ewogICJ0aW1lc3RhbXAiIDogMTU4OTU2NzM1NzQyMSwKICAicHJvZmlsZUlkIiA6ICI5MzRiMmFhOGEyODQ0Yzc3ODg2NDhiNDBiY2IzYjAzMSIsCiAgInByb2ZpbGVOYW1lIiA6ICI0Z2wiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQzYjA2YzM4NTA0ZmZjMDIyOWI5NDkyMTQ3YzY5ZmNmNTlmZDJlZDc4ODVmNzg1MDIxNTJmNzdiNGQ1MGRlMSIKICAgIH0KICB9Cn0=";
	}
	
	//"Steve" skin signature
	public String getDefaultSkinSignature() {
		return "WMx2gG2+sM8D4qLHAjiiEWMNwxR6hT0H1uhqoiM1g2IJuM9dODDpgQOEEn7+9K1GxHr45Y7NQ4s9tFk7a5M1BL+IpLUNuZ3PIH2qAuoqVvaYrcYX05e9SBTuHLJCuSo+RjDqyT6AWkE4nYpD6vTLoOQS8Ku+ZXyPdFh2ALW95zQPHh2ZXrlaY+Ktdwf2TEq0vqr8agxzhDaksBxQBgbntu5VS4Z9yJ2hTeftZZALadewYegDI7Dkf/9yWr+6PEcCczhCyrf4xhHzBOjBLMDjg6ZRQhGGscv6dLP7hdqbIRPwvZ/tH0NW1GE1UWqof5TspCmlHNI592djxo2MqDhA0LrT4jbQfsnfZze0urQwMQG1V3fDDrf8kfZdD+7H29UmFAaTvfqMkwqKalPExm75oqeFole4qzxifl1Rv1i3bJy8ZZlgixZzxhl3idDIP5IkPnQHt9YFOEpOQLWtJV8dTixCP5TvYVQRtXkgFtzIUljDNkrqUmqlkeXPlZR27lEykuLIGPrV4U/bXanNVpHKcCMsD7vzFC1wu1XS0JWozN8SFZdwmVTCFpmBgqeKHBPbIuTqdOF0+YZ7xoxc7W869vgFJSaJ7jdlcTsHFt+AQcWUxlSqoV1n1kyQ7hF/zcjoi2YtAMy9XGh1IODS+UPl/edqs7Sq+fA054/ivaqzeh4=";
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getNoPerm() {
		return noPerm;
	}
	
	public String getNotPlayer() {
		return notPlayer;
	}
	
	public String getLastChatMessage() {
		return lastChatMessage;
	}
	
	public boolean isSupportMode() {
		return supportMode;
	}
	
	public List<String> getNickNames() {
		return nickNames;
	}
	
	public List<String> getReplaceNameInCommandBlackList() {
		return replaceNameInCommandBlackList;
	}
	
	public List<String> getBlackList() {
		return blackList;
	}
	
	public List<String> getWorldsWithDisabledLobbyMode() {
		return worldsWithDisabledLobbyMode;
	}
	
	public List<String> getWorldsWithDisabledPrefixAndSuffix() {
		return worldsWithDisabledPrefixAndSuffix;
	}
	
	public List<String> getWorldsWithDisabledActionBar() {
		return worldsWithDisabledActionBar;
	}
	
	public List<String> getWorldBlackList() {
		return worldBlackList;
	}
	
	public List<String> getMineSkinIds() {
		return mineSkinIds;
	}
	
	public ArrayList<UUID> getNickOnWorldChangePlayers() {
		return nickOnWorldChangePlayers;
	}
	
	public HashMap<UUID, NickedPlayerData> getNickedPlayers() {
		return nickedPlayers;
	}
	
	public HashMap<UUID, NickedPlayerData> getLastNickDatas() {
		return lastNickDatas;
	}
	
	public HashMap<UUID, Boolean> getCanUseNick() {
		return canUseNick;
	}
	
	public HashMap<UUID, Integer> getOldExperienceLevels() {
		return oldExperienceLevels;
	}
	
	public HashMap<UUID, Integer> getNickNameListPages() {
		return nickNameListPages;
	}
	
	public HashMap<UUID, String[]> getOldPermissionsExGroups() {
		return oldPermissionsExGroups;
	}
	
	public HashMap<UUID, String> getOldPermissionsExPrefixes() {
		return oldPermissionsExPrefixes;
	}
	
	public HashMap<UUID, String> getOldPermissionsExSuffixes() {
		return oldPermissionsExSuffixes;
	}
	
	public HashMap<UUID, String> getOldCloudNETPrefixes() {
		return oldCloudNETPrefixes;
	}
	
	public HashMap<UUID, String> getOldCloudNETSuffixes() {
		return oldCloudNETSuffixes;
	}
	
	public HashMap<UUID, Integer> getOldCloudNETTagIDs() {
		return oldCloudNETTagIDs;
	}
	
	public HashMap<UUID, Object> getOldLuckPermsGroups() {
		return oldLuckPermsGroups;
	}
	
	public HashMap<UUID, Object> getLuckPermsPrefixes() {
		return luckPermsPrefixes;
	}
	
	public HashMap<UUID, Object> getLuckPermsSuffixes() {
		return luckPermsSuffixes;
	}
	
	public HashMap<UUID, HashMap<String, Long>> getOldUltraPermissionsGroups() {
		return oldUltraPermissionsGroups;
	}
	
	public HashMap<UUID, String> getUltraPermissionsPrefixes() {
		return ultraPermissionsPrefixes;
	}
	
	public HashMap<UUID, String> getUltraPermissionsSuffixes() {
		return ultraPermissionsSuffixes;
	}
	
	public HashMap<UUID, String> getNametagEditPrefixes() {
		return nametagEditPrefixes;
	}
	
	public HashMap<UUID, String> getNametagEditSuffixes() {
		return nametagEditSuffixes;
	}
	
	public HashMap<UUID, ScoreboardTeamHandler> getScoreboardTeamManagers() {
		return scoreboardTeamHandlers;
	}
	
	public HashMap<UUID, String> getLastNickNames() {
		return lastNickNames;
	}
	
	public HashMap<UUID, String> getLastSkinNames() {
		return lastSkinNames;
	}
	
	public HashMap<UUID, String> getLastGUITexts() {
		return lastGUITexts;
	}
	
	public HashMap<UUID, String> getPlayersTypingNameInChat() {
		return playersTypingNameInChat;
	}
	
	public HashMap<UUID, Object> getIncomingPacketInjectors() {
		return incomingPacketInjectors;
	}
	
	public HashMap<Player, String> getTextsToComplete() {
		return textsToComplete;
	}
	
	public HashMap<UUID, NickReason> getSoonNickedPlayers() {
		return soonNickedPlayers;
	}
	
	public HashMap<String, List<String>> getLoadedPlugins() {
		return loadedPlugins;
	}

	public void setLastChatMessage(String lastChatMessage) {
		this.lastChatMessage = lastChatMessage;
	}

	public void setSupportMode(boolean supportMode) {
		this.supportMode = supportMode;
	}

}