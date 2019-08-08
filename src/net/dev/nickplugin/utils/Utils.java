package net.dev.nickplugin.utils;

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

import net.dev.nickplugin.main.Main;
import net.dev.nickplugin.utils.scoreboard.ScoreboardTeamManager;

public class Utils {

	public static String prefix;
	public static String noPerm;
	public static String notPlayer;

	public static Field field;
	public static PagesHandler<String> nickNamesHandler;

	public static ArrayList<UUID> nickedPlayers = new ArrayList<>();
	public static ArrayList<UUID> nickOnWorldChangePlayers = new ArrayList<>();
	public static HashMap<UUID, String> playerNicknames = new HashMap<>();
	public static List<String> nickNames = new ArrayList<>();
	public static List<String> blackList = new ArrayList<>();
	public static List<String> worldBlackList = new ArrayList<>();
	public static HashMap<UUID, String> oldDisplayNames = new HashMap<>();
	public static HashMap<UUID, String> oldPlayerListNames = new HashMap<>();
	public static HashMap<UUID, Boolean> canUseNick = new HashMap<>();
	public static HashMap<UUID, Integer> nickNameListPage = new HashMap<>();
	public static HashMap<UUID, String> oldPermissionsExPrefixes = new HashMap<>();
	public static HashMap<UUID, String> oldPermissionsExSuffixes = new HashMap<>();
	public static HashMap<UUID, String> oldCloudNETPrefixes = new HashMap<>();
	public static HashMap<UUID, String> oldCloudNETSuffixes = new HashMap<>();
	public static HashMap<UUID, Integer> oldCloudNETTagIDS = new HashMap<>();
	public static HashMap<UUID, String[]> oldPermissionsExGroups = new HashMap<>();
	public static HashMap<UUID, Object> luckPermsPrefixes = new HashMap<>();
	public static HashMap<UUID, Object> luckPermsSuffixes = new HashMap<>();
	public static HashMap<UUID, String> ultraPermsPrefixes = new HashMap<>();
	public static HashMap<UUID, String> ultraPermsSuffixes = new HashMap<>();
	
	public static HashMap<UUID, ScoreboardTeamManager> scoreboardTeamManagers = new HashMap<>();
	public static HashMap<UUID, String> nameCache = new HashMap<>();
	public static HashMap<UUID, String> lastSkinNames = new HashMap<>();
	public static HashMap<UUID, String> lastNickNames = new HashMap<>();

	public static boolean placeholderAPIStatus() {
		return (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null);
	}
	
	public static boolean cloudNetStatus() {
		return (Bukkit.getPluginManager().getPlugin("CloudNetAPI") != null);
	}

	public static boolean coloredTagsStatus() {
		return (Bukkit.getPluginManager().getPlugin("ColoredTags") != null);
	}

	public static boolean nameTagEditStatus() {
		return (Bukkit.getPluginManager().getPlugin("NametagEdit") != null);
	}

	public static boolean permissionsExStatus() {
		return (Bukkit.getPluginManager().getPlugin("PermissionsEx") != null);
	}
	
	public static boolean luckPermsStatus() {
		return (Bukkit.getPluginManager().getPlugin("LuckPerms") != null);
	}
	
	public static boolean datenschutzStatus() {
		return (Bukkit.getPluginManager().getPlugin("Datenschutz") != null);
	}
	
	public static boolean ultraPermissionsStatus() {
		return (Bukkit.getPluginManager().getPlugin("UltraPermissions") != null);
	}
	
	public static boolean vaultStatus() {
		return (Bukkit.getPluginManager().getPlugin("Vault") != null);
	}
	
	public static boolean authMeReloadedStatus(String version) {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("AuthMe");
		
		if(plugin != null) {
			if(plugin.getDescription().getMain().equalsIgnoreCase("fr.xephi.authme.AuthMe"))
				return plugin.getDescription().getVersion().contains(version);
		}
			
		return false;
	}
	
	public static boolean tabStatus() {
		return (Bukkit.getPluginManager().getPlugin("TAB") != null) && (Bukkit.getPluginManager().getPlugin("TAB").getDescription().getAuthors().contains("NEZNAMY"));
	}
	
	public static boolean deluxeChatStatus() {
		return (Bukkit.getPluginManager().getPlugin("DeluxeChat") != null);
	}
	
	public static boolean chatControlProStatus() {
		return (Bukkit.getPluginManager().getPlugin("ChatControl") != null);
	}
	
	public static void sendConsole(String msg) {
		Bukkit.getConsoleSender().sendMessage(prefix + msg);
	}

	public static ItemStack createItem(Material mat, int amount, int metaData, String displayName, String lore, boolean enchantedItem) {
		ItemStack is = new ItemStack(mat, amount, (byte) metaData);
		ItemMeta m = is.getItemMeta();
		m.setDisplayName(displayName.trim());
		m.setLore(Arrays.asList(lore));

		if (enchantedItem) {
			m.addEnchant(Enchantment.DURABILITY, 1, true);

			if(!(Main.version.equalsIgnoreCase("1_7_R4")) && !(Bukkit.getVersion().contains("1.14.3")))
				m.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_UNBREAKABLE });
		}

		is.setItemMeta(m);
		
		return is;
	}

	public static ItemStack createItem(Material mat, int amount, int metaData, String displayName) {
		ItemStack is = new ItemStack(mat, amount, (byte) metaData);
		ItemMeta m = is.getItemMeta();
		m.setDisplayName(displayName.trim());

		is.setItemMeta(m);

		return is;
	}

	public static ItemStack createSkull(int amount, String displayName, String owner) {
		ItemStack is = new ItemStack(Material.getMaterial((Main.version.startsWith("1_13") || Main.version.startsWith("1_14")) ? "LEGACY_SKULL_ITEM" : "SKULL_ITEM"), amount, (byte) 3);
		SkullMeta m = (SkullMeta) is.getItemMeta();
		m.setDisplayName(displayName);
		m.setOwner(owner);
		is.setItemMeta(m);
		
		return is;
	}

	public static int getOnlinePlayers() {
		if(Main.version.equals("1_7_R4")) {
			try {
				return Bukkit.getOnlinePlayers().getClass().getField("length").getInt(Bukkit.getOnlinePlayers());
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
		} else
			return Bukkit.getOnlinePlayers().size();
		
		return 0;
	}

}