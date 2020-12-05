package net.dev.eazynick.commands;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.api.PlayerUnnickEvent;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.GUIFileUtils;
import net.dev.eazynick.utils.Utils;
import net.dev.eazynick.utils.bookutils.BookPage;
import net.dev.eazynick.utils.bookutils.NMSBookBuilder;
import net.dev.eazynick.utils.bookutils.NMSBookUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class BookGUICommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		GUIFileUtils guiFileUtils = eazyNick.getGUIFileUtils();
		NMSBookUtils nmsBookUtils = eazyNick.getNMSBookUtils();
		NMSBookBuilder nmsBookBuilder = eazyNick.getNMSBookBuilder();
		
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.gui")) {
				if(new NickManager(p).isNicked())
					Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(p));
				
				if(args.length == 0) {
					if(guiFileUtils.getConfig().getBoolean("BookGUI.Page1.Enabled")) {
						TextComponent option = new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.Accept.Text"));
						option.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui accept"));
						option.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.Accept.Hover")) }));
						
						nmsBookUtils.open(p, nmsBookBuilder.create(guiFileUtils.getConfigString(p, "BookGUI.Page1.Title"), new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.Page1.Text")), option));
						return true;
					} else
						args = new String[] { "accept" };
				}
				
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("accept")) {
						ArrayList<TextComponent> textComponentsOfFirstPage = new ArrayList<>();
						ArrayList<TextComponent> textComponentsOfSecondPage = new ArrayList<>();
						
						textComponentsOfFirstPage.add(new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.Page2.Text")));
						
						for (int i = 1; i <= 18; i++) {
							String permission = guiFileUtils.getConfigString(p, "RankGUI.Rank" + i + ".Permission");
							
							if(guiFileUtils.getConfig().getBoolean("RankGUI.Rank" + i + ".Enabled") && (permission.equalsIgnoreCase("NONE") || p.hasPermission(permission))) {
								String rank = guiFileUtils.getConfigString(p, "RankGUI.Rank" + i + ".Rank");
								TextComponent textComponent = new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.Rank.Text").replace("%rank%", rank));
								textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + guiFileUtils.getConfigString(p, "RankGUI.Rank" + i + ".RankName")));
								textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.Rank.Hover").replace("%rank%", rank)) }));
								
								if(textComponentsOfFirstPage.size() < 7)
									textComponentsOfFirstPage.add(textComponent);
								else
									textComponentsOfSecondPage.add(textComponent);
							}
						}
						
						nmsBookUtils.open(p, nmsBookBuilder.create(guiFileUtils.getConfigString(p, "BookGUI.Page2.Title"), new BookPage(textComponentsOfFirstPage), new BookPage(textComponentsOfSecondPage)));
					}
				} else if(args.length == 2) {
					TextComponent option1 = new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.NormalSkin.Text"));
					option1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " DEFAULT"));
					option1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.NormalSkin.Hover")) }));
					TextComponent option2 = new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.SteveAlexSkin.Text"));
					option2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " NORMAL"));
					option2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.SteveAlexSkin.Hover")) }));
					TextComponent option3 = new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.RandomSkin.Text"));
					option3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " RANDOM"));
					option3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.RandomSkin.Hover")) }));
					TextComponent option4 = new TextComponent(fileUtils.getConfig().getBoolean("AllowBookGUISkinFromName") ? guiFileUtils.getConfigString(p, "BookGUI.SkinFromName.Text") : "");
					option4.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " SKINFROMNAME"));
					option4.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.SkinFromName.Hover")) }));
					
					TextComponent option5 = new TextComponent(utils.getLastSkinNames().containsKey(p.getUniqueId()) ? (guiFileUtils.getConfigString(p, "BookGUI.ReuseSkin.Text").replace("%skin%", utils.getLastSkinNames().get(p.getUniqueId()))) : "");
					
					if(utils.getLastSkinNames().containsKey(p.getUniqueId())) {
						option5.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " " + utils.getLastSkinNames().get(p.getUniqueId())));
						option5.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.ReuseSkin.Hover")) }));
					}
						
					nmsBookUtils.open(p, nmsBookBuilder.create(guiFileUtils.getConfigString(p, "BookGUI.Page3.Title"),  new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.Page3.Text")), option1, option2, option3, option4, option5));
				} else if(args.length == 3) {
					TextComponent option1 = new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.EnterName.Text"));
					option1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " " + args[2] + " ENTERNAME"));
					option1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.EnterName.Hover")) }));
					TextComponent option2 = new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.RandomName.Text"));
					option2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " " + args[2] + " RANDOM"));
					option2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.RandomName.Hover")) }));
					
					TextComponent option3 = new TextComponent(utils.getLastSkinNames().containsKey(p.getUniqueId()) ? (guiFileUtils.getConfigString(p, "BookGUI.ReuseName.Text").replace("%name%", utils.getLastNickNames().get(p.getUniqueId()))) : "");
					
					if(utils.getLastNickNames().containsKey(p.getUniqueId())) {
						option3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guinick " + args[1] + " " + args[2] + " " + utils.getLastNickNames().get(p.getUniqueId())));
						option3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.ReuseName.Hover").replace("%name%", utils.getLastNickNames().get(p.getUniqueId()))) }));
					}

					if(fileUtils.getConfig().getBoolean("AllowBookGUICustomName") && (p.hasPermission("nick.customnickname")))
						nmsBookUtils.open(p, nmsBookBuilder.create(guiFileUtils.getConfigString(p, "BookGUI.Page4.Title"), new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.Page4.Text")), option1, option2, option3, new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.Page5.Text2"))));
					else
						nmsBookUtils.open(p, nmsBookBuilder.create(guiFileUtils.getConfigString(p, "BookGUI.Page4.Title"), new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.Page4.Text")), option2, option3, new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.Page5.Text2"))));
				} else {
					if(args[3].equalsIgnoreCase("RANDOM")) {
						String name = utils.getNickNames().get((new Random().nextInt(utils.getNickNames().size())));
						boolean nickNameIsInUse = false;
						
						for (String nickName : utils.getPlayerNicknames().values()) {
							if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
								nickNameIsInUse = true;
						}
						
						while (nickNameIsInUse) {
							nickNameIsInUse = false;
							name = utils.getNickNames().get((new Random().nextInt(utils.getNickNames().size())));
							
							for (String nickName : utils.getPlayerNicknames().values()) {
								if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
									nickNameIsInUse = true;
							}
						}
					
						TextComponent option1 = new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.OptionUseName.Text"));
						option1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guinick " + args[1] + " " + args[2]  + " " + name));
						option1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.OptionUseName.Hover")) }));
						TextComponent option2 = new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.OptionTryAgain.Text"));
						option2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " " + args[2]  + " RANDOM"));
						option2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.OptionTryAgain.Hover")) }));
						TextComponent option3 = new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.OptionEnterName.Text"));
						option3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " " + args[2]  + " ENTERNAME"));
						option3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.OptionEnterName.Hover")) }));
						
						if(fileUtils.getConfig().getBoolean("AllowBookGUICustomName") && (p.hasPermission("nick.customnickname")))
							nmsBookUtils.open(p, nmsBookBuilder.create(guiFileUtils.getConfigString(p, "BookGUI.Page5.Title"), new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.Page5.Text").replace("%name%", name)), option1, option2, option3));
						else
							nmsBookUtils.open(p, nmsBookBuilder.create(guiFileUtils.getConfigString(p, "BookGUI.Page5.Title"), new TextComponent(guiFileUtils.getConfigString(p, "BookGUI.Page5.Text").replace("%name%", name)), option1, option2));
					} else if(args[3].equalsIgnoreCase("ENTERNAME")) {
						if(fileUtils.getConfig().getBoolean("AllowBookGUICustomName") && (p.hasPermission("nick.customnickname")))
							utils.openCustomGUI(p, args[1], args[2]);
					}
				}
			} else
				p.sendMessage(utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}
	
}
