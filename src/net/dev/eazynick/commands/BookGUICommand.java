package net.dev.eazynick.commands;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.api.PlayerUnnickEvent;
import net.dev.eazynick.utils.BookGUIFileUtils;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.Utils;
import net.dev.eazynick.utils.anvilutils.AnvilGUI;
import net.dev.eazynick.utils.bookutils.BookPage;
import net.dev.eazynick.utils.bookutils.NMSBookBuilder;
import net.dev.eazynick.utils.bookutils.NMSBookUtils;
import net.dev.eazynick.utils.signutils.SignGUI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class BookGUICommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		BookGUIFileUtils bookGUIFileUtils = eazyNick.getBookGUIFileUtils();
		NMSBookUtils nmsBookUtils = eazyNick.getNMSBookUtils();
		NMSBookBuilder nmsBookBuilder = eazyNick.getNMSBookBuilder();
		
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.gui")) {
				if(new NickManager(p).isNicked())
					Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(p));
				
				if(args.length == 0) {
					if(bookGUIFileUtils.cfg.getBoolean("BookGUI.Page1.Enabled")) {
						TextComponent option = new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.Accept.Text"));
						option.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui accept"));
						option.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.Accept.Hover")) }));
						
						nmsBookUtils.open(p, nmsBookBuilder.create(bookGUIFileUtils.getConfigString("BookGUI.Page1.Title"), new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.Page1.Text")), option));
						return true;
					} else
						args = new String[] { "accept" };
				}
				
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("accept")) {
						ArrayList<TextComponent> textComponentsOfFirstPage = new ArrayList<>();
						ArrayList<TextComponent> textComponentsOfSecondPage = new ArrayList<>();
						
						textComponentsOfFirstPage.add(new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.Page2.Text")));
						
						for (int i = 1; i <= 18; i++) {
							String permission = bookGUIFileUtils.getConfigString("BookGUI.Rank" + i + ".Permission");
							
							if(bookGUIFileUtils.cfg.getBoolean("BookGUI.Rank" + i + ".Enabled") && (permission.equalsIgnoreCase("NONE") || p.hasPermission(permission))) {
								TextComponent textComponent = new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.Rank.Text").replace("%rank%", bookGUIFileUtils.getConfigString("BookGUI.Rank" + i + ".Rank")));
								textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + bookGUIFileUtils.getConfigString("BookGUI.Rank" + i + ".RankName")));
								textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.Rank.Hover").replace("%rank%", bookGUIFileUtils.getConfigString("BookGUI.Rank" + i + ".Rank"))) }));
								
								if(textComponentsOfFirstPage.size() < 7)
									textComponentsOfFirstPage.add(textComponent);
								else
									textComponentsOfSecondPage.add(textComponent);
							}
						}
						
						nmsBookUtils.open(p, nmsBookBuilder.create(bookGUIFileUtils.getConfigString("BookGUI.Page2.Title"), new BookPage(textComponentsOfFirstPage), new BookPage(textComponentsOfSecondPage)));
					}
				} else if(args.length == 2) {
					TextComponent option1 = new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.NormalSkin.Text"));
					option1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " DEFAULT"));
					option1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.NormalSkin.Hover")) }));
					TextComponent option2 = new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.SteveAlexSkin.Text"));
					option2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " NORMAL"));
					option2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.SteveAlexSkin.Hover")) }));
					TextComponent option3 = new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.RandomSkin.Text"));
					option3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " +args[1] + " RANDOM"));
					option3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.RandomSkin.Hover")) }));
					
					TextComponent option4 = new TextComponent(utils.getLastSkinNames().containsKey(p.getUniqueId()) ? (bookGUIFileUtils.getConfigString("BookGUI.ReuseSkin.Text").replace("%skin%", utils.getLastSkinNames().get(p.getUniqueId()))) : "");
					
					if(utils.getLastSkinNames().containsKey(p.getUniqueId())) {
						option4.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " " + utils.getLastSkinNames().get(p.getUniqueId())));
						option4.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.ReuseSkin.Hover")) }));
					}
						
					nmsBookUtils.open(p, nmsBookBuilder.create(bookGUIFileUtils.getConfigString("BookGUI.Page3.Title"),  new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.Page3.Text")), option1, option2, option3, option4));
				} else if(args.length == 3) {
					TextComponent option1 = new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.EnterName.Text"));
					option1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " " + args[2] + " ENTERNAME"));
					option1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.EnterName.Hover")) }));
					TextComponent option2 = new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.RandomName.Text"));
					option2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " " + args[2] + " RANDOM"));
					option2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.RandomName.Hover")) }));
					
					TextComponent option3 = new TextComponent(utils.getLastSkinNames().containsKey(p.getUniqueId()) ? (bookGUIFileUtils.getConfigString("BookGUI.ReuseName.Text").replace("%name%", utils.getLastNickNames().get(p.getUniqueId()))) : "");
					
					if(utils.getLastNickNames().containsKey(p.getUniqueId())) {
						option3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/booknick " + args[1] + " " + args[2] + " " + utils.getLastNickNames().get(p.getUniqueId())));
						option3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.ReuseName.Hover").replace("%name%", utils.getLastNickNames().get(p.getUniqueId()))) }));
					}

					if(fileUtils.cfg.getBoolean("AllowBookGUICustomName") && (p.hasPermission("nick.customnickname")))
						nmsBookUtils.open(p, nmsBookBuilder.create(bookGUIFileUtils.getConfigString("BookGUI.Page4.Title"), new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.Page4.Text")), option1, option2, option3, new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.Page5.Text2"))));
					else
						nmsBookUtils.open(p, nmsBookBuilder.create(bookGUIFileUtils.getConfigString("BookGUI.Page4.Title"), new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.Page4.Text")), option2, option3, new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.Page5.Text2"))));
				} else {
					if(args[3].equalsIgnoreCase("RANDOM")) {
						String name = utils.getNickNames().get((new Random().nextInt(utils.getNickNames().size())));
						boolean nickNameIsInUse = false;
						
						for (String nickName : utils.getPlayerNicknames().values())
							if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
								nickNameIsInUse = true;
						
						while (nickNameIsInUse) {
							nickNameIsInUse = false;
							name = utils.getNickNames().get((new Random().nextInt(utils.getNickNames().size())));
							
							for (String nickName : utils.getPlayerNicknames().values())
								if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
									nickNameIsInUse = true;
						}
					
						TextComponent option1 = new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.OptionUseName.Text"));
						option1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/booknick " + args[1] + " " + args[2]  + " " + name));
						option1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.OptionUseName.Hover")) }));
						TextComponent option2 = new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.OptionTryAgain.Text"));
						option2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " " + args[2]  + " RANDOM"));
						option2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.OptionTryAgain.Hover")) }));
						TextComponent option3 = new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.OptionEnterName.Text"));
						option3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " " + args[2]  + " ENTERNAME"));
						option3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.OptionEnterName.Hover")) }));
						
						if(fileUtils.cfg.getBoolean("AllowBookGUICustomName") && (p.hasPermission("nick.customnickname")))
							nmsBookUtils.open(p, nmsBookBuilder.create(bookGUIFileUtils.getConfigString("BookGUI.Page5.Title"), new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.Page5.Text").replace("%name%", name)), option1, option2, option3));
						else
							nmsBookUtils.open(p, nmsBookBuilder.create(bookGUIFileUtils.getConfigString("BookGUI.Page5.Title"), new TextComponent(bookGUIFileUtils.getConfigString("BookGUI.Page5.Text").replace("%name%", name)), option1, option2));
					} else if(args[3].equalsIgnoreCase("ENTERNAME")) {
						if(fileUtils.cfg.getBoolean("AllowBookGUICustomName") && (p.hasPermission("nick.customnickname"))) {
							String rankName = args[1], skinType = args[2];
							
							if(fileUtils.cfg.getBoolean("UseSignGUIForCustomName")) {
								eazyNick.getSignGUI().open(p, bookGUIFileUtils.getConfigString("SignGUI.Line1"), bookGUIFileUtils.getConfigString("SignGUI.Line2"), bookGUIFileUtils.getConfigString("SignGUI.Line3"), bookGUIFileUtils.getConfigString("SignGUI.Line4"), new SignGUI.EditCompleteListener() {
									
									@Override
									public void onEditComplete(SignGUI.EditCompleteEvent e) {
										utils.performRankedNick(p, rankName, skinType, e.getLines()[0]);
									}
								});
							} else {
								AnvilGUI gui = new AnvilGUI(p, new AnvilGUI.AnvilClickEventHandler() {
		
									@Override
									public void onAnvilClick(AnvilGUI.AnvilClickEvent e) {
										if (e.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
											e.setWillClose(true);
											e.setWillDestroy(true);
											
											utils.performRankedNick(p, rankName, skinType, e.getName());
										} else {
											e.setWillClose(false);
											e.setWillDestroy(false);
										}
									}
								});
								
								gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, utils.createItem(Material.PAPER, 1, 0, bookGUIFileUtils.getConfigString("AnvilGUI.Title")));
		
								try {
									gui.open();
								} catch (IllegalAccessException | InvocationTargetException | InstantiationException ex) {
									ex.printStackTrace();
								}
							}
						}
					}
				}
			} else
				p.sendMessage(utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}
	
}
