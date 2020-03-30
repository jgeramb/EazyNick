package net.dev.eazynick.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utils.BookGUIFileUtils;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.LanguageFileUtils;
import net.dev.eazynick.utils.NickNameFileUtils;
import net.dev.eazynick.utils.Utils;

public class ReloadConfigCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
		BookGUIFileUtils bookGUIFileUtils = eazyNick.getBookGUIFileUtils();
		NickNameFileUtils nickNameFileUtils = eazyNick.getNickNameFileUtils();
		
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.reload")) {
				fileUtils.cfg = YamlConfiguration.loadConfiguration(eazyNick.getFileUtils().getFile());
				fileUtils.saveFile();
				
				nickNameFileUtils.cfg = YamlConfiguration.loadConfiguration(nickNameFileUtils.getFile());
				nickNameFileUtils.saveFile();
				
				bookGUIFileUtils.cfg = YamlConfiguration.loadConfiguration(bookGUIFileUtils.getFile());
				bookGUIFileUtils.saveFile();
				
				languageFileUtils.cfg = YamlConfiguration.loadConfiguration(languageFileUtils.getFile());
				languageFileUtils.saveFile();
				
				utils.setNickNames(nickNameFileUtils.cfg.getStringList("NickNames"));
				
				List<String> blackList = fileUtils.cfg.getStringList("BlackList");
				List<String> worldBlackList = fileUtils.cfg.getStringList("AutoNickWorldBlackList");
				
				if (blackList.size() >= 1) {
					ArrayList<String> toAdd = new ArrayList<>();
					
					for (String blackListName : blackList)
						toAdd.add(blackListName.toUpperCase());
					
					utils.setBlackList(new ArrayList<>(toAdd));
				}

				if (worldBlackList.size() >= 1) {
					ArrayList<String> toAdd = new ArrayList<>();
					
					for (String blackListWorld : worldBlackList)
						toAdd.add(blackListWorld.toUpperCase());
					
					utils.setWorldBlackList(new ArrayList<>(toAdd));
				}

				utils.setPrefix(languageFileUtils.getConfigString("Messages.Prefix"));
				utils.setNoPerm(languageFileUtils.getConfigString("Messages.NoPerm"));
				utils.setNotPlayer(languageFileUtils.getConfigString("Messages.NotPlayer"));
				
				p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.ReloadConfig"));
			} else
				p.sendMessage(utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}
	
}
