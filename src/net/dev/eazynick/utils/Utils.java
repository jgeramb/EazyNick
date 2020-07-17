package net.dev.eazynick.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.api.PlayerNickEvent;
import net.dev.eazynick.sql.MySQLNickManager;
import net.dev.eazynick.sql.MySQLPlayerDataManager;
import net.dev.eazynick.utils.anvilutils.AnvilGUI;
import net.dev.eazynick.utils.bookutils.NMSBookBuilder;
import net.dev.eazynick.utils.bookutils.NMSBookUtils;
import net.dev.eazynick.utils.scoreboard.ScoreboardTeamManager;
import net.dev.eazynick.utils.signutils.SignGUI;
import net.md_5.bungee.api.chat.TextComponent;

public class Utils {

	private String prefix;
	private String noPerm;
	private String notPlayer;
	private String lastChatMessage = "NONE";

	private Field nameField, uuidField;
	
	private List<String> nickNames = new ArrayList<>();
	private List<String> blackList = new ArrayList<>();
	private List<String> worldsWithDisabledPrefixAndSuffix = new ArrayList<>();
	private List<String> worldBlackList = new ArrayList<>();
	private List<String> mineSkinIds = new ArrayList<>();
	private ArrayList<UUID> nickedPlayers = new ArrayList<>();
	private ArrayList<UUID> nickOnWorldChangePlayers = new ArrayList<>();
	private HashMap<UUID, String> playerNicknames = new HashMap<>();
	private HashMap<UUID, String> oldDisplayNames = new HashMap<>();
	private HashMap<UUID, String> oldPlayerListNames = new HashMap<>();
	private HashMap<UUID, Boolean> canUseNick = new HashMap<>();
	private HashMap<UUID, Integer> nickNameListPages = new HashMap<>();
	private HashMap<UUID, String[]> oldPermissionsExGroups = new HashMap<>();
	private HashMap<UUID, String> oldPermissionsExPrefixes = new HashMap<>();
	private HashMap<UUID, String> oldPermissionsExSuffixes = new HashMap<>();
	private HashMap<UUID, String> oldCloudNETPrefixes = new HashMap<>();
	private HashMap<UUID, String> oldCloudNETSuffixes = new HashMap<>();
	private HashMap<UUID, Integer> oldCloudNETTagIDS = new HashMap<>();
	private HashMap<UUID, String> oldLuckPermsGroups = new HashMap<>();
	private HashMap<UUID, Object> luckPermsPrefixes = new HashMap<>();
	private HashMap<UUID, Object> luckPermsSuffixes = new HashMap<>();
	private HashMap<UUID, HashMap<String, Long>> oldUltraPermissionsGroups = new HashMap<>();
	private HashMap<UUID, String> ultraPermissionsPrefixes = new HashMap<>();
	private HashMap<UUID, String> ultraPermissionsSuffixes = new HashMap<>();
	private HashMap<UUID, String> nametagEditPrefixes = new HashMap<>();
	private HashMap<UUID, String> nametagEditSuffixes = new HashMap<>();
	private HashMap<UUID, ScoreboardTeamManager> scoreboardTeamManagers = new HashMap<>();
	private HashMap<UUID, String> nameCache = new HashMap<>();
	private HashMap<UUID, String> lastSkinNames = new HashMap<>();
	private HashMap<UUID, String> lastNickNames = new HashMap<>();
	private HashMap<UUID, String> chatPrefixes = new HashMap<>();
	private HashMap<UUID, String> chatSuffixes = new HashMap<>();
	private HashMap<UUID, String> tabPrefixes = new HashMap<>();
	private HashMap<UUID, String> tabSuffixes = new HashMap<>();
	private HashMap<UUID, String> groupNames = new HashMap<>();
	private HashMap<UUID, String> lastGUITexts = new HashMap<>();
	private HashMap<UUID, String> playersTypingNameInChat = new HashMap<>();

	public boolean placeholderAPIStatus() {
		return (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null);
	}
	
	public boolean cloudNetStatus() {
		return (Bukkit.getPluginManager().getPlugin("CloudNetAPI") != null);
	}

	public boolean coloredTagsStatus() {
		return (Bukkit.getPluginManager().getPlugin("ColoredTags") != null);
	}

	public boolean nameTagEditStatus() {
		return (Bukkit.getPluginManager().getPlugin("NametagEdit") != null);
	}

	public boolean permissionsExStatus() {
		return (Bukkit.getPluginManager().getPlugin("PermissionsEx") != null);
	}
	
	public boolean luckPermsStatus() {
		return (Bukkit.getPluginManager().getPlugin("LuckPerms") != null);
	}
	
	public boolean datenschutzStatus() {
		return (Bukkit.getPluginManager().getPlugin("Datenschutz") != null);
	}
	
	public boolean ultraPermissionsStatus() {
		return (Bukkit.getPluginManager().getPlugin("UltraPermissions") != null);
	}
	
	public boolean vaultStatus() {
		return (Bukkit.getPluginManager().getPlugin("Vault") != null);
	}
	
	public boolean survivalGamesStatus() {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("SurvivalGames");
		
		if(plugin != null) {
			if(plugin.getDescription().getMain().equalsIgnoreCase("me.wazup.survivalgames.SurvivalGames"))
				return true;
		}
			
		return false;
	}
	
	public boolean authMeReloadedStatus(String version) {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("AuthMe");
		
		if(plugin != null) {
			if(plugin.getDescription().getMain().equalsIgnoreCase("fr.xephi.authme.AuthMe"))
				return plugin.getDescription().getVersion().contains(version);
		}
			
		return false;
	}
	
	public boolean tabStatus() {
		return (Bukkit.getPluginManager().getPlugin("TAB") != null) && (Bukkit.getPluginManager().getPlugin("TAB").getDescription().getAuthors().contains("NEZNAMY"));
	}
	
	public boolean deluxeChatStatus() {
		return (Bukkit.getPluginManager().getPlugin("DeluxeChat") != null);
	}
	
	public boolean chatControlProStatus() {
		return (Bukkit.getPluginManager().getPlugin("ChatControl") != null);
	}
	
