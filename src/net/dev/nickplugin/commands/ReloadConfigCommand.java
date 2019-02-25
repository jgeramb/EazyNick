package net.dev.nickplugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.dev.nickplugin.utils.BookGUIFileUtils;
import net.dev.nickplugin.utils.FileUtils;
import net.dev.nickplugin.utils.NickNameFileUtils;
import net.dev.nickplugin.utils.Utils;

public class ReloadConfigCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.reload") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.reload")) {
				FileUtils.cfg = YamlConfiguration.loadConfiguration(FileUtils.file);
				FileUtils.saveFile();
				
				NickNameFileUtils.cfg = YamlConfiguration.loadConfiguration(NickNameFileUtils.file);
				NickNameFileUtils.saveFile();
				
				BookGUIFileUtils.cfg = YamlConfiguration.loadConfiguration(BookGUIFileUtils.file);
				BookGUIFileUtils.saveFile();
				
				Utils.nickNames = NickNameFileUtils.cfg.getStringList("NickNames");
				Utils.blackList = FileUtils.cfg.getStringList("BlackList");
				Utils.worldBlackList = FileUtils.cfg.getStringList("AutoNickWorldBlackList");
				
				for (String blackListName : Utils.blackList) {
					Utils.blackList.remove(blackListName);
					Utils.blackList.add(blackListName.toUpperCase());
				}
				
				for (String blackListWorld : Utils.worldBlackList) {
					Utils.worldBlackList.remove(blackListWorld);
					Utils.worldBlackList.add(blackListWorld.toUpperCase());
				}
				
				
				Utils.PREFIX = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.Prefix")) + " ";
				Utils.NO_PERM = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.NoPerm"));
				Utils.NOT_PLAYER = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.NotPlayer"));
				
				p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.ReloadConfig")));
			} else {
				p.sendMessage(Utils.NO_PERM);
			}
		} else {
			Utils.sendConsole(Utils.NOT_PLAYER);
		}
		
		return true;
	}
	
}
