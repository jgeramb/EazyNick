package net.dev.eazynick.api;

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
import com.nametagedit.plugin.api.INametagApi;
import com.nametagedit.plugin.api.data.Nametag;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.hooks.LuckPermsHook;
import net.dev.eazynick.hooks.TABHook;
import net.dev.eazynick.utils.ActionBarUtils;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.LanguageFileUtils;
import net.dev.eazynick.utils.ReflectUtils;
import net.dev.eazynick.utils.StringUtils;
import net.dev.eazynick.utils.Utils;
import net.dev.eazynick.utils.nickutils.NMSNickManager;
import net.dev.eazynick.utils.nickutils.NMSNickManager.UpdateType;
import net.dev.eazynick.utils.scoreboard.ScoreboardTeamManager;
import net.milkbowl.vault.chat.Chat;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.UltraPermissionsAPI;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.clip.placeholderapi.PlaceholderAPI;
import me.wazup.survivalgames.PlayerData;
import me.wazup.survivalgames.SurvivalGames;
import me.wazup.survivalgames.SurvivalGamesAPI;

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

	private EazyNick eazyNick;
	private Player p;
	
	public NickManager(Player p) {
		this.eazyNick = EazyNick.getInstance();
		this.p = p;
	}
	
	public void setPlayerListName(String name) {
		NMSNickManager nmsNickManager = eazyNick.getNMSNickManager();
		
		if(eazyNick.getFileUtils().cfg.getBoolean("Settings.ChangeOptions.PlayerListName")) {
			if(eazyNick.getVersion().equals("1_7_R4"))
				nmsNickManager.updatePlayerListName_1_7_R4(p, name);
			else
				nmsNickManager.updatePlayerListName(p, name);
		}
	}
	
	public void changeSkin(String skinName) {
		NMSNickManager nmsNickManager = eazyNick.getNMSNickManager();
		
		if(skinName != null) {
			if(eazyNick.getVersion().equals("1_7_R4"))
				nmsNickManager.updateSkin_1_7_R4(p, skinName);
			else if(eazyNick.getVersion().equals("1_8_R1"))
				nmsNickManager.updateSkin_1_8_R1(p, skinName);
			else
				nmsNickManager.updateSkin(p, skinName);
		}
	}
	
	public void updatePlayer() {
		updatePlayer(UpdateType.UPDATE, null, false);
	}
	
	public void updatePlayer(UpdateType type, String skinName, boolean forceUpdate) {
		eazyNick.getNMSNickManager().updatePlayer(p, type, skinName, forceUpdate);
	}
	
	public void setName(String nickName) {
		Utils utils = eazyNick.getUtils();
		NMSNickManager nmsNickManager = eazyNick.getNMSNickManager();
		ReflectUtils reflectUtils = eazyNick.getReflectUtils();
		
		if(utils.survivalGamesStatus()) {
			if(nickName != p.getName()) {
				try {
					SurvivalGames survivalGames = (SurvivalGames) reflectUtils.getField(SurvivalGamesAPI.class, "plugin").get(SurvivalGames.api);
					HashMap<String, PlayerData> playerData = (HashMap<String, PlayerData>) reflectUtils.getField(SurvivalGames.class, "playerData").get(survivalGames);
					playerData.put(nickName, playerData.get(p.getName()));
					playerData.remove(p.getName());
					reflectUtils.setField(survivalGames, "playerData", playerData);
					
					Object config = (Object) reflectUtils.getField(SurvivalGames.class, "config").get(survivalGames);
					
					if((boolean) reflectUtils.getField(config.getClass(), "BungeeMode").get(config)) {
						ArrayList<String> grace = (ArrayList<String>) reflectUtils.getField(SurvivalGames.class, "grace").get(survivalGames);
						grace.add(nickName);
						grace.remove(p.getName());
						reflectUtils.setField(survivalGames, "grace", grace);
						
						ArrayList<String> spectator = (ArrayList<String>) reflectUtils.getField(SurvivalGames.class, "spectator").get(survivalGames);
					    spectator.add(nickName);
				    	spectator.remove(p.getName());
					    reflectUtils.setField(survivalGames, "spectator", spectator);
					}
				} catch (Exception e) {
				}
			}
		}
		
		nmsNickManager.updateName(p, nickName);
		
		if(utils.survivalGamesStatus()) {
			if(nickName != p.getName()) {
				try {
					SurvivalGames survivalGames = (SurvivalGames) reflectUtils.getField(SurvivalGamesAPI.class, "plugin").get(SurvivalGames.api);
					HashMap<String, PlayerData> playerData = (HashMap<String, PlayerData>) reflectUtils.getField(SurvivalGames.class, "playerData").get(survivalGames);
					PlayerData data = playerData.get(nickName);
					playerData.remove(nickName);
					PlayerData.class.getMethod("loadStats", Player.class).invoke(playerData, p);
					playerData.put(nickName, data);
					reflectUtils.setField(survivalGames, "playerData", playerData);
				} catch (Exception e) {
				}
			}
		}
		
		performAuthMeLogin();
		
		if(utils.datenschutzStatus()) {
			try {
				me.tim.Main.Main.PlayerDatenschutz_Config.set(p.getName() + ".Entscheidung", true);
				me.tim.Main.Main.PlayerDatenschutz_Config.save(me.tim.Main.Main.PlayerDatenschutz);
			} catch (IOException e) {
			}
		}
	}
	
	private void performAuthMeLogin() {
		Utils utils = eazyNick.getUtils();
		
		try {
			if(utils.authMeReloadedStatus("5.5.0")) {
				NewAPI api = AuthMe.getApi();
				String name = p.getName();
				
				if(!(api.isRegistered(name)))
					api.forceRegister(p, Base64.getEncoder().encodeToString(UUID.randomUUID().toString().replace("-", "").getBytes()).substring(0, 10), true);
				else
					api.forceLogin(p);
			} else if(utils.authMeReloadedStatus("5.5.1") || utils.authMeReloadedStatus("5.6.0")) {
				AuthMeApi api = AuthMeApi.getInstance();
				String name = p.getName();
				
				if(!(api.isRegistered(name)))
					api.forceRegister(p, Base64.getEncoder().encodeToString(UUID.randomUUID().toString().replace("-", "").getBytes()).substring(0, 10), true);
				else
					api.forceLogin(p);
			}
		} catch (Exception ex) {
		}
	}

	public void nickPlayer(String nickName) {
		nickPlayer(nickName, nickName);
	}
	
	public void nickPlayer(String nickName, String skinName) {
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
		
		utils.getOldDisplayNames().put(p.getUniqueId(), (p.getDisplayName() != null) ? p.getDisplayName() : p.getName());
		utils.getOldPlayerListNames().put(p.getUniqueId(), (p.getPlayerListName() != null) ? p.getPlayerListName() :  p.getName());
		
		if (!(eazyNick.getVersion().equalsIgnoreCase("1_7_R4")))
			p.setCustomName(nickName);
		
		if(fileUtils.cfg.getBoolean("BungeeCord"))
			eazyNick.getMySQLNickManager().addPlayer(p.getUniqueId(), nickName, skinName);
		
		utils.getNickedPlayers().add(p.getUniqueId());
		utils.getPlayerNicknames().put(p.getUniqueId(), nickName);
		
		setName(new StringUtils(nickName).removeColorCodes().getString());
		updatePlayer(UpdateType.NICK, new StringUtils(skinName).removeColorCodes().getString(), false);
		
		if(fileUtils.cfg.getBoolean("NickActionBarMessage")) {
			new Timer().schedule(new TimerTask() {
				
				UUID uuid = p.getUniqueId();
				
				@Override
				public void run() {
					ActionBarUtils actionBarUtils = eazyNick.getActionBarUtils();
					
					if(eazyNick.isEnabled() && utils.getNickedPlayers().contains(uuid) && (p != null) && p.isOnline())
						actionBarUtils.sendActionBar(Bukkit.getPlayer(uuid), eazyNick.getLanguageFileUtils().getConfigString("NickActionBarMessage").replace("%nickName%", nickName).replace("%prefix%", utils.getPrefix()), 20);
					else {
						actionBarUtils.sendActionBar(Bukkit.getPlayer(uuid), "", 5);
						cancel();
					}
				}
			}, 0, 1000);
		}
		
		if(fileUtils.cfg.getBoolean("NickItem.getOnJoin")  && (p.hasPermission("nick.item"))) {
			for (int slot = 0; slot < p.getInventory().getSize(); slot++) {
				ItemStack item = p.getInventory().getItem(slot);
				
				if((item != null) && (item.getType() != Material.AIR) && (item.getItemMeta() != null) && (item.getItemMeta().getDisplayName() != null)) {
					if(item.getItemMeta().getDisplayName().equalsIgnoreCase(eazyNick.getLanguageFileUtils().getConfigString("NickItem.DisplayName.Disabled")))
						p.getInventory().setItem(slot, utils.createItem(Material.getMaterial(fileUtils.cfg.getString("NickItem.ItemType.Enabled")), fileUtils.cfg.getInt("NickItem.ItemAmount.Enabled"), fileUtils.cfg.getInt("NickItem.MetaData.Enabled"), languageFileUtils.getConfigString("NickItem.DisplayName.Enabled"), languageFileUtils.getConfigString("NickItem.ItemLore.Enabled").replace("&n", "\n"), fileUtils.cfg.getBoolean("NickItem.Enchanted.Enabled")));
				}
			}
		}
	}
	
	public void unnickPlayer() {
		if(eazyNick.getFileUtils().cfg.getBoolean("BungeeCord")) {
			eazyNick.getMySQLNickManager().removePlayer(p.getUniqueId());
			eazyNick.getMySQLPlayerDataManager().removeData(p.getUniqueId());
		}
		
		unnickPlayerWithoutRemovingMySQL(false);
	}
	
	public void unnickPlayerWithoutRemovingMySQL(boolean isQuitUnnick) {
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
		
		String nickName = getRealName();
		
		if (!(eazyNick.getVersion().equalsIgnoreCase("1_7_R4")))
			p.setCustomName(nickName);
		
		setName(nickName);
		updatePlayer(isQuitUnnick ? UpdateType.QUIT : UpdateType.UNNICK, nickName, true);
		
		utils.getNickedPlayers().remove(p.getUniqueId());
		utils.getPlayerNicknames().remove(p.getUniqueId());
		
		if(utils.getChatPrefixes().containsKey(p.getUniqueId()))
			utils.getChatPrefixes().remove(p.getUniqueId());
			
		if(utils.getChatSuffixes().containsKey(p.getUniqueId()))
			utils.getChatSuffixes().remove(p.getUniqueId());
		
		if(utils.getTabPrefixes().containsKey(p.getUniqueId()))
			utils.getTabPrefixes().remove(p.getUniqueId());
		
		if(utils.getTabSuffixes().containsKey(p.getUniqueId()))
			utils.getTabSuffixes().remove(p.getUniqueId());
		
		unsetGroupName();
		resetCloudNET();
		
		if(utils.luckPermsStatus())
			new LuckPermsHook(p).resetNodes();
		
		if(utils.tabStatus())
			new TABHook(p).reset();
		
		if(utils.ultraPermissionsStatus()) {
			UltraPermissionsAPI api = UltraPermissions.getAPI();
			me.TechsCode.UltraPermissions.storage.objects.User user = api.getUsers().uuid(p.getUniqueId());
		
			if(fileUtils.cfg.getBoolean("SwitchUltraPermissionsGroupByNicking")) {
				if(utils.getOldUltraPermissionsGroups().containsKey(p.getUniqueId())) {
					HashMap<String, Long> data = utils.getOldUltraPermissionsGroups().get(p.getUniqueId());
					
					data.keySet().forEach(group -> user.addGroup(api.getGroups().name(group), data.get(group)));
					
					utils.getOldUltraPermissionsGroups().remove(p.getUniqueId());
				}
			}
		}
		
		if(utils.permissionsExStatus()) {
			PermissionUser user = PermissionsEx.getUser(p);
		
			if(fileUtils.cfg.getBoolean("SwitchPermissionsExGroupByNicking")) {
				if(utils.getOldPermissionsExGroups().containsKey(p.getUniqueId())) {
					user.setGroups(utils.getOldPermissionsExGroups().get(p.getUniqueId()));
					
					utils.getOldPermissionsExGroups().remove(p.getUniqueId());
				}
			} else if(utils.getOldPermissionsExPrefixes().containsKey(p.getUniqueId()) && utils.getOldPermissionsExSuffixes().containsKey(p.getUniqueId())) {
				user.setPrefix(utils.getOldPermissionsExPrefixes().get(p.getUniqueId()), p.getWorld().getName());
				user.setSuffix(utils.getOldPermissionsExSuffixes().get(p.getUniqueId()), p.getWorld().getName());
			}
		}
		
		if(fileUtils.cfg.getBoolean("Settings.ChangeOptions.NameTag")) {
			if(utils.getScoreboardTeamManagers().containsKey(p.getUniqueId())) {
				utils.getScoreboardTeamManagers().get(p.getUniqueId()).destroyTeam();
				utils.getScoreboardTeamManagers().remove(p.getUniqueId());
			}
		}
		
		if(utils.ultraPermissionsStatus()) {
			if(utils.getUltraPermissionsPrefixes().containsKey(p.getUniqueId()) && utils.getUltraPermissionsSuffixes().containsKey(p.getUniqueId())) {
				me.TechsCode.UltraPermissions.storage.objects.User user = UltraPermissions.getAPI().getUsers().uuid(p.getUniqueId());
				
				user.setPrefix(utils.getUltraPermissionsPrefixes().get(p.getUniqueId()));
				user.setSuffix(utils.getUltraPermissionsSuffixes().get(p.getUniqueId()));
				user.save();
				
				utils.getUltraPermissionsPrefixes().remove(p.getUniqueId());
				utils.getUltraPermissionsSuffixes().remove(p.getUniqueId());
			}
		}
		
		if(utils.nameTagEditStatus()) {
			if(utils.getNametagEditPrefixes().containsKey(p.getUniqueId()) && utils.getNametagEditSuffixes().containsKey(p.getUniqueId())) {
				INametagApi nametagEditAPI = NametagEdit.getApi();
				String prefix = utils.getNametagEditPrefixes().get(p.getUniqueId());
				String suffix = utils.getNametagEditSuffixes().get(p.getUniqueId());
				
				nametagEditAPI.setPrefix(p, prefix);
				nametagEditAPI.setSuffix(p, suffix);
				nametagEditAPI.reloadNametag(p);
				
				utils.getNametagEditPrefixes().remove(p.getUniqueId());
				utils.getNametagEditSuffixes().remove(p.getUniqueId());
			}
		}
		
		if(!(eazyNick.isEnabled()))
			return;
		
		if(utils.getOldDisplayNames().containsKey(p.getUniqueId()) && utils.getOldPlayerListNames().containsKey(p.getUniqueId())) {
			UUID uuid = p.getUniqueId();
			String oldDisplayName = getOldDisplayName();
			String oldPlayerListName = getOldPlayerListName();
			
			Bukkit.getScheduler().runTaskLater(eazyNick, new Runnable() {
				
				@Override
				public void run() {
					p.setDisplayName(oldDisplayName);
					p.setPlayerListName(oldPlayerListName);
					
					utils.getOldDisplayNames().remove(uuid);
					utils.getOldPlayerListNames().remove(uuid);
				}
			}, 5 + (fileUtils.cfg.getBoolean("RandomDisguiseDelay") ? 40 : 0));
		}
		
		if(fileUtils.cfg.getBoolean("NickItem.getOnJoin")  && (p.hasPermission("nick.item"))) {
			for (int slot = 0; slot < p.getInventory().getSize(); slot++) {
				ItemStack item = p.getInventory().getItem(slot);
				
				if((item != null) && (item.getType() != Material.AIR) && (item.getItemMeta() != null) && (item.getItemMeta().getDisplayName() != null)) {
					if(item.getItemMeta().getDisplayName().equalsIgnoreCase(eazyNick.getLanguageFileUtils().getConfigString("NickItem.DisplayName.Enabled")))
						p.getInventory().setItem(slot, utils.createItem(Material.getMaterial(fileUtils.cfg.getString("NickItem.ItemType.Disabled")), fileUtils.cfg.getInt("NickItem.ItemAmount.Disabled"), fileUtils.cfg.getInt("NickItem.MetaData.Disabled"), languageFileUtils.getConfigString("NickItem.DisplayName.Disabled"), languageFileUtils.getConfigString("NickItem.ItemLore.Disabled").replace("&n", "\n"), fileUtils.cfg.getBoolean("NickItem.Enchanted.Disabled")));
				}
			}
		}
	}
	
	public String getRealName() {
		Utils utils = eazyNick.getUtils();
		
		String realName = p.getName();
		
		if(!(Bukkit.getOnlineMode()) && utils.getNameCache().containsKey(p.getUniqueId()))
			realName = utils.getNameCache().get(p.getUniqueId());
		else {
			if(eazyNick.getVersion().equalsIgnoreCase("1_7_R4"))
				realName = eazyNick.getUUIDFetcher_1_7().getName(realName, p.getUniqueId());
			else if(eazyNick.getVersion().equalsIgnoreCase("1_8_R1"))
				realName = eazyNick.getUUIDFetcher_1_8_R1().getName(realName, p.getUniqueId());
			else
				realName = eazyNick.getUUIDFetcher().getName(realName, p.getUniqueId());
		}
		
		return realName;
	}
	
	public String getChatPrefix() {
		Utils utils = eazyNick.getUtils();
		
		return utils.getChatPrefixes().containsKey(p.getUniqueId()) ? utils.getChatPrefixes().get(p.getUniqueId()) : ((utils.vaultStatus() && eazyNick.getFileUtils().cfg.getBoolean("ServerIsUsingVaultPrefixesAndSuffixes")) ? ((Chat) Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class).getProvider()).getPlayerPrefix(p) : "");
	}

	public void setChatPrefix(String chatPrefix) {
		Utils utils = eazyNick.getUtils();
		
		if(utils.getChatPrefixes().containsKey(p.getUniqueId()))
			utils.getChatPrefixes().remove(p.getUniqueId());
		
		utils.getChatPrefixes().put(p.getUniqueId(), ChatColor.translateAlternateColorCodes('&', chatPrefix));
		
		if(eazyNick.getVersion().equals("1_7_R4")) {
			String nameFormatChat = utils.getChatPrefixes().get(p.getUniqueId()) + getNickName() + utils.getChatSuffixes().get(p.getUniqueId());
			String nameFormatTab = utils.getTabPrefixes().get(p.getUniqueId()) + getNickName() + utils.getTabSuffixes().get(p.getUniqueId());
			
			if(nameFormatTab.length() <= 16) {
				p.setDisplayName(nameFormatChat);
				setPlayerListName(nameFormatTab);
			} else {
				p.setDisplayName(nameFormatChat.substring(0, 16));
				setPlayerListName(p.getName());
			}
		} else {
			p.setDisplayName(utils.getChatPrefixes().get(p.getUniqueId()) + getNickName() + utils.getChatSuffixes().get(p.getUniqueId()));
			setPlayerListName(utils.getTabPrefixes().get(p.getUniqueId()) + getNickName() + utils.getTabSuffixes().get(p.getUniqueId()));
		}
	}

	public String getChatSuffix() {
		Utils utils = eazyNick.getUtils();
		
		return utils.getChatSuffixes().containsKey(p.getUniqueId()) ? utils.getChatSuffixes().get(p.getUniqueId()) : ((utils.vaultStatus() && eazyNick.getFileUtils().cfg.getBoolean("ServerIsUsingVaultPrefixesAndSuffixes")) ? ((Chat) Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class).getProvider()).getPlayerSuffix(p) : "");
	}

	public void setChatSuffix(String chatSuffix) {
		Utils utils = eazyNick.getUtils();
		
		if(utils.getChatSuffixes().containsKey(p.getUniqueId()))
			utils.getChatSuffixes().remove(p.getUniqueId());
		
		utils.getChatSuffixes().put(p.getUniqueId(), ChatColor.translateAlternateColorCodes('&', chatSuffix));
		
		if(eazyNick.getVersion().equals("1_7_R4")) {
			String nameFormatChat = utils.getChatPrefixes().get(p.getUniqueId()) + getNickName() + utils.getChatSuffixes().get(p.getUniqueId());
			String nameFormatTab = utils.getTabPrefixes().get(p.getUniqueId()) + getNickName() + utils.getTabSuffixes().get(p.getUniqueId());
			
			if(nameFormatTab.length() <= 16) {
				p.setDisplayName(nameFormatChat);
				setPlayerListName(nameFormatTab);
			} else {
				p.setDisplayName(nameFormatChat.substring(0, 16));
				setPlayerListName(p.getName());
			}
		} else {
			p.setDisplayName(utils.getChatPrefixes().get(p.getUniqueId()) + getNickName() + utils.getChatSuffixes().get(p.getUniqueId()));
			setPlayerListName(utils.getTabPrefixes().get(p.getUniqueId()) + getNickName() + utils.getTabSuffixes().get(p.getUniqueId()));
		}
	}

	public String getTabPrefix() {
		Utils utils = eazyNick.getUtils();
		
		return utils.getTabPrefixes().containsKey(p.getUniqueId()) ? utils.getTabPrefixes().get(p.getUniqueId()) : ((utils.vaultStatus() && eazyNick.getFileUtils().cfg.getBoolean("ServerIsUsingVaultPrefixesAndSuffixes")) ? ((Chat) Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class).getProvider()).getPlayerPrefix(p) : "");
	}

	public void setTabPrefix(String tabPrefix) {
		Utils utils = eazyNick.getUtils();
		
		if(utils.getTabPrefixes().containsKey(p.getUniqueId()))
			utils.getTabPrefixes().remove(p.getUniqueId());
		
		utils.getTabPrefixes().put(p.getUniqueId(), ChatColor.translateAlternateColorCodes('&', tabPrefix));
		
		if(eazyNick.getVersion().equals("1_7_R4")) {
			String nameFormatChat = utils.getChatPrefixes().get(p.getUniqueId()) + getNickName() + utils.getChatSuffixes().get(p.getUniqueId());
			String nameFormatTab = utils.getTabPrefixes().get(p.getUniqueId()) + getNickName() + utils.getTabSuffixes().get(p.getUniqueId());
			
			if(nameFormatTab.length() <= 16) {
				p.setDisplayName(nameFormatChat);
				setPlayerListName(nameFormatTab);
			} else {
				p.setDisplayName(nameFormatChat.substring(0, 16));
				setPlayerListName(p.getName());
			}
		} else {
			p.setDisplayName(utils.getChatPrefixes().get(p.getUniqueId()) + getNickName() + utils.getChatSuffixes().get(p.getUniqueId()));
			setPlayerListName(utils.getTabPrefixes().get(p.getUniqueId()) + getNickName() + utils.getTabSuffixes().get(p.getUniqueId()));
		}
	}

	public String getTabSuffix() {
		Utils utils = eazyNick.getUtils();
		
		return utils.getTabSuffixes().containsKey(p.getUniqueId()) ? utils.getTabSuffixes().get(p.getUniqueId()) : ((utils.vaultStatus() && eazyNick.getFileUtils().cfg.getBoolean("ServerIsUsingVaultPrefixesAndSuffixes")) ? ((Chat) Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class).getProvider()).getPlayerSuffix(p) : "");
	}

	public void setTabSuffix(String tabSuffix) {
		Utils utils = eazyNick.getUtils();
		
		if(utils.getTabSuffixes().containsKey(p.getUniqueId()))
			utils.getTabSuffixes().remove(p.getUniqueId());
		
		utils.getTabSuffixes().put(p.getUniqueId(), ChatColor.translateAlternateColorCodes('&', tabSuffix));
		
		if(eazyNick.getVersion().equals("1_7_R4")) {
			String nameFormatChat = utils.getChatPrefixes().get(p.getUniqueId()) + getNickName() + utils.getChatSuffixes().get(p.getUniqueId());
			String nameFormatTab = utils.getTabPrefixes().get(p.getUniqueId()) + getNickName() + utils.getTabSuffixes().get(p.getUniqueId());
			
			if(nameFormatTab.length() <= 16) {
				p.setDisplayName(nameFormatChat);
				setPlayerListName(nameFormatTab);
			} else {
				p.setDisplayName(nameFormatChat.substring(0, 16));
				setPlayerListName(p.getName());
			}
		} else {
			p.setDisplayName(utils.getChatPrefixes().get(p.getUniqueId()) + getNickName() + utils.getChatSuffixes().get(p.getUniqueId()));
			setPlayerListName(utils.getTabPrefixes().get(p.getUniqueId()) + getNickName() + utils.getTabSuffixes().get(p.getUniqueId()));
		}
	}

	public boolean isNicked() {
		return eazyNick.getUtils().getNickedPlayers().contains(p.getUniqueId());
	}
	
	public String getRandomStringFromList(ArrayList<String> list) {
		return list.size() != 0 ? list.get((new Random()).nextInt(list.size())) : p.getName();
	}
	
	public String getRandomName() {
		Utils utils = eazyNick.getUtils();
		
		return utils.getNickNames().get((new Random()).nextInt(utils.getNickNames().size()));
	}
	
	public String getNickName() {
		Utils utils = eazyNick.getUtils();
		
		return (utils.getPlayerNicknames().containsKey(p.getUniqueId()) ? utils.getPlayerNicknames().get(p.getUniqueId()) : p.getName());
	}
	
	public String getNickFormat() {
		return getChatPrefix() + getNickName() + getChatSuffix();
	}
	
	public String getOldDisplayName() {
		Utils utils = eazyNick.getUtils();
		
		return utils.getOldDisplayNames().containsKey(p.getUniqueId()) ? utils.getOldDisplayNames().get(p.getUniqueId()) : p.getName();
	}
	
	public String getOldPlayerListName() {
		Utils utils = eazyNick.getUtils();
		
		return utils.getOldPlayerListNames().containsKey(p.getUniqueId()) ? utils.getOldPlayerListNames().get(p.getUniqueId()) : p.getName();
	}
	
	public String getGroupName() {
		Utils utils = eazyNick.getUtils();
		
		return utils.getGroupNames().containsKey(p.getUniqueId()) ? utils.getGroupNames().get(p.getUniqueId()) : "NONE";
	}
	
	public void setGroupName(String rank) {
		Utils utils = eazyNick.getUtils();
		
		utils.getGroupNames().put(p.getUniqueId(), rank);
	}
	
	public void unsetGroupName() {
		Utils utils = eazyNick.getUtils();
		
		if(utils.getGroupNames().containsKey(p.getUniqueId()))
			utils.getGroupNames().remove(p.getUniqueId());
	}
	
	public void updatePrefixSuffix(String tagPrefix, String tagSuffix, String chatPrefix, String chatSuffix, String tabPrefix, String tabSuffix) {
		updatePrefixSuffix(tagPrefix, tagSuffix, chatPrefix, chatSuffix, tabPrefix, tabSuffix, "NONE");
	}
	
	public void updatePrefixSuffix(String tagPrefix, String tagSuffix, String chatPrefix, String chatSuffix, String tabPrefix, String tabSuffix, String groupName) {
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		
		String finalTabPrefix = tabPrefix, finalTabSuffix = tabSuffix;
		
		if(utils.getChatPrefixes().containsKey(p.getUniqueId()))
			utils.getChatPrefixes().remove(p.getUniqueId());
			
		if(utils.getChatSuffixes().containsKey(p.getUniqueId()))
			utils.getChatSuffixes().remove(p.getUniqueId());
		
		if(utils.getTabPrefixes().containsKey(p.getUniqueId()))
			utils.getTabPrefixes().remove(p.getUniqueId());
		
		if(utils.getTabSuffixes().containsKey(p.getUniqueId()))
			utils.getTabSuffixes().remove(p.getUniqueId());
		
		utils.getChatPrefixes().put(p.getUniqueId(), chatPrefix);
		utils.getChatSuffixes().put(p.getUniqueId(), chatSuffix);
		utils.getTabPrefixes().put(p.getUniqueId(), tabPrefix);
		utils.getTabSuffixes().put(p.getUniqueId(), tabSuffix);
		
		if(fileUtils.cfg.getBoolean("Settings.ChangeOptions.NameTag")) {
			if(utils.getScoreboardTeamManagers().containsKey(p.getUniqueId()))
				utils.getScoreboardTeamManagers().remove(p.getUniqueId());
				
			utils.getScoreboardTeamManagers().put(p.getUniqueId(), new ScoreboardTeamManager(p, tagPrefix, tagSuffix));
			
			ScoreboardTeamManager sbtm = utils.getScoreboardTeamManagers().get(p.getUniqueId());
			
			new Timer().schedule(new TimerTask() {
				
				@Override
				public void run() {
					UUID uuid = p.getUniqueId();

					sbtm.destroyTeam();
					
					if(eazyNick.isEnabled() && utils.getNickedPlayers().contains(uuid) && (p != null) && p.isOnline()) {
						sbtm.createTeam();
						
						if(fileUtils.cfg.getBoolean("Settings.ChangeOptions.PlayerListName")) {
							String tmpTabPrefix = finalTabPrefix;
							String tmpTabSuffix = finalTabSuffix;
							
							if(utils.placeholderAPIStatus()) {
								tmpTabPrefix = PlaceholderAPI.setPlaceholders(p, tmpTabPrefix);
								tmpTabSuffix = PlaceholderAPI.setPlaceholders(p, tmpTabSuffix);
							}
							
							setPlayerListName(tmpTabPrefix + p.getName() + tmpTabSuffix);
						}
					} else
						cancel();
				}
			}, 0, 175);
		}
		
		if(utils.placeholderAPIStatus()) {
			tagPrefix = PlaceholderAPI.setPlaceholders(p, tagPrefix);
			tagSuffix = PlaceholderAPI.setPlaceholders(p, tagSuffix);
			tabPrefix = PlaceholderAPI.setPlaceholders(p, tabPrefix);
			tabSuffix = PlaceholderAPI.setPlaceholders(p, tabSuffix);
		}
		
		changeCloudNET(tagPrefix, tagSuffix);
		
		if(fileUtils.cfg.getBoolean("Settings.ChangeOptions.PlayerListName"))
			setPlayerListName(tabPrefix + p.getName() + tabSuffix);
		
		if(fileUtils.cfg.getBoolean("Settings.ChangeOptions.DisplayName"))
			p.setDisplayName(chatPrefix + p.getName() + chatSuffix);
		
		if(utils.nameTagEditStatus()) {
			if(utils.getNametagEditPrefixes().containsKey(p.getUniqueId()) && utils.getNametagEditSuffixes().containsKey(p.getUniqueId())) {
				utils.getNametagEditPrefixes().remove(p.getUniqueId());
				utils.getNametagEditSuffixes().remove(p.getUniqueId());
			}
			
			INametagApi nametagEditAPI = NametagEdit.getApi();
			Nametag nametag = nametagEditAPI.getNametag(p);

			utils.getNametagEditPrefixes().put(p.getUniqueId(), nametag.getPrefix());
			utils.getNametagEditSuffixes().put(p.getUniqueId(), nametag.getSuffix());

			nametagEditAPI.setPrefix(p, tagPrefix);
			nametagEditAPI.setSuffix(p, tagSuffix);
		}
		
		if(utils.ultraPermissionsStatus()) {
			if(utils.getUltraPermissionsPrefixes().containsKey(p.getUniqueId()) && utils.getUltraPermissionsSuffixes().containsKey(p.getUniqueId())) {
				utils.getUltraPermissionsPrefixes().remove(p.getUniqueId());
				utils.getUltraPermissionsSuffixes().remove(p.getUniqueId());
			}
			
			UltraPermissionsAPI api = UltraPermissions.getAPI();
			me.TechsCode.UltraPermissions.storage.objects.User user = api.getUsers().uuid(p.getUniqueId());
			
			utils.getUltraPermissionsPrefixes().put(p.getUniqueId(), user.getPrefix());
			utils.getUltraPermissionsSuffixes().put(p.getUniqueId(), user.getSuffix());
			
			user.setPrefix(tabPrefix);
			user.setSuffix(tabSuffix);
			user.save();
			
			if(fileUtils.cfg.getBoolean("SwitchUltraPermissionsGroupByNicking") && !(groupName.equalsIgnoreCase("NONE"))) {
				if(!(utils.getOldUltraPermissionsGroups().containsKey(p.getUniqueId())))
					utils.getOldUltraPermissionsGroups().put(p.getUniqueId(), new HashMap<>());
				
				for (Group group : user.getGroupsMap().keySet()) {
					utils.getOldUltraPermissionsGroups().get(p.getUniqueId()).put(group.getName(), user.getGroupsMap().get(group));
					
					user.removeGroup(group);
				}
				
				Group group = api.getGroups().name(groupName);
				
				if(group != null)
					user.addGroup(group);
			}
		}
		
		if(utils.permissionsExStatus()) {
			PermissionUser user = PermissionsEx.getUser(p);
		
			if(fileUtils.cfg.getBoolean("SwitchPermissionsExGroupByNicking") && !(groupName.equalsIgnoreCase("NONE"))) {
				String groupNames = "";

				for (PermissionGroup group : user.getGroups())
					groupNames += (" " + group.getName());
				
				if(!(utils.getOldPermissionsExGroups().containsKey(p.getUniqueId())))
					utils.getOldPermissionsExGroups().put(p.getUniqueId(), groupNames.trim().split(" "));
				
				user.setGroups(new String[] { groupName });
			} else {
				utils.getOldPermissionsExPrefixes().put(p.getUniqueId(), user.getPrefix());
				utils.getOldPermissionsExSuffixes().put(p.getUniqueId(), user.getSuffix());
				
				user.setPrefix(tabPrefix, p.getWorld().getName());
				user.setSuffix(tabSuffix, p.getWorld().getName());
			}
		}
	}
	
	public void changeCloudNET(String prefix, String suffix) {
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		
		if(utils.cloudNetStatus()) {
			CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(p.getUniqueId());
			
			if(fileUtils.cfg.getBoolean("ServerIsUsingCloudNETPrefixesAndSuffixes")) {
				PermissionEntity entity = cloudPlayer.getPermissionEntity();
				de.dytanic.cloudnet.lib.player.permission.PermissionGroup highestPermissionGroup = entity.getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool());
				
				if(utils.getOldCloudNETPrefixes().containsKey(p.getUniqueId()))
					utils.getOldCloudNETPrefixes().remove(p.getUniqueId());
				
				if(utils.getOldCloudNETSuffixes().containsKey(p.getUniqueId()))
					utils.getOldCloudNETSuffixes().remove(p.getUniqueId());
				
				if(utils.getOldCloudNETTagIDS().containsKey(p.getUniqueId()))
					utils.getOldCloudNETTagIDS().remove(p.getUniqueId());
				
				utils.getOldCloudNETPrefixes().put(p.getUniqueId(), entity.getPrefix());
				utils.getOldCloudNETSuffixes().put(p.getUniqueId(), entity.getSuffix());
				utils.getOldCloudNETTagIDS().put(p.getUniqueId(), highestPermissionGroup.getTagId());
				
				entity.setPrefix(prefix);
				entity.setSuffix(suffix);
				highestPermissionGroup.setPrefix(prefix);
				highestPermissionGroup.setSuffix(suffix);
				highestPermissionGroup.setTagId(Integer.MAX_VALUE);
			}
		}
	}
	
	public void resetCloudNET() {
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		
		if(utils.cloudNetStatus()) {
			CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(p.getUniqueId());
			
			if(fileUtils.cfg.getBoolean("ServerIsUsingCloudNETPrefixesAndSuffixes")) {
				PermissionEntity entity = cloudPlayer.getPermissionEntity();
				de.dytanic.cloudnet.lib.player.permission.PermissionGroup highestPermissionGroup = entity.getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool());
				
				if(utils.getOldCloudNETPrefixes().containsKey(p.getUniqueId())) {
					entity.setPrefix(utils.getOldCloudNETPrefixes().get(p.getUniqueId()));
					highestPermissionGroup.setPrefix(utils.getOldCloudNETPrefixes().get(p.getUniqueId()));
					utils.getOldCloudNETPrefixes().remove(p.getUniqueId());
				}
				
				if(utils.getOldCloudNETSuffixes().containsKey(p.getUniqueId())) {
					entity.setSuffix(utils.getOldCloudNETSuffixes().get(p.getUniqueId()));
					highestPermissionGroup.setSuffix(utils.getOldCloudNETSuffixes().get(p.getUniqueId()));
					utils.getOldCloudNETSuffixes().remove(p.getUniqueId());
				}
				
				if(utils.getOldCloudNETTagIDS().containsKey(p.getUniqueId())) {
					highestPermissionGroup.setTagId(utils.getOldCloudNETTagIDS().get(p.getUniqueId()));
					utils.getOldCloudNETTagIDS().remove(p.getUniqueId());
				}
			}
		}
	}

}
