package net.dev.eazynick.api;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utilities.*;
import net.dev.eazynick.utilities.configuration.yaml.SetupYamlFile;

public class NickedPlayerData {

	private UUID uniqueId, spoofedUniqueId;
	private String oldDisplayName, oldPlayerListName, realName, nickName, skinName, chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix, groupName;
	private int sortID;
	private Object skinProfile;
	
	public NickedPlayerData(UUID uniqueId, UUID spoofedUniqueId, String oldDisplayName, String oldPlayerListName, String realName, String nickName, String skinName, String chatPrefix, String chatSuffix, String tabPrefix, String tabSuffix, String tagPrefix, String tagSuffix, String groupName, int sortID) {
		this.uniqueId = uniqueId;
		this.spoofedUniqueId = spoofedUniqueId;
		this.oldDisplayName = oldDisplayName;
		this.oldPlayerListName = oldPlayerListName;
		this.realName = realName;
		this.nickName = nickName;
		this.skinName = skinName;
		this.chatPrefix = chatPrefix;
		this.chatSuffix = chatSuffix;
		this.tabPrefix = tabPrefix;
		this.tabSuffix = tabSuffix;
		this.tagPrefix = tagPrefix;
		this.tagSuffix = tagSuffix;
		this.groupName = groupName;
		this.sortID = sortID;
		
		//Load skin
		prepareSkinProfile();
	}
	
