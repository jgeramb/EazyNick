package net.dev.eazynick.hooks;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.sql.MySQLNickManager;
import net.dev.eazynick.sql.MySQLPlayerDataManager;
import net.dev.eazynick.utilities.configuration.yaml.SetupYamlFile;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceHolderExpansion extends PlaceholderExpansion {
	
	private Plugin plugin;
	
	public PlaceHolderExpansion(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
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
				return String.valueOf(Math.round(player.getHealth() + ((version.startsWith("1_14") || version.startsWith("1_15") || version.startsWith("1_16") || version.startsWith("1_17")) ? player.getAbsorptionAmount() : (player.hasPotionEffect(PotionEffectType.ABSORPTION) ? ((player.getActivePotionEffects().stream().filter(currentPotionEffect -> currentPotionEffect.getType().equals(PotionEffectType.ABSORPTION)).findFirst().get().getAmplifier() + 1) * 2) : 0))));
		}
		
		return null;
	}
	
	@Override
	public String getIdentifier() {
		return plugin.getDescription().getName();
	}
	
	@Override
	public boolean canRegister() {
		return true;
	}
	
	@Override
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}
	
	@Override
	public String getAuthor() {
		return plugin.getDescription().getAuthors().toString().replace("[", "").replace("]", "");
	}
	
}