	public boolean skinsRestorerStatus() {
		return (Bukkit.getPluginManager().getPlugin("SkinsRestorer") != null);
	}
	
	public void sendConsole(String msg) {
		Bukkit.getConsoleSender().sendMessage(prefix + msg);
	}

	public boolean isNewVersion() {
		return (Integer.parseInt(EazyNick.getInstance().getVersion().split("_")[1]) > 12);
	}
	
	public int getOnlinePlayerCount() {
		return Bukkit.getOnlinePlayers().size();
	}
	
	public void reloadConfigs() {
		EazyNick eazyNick = EazyNick.getInstance();
		FileUtils fileUtils = eazyNick.getFileUtils();
		LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
		GUIFileUtils guiFileUtils = eazyNick.getGUIFileUtils();
		NickNameFileUtils nickNameFileUtils = eazyNick.getNickNameFileUtils();
		
		new ArrayList<>(nickedPlayers).forEach(uuid -> new NickManager(Bukkit.getPlayer(uuid)).unnickPlayerWithoutRemovingMySQL(false));
		
		fileUtils.setConfig(YamlConfiguration.loadConfiguration(eazyNick.getFileUtils().getFile()));
		fileUtils.saveFile();
		
		nickNameFileUtils.setConfig(YamlConfiguration.loadConfiguration(nickNameFileUtils.getFile()));
		nickNameFileUtils.saveFile();
		
		guiFileUtils.setConfig(YamlConfiguration.loadConfiguration(guiFileUtils.getFile()));
		guiFileUtils.saveFile();
		
		languageFileUtils.setConfig(YamlConfiguration.loadConfiguration(languageFileUtils.getFile()));
		languageFileUtils.saveFile();
		
		this.nickNames = nickNameFileUtils.getConfig().getStringList("NickNames");
		
		List<String> blackList = fileUtils.getConfig().getStringList("BlackList");
		List<String> worldsWithDisabledPrefixAndSuffix = fileUtils.getConfig().getStringList("WorldsWithDisabledPrefixAndSuffix");
		List<String> worldBlackList = fileUtils.getConfig().getStringList("AutoNickWorldBlackList");
		
		if (blackList.size() >= 1) {
			ArrayList<String> toAdd = new ArrayList<>();
			
			for (String blackListName : blackList)
				toAdd.add(blackListName.toUpperCase());
			
			this.blackList = toAdd;
		}

		if (worldsWithDisabledPrefixAndSuffix.size() >= 1) {
			ArrayList<String> toAdd = new ArrayList<>();
			
			for (String worldWithDisabledPrefixAndSuffix : worldsWithDisabledPrefixAndSuffix)
				toAdd.add(worldWithDisabledPrefixAndSuffix.toUpperCase());
			
			this.worldsWithDisabledPrefixAndSuffix = toAdd;
		}
		
		if (worldBlackList.size() >= 1) {
			ArrayList<String> toAdd = new ArrayList<>();
			
			for (String blackListWorld : worldBlackList)
				toAdd.add(blackListWorld.toUpperCase());
			
			this.worldBlackList = toAdd;
		}
		
		this.mineSkinIds = fileUtils.getConfig().getStringList("MineSkinIds");

		this.prefix = languageFileUtils.getConfigString("Messages.Prefix");
		this.noPerm = languageFileUtils.getConfigString("Messages.NoPerm");
		this.notPlayer = languageFileUtils.getConfigString("Messages.NotPlayer");
	}