	public Object getFakeGameProfile(boolean spoofUniqueId) {
		EazyNick eazyNick = EazyNick.getInstance();
		
		if(this.spoofedUniqueId == null)
			this.spoofedUniqueId = this.uniqueId;
		
		String version = eazyNick.getVersion();
		boolean changeNameTag = eazyNick.getSetupYamlFile().getConfiguration().getBoolean("Settings.ChangeOptions.NameTag");
		
		try {
			//Create and return new game profile
			if(version.startsWith("1_7")) {
				net.minecraft.util.com.mojang.authlib.GameProfile gameProfile = new net.minecraft.util.com.mojang.authlib.GameProfile(spoofUniqueId ? spoofedUniqueId : uniqueId, changeNameTag ? nickName : realName);
				gameProfile.getProperties().removeAll("textures");
				
				if(skinProfile == null)
					skinProfile = eazyNick.getUtils().getDefaultGameProfile_1_7();
				
				gameProfile.getProperties().putAll("textures", ((net.minecraft.util.com.mojang.authlib.GameProfile) skinProfile).getProperties().get("textures"));
				
				return gameProfile;
			} else {
				GameProfile gameProfile = new GameProfile(spoofUniqueId ? spoofedUniqueId : uniqueId, changeNameTag ? nickName : realName);
				gameProfile.getProperties().removeAll("textures");
				
				if(skinProfile == null)
					skinProfile = eazyNick.getUtils().getDefaultGameProfile();
				
				gameProfile.getProperties().putAll("textures", ((GameProfile) skinProfile).getProperties().get("textures"));
				
				return gameProfile;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
		
		//Return default game profile
		return (version.startsWith("1_7") ? new net.minecraft.util.com.mojang.authlib.GameProfile(spoofUniqueId ? spoofedUniqueId : uniqueId, nickName) : new GameProfile(spoofUniqueId ? spoofedUniqueId : uniqueId, nickName));
	}
	
	public Object getTABGameProfile(String name, boolean spoofUniqueId) {
		EazyNick eazyNick = EazyNick.getInstance();
		
		if(this.spoofedUniqueId == null)
			this.spoofedUniqueId = this.uniqueId;
		
		String version = eazyNick.getVersion();
		
		try {
			//Create and return new game profile
			if(version.startsWith("1_7")) {
				net.minecraft.util.com.mojang.authlib.GameProfile gameProfile = new net.minecraft.util.com.mojang.authlib.GameProfile(spoofUniqueId ? spoofedUniqueId : uniqueId, name);
				gameProfile.getProperties().removeAll("textures");
				gameProfile.getProperties().putAll("textures", ((net.minecraft.util.com.mojang.authlib.GameProfile) skinProfile).getProperties().get("textures"));
				
				return gameProfile;
			} else {
				GameProfile gameProfile = new GameProfile(spoofUniqueId ? spoofedUniqueId : uniqueId, name);
				gameProfile.getProperties().removeAll("textures");
				gameProfile.getProperties().putAll("textures", ((GameProfile) skinProfile).getProperties().get("textures"));
				
				return gameProfile;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		//Return default game profile
		return (version.startsWith("1_7") ? new net.minecraft.util.com.mojang.authlib.GameProfile(spoofUniqueId ? spoofedUniqueId : uniqueId, name) : new GameProfile(spoofUniqueId ? spoofedUniqueId : uniqueId, name));
	}
	
	private void prepareSkinProfile() {
		new Thread(() -> {
			EazyNick eazyNick = EazyNick.getInstance();
			Utils utils = eazyNick.getUtils();
			MineSkinAPI mineSkinAPI = eazyNick.getMineSkinAPI();
			SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
			
			String version = eazyNick.getVersion();
			
			if(setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.Skin")) {
				skinProfile = version.startsWith("1_7") ? utils.getDefaultGameProfile_1_7() : utils.getDefaultGameProfile();
				
				try {
					Object profile = null;
					
					if(skinName.startsWith("MINESKIN:")) {
						//Load skin from mineskin.org
						if(version.startsWith("1_7")) {
							((GameProfile) skinProfile).getProperties().removeAll("textures");
							((net.minecraft.util.com.mojang.authlib.GameProfile) skinProfile).getProperties().putAll("textures", mineSkinAPI.getTextureProperties_1_7(skinName.equals("MINESKIN:RANDOM") ? utils.getRandomStringFromList(utils.getMineSkinUUIDs()) : skinName.split(":")[1]));
						} else {
							((GameProfile) skinProfile).getProperties().removeAll("textures");
							((GameProfile) skinProfile).getProperties().putAll("textures", mineSkinAPI.getTextureProperties(skinName.equals("MINESKIN:RANDOM") ? utils.getRandomStringFromList(utils.getMineSkinUUIDs()) : skinName.split(":")[1]));
						}
					} else {
						//Load skin from mojang api
						if(version.startsWith("1_7"))
							profile = eazyNick.getGameProfileBuilder_1_7().fetch(eazyNick.getUUIDFetcher_1_7().getUUID(skinName));
						else if(version.equals("1_8_R1"))
							profile = eazyNick.getGameProfileBuilder_1_8_R1().fetch(eazyNick.getUUIDFetcher_1_8_R1().getUUID(skinName));
						else
							profile = eazyNick.getGameProfileBuilder().fetch(eazyNick.getUUIDFetcher().getUUID(skinName));
					}
					
					if(profile != null)
						skinProfile = profile;
				} catch (Exception ex) {
					if(setupYamlFile.getConfiguration().getBoolean("ShowProfileErrorMessages")) {
						if(utils.isSupportMode()) {
							utils.sendConsole("§cAn error occured while preparing skin profile§7:");
							
							ex.printStackTrace();
						} else
							utils.sendConsole("§cAn error occured while preparing skin profile§7, §cthis is NOT a plugin error§7!");
					}
				}
			} else {
				Player player = Bukkit.getPlayer(uniqueId);
				
				if(player != null) {
					try {
						skinProfile = player.getClass().getMethod("getProfile").invoke(player);
					} catch (Exception ignore) {
					}
	
				}
			}
			
			Thread.currentThread().interrupt();
		}).start();
	}
	
	public boolean isSkinProfileAvailable() {
		return (skinProfile != null);
	}
	
	public UUID getUniqueId() {
		return uniqueId;
	}

	public UUID getSpoofedUniqueId() {
		return spoofedUniqueId;
	}

	public String getOldDisplayName() {
		return oldDisplayName;
	}

	public String getOldPlayerListName() {
		return oldPlayerListName;
	}
	
	public String getRealName() {
		return realName;
	}
	
	public String getNickName() {
		return nickName;
	}
	
	public String getSkinName() {
		return skinName;
	}

	public String getChatPrefix() {
		return chatPrefix;
	}

	public String getChatSuffix() {
		return chatSuffix;
	}

	public String getTabPrefix() {
		return tabPrefix;
	}

	public String getTabSuffix() {
		return tabSuffix;
	}

	public String getTagPrefix() {
		return tagPrefix;
	}

	public String getTagSuffix() {
		return tagSuffix;
	}

	public String getGroupName() {
		return groupName;
	}
	
	public int getSortID() {
		return sortID;
	}

	public void setSpoofedUniqueId(UUID spoofedUniqueId) {
		this.spoofedUniqueId = spoofedUniqueId;
	}

	public void setOldDisplayName(String oldDisplayName) {
		this.oldDisplayName = oldDisplayName;
	}

	public void setOldPlayerListName(String oldPlayerListName) {
		this.oldPlayerListName = oldPlayerListName;
	}
	
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public void setSkinName(String skinName) {
		this.skinName = skinName;
		
		//Reload skin
		prepareSkinProfile();
	}

	public void setChatPrefix(String chatPrefix) {
		this.chatPrefix = chatPrefix;
	}

	public void setChatSuffix(String chatSuffix) {
		this.chatSuffix = chatSuffix;
	}

	public void setTabPrefix(String tabPrefix) {
		this.tabPrefix = tabPrefix;
	}

	public void setTabSuffix(String tabSuffix) {
		this.tabSuffix = tabSuffix;
	}

	public void setTagPrefix(String tagPrefix) {
		this.tagPrefix = tagPrefix;
	}

	public void setTagSuffix(String tagSuffix) {
		this.tagSuffix = tagSuffix;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public void setSkinProfile(Object skinProfile) {
		this.skinProfile = skinProfile;
	}
	
	public NickedPlayerData clone() {
		NickedPlayerData nickedPlayerData = new NickedPlayerData(uniqueId, spoofedUniqueId, oldDisplayName, oldPlayerListName, realName, nickName, skinName, chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix, groupName, sortID);
		nickedPlayerData.setSkinProfile(skinProfile);
		
		return nickedPlayerData;
	}
	
	@Override
	public String toString() {
		return "NickedPlayerData={realUniqueId=" + uniqueId + " | spoofedUniqueId=" + spoofedUniqueId + " | oldDisplayName=" + oldDisplayName + " | oldPlayerListName=" + oldPlayerListName+ " | realName=" + realName+ " | nickName=" + nickName + " | skinName=" + skinName + " | chatPrefix=" + chatPrefix + " | chatSuffix=" + chatSuffix + " | tabPrefix=" + tabPrefix + " | tabSuffix=" + tabSuffix + " | tagPrefix=" + tagPrefix + " | tagSuffix=" + tagSuffix + " | groupName=" + groupName + " | sortID=" + sortID + "}";
	}

}
