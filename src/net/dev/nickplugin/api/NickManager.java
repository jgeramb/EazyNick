package net.dev.nickplugin.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.nametagedit.plugin.NametagEdit;

import net.dev.nickplugin.main.Main;
import net.dev.nickplugin.sql.MySQLNickManager;
import net.dev.nickplugin.sql.MySQLPlayerDataManager;
import net.dev.nickplugin.utils.ActionBarUtils;
import net.dev.nickplugin.utils.FileUtils;
import net.dev.nickplugin.utils.LanguageFileUtils;
import net.dev.nickplugin.utils.StringUtils;
import net.dev.nickplugin.utils.Utils;
import net.dev.nickplugin.utils.nickUtils.NMSNickManager;
import net.dev.nickplugin.utils.nickUtils.UUIDFetcher;
import net.dev.nickplugin.utils.nickUtils.UUIDFetcher_1_7;
import net.dev.nickplugin.utils.nickUtils.UUIDFetcher_1_8_R1;
import net.dev.nickplugin.utils.scoreboard.ScoreboardTeamManager;
import net.milkbowl.vault.chat.Chat;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.storage.objects.User;
import me.clip.placeholderapi.PlaceholderAPI;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import me.neznamy.tab.bukkit.api.TABAPI;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import fr.xephi.authme.AuthMe;
import fr.xephi.authme.api.NewAPI;
import fr.xephi.authme.api.v3.AuthMeApi;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class NickManager {

	private Player p;
	private static HashMap<UUID, String> chatPrefixes = new HashMap<>();
	private static HashMap<UUID, String> chatSuffixes = new HashMap<>();
	private static HashMap<UUID, String> tabPrefixes = new HashMap<>();
	private static HashMap<UUID, String> tabSuffixes = new HashMap<>();
	
	public NickManager(Player p) {
		this.p = p;
	}
	
	public void setPlayerListName(String name) {
		if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.PlayerListNameColored")) {
			if(Main.version.equals("1_7_R4"))
				NMSNickManager.updatePlayerListName_1_7_R4(p, name);
			else
				NMSNickManager.updatePlayerListName(p, name);
		}
	}
	
	public void changeSkin(String skinName) {
		if(Main.version.equals("1_7_R4"))
			NMSNickManager.updateSkin_1_7_R4(p, skinName);
		else if(Main.version.equals("1_8_R1"))
			NMSNickManager.updateSkin_1_8_R1(p, skinName);
		else
			NMSNickManager.updateSkin(p, skinName);
	}
	
	public void updatePlayer() {
		if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.RefreshPlayer"))
			NMSNickManager.updatePlayer(p);
	}
	
	public void setName(String nickName) {
		NMSNickManager.updateName(p, nickName);
		
		performAuthMeLogin();
		
		if(Utils.datenschutzStatus()) {
			try {
				me.tim.Main.Main.PlayerDatenschutz_Config.set(p.getName() + ".Entscheidung", true);
				me.tim.Main.Main.PlayerDatenschutz_Config.save(me.tim.Main.Main.PlayerDatenschutz);
			} catch (IOException e) {
			}
		}
	}
	
	private void performAuthMeLogin() {
		if(Utils.authMeReloadedStatus("5.5.0")) {
			NewAPI api = AuthMe.getApi();
			String name = p.getName();
			
			if(!(api.isRegistered(name)))
				api.forceRegister(p, Base64.getEncoder().encodeToString(UUID.randomUUID().toString().replace("-", "").getBytes()).substring(0, 10), true);
			else
				api.forceLogin(p);
		} else if(Utils.authMeReloadedStatus("5.5.1")) {
			AuthMeApi api = AuthMeApi.getInstance();
			String name = p.getName();
			
			if(!(api.isRegistered(name)))
				api.forceRegister(p, Base64.getEncoder().encodeToString(UUID.randomUUID().toString().replace("-", "").getBytes()).substring(0, 10), true);
			else
				api.forceLogin(p);
		}
	}

	public void nickPlayer(String nickName) {
		nickPlayer(nickName, nickName);
	}
	
	public void nickPlayer(String nickName, String skinName) {
		Utils.oldDisplayNames.put(p.getUniqueId(), p.getDisplayName());
		Utils.oldPlayerListNames.put(p.getUniqueId(), p.getPlayerListName());
		
		if (!(Main.version.equalsIgnoreCase("1_7_R4")))
			p.setCustomName(nickName);
		
		MySQLNickManager.addPlayer(p.getUniqueId(), nickName, skinName);
		
		setName(new StringUtils(nickName).removeColorCodes().getString());
		changeSkin(new StringUtils(skinName).removeColorCodes().getString());
		updatePlayer();
		
		Utils.nickedPlayers.add(p.getUniqueId());
		Utils.playerNicknames.put(p.getUniqueId(), nickName);
		
		if(FileUtils.cfg.getBoolean("NickActionBarMessage")) {
			new Timer().schedule(new TimerTask() {
				
				UUID uuid = p.getUniqueId();
				
				@Override
				public void run() {
					if(Main.getPlugin(Main.class).isEnabled()) {
						if(Utils.nickedPlayers.contains(uuid))
							ActionBarUtils.sendActionBar(Bukkit.getPlayer(uuid), ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickActionBarMessage").replace("%nickName%", nickName).replace("%prefix%", Utils.prefix)), 20);
						else {
							ActionBarUtils.sendActionBar(Bukkit.getPlayer(uuid), "", 5);
							cancel();
						}
					} else
						cancel();
				}
			}, 0, 1000);
		}
		
		if(FileUtils.cfg.getBoolean("NickItem.getOnJoin")  && (p.hasPermission("nick.item") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.item"))) {
			for (int slot = 0; slot < p.getInventory().getSize(); slot++) {
				ItemStack item = p.getInventory().getItem(slot);
				
				if((item != null) && (item.getType() != Material.AIR) && (item.getItemMeta() != null) && (item.getItemMeta().getDisplayName() != null)) {
					if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.DisplayName.Disabled"))))
						p.getInventory().setItem(slot, Utils.createItem(Material.getMaterial(FileUtils.cfg.getString("NickItem.ItemType.Enabled")), FileUtils.cfg.getInt("NickItem.ItemAmount.Enabled"), FileUtils.cfg.getInt("NickItem.MetaData.Enabled"), ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.DisplayName.Enabled")), ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.ItemLore.Enabled").replace("&n", "\n")), FileUtils.cfg.getBoolean("NickItem.Enchanted.Enabled")));
				}
			}
		}
	}
	
	public void unnickPlayer() {
		if(FileUtils.cfg.getBoolean("BungeeCord")) {
			MySQLNickManager.removePlayer(p.getUniqueId());
			MySQLPlayerDataManager.removeData(p.getUniqueId());
		}
		
		unnickPlayerWithoutRemovingMySQL();
	}
	
	public void unnickPlayerWithoutRemovingMySQL() {
		String nickName = getRealName();
		
		if (!(Main.version.equalsIgnoreCase("1_7_R4")))
			p.setCustomName(nickName);
		
		setName(nickName);
		changeSkin(nickName);
		updatePlayer();
		
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), new Runnable() {
			
			@Override
			public void run() {
				p.setDisplayName(getOldDisplayName());
				setPlayerListName(getOldPlayerListName());
				
				Utils.oldDisplayNames.remove(p.getUniqueId());
				Utils.oldPlayerListNames.remove(p.getUniqueId());
			}
		}, 5 + 1 + (FileUtils.cfg.getBoolean("RandomDisguiseDelay") ? (20 * 2) : 0));

		Utils.nickedPlayers.remove(p.getUniqueId());
		Utils.playerNicknames.remove(p.getUniqueId());
		
		if(chatPrefixes.containsKey(p.getUniqueId()))
			chatPrefixes.remove(p.getUniqueId());
			
		if(chatSuffixes.containsKey(p.getUniqueId()))
			chatSuffixes.remove(p.getUniqueId());
		
		if(tabPrefixes.containsKey(p.getUniqueId()))
			tabPrefixes.remove(p.getUniqueId());
		
		if(tabSuffixes.containsKey(p.getUniqueId()))
			tabSuffixes.remove(p.getUniqueId());
		
		if(Utils.tabStatus()) {
			TABAPI.removeTemporaryTabPrefix(p);
			TABAPI.removeTemporaryTabSuffix(p);
			TABAPI.removeTemporaryTagPrefix(p);
			TABAPI.removeTemporaryTagSuffix(p);
		}
		
		resetCloudNET();
		resetLuckPerms();
		
		if(Utils.permissionsExStatus()) {
			PermissionUser user = PermissionsEx.getUser(p);
		
			if(FileUtils.cfg.getBoolean("SwitchPermissionsExGroupByNicking")) {
				if(Utils.oldPermissionsExGroups.containsKey(p.getUniqueId()))
					user.setGroups(Utils.oldPermissionsExGroups.get(p.getUniqueId()));
			} else {
				user.setPrefix(Utils.oldPermissionsExPrefixes.get(p.getUniqueId()), p.getWorld().getName());
				user.setSuffix(Utils.oldPermissionsExSuffixes.get(p.getUniqueId()), p.getWorld().getName());
			}
		}
		
		if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.NameTagColored")) {
			if(Utils.scoreboardTeamManagers.containsKey(p.getUniqueId())) {
				ScoreboardTeamManager sbtm = Utils.scoreboardTeamManagers.get(p.getUniqueId());
				
				sbtm.removePlayerFromTeam();
				sbtm.destroyTeam();
				sbtm.createTeam();
				
				Utils.scoreboardTeamManagers.remove(p.getUniqueId());
			}
		}
		
		if(Utils.ultraPermissionsStatus()) {
			User user = UltraPermissions.getAPI().getUsers().uuid(p.getUniqueId());
			
			user.setPrefix(Utils.ultraPermsPrefixes.get(p.getUniqueId()));
			user.setSuffix(Utils.ultraPermsSuffixes.get(p.getUniqueId()));
			user.save();
			
			Utils.ultraPermsPrefixes.remove(p.getUniqueId());
			Utils.ultraPermsSuffixes.remove(p.getUniqueId());
		}
		
		if(FileUtils.cfg.getBoolean("NickItem.getOnJoin")  && (p.hasPermission("nick.item") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.item"))) {
			for (int slot = 0; slot < p.getInventory().getSize(); slot++) {
				ItemStack item = p.getInventory().getItem(slot);
				
				if((item != null) && (item.getType() != Material.AIR) && (item.getItemMeta() != null) && (item.getItemMeta().getDisplayName() != null)) {
					if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.DisplayName.Enabled"))))
						p.getInventory().setItem(slot, Utils.createItem(Material.getMaterial(FileUtils.cfg.getString("NickItem.ItemType.Disabled")), FileUtils.cfg.getInt("NickItem.ItemAmount.Disabled"), FileUtils.cfg.getInt("NickItem.MetaData.Disabled"), ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.DisplayName.Disabled")), ChatColor.translateAlternateColorCodes('&', LanguageFileUtils.cfg.getString("NickItem.ItemLore.Disabled").replace("&n", "\n")), FileUtils.cfg.getBoolean("NickItem.Enchanted.Disabled")));
				}
			}
		}
	}
	
	public String getRealName() {
		String realName = p.getName();
		
		if(!(Bukkit.getOnlineMode()) && Utils.nameCache.containsKey(p.getUniqueId()))
			realName = Utils.nameCache.get(p.getUniqueId());
		else {
			if(Main.version.equalsIgnoreCase("1_7_R4"))
				realName = UUIDFetcher_1_7.getName(p.getUniqueId());
			else if(Main.version.equalsIgnoreCase("1_8_R1"))
				realName = UUIDFetcher_1_8_R1.getName(p.getUniqueId());
			else
				realName = UUIDFetcher.getName(p.getUniqueId());
		}
		
		return realName;
	}
	
	public String getChatPrefix() {
		return chatPrefixes.containsKey(p.getUniqueId()) ? chatPrefixes.get(p.getUniqueId()) : ((Utils.vaultStatus() && FileUtils.cfg.getBoolean("ServerIsUsingVaultPrefixesAndSuffixes")) ? ((Chat) Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class).getProvider()).getPlayerPrefix(p) : "");
	}

	public void setChatPrefix(String chatPrefix) {
		if(chatPrefixes.containsKey(p.getUniqueId()))
			chatPrefixes.remove(p.getUniqueId());
		
		chatPrefixes.put(p.getUniqueId(), ChatColor.translateAlternateColorCodes('&', chatPrefix));
		
		if(Main.version.equals("1_7_R4")) {
			String nameFormatChat = chatPrefixes.get(p.getUniqueId()) + getNickName() + chatSuffixes.get(p.getUniqueId());
			String nameFormatTab = tabPrefixes.get(p.getUniqueId()) + getNickName() + tabSuffixes.get(p.getUniqueId());
			
			if(nameFormatTab.length() <= 16) {
				p.setDisplayName(nameFormatChat);
				setPlayerListName(nameFormatTab);
			} else {
				p.setDisplayName(nameFormatChat.substring(0, 16));
				setPlayerListName(p.getName());
			}
		} else {
			p.setDisplayName(chatPrefixes.get(p.getUniqueId()) + getNickName() + chatSuffixes.get(p.getUniqueId()));
			setPlayerListName(tabPrefixes.get(p.getUniqueId()) + getNickName() + tabSuffixes.get(p.getUniqueId()));
		}
	}

	public String getChatSuffix() {
		return chatSuffixes.containsKey(p.getUniqueId()) ? chatSuffixes.get(p.getUniqueId()) : ((Utils.vaultStatus() && FileUtils.cfg.getBoolean("ServerIsUsingVaultPrefixesAndSuffixes")) ? ((Chat) Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class).getProvider()).getPlayerSuffix(p) : "");
	}

	public void setChatSuffix(String chatSuffix) {
		if(chatSuffixes.containsKey(p.getUniqueId()))
			chatSuffixes.remove(p.getUniqueId());
		
		chatSuffixes.put(p.getUniqueId(), ChatColor.translateAlternateColorCodes('&', chatSuffix));
		
		if(Main.version.equals("1_7_R4")) {
			String nameFormatChat = chatPrefixes.get(p.getUniqueId()) + getNickName() + chatSuffixes.get(p.getUniqueId());
			String nameFormatTab = tabPrefixes.get(p.getUniqueId()) + getNickName() + tabSuffixes.get(p.getUniqueId());
			
			if(nameFormatTab.length() <= 16) {
				p.setDisplayName(nameFormatChat);
				setPlayerListName(nameFormatTab);
			} else {
				p.setDisplayName(nameFormatChat.substring(0, 16));
				setPlayerListName(p.getName());
			}
		} else {
			p.setDisplayName(chatPrefixes.get(p.getUniqueId()) + getNickName() + chatSuffixes.get(p.getUniqueId()));
			setPlayerListName(tabPrefixes.get(p.getUniqueId()) + getNickName() + tabSuffixes.get(p.getUniqueId()));
		}
	}

	public String getTabPrefix() {
		return tabPrefixes.containsKey(p.getUniqueId()) ? tabPrefixes.get(p.getUniqueId()) : ((Utils.vaultStatus() && FileUtils.cfg.getBoolean("ServerIsUsingVaultPrefixesAndSuffixes")) ? ((Chat) Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class).getProvider()).getPlayerPrefix(p) : "");
	}

	public void setTabPrefix(String tabPrefix) {
		if(tabPrefixes.containsKey(p.getUniqueId()))
			tabPrefixes.remove(p.getUniqueId());
		
		tabPrefixes.put(p.getUniqueId(), ChatColor.translateAlternateColorCodes('&', tabPrefix));
		
		if(Main.version.equals("1_7_R4")) {
			String nameFormatChat = chatPrefixes.get(p.getUniqueId()) + getNickName() + chatSuffixes.get(p.getUniqueId());
			String nameFormatTab = tabPrefixes.get(p.getUniqueId()) + getNickName() + tabSuffixes.get(p.getUniqueId());
			
			if(nameFormatTab.length() <= 16) {
				p.setDisplayName(nameFormatChat);
				setPlayerListName(nameFormatTab);
			} else {
				p.setDisplayName(nameFormatChat.substring(0, 16));
				setPlayerListName(p.getName());
			}
		} else {
			p.setDisplayName(chatPrefixes.get(p.getUniqueId()) + getNickName() + chatSuffixes.get(p.getUniqueId()));
			setPlayerListName(tabPrefixes.get(p.getUniqueId()) + getNickName() + tabSuffixes.get(p.getUniqueId()));
		}
	}

	public String getTabSuffix() {
		return tabSuffixes.containsKey(p.getUniqueId()) ? tabSuffixes.get(p.getUniqueId()) : ((Utils.vaultStatus() && FileUtils.cfg.getBoolean("ServerIsUsingVaultPrefixesAndSuffixes")) ? ((Chat) Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class).getProvider()).getPlayerSuffix(p) : "");
	}

	public void setTabSuffix(String tabSuffix) {
		if(tabSuffixes.containsKey(p.getUniqueId()))
			tabSuffixes.remove(p.getUniqueId());
		
		tabSuffixes.put(p.getUniqueId(), ChatColor.translateAlternateColorCodes('&', tabSuffix));
		
		if(Main.version.equals("1_7_R4")) {
			String nameFormatChat = chatPrefixes.get(p.getUniqueId()) + getNickName() + chatSuffixes.get(p.getUniqueId());
			String nameFormatTab = tabPrefixes.get(p.getUniqueId()) + getNickName() + tabSuffixes.get(p.getUniqueId());
			
			if(nameFormatTab.length() <= 16) {
				p.setDisplayName(nameFormatChat);
				setPlayerListName(nameFormatTab);
			} else {
				p.setDisplayName(nameFormatChat.substring(0, 16));
				setPlayerListName(p.getName());
			}
		} else {
			p.setDisplayName(chatPrefixes.get(p.getUniqueId()) + getNickName() + chatSuffixes.get(p.getUniqueId()));
			setPlayerListName(tabPrefixes.get(p.getUniqueId()) + getNickName() + tabSuffixes.get(p.getUniqueId()));
		}
	}

	public boolean isNicked() {
		return Utils.nickedPlayers.contains(p.getUniqueId());
	}
	
	public String getRandomStringFromList(ArrayList<String> list) {
		return list.size() != 0 ? list.get((new Random()).nextInt(list.size())) : p.getName();
	}
	
	public static String getRandomName() {
		return Utils.nickNames.get((new Random()).nextInt(Utils.nickNames.size()));
	}
	
	public String getNickName() {
		return (Utils.playerNicknames.containsKey(p.getUniqueId()) ? Utils.playerNicknames.get(p.getUniqueId()) : p.getName());
	}
	
	public String getNickFormat() {
		return getChatPrefix() + getNickName() + getChatSuffix();
	}
	
	public String getOldDisplayName() {
		return Utils.oldDisplayNames.containsKey(p.getUniqueId()) ? Utils.oldDisplayNames.get(p.getUniqueId()) : p.getName();
	}
	
	public String getOldPlayerListName() {
		return Utils.oldPlayerListNames.containsKey(p.getUniqueId()) ? Utils.oldPlayerListNames.get(p.getUniqueId()) : p.getName();
	}

	public void updateLuckPerms(String prefix, String suffix) {
		if(Utils.luckPermsStatus()) {
			Utils.luckPermsPrefixes.put(p.getUniqueId(), prefix);
			Utils.luckPermsSuffixes.put(p.getUniqueId(), suffix);

			LuckPermsApi api = LuckPerms.getApi();
			me.lucko.luckperms.api.User user = api.getUser(p.getUniqueId());
			user.setPermission(api.getNodeFactory().makePrefixNode(100, prefix).build());
			user.setPermission(api.getNodeFactory().makeSuffixNode(100, suffix).build());
			api.getUserManager().saveUser(user);
		}
	}
	
	public void resetLuckPerms() {
		if(Utils.luckPermsStatus()) {
			LuckPermsApi api = LuckPerms.getApi();
			me.lucko.luckperms.api.User user = api.getUser(p.getUniqueId());
			user.unsetPermission(api.getNodeFactory().makePrefixNode(100, Utils.luckPermsPrefixes.get(p.getUniqueId())).build());
			user.unsetPermission(api.getNodeFactory().makeSuffixNode(100, Utils.luckPermsSuffixes.get(p.getUniqueId())).build());
			api.getUserManager().saveUser(user);
			
			Utils.luckPermsPrefixes.remove(p.getUniqueId());
			Utils.luckPermsSuffixes.remove(p.getUniqueId());
		}
	}
	
	public void updatePrefixSuffix(String tagPrefix, String tagSuffix, String chatPrefix, String chatSuffix, String tabPrefix, String tabSuffix) {
		updatePrefixSuffix(tagPrefix, tagSuffix, chatPrefix, chatSuffix, tabPrefix, tabSuffix, "NONE");
	}
	
	public void updatePrefixSuffix(String tagPrefix, String tagSuffix, String chatPrefix, String chatSuffix, String tabPrefix, String tabSuffix, String groupName) {
		if(Utils.placeholderAPIStatus()) {
			tagPrefix = PlaceholderAPI.setPlaceholders(p, tagPrefix);
			tagSuffix = PlaceholderAPI.setPlaceholders(p, tagSuffix);
			chatPrefix = PlaceholderAPI.setPlaceholders(p, chatPrefix);
			chatSuffix = PlaceholderAPI.setPlaceholders(p, chatSuffix);
			tabPrefix = PlaceholderAPI.setPlaceholders(p, tabPrefix);
			tabSuffix = PlaceholderAPI.setPlaceholders(p, tabSuffix);
		}

		if(chatPrefixes.containsKey(p.getUniqueId()))
			chatPrefixes.remove(p.getUniqueId());
			
		if(chatSuffixes.containsKey(p.getUniqueId()))
			chatSuffixes.remove(p.getUniqueId());
		
		if(tabPrefixes.containsKey(p.getUniqueId()))
			tabPrefixes.remove(p.getUniqueId());
		
		if(tabSuffixes.containsKey(p.getUniqueId()))
			tabSuffixes.remove(p.getUniqueId());
		
		chatPrefixes.put(p.getUniqueId(), chatPrefix);
		chatSuffixes.put(p.getUniqueId(), chatSuffix);
		tabPrefixes.put(p.getUniqueId(), tabPrefix);
		tabSuffixes.put(p.getUniqueId(), tabSuffix);
		
		changeCloudNET(tagPrefix, tagSuffix);

		if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.NameTagColored")) {
			if(Utils.scoreboardTeamManagers.containsKey(p.getUniqueId()))
				Utils.scoreboardTeamManagers.remove(p.getUniqueId());
				
			Utils.scoreboardTeamManagers.put(p.getUniqueId(), new ScoreboardTeamManager(p, tagPrefix, tagSuffix));
			
			ScoreboardTeamManager sbtm = Utils.scoreboardTeamManagers.get(p.getUniqueId());
			UUID uuid = p.getUniqueId();
			String finalTabPrefix = tabPrefix;
			String finalTabSuffix = tabSuffix;
			
			new Timer().schedule(new TimerTask() {
				
				@Override
				public void run() {
					if(Main.getPlugin(Main.class).isEnabled()) {
						if(Utils.nickedPlayers.contains(uuid)) {
							sbtm.destroyTeam();
							sbtm.createTeam();
							
							if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.PlayerListNameColored"))
								setPlayerListName(finalTabPrefix + p.getName() + finalTabSuffix);
						} else {
							sbtm.destroyTeam();
							cancel();
						}
					} else {
						sbtm.destroyTeam();
						cancel();
					}
				}
			}, 1000, 1000);
		}
		
		if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.PlayerListNameColored"))
			setPlayerListName(tabPrefix + p.getName() + tabSuffix);
		
		if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.DisplayNameColored"))
			p.setDisplayName(chatPrefix + p.getName() + chatSuffix);
		
		if(Utils.nameTagEditStatus()) {
			NametagEdit.getApi().setPrefix(p.getPlayer(), tabSuffix);
			NametagEdit.getApi().setSuffix(p.getPlayer(), tabPrefix);
		}
		
		if(Utils.ultraPermissionsStatus()) {
			if(Utils.ultraPermsPrefixes.containsKey(p.getUniqueId()) || Utils.ultraPermsSuffixes.containsKey(p.getUniqueId())) {
				Utils.ultraPermsPrefixes.remove(p.getUniqueId());
				Utils.ultraPermsSuffixes.remove(p.getUniqueId());
			}
			
			User user = UltraPermissions.getAPI().getUsers().uuid(p.getUniqueId());
			
			Utils.ultraPermsPrefixes.put(p.getUniqueId(), user.getPrefix());
			Utils.ultraPermsSuffixes.put(p.getUniqueId(), user.getSuffix());
			
			user.setPrefix(tabPrefix);
			user.setSuffix(tabSuffix);
			user.save();
		}
		
		if(Utils.permissionsExStatus()) {
			PermissionUser user = PermissionsEx.getUser(p);
		
			if(FileUtils.cfg.getBoolean("SwitchPermissionsExGroupByNicking") && !(groupName.equalsIgnoreCase("NONE"))) {
				String groupNames = "";

				for (PermissionGroup group : user.getGroups())
					groupNames += (" " + group.getName());
				
				if(!(Utils.oldPermissionsExGroups.containsKey(p.getUniqueId())))
					Utils.oldPermissionsExGroups.put(p.getUniqueId(), groupNames.trim().split(" "));
				
				user.setGroups(new String[] { groupName });
			} else {
				Utils.oldPermissionsExPrefixes.put(p.getUniqueId(), user.getPrefix());
				Utils.oldPermissionsExSuffixes.put(p.getUniqueId(), user.getSuffix());
				
				if(Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
					user.setPrefix(tabPrefix, p.getWorld().getName());
					user.setSuffix(tabSuffix, p.getWorld().getName());
				} else {
					user.setPrefix(tabPrefix, p.getWorld().getName());
					user.setSuffix(tabSuffix, p.getWorld().getName());
				}
			}
		}
		
		if(Utils.tabStatus()) {
			TABAPI.setTabPrefixTemporarily(p, tabPrefix);
			TABAPI.setTabSuffixTemporarily(p, tabSuffix);
			TABAPI.setTagPrefixTemporarily(p, tagPrefix);
			TABAPI.setTagSuffixTemporarily(p, tagSuffix);
		}
	}
	
	public void changeCloudNET(String prefix, String suffix) {
		if(Utils.cloudNetStatus()) {
			CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(p.getUniqueId());
			
			if(FileUtils.cfg.getBoolean("ServerIsUsingCloudNETPrefixesAndSuffixes")) {
				PermissionEntity entity = cloudPlayer.getPermissionEntity();
				de.dytanic.cloudnet.lib.player.permission.PermissionGroup highestPermissionGroup = entity.getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool());
				
				if(Utils.oldCloudNETPrefixes.containsKey(p.getUniqueId()))
					Utils.oldCloudNETPrefixes.remove(p.getUniqueId());
				
				if(Utils.oldCloudNETSuffixes.containsKey(p.getUniqueId()))
					Utils.oldCloudNETSuffixes.remove(p.getUniqueId());
				
				if(Utils.oldCloudNETTagIDS.containsKey(p.getUniqueId()))
					Utils.oldCloudNETTagIDS.remove(p.getUniqueId());
				
				Utils.oldCloudNETPrefixes.put(p.getUniqueId(), entity.getPrefix());
				Utils.oldCloudNETSuffixes.put(p.getUniqueId(), entity.getSuffix());
				Utils.oldCloudNETTagIDS.put(p.getUniqueId(), highestPermissionGroup.getTagId());
				
				entity.setPrefix(prefix);
				entity.setSuffix(suffix);
				highestPermissionGroup.setPrefix(prefix);
				highestPermissionGroup.setSuffix(suffix);
				highestPermissionGroup.setTagId(Integer.MAX_VALUE);
			}
		}
	}
	
	public void resetCloudNET() {
		if(Utils.cloudNetStatus()) {
			CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(p.getUniqueId());
			
			if(FileUtils.cfg.getBoolean("ServerIsUsingCloudNETPrefixesAndSuffixes")) {
				PermissionEntity entity = cloudPlayer.getPermissionEntity();
				de.dytanic.cloudnet.lib.player.permission.PermissionGroup highestPermissionGroup = entity.getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool());
				
				if(Utils.oldCloudNETPrefixes.containsKey(p.getUniqueId())) {
					entity.setPrefix(Utils.oldCloudNETPrefixes.get(p.getUniqueId()));
					highestPermissionGroup.setPrefix(Utils.oldCloudNETPrefixes.get(p.getUniqueId()));
					Utils.oldCloudNETPrefixes.remove(p.getUniqueId());
				}
				
				if(Utils.oldCloudNETSuffixes.containsKey(p.getUniqueId())) {
					entity.setSuffix(Utils.oldCloudNETSuffixes.get(p.getUniqueId()));
					highestPermissionGroup.setSuffix(Utils.oldCloudNETSuffixes.get(p.getUniqueId()));
					Utils.oldCloudNETSuffixes.remove(p.getUniqueId());
				}
				
				if(Utils.oldCloudNETTagIDS.containsKey(p.getUniqueId())) {
					highestPermissionGroup.setTagId(Utils.oldCloudNETTagIDS.get(p.getUniqueId()));
					Utils.oldCloudNETTagIDS.remove(p.getUniqueId());
				}
			}
		}
	}

}
