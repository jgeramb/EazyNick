package net.dev.nickplugin.commands;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.nickplugin.utils.BookGUIFileUtils;
import net.dev.nickplugin.utils.NickManager;
import net.dev.nickplugin.utils.Utils;
import net.dev.nickplugin.utils.anvilutils.AnvilGUI;
import net.dev.nickplugin.utils.bookutils.BookBuilder_1_9_R2;
import net.dev.nickplugin.utils.bookutils.BookUtils_1_9_R2;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class BookGUICommand_1_9_R2 implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.gui") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.gui")) {
				if(new NickManager(p).isNicked()) {
					p.chat("/unnick");
				}
				
				if(args.length == 0) {
					String arrow = "§0\u27A4";
					
					TextComponent option1 = new TextComponent(arrow + " " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank1.Rank")) + "\n§0");
					option1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + BookGUIFileUtils.cfg.getString("BookGUI.Rank1.RankName")));
					option1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§0§fClick here to be show as " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank1.Rank"))) }));
					TextComponent option2 = new TextComponent(arrow + " " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank2.Rank")) + "\n§0");
					option2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + BookGUIFileUtils.cfg.getString("BookGUI.Rank2.RankName")));
					option2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§0§fClick here to be show as " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank2.Rank"))) }));
					TextComponent option3 = new TextComponent(arrow + " " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank3.Rank")) + "\n§0");
					option3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + BookGUIFileUtils.cfg.getString("BookGUI.Rank3.RankName")));
					option3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§0§fClick here to be show as " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank3.Rank"))) }));
					TextComponent option4 = new TextComponent(arrow + " " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank4.Rank")) + "\n§0");
					option4.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + BookGUIFileUtils.cfg.getString("BookGUI.Rank4.RankName")));
					option4.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§0§fClick here to be show as " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank4.Rank"))) }));
					TextComponent option5 = new TextComponent(arrow + " " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank5.Rank")) + "\n§0");
					option5.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + BookGUIFileUtils.cfg.getString("BookGUI.Rank5.RankName")));
					option5.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§0§fClick here to be show as " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank5.Rank"))) }));
					TextComponent option6 = new TextComponent(arrow + " " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank5.Rank")) + "\n§0");
					option6.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + BookGUIFileUtils.cfg.getString("BookGUI.Rank6.RankName")));
					option6.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§0§fClick here to be show as " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank6.Rank"))) }));
					TextComponent option7 = new TextComponent(arrow + " " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank5.Rank")) + "\n§0");
					option7.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + BookGUIFileUtils.cfg.getString("BookGUI.Rank7.RankName")));
					option7.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§0§fClick here to be show as " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank7.Rank"))) }));
					TextComponent option8 = new TextComponent(arrow + " " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank5.Rank")) + "\n§0");
					option8.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + BookGUIFileUtils.cfg.getString("BookGUI.Rank8.RankName")));
					option8.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§0§fClick here to be show as " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank8.Rank"))) }));
					
					BookUtils_1_9_R2.open(p, BookBuilder_1_9_R2.create("Ranks", 
							new TextComponent("§0Let's get you set up\nwith your nickname!\nFirst, you'll need to choose which §0§lRANK§0\nyou would like to be\nshown as when nicked.\n\n"),
							((BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank1.Enabled") == true)) ? option1 : new TextComponent(""), 
							((BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank2.Enabled") == true)) ? option2 : new TextComponent(""), 
							((BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank3.Enabled") == true)) ? option3 : new TextComponent(""), 
							((BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank4.Enabled") == true)) ? option4 : new TextComponent(""), 
							((BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank5.Enabled") == true)) ? option5 : new TextComponent(""),
							((BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank6.Enabled") == true)) ? option6 : new TextComponent(""),
							((BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank7.Enabled") == true)) ? option7 : new TextComponent(""),
							((BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank8.Enabled") == true)) ? option8 : new TextComponent("")), 
							false);
				} else if(args.length == 1) {
					String arrow = "§0\u27A4";
					TextComponent option1 = new TextComponent(arrow + " §0My normal skin\n");
					option1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " DEFAULT"));
					option1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to use your normal skin") }));
					TextComponent option2 = new TextComponent(arrow + " §0Steven/Alex skin\n");
					option2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " NORMAL"));
					option2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to use a Steven/Alex skin") }));
					TextComponent option3 = new TextComponent(arrow + " §0Random skin\n");
					option3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " RANDOM"));
					option3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to use a random skin") }));
					
					BookUtils_1_9_R2.open(p, BookBuilder_1_9_R2.create("Skin", 
							new TextComponent("§0Awesome! Now, wich\n§0§lSKIN §0would you like\nto have while nicked?\n\n"), 
							option1, option2, option3),
							false);
				} else if(args.length == 2) {
					String arrow = "§0\u27A4";
					TextComponent option1 = new TextComponent(arrow + " §0Enter a name\n");
					option1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " ENTERNAME"));
					option1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to enter a name") }));
					TextComponent option2 = new TextComponent(arrow + " §0Use random name\n§0");
					option2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " RANDOM"));
					option2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§0§fClick here to use a random name") }));

					BookUtils_1_9_R2.open(p, BookBuilder_1_9_R2.create("Name", 
							new TextComponent("§0Alright, now you'll need\n§0to choose the §0§lNAME §0to use!\n\n"),
							option1, option2,
							new TextComponent("\n§0To go back to being\n§0your usual self, type:\n§0§l/unnick")), 
							false);
				} else {
					if(args[2].equalsIgnoreCase("RANDOM")) {
						String name = Utils.nickNames.get((new Random().nextInt(Utils.nickNames.size())));
						boolean nickNameIsInUse = false;
						
						for (String nickName : Utils.playerNicknames.values()) {
							if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase())) {
								nickNameIsInUse = true;
							}
						}
						
						while (nickNameIsInUse == true) {
							nickNameIsInUse = false;
							name = Utils.nickNames.get((new Random().nextInt(Utils.nickNames.size())));
							
							for (String nickName : Utils.playerNicknames.values()) {
								if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase())) {
									nickNameIsInUse = true;
								}
							}
						}
					
						TextComponent option1 = new TextComponent("     §a§nUSE NAME\n");
						option1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/booknick " + args[0] + " " + args[1] + " " + name));
						option1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to use this name.") }));
						TextComponent option2 = new TextComponent("§0     §c§nTRY AGAIN\n§0\n§0");
						option2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " RANDOM"));
						option2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to generate another name.") }));
						TextComponent option3 = new TextComponent("§0§nOr enter a name to use.");
						option3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " ENTERNAME"));
						option3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to enter a name") }));
						
						BookUtils_1_9_R2.open(p, BookBuilder_1_9_R2.create("RandomNick",
								new TextComponent("§0We've generated a\n§0random username for\n§0you:\n§0§l" + name + "\n\n"),
								option1, option2, option3),
								false);
					} else if(args[2].equalsIgnoreCase("ENTERNAME")) {
						AnvilGUI gui = new AnvilGUI(p, new AnvilGUI.AnvilClickEventHandler() {

							@Override
							public void onAnvilClick(AnvilGUI.AnvilClickEvent event) {
								if (event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
									event.setWillClose(true);
									event.setWillDestroy(true);
									
									p.chat("/booknick " + args[0] + " " + args[1] + " " + event.getName());
								} else {
									event.setWillClose(false);
									event.setWillDestroy(false);
								}
							}
						});
						
						gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, Utils.createItem(Material.PAPER, 1, 0, "Enter name here"));

						try {
							gui.open();
						} catch (IllegalAccessException | InvocationTargetException | InstantiationException ex) {
							ex.printStackTrace();
						}
					}
				}
			} else {
				p.sendMessage(Utils.NO_PERM);
			}
		} else {
			Utils.sendConsole(Utils.NOT_PLAYER);
		}
		
		return true;
	}
	
}
