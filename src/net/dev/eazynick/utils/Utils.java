package net.dev.eazynick.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utils.scoreboard.ScoreboardTeamManager;

public class Utils {

	private String prefix;
	private String noPerm;
	private String notPlayer;
	private String lastChatMessage = "NONE";

	private Field nameField, uuidField;
	
	private PagesHandler<String> nickNamesHandler;

	private List<String> nickNames = new ArrayList<>();
	private List<String> blackList = new ArrayList<>();
	private List<String> worldBlackList = new ArrayList<>();
	private ArrayList<UUID> nickedPlayers = new ArrayList<>();
	private ArrayList<UUID> nickOnWorldChangePlayers = new ArrayList<>();
	private HashMap<UUID, String> playerNicknames = new HashMap<>();
	private HashMap<UUID, String> oldDisplayNames = new HashMap<>();
	private HashMap<UUID, String> oldPlayerListNames = new HashMap<>();
	private HashMap<UUID, Boolean> canUseNick = new HashMap<>();
	private HashMap<UUID, Integer> nickNameListPage = new HashMap<>();
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
	
	public void sendConsole(String msg) {
		Bukkit.getConsoleSender().sendMessage(prefix + msg);
	}

	public ItemStack createItem(Material mat, int amount, int metaData, String displayName, String lore, boolean enchantedItem) {
		ItemStack is = new ItemStack(mat, amount, (byte) metaData);
		ItemMeta m = is.getItemMeta();
		m.setDisplayName(displayName.trim());
		m.setLore(Arrays.asList(lore));

		if (enchantedItem) {
			m.addEnchant(Enchantment.DURABILITY, 1, true);

			if(!(EazyNick.getInstance().getVersion().equalsIgnoreCase("1_7_R4")))
				m.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_UNBREAKABLE });
		}

		is.setItemMeta(m);
		
		return is;
	}

	public ItemStack createItem(Material mat, int amount, int metaData, String displayName) {
		ItemStack is = new ItemStack(mat, amount, (byte) metaData);
		ItemMeta m = is.getItemMeta();
		m.setDisplayName(displayName.trim());

		is.setItemMeta(m);

		return is;
	}

	public ItemStack createSkull(int amount, String displayName, String owner) {
		EazyNick eazyNick = EazyNick.getInstance();
		
		ItemStack is = new ItemStack(Material.getMaterial((eazyNick.getVersion().startsWith("1_13") || eazyNick.getVersion().startsWith("1_14") || eazyNick.getVersion().startsWith("1_15")) ? "PLAYER_HEAD" : "SKULL_ITEM"), amount, (byte) 3);
		SkullMeta m = (SkullMeta) is.getItemMeta();
		m.setDisplayName(displayName);
		m.setOwner(owner);
		is.setItemMeta(m);
		
		return is;
	}

	public int getOnlinePlayers() {
		if(EazyNick.getInstance().getVersion().equals("1_7_R4")) {
			try {
				return Bukkit.getOnlinePlayers().getClass().getField("length").getInt(Bukkit.getOnlinePlayers());
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
		} else
			return Bukkit.getOnlinePlayers().size();
		
		return 0;
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
	
	public Field getNameField() {
		return nameField;
	}
	
	public Field getUUIDField() {
		return uuidField;
	}
	
	public PagesHandler<String> getNickNamesHandler() {
		return nickNamesHandler;
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
	
	public List<String> getWorldBlackList() {
		return worldBlackList;
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
	
	public HashMap<UUID, Integer> getNickNameListPage() {
		return nickNameListPage;
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
	
	public String getLastChatMessage() {
		return lastChatMessage;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public void setNoPerm(String noPerm) {
		this.noPerm = noPerm;
	}
	
	public void setNotPlayer(String notPlayer) {
		this.notPlayer = notPlayer;
	}
	
	public void setNickNamesHandler(PagesHandler<String> nickNamesHandler) {
		this.nickNamesHandler = nickNamesHandler;
	}
	
	public void setNickNames(List<String> nickNames) {
		this.nickNames = nickNames;
	}
	
	public void setBlackList(List<String> blackList) {
		this.blackList = blackList;
	}
	
	public void setWorldBlackList(List<String> worldBlackList) {
		this.worldBlackList = worldBlackList;
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