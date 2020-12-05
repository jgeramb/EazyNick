package net.dev.eazynick.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.api.PlayerNickEvent;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.GUIFileUtils;
import net.dev.eazynick.utils.Utils;

public class PlayerRespawnListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		EazyNick eazyNick = EazyNick.getInstance();
		GUIFileUtils guiFileUtils = eazyNick.getGUIFileUtils();
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		
		Player p = e.getPlayer();
		NickManager api = new NickManager(p);
		
		if(utils.getPlayerNicknames().containsKey(p.getUniqueId())) {
			String nickName = utils.getPlayerNicknames().get(p.getUniqueId()), skinName = utils.getLastSkinNames().containsKey(p.getUniqueId()) ? utils.getLastSkinNames().get(p.getUniqueId()) : nickName, rankName = api.getGroupName(), chatPrefix = "", chatSuffix = "", tabPrefix = "", tabSuffix = "", tagPrefix = "", tagSuffix = "";
			int sortID = 9999;
			
			if(rankName.equals(fileUtils.getConfigString(p, "Settings.NickFormat.ServerFullRank.GroupName"))) {
				chatPrefix = fileUtils.getConfigString(p, "Settings.NickFormat.ServerFullRank.Chat.Prefix");
				chatSuffix = fileUtils.getConfigString(p, "Settings.NickFormat.ServerFullRank.Chat.Suffix");
				tabPrefix = fileUtils.getConfigString(p, "Settings.NickFormat.ServerFullRank.PlayerList.Prefix");
				tabSuffix = fileUtils.getConfigString(p, "Settings.NickFormat.ServerFullRank.PlayerList.Suffix");
				tagPrefix = fileUtils.getConfigString(p, "Settings.NickFormat.ServerFullRank.NameTag.Prefix");
				tagSuffix = fileUtils.getConfigString(p, "Settings.NickFormat.ServerFullRank.NameTag.Suffix");
				sortID = fileUtils.getConfig().getInt("Settings.NickFormat.ServerFullRank.SortID");
			} else if(rankName.equals(fileUtils.getConfigString(p, "Settings.NickFormat.GroupName"))) {
				chatPrefix = fileUtils.getConfigString(p, "Settings.NickFormat.Chat.Prefix");
				chatSuffix = fileUtils.getConfigString(p, "Settings.NickFormat.Chat.Suffix");
				tabPrefix = fileUtils.getConfigString(p, "Settings.NickFormat.PlayerList.Prefix");
				tabSuffix = fileUtils.getConfigString(p, "Settings.NickFormat.PlayerList.Suffix");
				tagPrefix = fileUtils.getConfigString(p, "Settings.NickFormat.NameTag.Prefix");
				tagSuffix = fileUtils.getConfigString(p, "Settings.NickFormat.NameTag.Suffix");
				sortID = fileUtils.getConfig().getInt("Settings.NickFormat.SortID");
			} else {
				for (int i = 1; i <= 18; i++) {
					if(rankName.equals(guiFileUtils.getConfig().getString("RankGUI.Rank" + i + ".RankName"))) {
						chatPrefix = guiFileUtils.getConfigString(p, "Settings.NickFormat.Rank" + i + ".ChatPrefix");
						chatSuffix = guiFileUtils.getConfigString(p, "Settings.NickFormat.Rank" + i + ".ChatSuffix");
						tabPrefix = guiFileUtils.getConfigString(p, "Settings.NickFormat.Rank" + i + ".TabPrefix");
						tabSuffix = guiFileUtils.getConfigString(p, "Settings.NickFormat.Rank" + i + ".TabSuffix");
						tagPrefix = guiFileUtils.getConfigString(p, "Settings.NickFormat.Rank" + i + ".TagPrefix");
						tagSuffix = guiFileUtils.getConfigString(p, "Settings.NickFormat.Rank" + i + ".TagSuffix");
						sortID = guiFileUtils.getConfig().getInt("Settings.NickFormat.Rank" + i + ".SortID");
					}
				}
				
				String randomColor = "§" + ("0123456789abcdef".charAt(new Random().nextInt(16)));
				
				chatPrefix = chatPrefix.replaceAll("%randomColor%", randomColor);
				chatSuffix = chatSuffix.replaceAll("%randomColor%", randomColor);
				tabPrefix = tabPrefix.replaceAll("%randomColor%", randomColor);
				tabSuffix = tabSuffix.replaceAll("%randomColor%", randomColor);
				tagPrefix = tagPrefix.replaceAll("%randomColor%", randomColor);
				tagSuffix = tagSuffix.replaceAll("%randomColor%", randomColor);
			}
			
			api.unnickPlayerWithoutRemovingMySQL(false);

			if(utils.getLastSkinNames().containsKey(p.getUniqueId()))
				utils.getLastSkinNames().remove(p.getUniqueId());
			
			if(utils.getLastNickNames().containsKey(p.getUniqueId()))
				utils.getLastNickNames().remove(p.getUniqueId());
			
			utils.getLastSkinNames().put(p.getUniqueId(), skinName);
			utils.getLastNickNames().put(p.getUniqueId(), nickName);
			
			String finalChatPrefix = chatPrefix, finalChatSuffix = chatSuffix, finalTabPrefix = tabPrefix, finalTabSuffix = tabSuffix, finalTagPrefix = tagPrefix, finalTagSuffix = tagSuffix;
			int finalSortID = sortID;
			
			Bukkit.getScheduler().runTaskLater(eazyNick, () -> {
				if(p.isOnline()) {
					new NickManager(p).setGroupName(rankName);
					
					Bukkit.getPluginManager().callEvent(new PlayerNickEvent(p, nickName, skinName, finalChatPrefix, finalChatSuffix, finalTabPrefix, finalTabSuffix, finalTagPrefix, finalTagSuffix, false, true, finalSortID, rankName));
				}
			}, 22 + (fileUtils.getConfig().getBoolean("RandomDisguiseDelay") ? 60 : 0));
		}
	}

}
