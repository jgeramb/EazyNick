package com.justixdev.eazynick.hooks;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.sql.MySQLNickManager;
import com.justixdev.eazynick.sql.MySQLPlayerDataManager;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class PlaceHolderExpansion extends PlaceholderExpansion {
	
	private final EazyNick eazyNick;
	
	public PlaceHolderExpansion(EazyNick eazyNick) {
		this.eazyNick = eazyNick;
	}

	@Override
	public String onPlaceholderRequest(Player player, @NotNull String identifier) {
		EazyNick eazyNick = EazyNick.getInstance();
		SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
		MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
		MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMySQLPlayerDataManager();
		
		if(player != null) {
			String version = eazyNick.getVersion();
			NickManager api = new NickManager(player);
			boolean isMySQLNicked = (mysqlNickManager != null) && mysqlNickManager.isPlayerNicked(player.getUniqueId()) && !(api.isNicked()), isLobbyMode = setupYamlFile.getConfiguration().getBoolean("LobbyMode");
			
			if(identifier.equals("is_nicked") || identifier.equals("is_disguised"))
				return String.valueOf(api.isNicked());
			
			if(identifier.equals("display_name"))
				return api.getNickName();
			
			if(identifier.equals("skin_name"))
				return (isMySQLNicked && !(isLobbyMode)) ? mysqlNickManager.getSkinName(player.getUniqueId()) : (api.isNicked() ? eazyNick.getUtils().getNickedPlayers().get(player.getUniqueId()).getSkinName() : player.getName());
			
			if(identifier.equals("global_name"))
				return isMySQLNicked ? mysqlNickManager.getNickName(player.getUniqueId()) : api.getNickName();
			
			if(identifier.equals("chat_prefix"))
				return (isMySQLNicked && !(isLobbyMode)) ? mysqlPlayerDataManager.getChatPrefix(player.getUniqueId()) : api.getChatPrefix();
			
			if(identifier.equals("chat_suffix"))
				return (isMySQLNicked && !(isLobbyMode)) ? mysqlPlayerDataManager.getChatSuffix(player.getUniqueId()) : api.getChatSuffix();
			
			if(identifier.equals("tab_prefix"))
				return (isMySQLNicked && !(isLobbyMode)) ? mysqlPlayerDataManager.getTabPrefix(player.getUniqueId()) : api.getTabPrefix();
			
			if(identifier.equals("tab_suffix"))
				return (isMySQLNicked && !(isLobbyMode)) ? mysqlPlayerDataManager.getTabSuffix(player.getUniqueId()) : api.getTabSuffix();
			
			if(identifier.equals("tag_prefix"))
				return (isMySQLNicked && !(isLobbyMode)) ? mysqlPlayerDataManager.getTagPrefix(player.getUniqueId()) : api.getTagPrefix();
			
			if(identifier.equals("tag_suffix"))
				return (isMySQLNicked && !(isLobbyMode)) ? mysqlPlayerDataManager.getTagSuffix(player.getUniqueId()) : api.getTagSuffix();
			
			if(identifier.equals("real_name"))
				return api.getRealName();
			
			if(identifier.equals("rank"))
				return (isMySQLNicked && !(isLobbyMode)) ? mysqlPlayerDataManager.getGroupName(player.getUniqueId()) : api.getGroupName();
			
			if(identifier.equals("custom"))
				return setupYamlFile.getConfigString(player, api.isNicked() ? "CustomPlaceholders.Local.Nicked" : "CustomPlaceholders.Local.Default");
			
			if(identifier.equals("global_custom"))
				return setupYamlFile.getConfigString(player, ((mysqlNickManager != null) && mysqlNickManager.isPlayerNicked(player.getUniqueId())) ? "CustomPlaceholders.Global.Nicked" : "CustomPlaceholders.Global.Default");
		
			if(identifier.equals("health"))
				//noinspection OptionalGetWithoutIsPresent
				return String.valueOf(Math.round(player.getHealth() + ((version.startsWith("1_14") || version.startsWith("1_15") || version.startsWith("1_16") || version.startsWith("1_17") || version.startsWith("1_18")) ? player.getAbsorptionAmount() : (player.hasPotionEffect(PotionEffectType.ABSORPTION) ? ((player.getActivePotionEffects().stream().filter(currentPotionEffect -> currentPotionEffect.getType().equals(PotionEffectType.ABSORPTION)).findFirst().get().getAmplifier() + 1) * 2) : 0))));
		}
		
		return "";
	}
	
	@Override
    public boolean canRegister() {
        return true;
    }
	
	@Override
    public boolean persist() {
        return true;
    }
	
	@Override
	public @NotNull String getAuthor() {
		return eazyNick.getDescription().getAuthors().get(0);
	}
	
	@Override
	public @NotNull String getIdentifier() {
		return eazyNick.getName().toLowerCase();
	}
	
	@Override
	public String getRequiredPlugin() {
		return null;
	}
	
	@Override
	public @NotNull String getVersion() {
		return eazyNick.getDescription().getVersion();
	}
	
}
