package net.dev.eazynick.commands;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.sql.MySQLNickManager;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.LanguageFileUtils;
import net.dev.eazynick.utils.Utils;

public class ToggleBungeeNickCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
		MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
		
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.use") && p.hasPermission("nick.item")) {
				if (fileUtils.cfg.getBoolean("BungeeCord")) {
					boolean hasItem = false;
					
					if(fileUtils.cfg.getBoolean("NeedItemToToggleNick")) {
						 if(!((p.getItemInHand() != null) && (p.getItemInHand().getType() != Material.AIR && p.getItemInHand().getItemMeta() != null && p.getItemInHand().getItemMeta().getDisplayName() != null) && (p.getItemInHand().getItemMeta().getDisplayName() .equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.BungeeCord.DisplayName.Disabled"))) || p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(languageFileUtils.getConfigString("NickItem.BungeeCord.DisplayName.Enabled"))))
							 return true;
						 else
							 hasItem = true;
					}
					
					if(mysqlNickManager.isPlayerNicked(p.getUniqueId())) {
						mysqlNickManager.removePlayer(p.getUniqueId());
						
						if(hasItem)
							p.getInventory().setItem(p.getInventory().getHeldItemSlot(), utils.createItem(Material.getMaterial(fileUtils.cfg.getString("NickItem.ItemType.Disabled")), fileUtils.cfg.getInt("NickItem.ItemAmount.Disabled"), fileUtils.cfg.getInt("NickItem.MetaData.Disabled"), languageFileUtils.getConfigString("NickItem.BungeeCord.DisplayName.Disabled"), languageFileUtils.getConfigString("NickItem.ItemLore.Disabled").replace("&n", "\n"), fileUtils.cfg.getBoolean("NickItem.Enchanted.Disabled")));
	
						p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.BungeeAutoNickDisabled"));
					} else {
						String name = utils.getNickNames().get((new Random().nextInt(utils.getNickNames().size())));
	
						mysqlNickManager.addPlayer(p.getUniqueId(), name, name);
						
						if(hasItem)
							p.getInventory().setItem(p.getInventory().getHeldItemSlot(), utils.createItem(Material.getMaterial(fileUtils.cfg.getString("NickItem.ItemType.Enabled")), fileUtils.cfg.getInt("NickItem.ItemAmount.Enabled"), fileUtils.cfg.getInt("NickItem.MetaData.Enabled"), languageFileUtils.getConfigString("NickItem.BungeeCord.DisplayName.Enabled"), languageFileUtils.getConfigString("NickItem.ItemLore.Enabled").replace("&n", "\n"), fileUtils.cfg.getBoolean("NickItem.Enchanted.Enabled")));
	
						p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.BungeeAutoNickEnabled"));
					}
				}
			} else
				p.sendMessage(utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}
	
}
