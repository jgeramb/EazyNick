package net.dev.eazynick.hooks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;

import me.clip.deluxechat.events.DeluxeChatEvent;
import me.clip.deluxechat.objects.DeluxeFormat;

public class DeluxeChatListener implements Listener {

	@EventHandler
	public void onDeluxeChat(DeluxeChatEvent event) {
		if(EazyNick.getInstance().getSetupYamlFile().getConfiguration().getBoolean("ChangeNameAndPrefixAndSuffixInDeluxeChatFormat")) {
			Player player = event.getPlayer();
			NickManager api = new NickManager(player);
			
			if(api.isNicked()) {
				DeluxeFormat format = event.getDeluxeFormat();
				format.setPrefix(api.getChatPrefix());
				format.setSuffix(api.getChatSuffix());
				format.setName(api.getNickName());
				format.setNameColor("");
				format.setChatColor("");
				event.setDeluxeFormat(format);
			}
		}
	}

}