	public void performRankedNick(Player p, String rankName, String skinType, String name) {
		EazyNick eazyNick = EazyNick.getInstance();
		FileUtils fileUtils = eazyNick.getFileUtils();
		LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
		GUIFileUtils guiFileUtils = eazyNick.getGUIFileUtils();
		NMSBookUtils nmsBookUtils = eazyNick.getNMSBookUtils();
		NMSBookBuilder nmsBookBuilder = eazyNick.getNMSBookBuilder();
		
		String chatPrefix = "", chatSuffix = "", tabPrefix = "", tabSuffix = "", tagPrefix = "", tagSuffix = "";
		String skinName = "";
		boolean isCancelled = false;
		
		if(new StringUtils(name).removeColorCodes().getString().length() <= 16) {
			if(!(blackList.contains(name.toUpperCase()))) {
				boolean nickNameIsInUse = false;
				
				for (String nickName : playerNicknames.values()) {
					if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
						nickNameIsInUse = true;
				}

				if(!(nickNameIsInUse) || fileUtils.getConfig().getBoolean("AllowPlayersToUseSameNickName")) {
					boolean playerWithNameIsKnown = false;
					
					for (Player all : Bukkit.getOnlinePlayers()) {
						if(all.getName().toUpperCase().equalsIgnoreCase(name.toUpperCase()))
							playerWithNameIsKnown = true;
					}
					
					if(Bukkit.getOfflinePlayers() != null) {
						for (OfflinePlayer all : Bukkit.getOfflinePlayers()) {
							if((all != null) && (all.getName() != null) && all.getName().toUpperCase().equalsIgnoreCase(name.toUpperCase()))
								playerWithNameIsKnown = true;
						}
					}
					
					if(!(fileUtils.getConfig().getBoolean("AllowPlayersToNickAsKnownPlayers")) && playerWithNameIsKnown)
						isCancelled = true;
					
					if(!(isCancelled)) {
						String groupName = "";
						
						for (int i = 1; i <= 18; i++) {
							String permission = guiFileUtils.getConfigString("RankGUI.Rank" + i + ".Permission");
							
							if(rankName.equalsIgnoreCase(guiFileUtils.getConfig().getString("RankGUI.Rank" + i + ".RankName")) && guiFileUtils.getConfig().getBoolean("RankGUI.Rank" + i + ".Enabled") && (permission.equalsIgnoreCase("NONE") || p.hasPermission(permission))) {
								chatPrefix = guiFileUtils.getConfigString("Settings.NickFormat.Rank" + i + ".ChatPrefix");
								chatSuffix = guiFileUtils.getConfigString("Settings.NickFormat.Rank" + i + ".ChatSuffix");
								tabPrefix = guiFileUtils.getConfigString("Settings.NickFormat.Rank" + i + ".TabPrefix");
								tabSuffix = guiFileUtils.getConfigString("Settings.NickFormat.Rank" + i + ".TabSuffix");
								tagPrefix = guiFileUtils.getConfigString("Settings.NickFormat.Rank" + i + ".TagPrefix");
								tagSuffix = guiFileUtils.getConfigString("Settings.NickFormat.Rank" + i + ".TagSuffix");
								groupName = guiFileUtils.getConfig().getString("Settings.NickFormat.Rank" + i + ".GroupName");
							}
						}
						
						if(groupName.isEmpty())
							return;
						
						String randomColor = "§" + ("0123456789abcdef".charAt(new Random().nextInt(16)));
						
						chatPrefix = chatPrefix.replaceAll("%randomColor%", randomColor);
						chatSuffix = chatSuffix.replaceAll("%randomColor%", randomColor);
						tabPrefix = tabPrefix.replaceAll("%randomColor%", randomColor);
						tabSuffix = tabSuffix.replaceAll("%randomColor%", randomColor);
						tagPrefix = tagPrefix.replaceAll("%randomColor%", randomColor);
						tagSuffix = tagSuffix.replaceAll("%randomColor%", randomColor);
						
						if(skinType.equalsIgnoreCase("DEFAULT"))
							skinName = p.getName();
						else if(skinType.equalsIgnoreCase("NORMAL"))
							skinName = new Random().nextBoolean() ? "Steve" : "Alex";
						else if(skinType.equalsIgnoreCase("RANDOM"))
							skinName = nickNames.get(new Random().nextInt(getNickNames().size()));
						else if(skinType.equalsIgnoreCase("SKINFROMNAME"))
							skinName = name;
						else
							skinName = skinType;
						
						if(lastSkinNames.containsKey(p.getUniqueId()))
							lastSkinNames.remove(p.getUniqueId());
						
						if(lastNickNames.containsKey(p.getUniqueId()))
							lastNickNames.remove(p.getUniqueId());
						
						lastSkinNames.put(p.getUniqueId(), skinName);
						lastNickNames.put(p.getUniqueId(), name);
						
						new NickManager(p).setGroupName(groupName);
						
						if(fileUtils.getConfig().getBoolean("BungeeCord") && fileUtils.getConfig().getBoolean("LobbyMode")) {
							eazyNick.getMySQLNickManager().addPlayer(p.getUniqueId(), name, skinName);
							eazyNick.getMySQLPlayerDataManager().insertData(p.getUniqueId(), "NONE", chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix);
							
							if(guiFileUtils.getConfig().getBoolean("BookGUI.Page6.Enabled") && !(eazyNick.getVersion().equals("1_7_R4")))
								nmsBookUtils.open(p, nmsBookBuilder.create("Done", new TextComponent(guiFileUtils.getConfigString("BookGUI.Page6.Text.BungeeCord").replace("%name%", tagPrefix + name + tagSuffix))));
						} else {
							Bukkit.getPluginManager().callEvent(new PlayerNickEvent(p, name, skinName, chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix, false, false, groupName));
						
							if(guiFileUtils.getConfig().getBoolean("BookGUI.Page6.Enabled") && !(eazyNick.getVersion().equals("1_7_R4")))
								nmsBookUtils.open(p, nmsBookBuilder.create("Done", new TextComponent(guiFileUtils.getConfigString("BookGUI.Page6.Text.SingleServer").replace("%name%", tagPrefix + name + tagSuffix))));
						}
					} else
						p.sendMessage(prefix + languageFileUtils.getConfigString("Messages.PlayerWithThisNameIsKnown"));
				} else
					p.sendMessage(prefix + languageFileUtils.getConfigString("Messages.NickNameAlreadyInUse"));
			} else
				p.sendMessage(prefix + languageFileUtils.getConfigString("Messages.NameNotAllowed"));
		} else
			p.sendMessage(prefix + languageFileUtils.getConfigString("Messages.NickTooLong"));
	}

	public void openNickList(Player p, int page) {
		EazyNick eazyNick = EazyNick.getInstance();
		GUIFileUtils guiFileUtils = eazyNick.getGUIFileUtils();
		
		Inventory inv = Bukkit.createInventory(null, 45, guiFileUtils.getConfigString("NickNameGUI.InventoryTitle").replace("%currentPage%", String.valueOf(page + 1)));
		ArrayList<String> toShow = new ArrayList<>();
		
		p.openInventory(inv);
		
		for (int i = 36 * page; i < nickNames.size(); i++) {
			if(toShow.size() >= 36)
				break;
			
			toShow.add(nickNames.get(i));
		}
		
		int i = 0;
		
		for (String nickName : toShow) {
			inv.setItem(i, new ItemBuilder(1).setDisplayName(guiFileUtils.getConfigString("NickNameGUI.NickName.DisplayName").replace("%nickName%", nickName)).setSkullOwner((toShow.size() > 12) ? "MHF_Question" : nickName).build());
			i++;
		}
			
		if(page != 0)
			inv.setItem(36, new ItemBuilder(Material.ARROW).setDisplayName(guiFileUtils.getConfigString("NickNameGUI.Previous.DisplayName")).build());
		
		if(nickNames.size() > ((page + 1) * 36))
			inv.setItem(44, new ItemBuilder(Material.ARROW).setDisplayName(guiFileUtils.getConfigString("NickNameGUI.Next.DisplayName")).build());
		
		nickNameListPages.put(p.getUniqueId(), page);
	}

