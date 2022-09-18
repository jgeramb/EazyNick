package com.justixdev.eazynick.utilities;

import com.google.common.primitives.Chars;
import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.api.NickedPlayerData;
import com.justixdev.eazynick.api.PlayerNickEvent;
import com.justixdev.eazynick.nms.ScoreboardTeamHandler;
import com.justixdev.eazynick.nms.fakegui.book.BookPage;
import com.justixdev.eazynick.nms.fakegui.book.NMSBookBuilder;
import com.justixdev.eazynick.nms.fakegui.book.NMSBookUtils;
import com.justixdev.eazynick.sql.MySQLNickManager;
import com.justixdev.eazynick.sql.MySQLPlayerDataManager;
import com.justixdev.eazynick.utilities.AsyncTask.AsyncRunnable;
import com.justixdev.eazynick.utilities.configuration.yaml.GUIYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.NickNameYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class Utils {

	// Support mode status
	private boolean supportMode;

	/**
	 * Used in
	 * {@link com.justixdev.eazynick.nms.netty.server.OutgoingPacketInjector}
	 * and
	 * {@link com.justixdev.eazynick.nms.netty.server.OutgoingPacketInjector_1_7}
	 * to avoid overwriting sent chat messages
	 * */
	// Used in
	private String lastChatMessage;
	
	// Store common used messages from the current language file in a variable
	private String prefix, noPerm, notPlayer;
	
	private final List<String> nickNames, blackList, worldsWithDisabledLobbyMode, worldsWithDisabledPrefixAndSuffix, worldsWithDisabledActionBar, replaceNameInCommandBlackList, worldBlackList, mineSkinUUIDs;
	
	// NickOnWorldChange: true -> every player whose nick item is enabled
	private final List<UUID> nickOnWorldChangePlayers;
	
	// Nicked player data handling
	private final Map<UUID, NickedPlayerData> nickedPlayers, lastNickData;
	// NameTag prefix/suffix managers
	private final Map<UUID, ScoreboardTeamHandler> scoreboardTeamHandlers;
	
	// PermissionsEx old player data
	private final Map<UUID, String[]> oldPermissionsExGroups;
	private final Map<UUID, String> oldPermissionsExPrefixes, oldPermissionsExSuffixes;
	
	// CloudNet old player data
	private final Map<UUID, String> oldCloudNETPrefixes, oldCloudNETSuffixes;
	private final Map<UUID, Integer> oldCloudNETTagIDs;
	
	// LuckPerms old player data
	private final Map<UUID, Object> oldLuckPermsGroups;
	private final Map<UUID, Object> luckPermsPrefixes, luckPermsSuffixes;
	
	//NameTagEdit old player data
	private final Map<UUID, String> nametagEditPrefixes, nametagEditSuffixes;
	
	// Last selected options in the BookGUI/RankedNickGUI
	private final Map<UUID, String> lastNickNames, lastSkinNames;
	
	// FakeExperienceLevel -> real experience level
	private final Map<UUID, Integer> oldExperienceLevels;
	
	// Current opened pages in the NickList gui
	private final Map<UUID, Integer> nickNameListPages;
	
	// Cooldown times for nicking
	private final Map<UUID, Long> canUseNick;
	
	// Current data for RankedNickGUI
	private final Map<UUID, String> lastGUITexts;
	
	// Chat inputs for BookGUI/RankedNickGUI
	private final Map<UUID, String> playersTypingNameInChat;
	
	// Packet handlers (client -> server)
	private final Map<UUID, Object> incomingPacketInjectors;
	
	// Texts read from PacketPlayInTextComplete in IncomingPacketInjector
	private final Map<Player, String> textsToComplete;
	
	// Players that will be nicked in a few milliseconds
	private final List<UUID> soonNickedPlayers;
	
	// Active plugins on the server
	private final Map<String, List<String>> loadedPlugins;

	public Utils() {
		this.supportMode = false;

		this.lastChatMessage = "NONE";

		this.nickNames = new ArrayList<>();
		this.blackList = new ArrayList<>();
		this.worldsWithDisabledLobbyMode = new ArrayList<>();
		this.worldsWithDisabledPrefixAndSuffix = new ArrayList<>();
		this.worldsWithDisabledActionBar = new ArrayList<>();
		this.replaceNameInCommandBlackList = new ArrayList<>();
		this.worldBlackList = new ArrayList<>();
		this.mineSkinUUIDs = new ArrayList<>();

		this.nickOnWorldChangePlayers = new ArrayList<>();

		this.nickedPlayers = new HashMap<>();
		this.lastNickData = new HashMap<>();
		this.scoreboardTeamHandlers = new HashMap<>();

		this.oldPermissionsExGroups = new HashMap<>();
		this.oldPermissionsExPrefixes = new HashMap<>();
		this.oldPermissionsExSuffixes = new HashMap<>();

		this.oldCloudNETPrefixes = new HashMap<>();
		this.oldCloudNETSuffixes = new HashMap<>();
		this.oldCloudNETTagIDs = new HashMap<>();

		this.oldLuckPermsGroups = new HashMap<>();
		this.luckPermsPrefixes = new HashMap<>();
		this.luckPermsSuffixes = new HashMap<>();

		this.nametagEditPrefixes = new HashMap<>();
		this.nametagEditSuffixes = new HashMap<>();

		this.lastNickNames = new HashMap<>();
		this.lastSkinNames = new HashMap<>();

		this.oldExperienceLevels = new HashMap<>();
		this.nickNameListPages = new HashMap<>();
		this.canUseNick = new HashMap<>();
		this.lastGUITexts = new HashMap<>();
		this.playersTypingNameInChat = new HashMap<>();
		this.incomingPacketInjectors = new HashMap<>();
		this.textsToComplete = new HashMap<>();
		this.soonNickedPlayers = new ArrayList<>();
		this.loadedPlugins = new HashMap<>();
	}

	// Check plugin status
	public boolean isPluginInstalled(String name) {
		return loadedPlugins.containsKey(name);
	}
	
	public boolean isPluginInstalled(String name, String author) {
		if(loadedPlugins.containsKey(name))
			return loadedPlugins.get(name).contains(author);
		
		return false;
	}
	
	// Send a message to the console including the plugin prefix
	public void sendConsole(String msg) {
		if(msg != null)
			Bukkit.getConsoleSender().sendMessage(prefix + msg);
	}

	// Check if version is newer than 1.12.2
	public boolean isVersion13OrLater() {
		return (Integer.parseInt(EazyNick.getInstance().getVersion().split("_")[1]) > 12);
	}
	
	// Returns the online player count
	public int getOnlinePlayerCount() {
		return Bukkit.getOnlinePlayers().size();
	}
	
	// Reload all configuration files
	public void reloadConfigs() {
		EazyNick eazyNick = EazyNick.getInstance();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		GUIYamlFile guiYamlFile = eazyNick.getGUIYamlFile();
		NickNameYamlFile nickNameYamlFile = eazyNick.getNickNameYamlFile();
		
		// Reload configurations
		setupYamlFile.reload();
		nickNameYamlFile.reload();
		guiYamlFile.reload();
		languageYamlFile.reload();
		
		// Import nickNames from 'nickNames.yml'
		nickNames.clear();
		nickNames.addAll(nickNameYamlFile.getConfiguration().getStringList("NickNames"));

		// Import from 'setup.yml'
		replaceNameInCommandBlackList.clear();
		replaceNameInCommandBlackList.addAll(setupYamlFile.getConfiguration().getStringList("ReplaceNameInCommandBlackList"));

		blackList.clear();
		blackList.addAll(setupYamlFile.getConfiguration().getStringList("BlackList"));

		worldsWithDisabledLobbyMode.clear();
		worldsWithDisabledLobbyMode.addAll(setupYamlFile.getConfiguration().getStringList("WorldsWithDisabledLobbyMode"));

		worldsWithDisabledPrefixAndSuffix.clear();
		worldsWithDisabledPrefixAndSuffix.addAll(setupYamlFile.getConfiguration().getStringList("WorldsWithDisabledPrefixAndSuffix"));

		worldsWithDisabledActionBar.clear();
		worldsWithDisabledActionBar.addAll(setupYamlFile.getConfiguration().getStringList("WorldsWithDisabledActionBar"));

		worldBlackList.clear();
		worldBlackList.addAll(setupYamlFile.getConfiguration().getStringList("AutoNickWorldBlackList"));

		mineSkinUUIDs.clear();
		mineSkinUUIDs.addAll(setupYamlFile.getConfiguration().getStringList("MineSkinUUIDs"));

		// Store common messages
		this.prefix = languageYamlFile.getConfigString("Messages.Prefix");
		this.noPerm = languageYamlFile.getConfigString("Messages.NoPerm");
		this.notPlayer = languageYamlFile.getConfigString("Messages.NotPlayer");
	}

	// BookGUI & RankedNickGUI
	public void performRankedNick(Player player, String rankName, String skinType, String name) {
		EazyNick eazyNick = EazyNick.getInstance();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		GUIYamlFile guiYamlFile = eazyNick.getGUIYamlFile();
		NMSBookUtils nmsBookUtils = eazyNick.getNMSBookUtils();
		NMSBookBuilder nmsBookBuilder = eazyNick.getNMSBookBuilder();
		
		// Initialize nick data
		String chatPrefix = "",
				chatSuffix = "",
				tabPrefix = "",
				tabSuffix = "",
				tagPrefix = "",
				tagSuffix = "",
				nameWithoutColors = new StringUtils(name).getPureString();
		String skinName;
		int sortID = 9999,
				nameLengthMin = Math.max(Math.min(setupYamlFile.getConfiguration().getInt("Settings.NameLength.Min"), 16), 1),
				nameLengthMax = Math.max(Math.min(setupYamlFile.getConfiguration().getInt("Settings.NameLength.Max"), 16), 1);
		
		// Check if name length is valid
		if(nameWithoutColors.length() > nameLengthMax) {
			languageYamlFile.sendMessage(
					player,
					languageYamlFile.getConfigString(player, "Messages.NickTooLong")
							.replace("%prefix%", prefix)
			);
			return;
		}

		if(nameWithoutColors.length() < nameLengthMin) {
			languageYamlFile.sendMessage(
					player,
					languageYamlFile.getConfigString(player, "Messages.NickTooShort")
							.replace("%prefix%", prefix)
			);
			return;
		}

		if(containsSpecialChars(nameWithoutColors)
				&& !(setupYamlFile.getConfiguration().getBoolean("AllowSpecialCharactersInCustomName"))) {
			languageYamlFile.sendMessage(
					player,
					languageYamlFile.getConfigString(player, "Messages.NickContainsSpecialCharacters")
							.replace("%prefix%", prefix)
			);
			return;
		}

		if(containsBlackListEntry(nameWithoutColors)) {
			languageYamlFile.sendMessage(
					player,
					languageYamlFile.getConfigString(player, "Messages.NameNotAllowed")
							.replace("%prefix%", prefix)
			);
			return;
		}

		if(nickedPlayers
				.values()
				.stream()
				.anyMatch(currentNickedPlayerData -> currentNickedPlayerData.getNickName().equalsIgnoreCase(name))
				&& !(setupYamlFile.getConfiguration().getBoolean("AllowPlayersToUseSameNickName"))) {
			languageYamlFile.sendMessage(
					player,
					languageYamlFile.getConfigString(player, "Messages.NickNameAlreadyInUse")
							.replace("%prefix%", prefix)
			);
			return;
		}

		if(
				(
						Bukkit.getOnlinePlayers()
								.stream()
								.anyMatch(currentPlayer -> currentPlayer.getName().equalsIgnoreCase(name))
								|| Stream.of(Bukkit.getOfflinePlayers())
								.anyMatch(currentOfflinePlayer -> name.equalsIgnoreCase(currentOfflinePlayer.getName()))
				)
						&& !(setupYamlFile.getConfiguration().getBoolean("AllowPlayersToNickAsKnownPlayers"))
		) {
			languageYamlFile.sendMessage(
					player,
					languageYamlFile.getConfigString(player, "Messages.PlayerWithThisNameIsKnown")
							.replace("%prefix%", prefix)
			);
			return;
		}

		// Import rank data from 'guis.yml'
		String groupName = "";

		for (int i = 1; i <= 18; i++) {
			String permission = guiYamlFile.getConfigString("RankGUI.Rank" + i + ".Permission");

			if(rankName.equalsIgnoreCase(guiYamlFile.getConfiguration().getString("RankGUI.Rank" + i + ".RankName"))
					&& guiYamlFile.getConfiguration().getBoolean("RankGUI.Rank" + i + ".Enabled")
					&& (
							permission.equalsIgnoreCase("NONE")
							|| player.hasPermission(permission)
					)
			) {
				chatPrefix = guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".ChatPrefix");
				chatSuffix = guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".ChatSuffix");
				tabPrefix = guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".TabPrefix");
				tabSuffix = guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".TabSuffix");
				tagPrefix = guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".TagPrefix");
				tagSuffix = guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".TagSuffix");
				groupName = guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".GroupName");
				sortID = guiYamlFile.getConfiguration().getInt("Settings.NickFormat.Rank" + i + ".SortID");
			}
		}

		if(groupName.isEmpty())
			return;

		//Add support for %randomColor% placeholder
		String randomColor = "§" + ("0123456789abcdef".charAt(new Random().nextInt(16)));

		chatPrefix = chatPrefix
				.replace("%randomColor%", randomColor)
				.replace("%randomcolor%", randomColor);
		chatSuffix = chatSuffix
				.replace("%randomColor%", randomColor)
				.replace("%randomcolor%", randomColor);
		tabPrefix = tabPrefix
				.replace("%randomColor%", randomColor)
				.replace("%randomcolor%", randomColor);
		tabSuffix = tabSuffix
				.replace("%randomColor%", randomColor)
				.replace("%randomcolor%", randomColor);
		tagPrefix = tagPrefix
				.replace("%randomColor%", randomColor)
				.replace("%randomcolor%", randomColor);
		tagSuffix = tagSuffix
				.replace("%randomColor%", randomColor)
				.replace("%randomcolor%", randomColor);

		//Set skin name depending on skin type
		if(skinType.equalsIgnoreCase("DEFAULT"))
			skinName = player.getName();
		else if(skinType.equalsIgnoreCase("NORMAL"))
			skinName = new Random().nextBoolean() ? "Steve" : "Alex";
		else if(skinType.equalsIgnoreCase("RANDOM"))
			skinName = setupYamlFile.getConfiguration().getBoolean("UseMineSkinAPI")
					? ("MINESKIN:" + getRandomStringFromList(mineSkinUUIDs))
					: nickNames.get(new Random().nextInt(getNickNames().size()));
		else if(skinType.equalsIgnoreCase("SKINFROMNAME"))
			skinName = name;
		else
			skinName = skinType;

		//Update last name & skin cache
		lastSkinNames.put(player.getUniqueId(), skinName.startsWith("MINESKIN:") ? "RANDOM" : skinName);
		lastNickNames.put(player.getUniqueId(), name);

		//Fix book still in inventory bug
		for (ItemStack item : player.getInventory().getContents()) {
			if((item != null) && item.getType().equals(Material.WRITTEN_BOOK) && (item.getItemMeta() != null)) {
				if(Objects.equals(((BookMeta) item.getItemMeta()).getAuthor(), eazyNick.getDescription().getName()))
					player.getInventory().remove(item);
			}
		}

		//Add nick data to mysql or nick player
		new NickManager(player).setGroupName(groupName);

		if(setupYamlFile.getConfiguration().getBoolean("BungeeCord")
				&& !(player.hasPermission("eazynick.bypasslobbymode")
				&& setupYamlFile.getConfiguration().getBoolean("EnableBypassLobbyModePermission"))
				&& setupYamlFile.getConfiguration().getBoolean("LobbyMode")
				&& !(worldsWithDisabledLobbyMode.contains(player.getWorld().getName().toUpperCase()))) {
			eazyNick.getMySQLNickManager().addPlayer(player.getUniqueId(), name, skinName);
			eazyNick.getMySQLPlayerDataManager().insertData(
					player.getUniqueId(),
					"NONE",
					chatPrefix,
					chatSuffix,
					tabPrefix,
					tabSuffix,
					tagPrefix,
					tagSuffix
			);

			if(guiYamlFile.getConfiguration().getBoolean("BookGUI.Page6.Enabled")
					&& !(eazyNick.getVersion().equals("1_7_R4"))) {
				ArrayList<TextComponent> textComponents = new ArrayList<>();

				Arrays.asList(guiYamlFile.getConfigString(player, "BookGUI.Page6.Text.BungeeCord")
						.replace("%name%", chatPrefix + name + chatSuffix)
						.split("%nl%"))
						.forEach(s -> textComponents.add(new TextComponent(s + "\n")));

				nmsBookUtils.open(player, nmsBookBuilder.create("Done", new BookPage(textComponents)));
			}
		} else {
			PlayerNickEvent playerNickEvent = new PlayerNickEvent(
					player,
					name,
					skinName,
					setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.UUID")
							? (
									eazyNick.getVersion().startsWith("1_7")
											? eazyNick.getUUIDFetcher_1_7().getUUID(name)
											: (
													eazyNick.getVersion().equals("1_8_R1")
															? eazyNick.getUUIDFetcher_1_8_R1().getUUID(name)
															: eazyNick.getUUIDFetcher().getUUID(name)
									)
							)
							: player.getUniqueId(),
					chatPrefix,
					chatSuffix,
					tabPrefix,
					tabSuffix,
					tagPrefix,
					tagSuffix,
					false,
					false,
					sortID,
					groupName
			);

			Bukkit.getPluginManager().callEvent(playerNickEvent);

			if(!(playerNickEvent.isCancelled())) {
				String finalChatPrefix = chatPrefix, finalChatSuffix = chatSuffix;

				new AsyncTask(new AsyncRunnable() {

					@Override
					public void run() {
						Bukkit.getScheduler().runTask(eazyNick, () -> {
							if(guiYamlFile.getConfiguration().getBoolean("BookGUI.Page6.Enabled")
									&& !(eazyNick.getVersion().equals("1_7_R4"))) {
								ArrayList<TextComponent> textComponents = new ArrayList<>();

								Arrays.asList(guiYamlFile.getConfigString(player, "BookGUI.Page6.Text.SingleServer")
										.replace("%name%", finalChatPrefix + name + finalChatSuffix)
										.split("%nl%"))
										.forEach(s -> textComponents.add(new TextComponent(s + "\n")));

								nmsBookUtils.open(player, nmsBookBuilder.create("Done", new BookPage(textComponents)));
							}
						});
					}
				}, 400 + (
						setupYamlFile.getConfiguration().getBoolean("RandomDisguiseDelay")
								? 2000 : 0
				)).run();
			}
		}
	}

	// Command 'nick', 'nickother', 'nickgui' -> Nick & nick item
	public void performNick(Player player, String customNickName) {
		EazyNick eazyNick = EazyNick.getInstance();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		
		//Process name
		String name = customNickName.equals("RANDOM")
				? nickNames.get((new Random().nextInt(nickNames.size())))
				: customNickName;
		
		// Make sure that random nick is not being used
		if(customNickName.equals("RANDOM") && !(setupYamlFile.getConfiguration().getBoolean("AllowPlayersToUseSameNickName"))) {
			AtomicReference<String> currentName = new AtomicReference<>();

			do {
				currentName.set(nickNames.get((new Random().nextInt(nickNames.size()))));
			} while (nickedPlayers
					.values()
					.stream()
					.anyMatch(currentNickedPlayerData -> currentNickedPlayerData.getNickName().equalsIgnoreCase(currentName.get())));
		}
		
		//Collect nick data
		boolean serverFull = getOnlinePlayerCount() >= Bukkit.getMaxPlayers();
		String nameWithoutColors = ChatColor.stripColor(name);
		String[] prefixSuffix = new String[] {};
		String chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix;
		
		try {
			prefixSuffix = name.split(nameWithoutColors);
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
			chatPrefix = (serverFull
					? setupYamlFile.getConfigString("Settings.NickFormat.ServerFullRank.Chat.Prefix")
					: setupYamlFile.getConfigString("Settings.NickFormat.Chat.Prefix"));
			chatSuffix = (serverFull
					? setupYamlFile.getConfigString("Settings.NickFormat.ServerFullRank.Chat.Suffix")
					: setupYamlFile.getConfigString("Settings.NickFormat.Chat.Suffix"));
			tabPrefix = (serverFull
					? setupYamlFile.getConfigString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix")
					: setupYamlFile.getConfigString("Settings.NickFormat.PlayerList.Prefix"));
			tabSuffix = (serverFull
					? setupYamlFile.getConfigString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix")
					: setupYamlFile.getConfigString("Settings.NickFormat.PlayerList.Suffix"));
			tagPrefix = (serverFull
					? setupYamlFile.getConfigString("Settings.NickFormat.ServerFullRank.NameTag.Prefix")
					: setupYamlFile.getConfigString("Settings.NickFormat.NameTag.Prefix"));
			tagSuffix = (serverFull
					? setupYamlFile.getConfigString("Settings.NickFormat.ServerFullRank.NameTag.Suffix")
					: setupYamlFile.getConfigString("Settings.NickFormat.NameTag.Suffix"));
		}

		// Nick player
		new NickManager(player).setGroupName(serverFull
				? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.GroupName")
				: setupYamlFile.getConfigString(player, "Settings.NickFormat.GroupName")
		);
		
		Bukkit.getPluginManager().callEvent(
				new PlayerNickEvent(
						player,
						nameWithoutColors,
						setupYamlFile.getConfiguration().getBoolean("UseMineSkinAPI")
								? ("MINESKIN:" + getRandomStringFromList(mineSkinUUIDs))
								: nameWithoutColors,
						setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.UUID")
								? (
										eazyNick.getVersion().startsWith("1_7")
												? eazyNick.getUUIDFetcher_1_7().getUUID(name)
												: (
														eazyNick.getVersion().equals("1_8_R1")
																? eazyNick.getUUIDFetcher_1_8_R1().getUUID(name)
																: eazyNick.getUUIDFetcher().getUUID(name)))
								: player.getUniqueId(),
						chatPrefix,
						chatSuffix,
						tabPrefix,
						tabSuffix,
						tagPrefix,
						tagSuffix,
						false,
						false,
						serverFull
								? setupYamlFile.getConfiguration().getInt("Settings.NickFormat.ServerFullRank.SortID")
								: setupYamlFile.getConfiguration().getInt("Settings.NickFormat.SortID"),
						serverFull
								? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.GroupName")
								: setupYamlFile.getConfigString(player, "Settings.NickFormat.GroupName")
				)
		);
	}
	
	public void performReNick(Player player) {
		// Nick player
		performReNick(
				player,
				nickOnWorldChangePlayers.contains(player.getUniqueId())
						? nickNames.get((new Random().nextInt(nickNames.size())))
						: EazyNick.getInstance().getMySQLNickManager().getNickName(player.getUniqueId())
		);
	}
	
	public void performReNick(Player player, String name) {
		if(!(new NickManager(player).isNicked())) {
			EazyNick eazyNick = EazyNick.getInstance();
			SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
			GUIYamlFile guiYamlFile = eazyNick.getGUIYamlFile();
			LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
			MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
			MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMySQLPlayerDataManager();

			if(nickedPlayers.values().stream().noneMatch(currentNickedPlayerData -> currentNickedPlayerData.getNickName().equalsIgnoreCase(name))
					|| setupYamlFile.getConfiguration().getBoolean("AllowPlayersToUseSameNickName")) {
				// Check if player is known on the server (in usercache.json)
				if(setupYamlFile.getConfiguration().getBoolean("AllowPlayersToNickAsKnownPlayers")
						|| (
								Bukkit.getOnlinePlayers().stream().noneMatch(currentPlayer -> currentPlayer.getName().equalsIgnoreCase(name))
								&& Stream.of(Bukkit.getOfflinePlayers()).noneMatch(currentOfflinePlayer -> name.equalsIgnoreCase(currentOfflinePlayer.getName()))
						)
				) {
					UUID spoofedUniqueId = setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.UUID")
							? (
									eazyNick.getVersion().startsWith("1_7")
											? eazyNick.getUUIDFetcher_1_7().getUUID(name)
											: (
													eazyNick.getVersion().equals("1_8_R1")
															? eazyNick.getUUIDFetcher_1_8_R1().getUUID(name)
															: eazyNick.getUUIDFetcher().getUUID(name)
											)
							)
							: player.getUniqueId();
					
					// Check if the nickname is different from the real name
					if(!(name.equalsIgnoreCase(player.getName()))) {
						if((mysqlPlayerDataManager != null) && mysqlPlayerDataManager.isRegistered(player.getUniqueId())) {
							new NickManager(player).setGroupName("Default");
							
							if(setupYamlFile.getConfiguration().contains("UseLocalRankPrefixes")) {
								// Use MySQL data as fallback data
								String groupName = mysqlPlayerDataManager.getGroupName(player.getUniqueId()),
										chatPrefix = mysqlPlayerDataManager.getChatPrefix(player.getUniqueId()),
										chatSuffix = mysqlPlayerDataManager.getChatSuffix(player.getUniqueId()),
										tabPrefix = mysqlPlayerDataManager.getTabPrefix(player.getUniqueId()),
										tabSuffix = mysqlPlayerDataManager.getTabSuffix(player.getUniqueId()),
										tagPrefix = mysqlPlayerDataManager.getTagPrefix(player.getUniqueId()),
										tagSuffix = mysqlPlayerDataManager.getTagSuffix(player.getUniqueId());
								int sortID = 9999;
								
								// Import from 'setup.yml'
								if(groupName.equalsIgnoreCase(setupYamlFile.getConfigString("Settings.NickFormat.GroupName"))
										|| groupName.equalsIgnoreCase(setupYamlFile.getConfigString("Settings.NickFormat.ServerFullRank.GroupName"))) {
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
											
											chatPrefix = chatPrefix
													.replace("%randomColor%", randomColor)
													.replace("%randomcolor%", randomColor);
											chatSuffix = chatSuffix
													.replace("%randomColor%", randomColor)
													.replace("%randomcolor%", randomColor);
											tabPrefix = tabPrefix
													.replace("%randomColor%", randomColor)
													.replace("%randomcolor%", randomColor);
											tabSuffix = tabSuffix
													.replace("%randomColor%", randomColor)
													.replace("%randomcolor%", randomColor);
											tagPrefix = tagPrefix
													.replace("%randomColor%", randomColor)
													.replace("%randomcolor%", randomColor);
											tagSuffix = tagSuffix
													.replace("%randomColor%", randomColor)
													.replace("%randomcolor%", randomColor);
											
											break;
										}
									}
								}
								
								// Nick player
								Bukkit.getPluginManager().callEvent(
										new PlayerNickEvent(
												player,
												name,
												mysqlNickManager.getSkinName(player.getUniqueId()),
												spoofedUniqueId,
												chatPrefix,
												chatSuffix,
												tabPrefix,
												tabSuffix,
												tagPrefix,
												tagSuffix,
												true,
												false,
												sortID,
												mysqlPlayerDataManager.getGroupName(player.getUniqueId())
										)
								);
							} else
								// Nick player using MySQL data
								Bukkit.getPluginManager().callEvent(
										new PlayerNickEvent(player, name, mysqlNickManager.getSkinName(player.getUniqueId()), spoofedUniqueId, mysqlPlayerDataManager.getChatPrefix(player.getUniqueId()), mysqlPlayerDataManager.getChatSuffix(player.getUniqueId()), mysqlPlayerDataManager.getTabPrefix(player.getUniqueId()), mysqlPlayerDataManager.getTabSuffix(player.getUniqueId()), mysqlPlayerDataManager.getTagPrefix(player.getUniqueId()), mysqlPlayerDataManager.getTagSuffix(player.getUniqueId()), true, false, 9999, mysqlPlayerDataManager.getGroupName(player.getUniqueId())));
						} else {
							// Import data from 'setup.yml'
							boolean serverFull = getOnlinePlayerCount() >= Bukkit.getMaxPlayers();
							String prefix = serverFull
									? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.NameTag.Prefix")
									: setupYamlFile.getConfigString(player, "Settings.NickFormat.NameTag.Prefix");
							String suffix = serverFull
									? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.NameTag.Suffix")
									: setupYamlFile.getConfigString(player, "Settings.NickFormat.NameTag.Suffix");
						
							// Nick player
							new NickManager(player).setGroupName(
									serverFull
											? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.GroupName")
											: setupYamlFile.getConfigString(player, "Settings.NickFormat.GroupName")
							);
							
							Bukkit.getPluginManager().callEvent(
									new PlayerNickEvent(
											player,
											name,
											mysqlNickManager.getSkinName(player.getUniqueId()),
											spoofedUniqueId,
											serverFull
													? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.Chat.Prefix")
													: setupYamlFile.getConfigString(player, "Settings.NickFormat.Chat.Prefix"),
											serverFull
													? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.Chat.Suffix")
													: setupYamlFile.getConfigString(player, "Settings.NickFormat.Chat.Suffix"),
											serverFull
													? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.PlayerList.Prefix")
													: setupYamlFile.getConfigString(player, "Settings.NickFormat.PlayerList.Prefix"),
											serverFull
													? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.PlayerList.Suffix")
													: setupYamlFile.getConfigString(player, "Settings.NickFormat.PlayerList.Suffix"),
											prefix,
											suffix,
											true,
											false,
											(getOnlinePlayerCount() >= Bukkit.getMaxPlayers())
													? setupYamlFile.getConfiguration().getInt("Settings.NickFormat.ServerFullRank.SortID")
													: setupYamlFile.getConfiguration().getInt("Settings.NickFormat.GrSortIDoupName"),
											(getOnlinePlayerCount() >= Bukkit.getMaxPlayers())
													? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.GroupName")
													: setupYamlFile.getConfigString(player, "Settings.NickFormat.GroupName")
									)
							);
						}
					} else
						languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.CanNotNickAsSelf").replace("%prefix%", prefix));
				} else
					languageYamlFile.sendMessage(
							player,
							languageYamlFile.getConfigString(player, "Messages.PlayerWithThisNameIsKnown")
									.replace("%prefix%", prefix)
					);
			} else
				languageYamlFile.sendMessage(
						player,
						languageYamlFile.getConfigString(player, "Messages.NickNameAlreadyInUse")
								.replace("%prefix%", prefix)
				);
		}
	}
	
	//Command 'togglebungeenick', nick item
	public void toggleBungeeNick(Player player) {
		EazyNick eazyNick = EazyNick.getInstance();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
		MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
		MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMySQLPlayerDataManager();
		
		try {
			// Check if the player has a nick item
			ItemStack itemInHand = (eazyNick.getVersion().startsWith("1_8")
					|| eazyNick.getVersion().startsWith("1_7"))
							? (ItemStack) player.getClass().getMethod("getItemInHand").invoke(player)
							: player.getInventory().getItemInMainHand();
			boolean hasItem = (itemInHand != null)
					&& (itemInHand.getType() != Material.AIR)
					&& (itemInHand.getItemMeta() != null)
					&& (
							itemInHand.getItemMeta().getDisplayName().equalsIgnoreCase(languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Disabled"))
							|| itemInHand.getItemMeta().getDisplayName().equalsIgnoreCase(languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Enabled"))
					);
			
			if(setupYamlFile.getConfiguration().getBoolean("NeedItemToToggleNick") && !(hasItem))
				return;
			
			if(mysqlNickManager.isPlayerNicked(player.getUniqueId())) {
				// Remove the player from MySQL
				mysqlNickManager.removePlayer(player.getUniqueId());
				mysqlPlayerDataManager.removeData(player.getUniqueId());
				
				// Update the nick item
				if(hasItem)
					player.getInventory().setItem(
							player.getInventory().getHeldItemSlot(),
							new ItemBuilder(
									Material.getMaterial(setupYamlFile.getConfigString("NickItem.ItemType.Disabled")),
									setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Disabled"),
									setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Disabled")
							)
									.setDisplayName(languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Disabled"))
									.setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Disabled").split("&n"))
									.setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Disabled"))
									.build()
					);
	
				languageYamlFile.sendMessage(
						player,
						languageYamlFile.getConfigString(player, "Messages.BungeeAutoNickDisabled").replace("%prefix%", prefix)
				);
			} else {
				String name = nickNames.get((new Random().nextInt(nickNames.size())));
	
				// Add the player to MySQL
				mysqlNickManager.addPlayer(player.getUniqueId(), name, name);
				
				// Update the nick item
				if(hasItem)
					player.getInventory().setItem(
							player.getInventory().getHeldItemSlot(),
							new ItemBuilder(
									Material.getMaterial(setupYamlFile.getConfigString("NickItem.ItemType.Enabled")),
									setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Enabled"),
									setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Enabled")
							)
									.setDisplayName(languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Enabled"))
									.setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Enabled").split("&n"))
									.setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Enabled"))
									.build()
					);
	
				languageYamlFile.sendMessage(
						player,
						languageYamlFile.getConfigString(player, "Messages.BungeeAutoNickEnabled").replace("%prefix%", prefix)
				);
			}
		} catch (Exception ignore) {
		}
	}
	
	// Get a random string from a list
	public String getRandomStringFromList(List<String> list) {
		return (list.isEmpty() ? "" : list.get((new Random()).nextInt(list.size())));
	}
	
	//String contains characters that are not allowed in minecraft usernames (allowed: a-z, A-Z, 0-9, _)
	public boolean containsSpecialChars(String str) {
		return !(new HashSet<>(Chars.asList("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_".toCharArray()))
				.containsAll(Chars.asList(str.toCharArray())));
	}
	
	public boolean containsBlackListEntry(String str) {
		String lowerCaseString = str.toLowerCase();

		return blackList.stream().anyMatch(currentEntry -> currentEntry.contains(lowerCaseString));
	}

	//Default game profile (Steve)
	public GameProfile getDefaultGameProfile() {
		GameProfile gameProfile = new GameProfile(getDefaultUniqueId(), getDefaultName());
		gameProfile.getProperties().put(
				"textures",
				new Property("textures", getDefaultSkinValue(), getDefaultSkinSignature())
		);
		
		return gameProfile;
	}
	
	//Default 1.7 game profile ("Steve")
	public net.minecraft.util.com.mojang.authlib.GameProfile getDefaultGameProfile_1_7() {
		net.minecraft.util.com.mojang.authlib.GameProfile gameProfile =
				new net.minecraft.util.com.mojang.authlib.GameProfile(getDefaultUniqueId(), getDefaultName());
		gameProfile.getProperties().put(
				"textures",
				new net.minecraft.util.com.mojang.authlib.properties.Property(
						"textures",
						getDefaultSkinValue(),
						getDefaultSkinSignature()
				)
		);
		
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

	public List<String> getWorldsWithDisabledPrefixAndSuffix() {
		return worldsWithDisabledPrefixAndSuffix;
	}
	
	public List<String> getWorldsWithDisabledActionBar() {
		return worldsWithDisabledActionBar;
	}
	
	public List<String> getWorldBlackList() {
		return worldBlackList;
	}
	
	public List<String> getMineSkinUUIDs() {
		return mineSkinUUIDs;
	}
	
	public List<UUID> getNickOnWorldChangePlayers() {
		return nickOnWorldChangePlayers;
	}
	
	public Map<UUID, NickedPlayerData> getNickedPlayers() {
		return nickedPlayers;
	}
	
	public Map<UUID, NickedPlayerData> getLastNickData() {
		return lastNickData;
	}
	
	public Map<UUID, Long> getCanUseNick() {
		return canUseNick;
	}
	
	public Map<UUID, Integer> getOldExperienceLevels() {
		return oldExperienceLevels;
	}
	
	public Map<UUID, Integer> getNickNameListPages() {
		return nickNameListPages;
	}
	
	public Map<UUID, String[]> getOldPermissionsExGroups() {
		return oldPermissionsExGroups;
	}
	
	public Map<UUID, String> getOldPermissionsExPrefixes() {
		return oldPermissionsExPrefixes;
	}
	
	public Map<UUID, String> getOldPermissionsExSuffixes() {
		return oldPermissionsExSuffixes;
	}
	
	public Map<UUID, String> getOldCloudNETPrefixes() {
		return oldCloudNETPrefixes;
	}
	
	public Map<UUID, String> getOldCloudNETSuffixes() {
		return oldCloudNETSuffixes;
	}
	
	public Map<UUID, Integer> getOldCloudNETTagIDs() {
		return oldCloudNETTagIDs;
	}
	
	public Map<UUID, Object> getOldLuckPermsGroups() {
		return oldLuckPermsGroups;
	}
	
	public Map<UUID, Object> getLuckPermsPrefixes() {
		return luckPermsPrefixes;
	}
	
	public Map<UUID, Object> getLuckPermsSuffixes() {
		return luckPermsSuffixes;
	}
	
	public Map<UUID, String> getNametagEditPrefixes() {
		return nametagEditPrefixes;
	}
	
	public Map<UUID, String> getNametagEditSuffixes() {
		return nametagEditSuffixes;
	}
	
	public Map<UUID, ScoreboardTeamHandler> getScoreboardTeamManagers() {
		return scoreboardTeamHandlers;
	}
	
	public Map<UUID, String> getLastNickNames() {
		return lastNickNames;
	}
	
	public Map<UUID, String> getLastSkinNames() {
		return lastSkinNames;
	}
	
	public Map<UUID, String> getLastGUITexts() {
		return lastGUITexts;
	}
	
	public Map<UUID, String> getPlayersTypingNameInChat() {
		return playersTypingNameInChat;
	}
	
	public Map<UUID, Object> getIncomingPacketInjectors() {
		return incomingPacketInjectors;
	}
	
	public Map<Player, String> getTextsToComplete() {
		return textsToComplete;
	}

	public List<UUID> getSoonNickedPlayers() {
		return soonNickedPlayers;
	}

	public Map<String, List<String>> getLoadedPlugins() {
		return loadedPlugins;
	}

	public void setLastChatMessage(String lastChatMessage) {
		this.lastChatMessage = lastChatMessage;
	}

	public void setSupportMode(boolean supportMode) {
		this.supportMode = supportMode;
	}

}