	public void performNick(Player p, String customNickName) {
		EazyNick eazyNick = EazyNick.getInstance();
		FileUtils fileUtils = eazyNick.getFileUtils();
		
		String name = customNickName.equals("RANDOM") ? nickNames.get((new Random().nextInt(nickNames.size()))) : customNickName;
		
		boolean nickNameIsInUse = false;
		
		for (String nickName : playerNicknames.values()) {
			if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
				nickNameIsInUse = true;
		}
		
		while (nickNameIsInUse) {
			nickNameIsInUse = false;
			name = nickNames.get((new Random().nextInt(nickNames.size())));
			
			for (String nickName : playerNicknames.values()) {
				if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
					nickNameIsInUse = true;
			}
		}
		
		boolean serverFull = getOnlinePlayerCount() >= Bukkit.getMaxPlayers();
		String nameWhithoutColors = ChatColor.stripColor(name);
		String[] prefixSuffix = name.split(nameWhithoutColors);
		String chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix;
		
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
			chatPrefix = (serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.Chat.Prefix") : fileUtils.getConfigString("Settings.NickFormat.Chat.Prefix"));
			chatSuffix = (serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.Chat.Suffix") : fileUtils.getConfigString("Settings.NickFormat.Chat.Suffix"));
			tabPrefix = (serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix") : fileUtils.getConfigString("Settings.NickFormat.PlayerList.Prefix"));
			tabSuffix = (serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix") : fileUtils.getConfigString("Settings.NickFormat.PlayerList.Suffix"));
			tagPrefix = (serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.NameTag.Prefix") : fileUtils.getConfigString("Settings.NickFormat.NameTag.Prefix"));
			tagSuffix = (serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.NameTag.Suffix") : fileUtils.getConfigString("Settings.NickFormat.NameTag.Suffix"));
		}

		new NickManager(p).setGroupName(serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.GroupName") : fileUtils.getConfigString("Settings.NickFormat.GroupName"));
		
		Bukkit.getPluginManager().callEvent(new PlayerNickEvent(p, nameWhithoutColors, fileUtils.getConfig().getBoolean("UseMineSkinAPI") ? "MineSkin" : nameWhithoutColors, chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix, false, false, serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.GroupName") : fileUtils.getConfigString("Settings.NickFormat.GroupName")));
	}
	
	public void performReNick(Player p) {
		if(!(new NickManager(p).isNicked())) {
			EazyNick eazyNick = EazyNick.getInstance();
			FileUtils fileUtils = eazyNick.getFileUtils();
			LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
			MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
			MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMySQLPlayerDataManager();
			
			String name = nickOnWorldChangePlayers.contains(p.getUniqueId()) ? nickNames.get((new Random().nextInt(nickNames.size()))) : mysqlNickManager.getNickName(p.getUniqueId());
			boolean isCancelled = false;
			boolean nickNameIsInUse = false;
			
			for (String nickName : playerNicknames.values()) {
				if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
					nickNameIsInUse = true;
			}
			
			while (nickNameIsInUse) {
				nickNameIsInUse = false;
				name = nickNames.get((new Random().nextInt(nickNames.size())));
				
				for (String nickName : playerNicknames.values()) {
					if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
						nickNameIsInUse = true;
				}
			}
			
			if(!(nickNameIsInUse) || fileUtils.getConfig().getBoolean("AllowPlayersToUseSameNickName")) {
				boolean playerWithNameIsKnown = false;
				
				for (Player all : Bukkit.getOnlinePlayers()) {
					if(all.getName().toUpperCase().equalsIgnoreCase(name.toUpperCase()))
						playerWithNameIsKnown = true;
				}
				
				if(Bukkit.getOfflinePlayers() != null) {
					for (OfflinePlayer all : Bukkit.getOfflinePlayers()) {
						if((all != null) && (all.getName() != null) && all.getName().toUpperCase().equalsIgnoreCase(name.toUpperCase()))
							playerWithNameIsKnown = true;
					}
				}
				
				if(!(fileUtils.getConfig().getBoolean("AllowPlayersToNickAsKnownPlayers")) && playerWithNameIsKnown)
					isCancelled = true;
				
				if(!(isCancelled)) {
					if(!(name.equalsIgnoreCase(p.getName()))) {
						if(mysqlPlayerDataManager.isRegistered(p.getUniqueId())) {
							new NickManager(p).setGroupName("Default");
							
							Bukkit.getPluginManager().callEvent(new PlayerNickEvent(p, name, mysqlNickManager.getSkinName(p.getUniqueId()),
									mysqlPlayerDataManager.getChatPrefix(p.getUniqueId()),
									mysqlPlayerDataManager.getChatSuffix(p.getUniqueId()),
									mysqlPlayerDataManager.getTabPrefix(p.getUniqueId()),
									mysqlPlayerDataManager.getTabSuffix(p.getUniqueId()),
									mysqlPlayerDataManager.getTagPrefix(p.getUniqueId()),
									mysqlPlayerDataManager.getTagSuffix(p.getUniqueId()),
									true,
									false,
									"NONE"));
						} else {
							boolean serverFull = getOnlinePlayerCount() >= Bukkit.getMaxPlayers();
							String prefix = serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.NameTag.Prefix") : fileUtils.getConfigString("Settings.NickFormat.NameTag.Prefix");
							String suffix = serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.NameTag.Suffix") : fileUtils.getConfigString("Settings.NickFormat.NameTag.Suffix");
						
							new NickManager(p).setGroupName("ServerFull");
							
							Bukkit.getPluginManager().callEvent(new PlayerNickEvent(p, name, mysqlNickManager.getSkinName(p.getUniqueId()),
									serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.Chat.Prefix") : fileUtils.getConfigString("Settings.NickFormat.Chat.Prefix"),
									serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.Chat.Suffix") : fileUtils.getConfigString("Settings.NickFormat.Chat.Suffix"),
									serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix") : fileUtils.getConfigString("Settings.NickFormat.PlayerList.Prefix"),
									serverFull ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix") : fileUtils.getConfigString("Settings.NickFormat.PlayerList.Suffix"),
									prefix,
									suffix,
									true,
									false,
									(getOnlinePlayerCount() >= Bukkit.getMaxPlayers()) ? fileUtils.getConfigString("Settings.NickFormat.ServerFullRank.GroupName") : fileUtils.getConfigString("Settings.NickFormat.GroupName")));
						}
					} else
						p.sendMessage(prefix + languageFileUtils.getConfigString("Messages.CanNotNickAsSelf"));
				} else
					p.sendMessage(prefix + languageFileUtils.getConfigString("Messages.PlayerWithThisNameIsKnown"));
			} else
				p.sendMessage(prefix + languageFileUtils.getConfigString("Messages.NickNameAlreadyInUse"));
		}
	}

	public void toggleBungeeNick(Player p) {
		EazyNick eazyNick = EazyNick.getInstance();
		FileUtils fileUtils = eazyNick.getFileUtils();
		LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
		MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
		MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMySQLPlayerDataManager();
		
		boolean hasItem = (p.getItemInHand() != null) && (p.getItemInHand().getType() != Material.AIR && p.getItemInHand().getItemMeta() != null && p.getItemInHand().getItemMeta().getDisplayName() != null) && (p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.BungeeCord.DisplayName.Disabled")) || p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.BungeeCord.DisplayName.Enabled")));
		
		if(fileUtils.getConfig().getBoolean("NeedItemToToggleNick") && !(hasItem))
			return;
		
		if(mysqlNickManager.isPlayerNicked(p.getUniqueId())) {
			mysqlNickManager.removePlayer(p.getUniqueId());
			mysqlPlayerDataManager.removeData(p.getUniqueId());
			
			if(hasItem)
				p.getInventory().setItem(p.getInventory().getHeldItemSlot(), new ItemBuilder(Material.getMaterial(fileUtils.getConfig().getString("NickItem.ItemType.Disabled")), fileUtils.getConfig().getInt("NickItem.ItemAmount.Disabled"), fileUtils.getConfig().getInt("NickItem.MetaData.Disabled")).setDisplayName(languageFileUtils.getConfigString("NickItem.BungeeCord.DisplayName.Disabled")).setLore(languageFileUtils.getConfigString("NickItem.ItemLore.Disabled").split("&n")).setEnchanted(fileUtils.getConfig().getBoolean("NickItem.Enchanted.Disabled")).build());

			p.sendMessage(prefix + languageFileUtils.getConfigString("Messages.BungeeAutoNickDisabled"));
		} else {
			String name = nickNames.get((new Random().nextInt(nickNames.size())));

			mysqlNickManager.addPlayer(p.getUniqueId(), name, name);
			
			if(hasItem)
				p.getInventory().setItem(p.getInventory().getHeldItemSlot(), new ItemBuilder(Material.getMaterial(fileUtils.getConfig().getString("NickItem.ItemType.Enabled")), fileUtils.getConfig().getInt("NickItem.ItemAmount.Enabled"), fileUtils.getConfig().getInt("NickItem.MetaData.Enabled")).setDisplayName(languageFileUtils.getConfigString("NickItem.BungeeCord.DisplayName.Enabled")).setLore(languageFileUtils.getConfigString("NickItem.ItemLore.Enabled").split("&n")).setEnchanted(fileUtils.getConfig().getBoolean("NickItem.Enchanted.Enabled")).build());

			p.sendMessage(prefix + languageFileUtils.getConfigString("Messages.BungeeAutoNickEnabled"));
		}
	}
	
	public void openCustomGUI(Player p, String rankName, String skinType) {
		EazyNick eazyNick = EazyNick.getInstance();
		FileUtils fileUtils = eazyNick.getFileUtils();
		GUIFileUtils guiFileUtils = eazyNick.getGUIFileUtils();
		
		if(fileUtils.getConfig().getBoolean("UseSignGUIForCustomName")) {
			eazyNick.getSignGUI().open(p, guiFileUtils.getConfigString("SignGUI.Line1"), guiFileUtils.getConfigString("SignGUI.Line2"), guiFileUtils.getConfigString("SignGUI.Line3"), guiFileUtils.getConfigString("SignGUI.Line4"), new SignGUI.EditCompleteListener() {
				
				@Override
				public void onEditComplete(SignGUI.EditCompleteEvent e) {
					performRankedNick(p, rankName, skinType, e.getLines()[0]);
				}
			});
		} else {
			AnvilGUI gui = new AnvilGUI(p, new AnvilGUI.AnvilClickEventHandler() {

				@Override
				public void onAnvilClick(AnvilGUI.AnvilClickEvent e) {
					if (e.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
						e.setWillClose(true);
						e.setWillDestroy(true);
						
						performRankedNick(p, rankName, skinType, e.getName());
					} else {
						e.setWillClose(false);
						e.setWillDestroy(false);
					}
				}
			});
			
			gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, new ItemBuilder(Material.PAPER).setDisplayName(guiFileUtils.getConfigString("AnvilGUI.Title")).build());

			try {
				gui.open();
			} catch (IllegalAccessException | InvocationTargetException | InstantiationException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void openRankedNickGUI(Player p, String text) {
		EazyNick eazyNick = EazyNick.getInstance();
		GUIFileUtils guiFileUtils = eazyNick.getGUIFileUtils();
		
		lastGUITexts.put(p.getUniqueId(), text);
		
		String[] args = text.isEmpty() ? new String[0] : text.split(" ");
		
		if(args.length == 0) {
			Inventory inv = Bukkit.createInventory(null, 27, guiFileUtils.getConfigString("RankedNickGUI.Step1.InventoryTitle"));
			
			for (int i = 0; i < inv.getSize(); i++)
				inv.setItem(i, new ItemBuilder(Material.getMaterial(isNewVersion() ? "BLACK_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE"), 1, isNewVersion() ? 0 : 15).setDisplayName("§r").build());
			
			ArrayList<ItemStack> availableRanks = new ArrayList<>();
			
			for (int i = 1; i <= 18; i++) {
				String permission = guiFileUtils.getConfigString("RankGUI.Rank" + i + ".Permission");
				
				if(guiFileUtils.getConfig().getBoolean("RankGUI.Rank" + i + ".Enabled") && (permission.equalsIgnoreCase("NONE") || p.hasPermission(permission)))
					availableRanks.add(new ItemBuilder(Material.valueOf(guiFileUtils.getConfigString("RankedNickGUI.Step1.Rank" + i + ".ItemType")), 1, guiFileUtils.getConfig().getInt("RankedNickGUI.Step1.Rank" + i + ".MetaData")).setDisplayName(guiFileUtils.getConfigString("RankGUI.Rank" + i + ".Rank")).build());
			}
			
			switch (availableRanks.size()) {
				case 1:
					inv.setItem(13, availableRanks.get(0));
					break;
				case 2:
					inv.setItem(11, availableRanks.get(0));
					inv.setItem(15, availableRanks.get(1));
					break;
				case 3:
					inv.setItem(10, availableRanks.get(0));
					inv.setItem(13, availableRanks.get(1));
					inv.setItem(16, availableRanks.get(2));
					break;
				case 4:
					inv.setItem(10, availableRanks.get(0));
					inv.setItem(12, availableRanks.get(1));
					inv.setItem(14, availableRanks.get(2));
					inv.setItem(16, availableRanks.get(3));
					break;
				case 5:
					inv.setItem(9, availableRanks.get(0));
					inv.setItem(11, availableRanks.get(1));
					inv.setItem(13, availableRanks.get(2));
					inv.setItem(15, availableRanks.get(3));
					inv.setItem(17, availableRanks.get(4));
					break;
				case 6:
					inv.setItem(4, availableRanks.get(0));
					inv.setItem(9, availableRanks.get(1));
					inv.setItem(11, availableRanks.get(2));
					inv.setItem(15, availableRanks.get(3));
					inv.setItem(17, availableRanks.get(4));
					inv.setItem(22, availableRanks.get(5));
					break;
				case 7:
					inv.setItem(2, availableRanks.get(0));
					inv.setItem(6, availableRanks.get(1));
					inv.setItem(10, availableRanks.get(2));
					inv.setItem(13, availableRanks.get(3));
					inv.setItem(16, availableRanks.get(4));
					inv.setItem(20, availableRanks.get(5));
					inv.setItem(24, availableRanks.get(6));
					break;
				case 8:
					inv.setItem(2, availableRanks.get(0));
					inv.setItem(6, availableRanks.get(1));
					inv.setItem(10, availableRanks.get(2));
					inv.setItem(12, availableRanks.get(3));
					inv.setItem(14, availableRanks.get(4));
					inv.setItem(16, availableRanks.get(5));
					inv.setItem(20, availableRanks.get(6));
					inv.setItem(24, availableRanks.get(7));
					break;
				case 9:
					inv.setItem(2, availableRanks.get(0));
					inv.setItem(6, availableRanks.get(1));
					inv.setItem(9, availableRanks.get(2));
					inv.setItem(11, availableRanks.get(3));
					inv.setItem(13, availableRanks.get(4));
					inv.setItem(15, availableRanks.get(5));
					inv.setItem(17, availableRanks.get(6));
					inv.setItem(20, availableRanks.get(7));
					inv.setItem(24, availableRanks.get(8));
					break;
				case 10:
					inv.setItem(2, availableRanks.get(0));
					inv.setItem(4, availableRanks.get(1));
					inv.setItem(6, availableRanks.get(2));
					inv.setItem(10, availableRanks.get(3));
					inv.setItem(12, availableRanks.get(4));
					inv.setItem(14, availableRanks.get(5));
					inv.setItem(16, availableRanks.get(6));
					inv.setItem(20, availableRanks.get(7));
					inv.setItem(22, availableRanks.get(8));
					inv.setItem(24, availableRanks.get(9));
					break;
				case 11:
					inv.setItem(1, availableRanks.get(0));
					inv.setItem(3, availableRanks.get(1));
					inv.setItem(5, availableRanks.get(2));
					inv.setItem(7, availableRanks.get(3));
					inv.setItem(11, availableRanks.get(4));
					inv.setItem(13, availableRanks.get(5));
					inv.setItem(15, availableRanks.get(6));
					inv.setItem(19, availableRanks.get(7));
					inv.setItem(21, availableRanks.get(8));
					inv.setItem(23, availableRanks.get(9));
					inv.setItem(25, availableRanks.get(10));
					break;
				case 12:
					inv.setItem(2, availableRanks.get(0));
					inv.setItem(3, availableRanks.get(1));
					inv.setItem(5, availableRanks.get(2));
					inv.setItem(6, availableRanks.get(3));
					inv.setItem(10, availableRanks.get(4));
					inv.setItem(12, availableRanks.get(5));
					inv.setItem(14, availableRanks.get(6));
					inv.setItem(16, availableRanks.get(7));
					inv.setItem(20, availableRanks.get(8));
					inv.setItem(21, availableRanks.get(9));
					inv.setItem(23, availableRanks.get(10));
					inv.setItem(24, availableRanks.get(11));
					break;
				case 13:
					inv.setItem(1, availableRanks.get(0));
					inv.setItem(3, availableRanks.get(1));
					inv.setItem(5, availableRanks.get(2));
					inv.setItem(7, availableRanks.get(3));
					inv.setItem(9, availableRanks.get(4));
					inv.setItem(11, availableRanks.get(5));
					inv.setItem(13, availableRanks.get(6));
					inv.setItem(15, availableRanks.get(7));
					inv.setItem(17, availableRanks.get(8));
					inv.setItem(19, availableRanks.get(9));
					inv.setItem(21, availableRanks.get(10));
					inv.setItem(23, availableRanks.get(11));
					inv.setItem(25, availableRanks.get(12));
					break;
				case 14:
					inv.setItem(0, availableRanks.get(0));
					inv.setItem(2, availableRanks.get(1));
					inv.setItem(4, availableRanks.get(2));
					inv.setItem(6, availableRanks.get(3));
					inv.setItem(8, availableRanks.get(4));
					inv.setItem(10, availableRanks.get(5));
					inv.setItem(12, availableRanks.get(6));
					inv.setItem(14, availableRanks.get(7));
					inv.setItem(16, availableRanks.get(8));
					inv.setItem(18, availableRanks.get(9));
					inv.setItem(20, availableRanks.get(10));
					inv.setItem(22, availableRanks.get(11));
					inv.setItem(24, availableRanks.get(12));
					inv.setItem(26, availableRanks.get(13));
					break;
				case 15:
					inv.setItem(0, availableRanks.get(0));
					inv.setItem(2, availableRanks.get(1));
					inv.setItem(4, availableRanks.get(2));
					inv.setItem(6, availableRanks.get(3));
					inv.setItem(8, availableRanks.get(4));
					inv.setItem(9, availableRanks.get(5));
					inv.setItem(11, availableRanks.get(6));
					inv.setItem(13, availableRanks.get(7));
					inv.setItem(15, availableRanks.get(8));
					inv.setItem(17, availableRanks.get(9));
					inv.setItem(18, availableRanks.get(10));
					inv.setItem(20, availableRanks.get(11));
					inv.setItem(22, availableRanks.get(12));
					inv.setItem(24, availableRanks.get(13));
					inv.setItem(26, availableRanks.get(14));
					break;
				case 16:
					inv.setItem(0, availableRanks.get(0));
					inv.setItem(2, availableRanks.get(1));
					inv.setItem(4, availableRanks.get(2));
					inv.setItem(6, availableRanks.get(3));
					inv.setItem(8, availableRanks.get(4));
					inv.setItem(9, availableRanks.get(5));
					inv.setItem(11, availableRanks.get(6));
					inv.setItem(12, availableRanks.get(7));
					inv.setItem(14, availableRanks.get(8));
					inv.setItem(15, availableRanks.get(9));
					inv.setItem(17, availableRanks.get(10));
					inv.setItem(18, availableRanks.get(11));
					inv.setItem(20, availableRanks.get(12));
					inv.setItem(22, availableRanks.get(13));
					inv.setItem(24, availableRanks.get(14));
					inv.setItem(26, availableRanks.get(15));
					break;
				case 17:
					inv.setItem(0, availableRanks.get(0));
					inv.setItem(2, availableRanks.get(1));
					inv.setItem(4, availableRanks.get(2));
					inv.setItem(6, availableRanks.get(3));
					inv.setItem(8, availableRanks.get(4));
					inv.setItem(9, availableRanks.get(5));
					inv.setItem(10, availableRanks.get(6));
					inv.setItem(12, availableRanks.get(7));
					inv.setItem(13, availableRanks.get(8));
					inv.setItem(14, availableRanks.get(9));
					inv.setItem(16, availableRanks.get(10));
					inv.setItem(17, availableRanks.get(11));
					inv.setItem(18, availableRanks.get(12));
					inv.setItem(20, availableRanks.get(13));
					inv.setItem(22, availableRanks.get(14));
					inv.setItem(24, availableRanks.get(15));
					inv.setItem(26, availableRanks.get(16));
					break;
				case 18:
					inv.setItem(0, availableRanks.get(0));
					inv.setItem(2, availableRanks.get(1));
					inv.setItem(4, availableRanks.get(2));
					inv.setItem(6, availableRanks.get(3));
					inv.setItem(8, availableRanks.get(4));
					inv.setItem(9, availableRanks.get(5));
					inv.setItem(10, availableRanks.get(6));
					inv.setItem(11, availableRanks.get(7));
					inv.setItem(12, availableRanks.get(8));
					inv.setItem(14, availableRanks.get(9));
					inv.setItem(15, availableRanks.get(10));
					inv.setItem(16, availableRanks.get(11));
					inv.setItem(17, availableRanks.get(12));
					inv.setItem(18, availableRanks.get(13));
					inv.setItem(20, availableRanks.get(14));
					inv.setItem(22, availableRanks.get(15));
					inv.setItem(24, availableRanks.get(16));
					inv.setItem(26, availableRanks.get(17));
					break;
				default:
					inv.setItem(13, new ItemBuilder(Material.valueOf(isNewVersion() ? "RED_STAINED_GLASS" : "GLASS"), 1, isNewVersion() ? 0 : 14).setDisplayName(guiFileUtils.getConfigString("RankedNickGUI.Step1.NoRankAvailable.DisplayName")).build());
					break;
			}
			
			p.openInventory(inv);
		} else if(args.length == 1) {
			Inventory inv = Bukkit.createInventory(null, 27, guiFileUtils.getConfigString("RankedNickGUI.Step2.InventoryTitle"));
			
			for (int i = 0; i < inv.getSize(); i++)
				inv.setItem(i, new ItemBuilder(Material.getMaterial(isNewVersion() ? "BLACK_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE"), 1, isNewVersion() ? 0 : 15).setDisplayName("§r").build());
			
			inv.setItem(11, new ItemBuilder(1).setDisplayName(guiFileUtils.getConfigString("RankedNickGUI.Step2.Default.DisplayName")).setSkullOwner(p.getName()).build());
			inv.setItem(13, new ItemBuilder(1).setDisplayName(guiFileUtils.getConfigString("RankedNickGUI.Step2.Normal.DisplayName")).build());
			inv.setItem(15, new ItemBuilder(1).setDisplayName(guiFileUtils.getConfigString("RankedNickGUI.Step2.Random.DisplayName")).setSkullOwner("MHF_Question").build());
			
			p.openInventory(inv);
		} else if(args.length == 2) {
			if(p.hasPermission("nick.customnickname")) {
				Inventory inv = Bukkit.createInventory(null, 27, guiFileUtils.getConfigString("RankedNickGUI.Step3.InventoryTitle"));
				
				for (int i = 0; i < inv.getSize(); i++)
					inv.setItem(i, new ItemBuilder(Material.getMaterial(isNewVersion() ? "BLACK_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE"), 1, isNewVersion() ? 0 : 15).setDisplayName("§r").build());
				
				inv.setItem(12, new ItemBuilder(Material.valueOf((isNewVersion() && !(eazyNick.getVersion().startsWith("1_13"))) ? "OAK_SIGN" : "SIGN")).setDisplayName(guiFileUtils.getConfigString("RankedNickGUI.Step3.Custom.DisplayName")).build());
				inv.setItem(14, new ItemBuilder(1).setDisplayName(guiFileUtils.getConfigString("RankedNickGUI.Step3.Random.DisplayName")).setSkullOwner("MHF_Question").build());
				
				p.openInventory(inv);
			} else
				openRankedNickGUI(p, text + " RANDOM");
		} else {
			Inventory inv = Bukkit.createInventory(null, 27, guiFileUtils.getConfigString("RankedNickGUI.Step4.InventoryTitle").replace("%nickName%", args[2]));
			
			for (int i = 0; i < inv.getSize(); i++)
				inv.setItem(i, new ItemBuilder(Material.getMaterial(isNewVersion() ? "BLACK_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE"), 1, isNewVersion() ? 0 : 15).setDisplayName("§r").build());
			
			inv.setItem(11, new ItemBuilder(Material.valueOf(isNewVersion() ? "LIME_WOOL" : "WOOL"), 1, isNewVersion() ? 0 : 5).setDisplayName(guiFileUtils.getConfigString("RankedNickGUI.Step4.Use.DisplayName")).build());
			inv.setItem(13, new ItemBuilder(1).setDisplayName(guiFileUtils.getConfigString("RankedNickGUI.Step4.Retry.DisplayName")).build());
			inv.setItem(15, new ItemBuilder(Material.valueOf((isNewVersion() && !(eazyNick.getVersion().startsWith("1_13"))) ? "OAK_SIGN" : "SIGN")).setDisplayName(guiFileUtils.getConfigString("RankedNickGUI.Step4.Custom.DisplayName")).build());
			
			p.openInventory(inv);
		}
	}

	public GameProfile getDefaultGameProfile() {
		GameProfile gameProfile = new GameProfile(UUID.fromString("8667ba71-b85a-4004-af54-457a9734eed7"), "Steve");
		gameProfile.getProperties().put("textures", new Property("textures", getDefaultSkinValue(), getDefaultSkinSignature()));
		
		return gameProfile;
	}
	
	public net.minecraft.util.com.mojang.authlib.GameProfile getDefaultGameProfile_1_7() {
		net.minecraft.util.com.mojang.authlib.GameProfile gameProfile = new net.minecraft.util.com.mojang.authlib.GameProfile(UUID.fromString("8667ba71-b85a-4004-af54-457a9734eed7"), "Steve");
		gameProfile.getProperties().put("textures", new net.minecraft.util.com.mojang.authlib.properties.Property("textures", getDefaultSkinValue(), getDefaultSkinSignature()));
		
		return gameProfile;
	}
	
	public String getDefaultSkinValue() {
		return "ewogICJ0aW1lc3RhbXAiIDogMTU4OTU2NzM1NzQyMSwKICAicHJvZmlsZUlkIiA6ICI5MzRiMmFhOGEyODQ0Yzc3ODg2NDhiNDBiY2IzYjAzMSIsCiAgInByb2ZpbGVOYW1lIiA6ICI0Z2wiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQzYjA2YzM4NTA0ZmZjMDIyOWI5NDkyMTQ3YzY5ZmNmNTlmZDJlZDc4ODVmNzg1MDIxNTJmNzdiNGQ1MGRlMSIKICAgIH0KICB9Cn0=";
	}
	
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
	
	public Field getNameField() {
		return nameField;
	}
	
	public Field getUUIDField() {
		return uuidField;
	}
	
	public ArrayList<UUID> getNickedPlayers() {
		return nickedPlayers;
	}
	
	public ArrayList<UUID> getNickOnWorldChangePlayers() {
		return nickOnWorldChangePlayers;
	}
	
	public HashMap<UUID, String> getPlayerNicknames() {
		return playerNicknames;
	}
	
	public List<String> getNickNames() {
		return nickNames;
	}
	
	public List<String> getBlackList() {
		return blackList;
	}
	
	public List<String> getWorldsWithDisabledPrefixAndSuffix() {
		return worldsWithDisabledPrefixAndSuffix;
	}
	
	public List<String> getWorldBlackList() {
		return worldBlackList;
	}
	
	public List<String> getMineSkinIds() {
		return mineSkinIds;
	}
	
	public HashMap<UUID, String> getOldDisplayNames() {
		return oldDisplayNames;
	}
	
	public HashMap<UUID, String> getOldPlayerListNames() {
		return oldPlayerListNames;
	}
	
	public HashMap<UUID, Boolean> getCanUseNick() {
		return canUseNick;
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
	
	public HashMap<UUID, Integer> getOldCloudNETTagIDS() {
		return oldCloudNETTagIDS;
	}
	
	public HashMap<UUID, String> getOldLuckPermsGroups() {
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
	
	public HashMap<UUID, ScoreboardTeamManager> getScoreboardTeamManagers() {
		return scoreboardTeamManagers;
	}
	
	public HashMap<UUID, String> getNameCache() {
		return nameCache;
	}
	
	public HashMap<UUID, String> getLastSkinNames() {
		return lastSkinNames;
	}
	
	public HashMap<UUID, String> getLastNickNames() {
		return lastNickNames;
	}
	
	public HashMap<UUID, String> getChatPrefixes() {
		return chatPrefixes;
	}
	
	public HashMap<UUID, String> getChatSuffixes() {
		return chatSuffixes;
	}
	
	public HashMap<UUID, String> getTabPrefixes() {
		return tabPrefixes;
	}
	
	public HashMap<UUID, String> getTabSuffixes() {
		return tabSuffixes;
	}
	
	public HashMap<UUID, String> getGroupNames() {
		return groupNames;
	}
	
	public HashMap<UUID, String> getLastGUITexts() {
		return lastGUITexts;
	}
	
	public HashMap<UUID, String> getPlayersTypingNameInChat() {
		return playersTypingNameInChat;
	}
	
	public void setNameField(Field nameField) {
		this.nameField = nameField;
	}
	
	public void setUUIDField(Field uuidField) {
		this.uuidField = uuidField;
	}

	public void setLastChatMessage(String lastChatMessage) {
		this.lastChatMessage = lastChatMessage;
	}

}