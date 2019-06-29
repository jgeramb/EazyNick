package net.dev.nickplugin.commands;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.nickplugin.api.NickManager;
import net.dev.nickplugin.utils.BookGUIFileUtils;
import net.dev.nickplugin.utils.FileUtils;
import net.dev.nickplugin.utils.Utils;
import net.dev.nickplugin.utils.anvilUtils.AnvilGUI;
import net.dev.nickplugin.utils.bookUtils.NMSBookBuilder;
import net.dev.nickplugin.utils.bookUtils.NMSBookUtils;
import net.dev.nickplugin.utils.signutils.SignGUI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class BookGUICommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.gui") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.gui")) {
				if(new NickManager(p).isNicked())
					p.chat("/unnick");
				
				String arrow = "§0\u27A4";
				
				if(args.length == 0) {
					TextComponent option = new TextComponent("§0§n\u27A4 I understand, set\n§nup my nickname");
					option.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui accept"));
					option.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to proceed") }));
					
					NMSBookUtils.open(p, NMSBookBuilder.create("Info", new TextComponent("§0Nicknames allow you to play with a different\nusername to not get recognized.\n\nAll rules still apply. You can still be reported and all name history is stored.\n\n"), option));
				} else if(args.length == 1) {
					if(args[0].equalsIgnoreCase("accept")) {
						TextComponent option1 = new TextComponent(arrow + " " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank1.Rank")) + "\n");
						option1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + BookGUIFileUtils.cfg.getString("BookGUI.Rank1.RankName")));
						option1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to be shown as " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank1.Rank"))) }));
						TextComponent option2 = new TextComponent(arrow + " " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank2.Rank")) + "\n");
						option2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + BookGUIFileUtils.cfg.getString("BookGUI.Rank2.RankName")));
						option2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to be shown as " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank2.Rank"))) }));
						TextComponent option3 = new TextComponent(arrow + " " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank3.Rank")) + "\n");
						option3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + BookGUIFileUtils.cfg.getString("BookGUI.Rank3.RankName")));
						option3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to be shown as " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank3.Rank"))) }));
						TextComponent option4 = new TextComponent(arrow + " " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank4.Rank")) + "\n");
						option4.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + BookGUIFileUtils.cfg.getString("BookGUI.Rank4.RankName")));
						option4.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to be shown as " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank4.Rank"))) }));
						TextComponent option5 = new TextComponent(arrow + " " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank5.Rank")) + "\n");
						option5.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + BookGUIFileUtils.cfg.getString("BookGUI.Rank5.RankName")));
						option5.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to be shown as " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank5.Rank"))) }));
						TextComponent option6 = new TextComponent(arrow + " " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank6.Rank")) + "\n");
						option6.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + BookGUIFileUtils.cfg.getString("BookGUI.Rank6.RankName")));
						option6.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to be shown as " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank6.Rank"))) }));
						TextComponent option7 = new TextComponent(arrow + " " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank7.Rank")) + "\n");
						option7.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + BookGUIFileUtils.cfg.getString("BookGUI.Rank7.RankName")));
						option7.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to be shown as " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank7.Rank"))) }));
						
						NMSBookUtils.open(p, NMSBookBuilder.create("Ranks", 
								new TextComponent("§0Let's get you set up with your nickname! First, you'll need to choose which §lRANK §0you would like to be shown as when nicked.\n\n"),
								(BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank1.Enabled") && (BookGUIFileUtils.cfg.getString("BookGUI.Rank1.Permission").equalsIgnoreCase("NONE") ? true : p.hasPermission(BookGUIFileUtils.cfg.getString("BookGUI.Rank1.Permission")))) ? option1 : new TextComponent(""), 
								(BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank2.Enabled") && (BookGUIFileUtils.cfg.getString("BookGUI.Rank2.Permission").equalsIgnoreCase("NONE") ? true : p.hasPermission(BookGUIFileUtils.cfg.getString("BookGUI.Rank2.Permission")))) ? option2 : new TextComponent(""), 
								(BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank3.Enabled") && (BookGUIFileUtils.cfg.getString("BookGUI.Rank3.Permission").equalsIgnoreCase("NONE") ? true : p.hasPermission(BookGUIFileUtils.cfg.getString("BookGUI.Rank3.Permission")))) ? option3 : new TextComponent(""), 
								(BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank4.Enabled") && (BookGUIFileUtils.cfg.getString("BookGUI.Rank4.Permission").equalsIgnoreCase("NONE") ? true : p.hasPermission(BookGUIFileUtils.cfg.getString("BookGUI.Rank4.Permission")))) ? option4 : new TextComponent(""), 
								(BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank5.Enabled") && (BookGUIFileUtils.cfg.getString("BookGUI.Rank5.Permission").equalsIgnoreCase("NONE") ? true : p.hasPermission(BookGUIFileUtils.cfg.getString("BookGUI.Rank5.Permission")))) ? option5 : new TextComponent(""),
								(BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank6.Enabled") && (BookGUIFileUtils.cfg.getString("BookGUI.Rank6.Permission").equalsIgnoreCase("NONE") ? true : p.hasPermission(BookGUIFileUtils.cfg.getString("BookGUI.Rank6.Permission")))) ? option6 : new TextComponent(""),
								(BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank7.Enabled") && (BookGUIFileUtils.cfg.getString("BookGUI.Rank7.Permission").equalsIgnoreCase("NONE") ? true : p.hasPermission(BookGUIFileUtils.cfg.getString("BookGUI.Rank7.Permission")))) ? option7 : new TextComponent("")));
					}
				} else if(args.length == 2) {
					TextComponent option1 = new TextComponent(arrow + " §0My normal skin\n");
					option1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " DEFAULT"));
					option1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to use your normal skin") }));
					TextComponent option2 = new TextComponent(arrow + " §0Steven/Alex skin\n");
					option2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " NORMAL"));
					option2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to use a Steven/Alex skin") }));
					TextComponent option3 = new TextComponent(arrow + " §0Random skin\n");
					option3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " +args[1] + " RANDOM"));
					option3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to use a random skin") }));
					
					TextComponent option4 = new TextComponent(Utils.lastSkinNames.containsKey(p.getUniqueId()) ? (arrow + " §0Reuse " + Utils.lastSkinNames.get(p.getUniqueId()) + "\n") : "");
					
					if(Utils.lastSkinNames.containsKey(p.getUniqueId())) {
						option4.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " " + Utils.lastSkinNames.get(p.getUniqueId())));
						option4.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to reuse your previous skin") }));
					}
						
					NMSBookUtils.open(p, NMSBookBuilder.create("Skin",  new TextComponent("§0Awesome! Now, wich §lSKIN §0would you like to have while nicked?\n\n"), option1, option2, option3, option4));
				} else if(args.length == 3) {
					TextComponent option1 = new TextComponent(arrow + " §0Enter a name\n");
					option1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " " + args[2] + " ENTERNAME"));
					option1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to enter a name") }));
					TextComponent option2 = new TextComponent(arrow + " §0Use random name\n");
					option2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " " + args[2] + " RANDOM"));
					option2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to use a random name") }));
					
					TextComponent option3 = new TextComponent(Utils.lastSkinNames.containsKey(p.getUniqueId()) ? (arrow + " §0Reuse '" + Utils.lastNickNames.get(p.getUniqueId()) + "'\n") : "");
					
					if(Utils.lastNickNames.containsKey(p.getUniqueId())) {
						option3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/booknick " + args[1] + " " + args[2] + " " + Utils.lastNickNames.get(p.getUniqueId())));
						option3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to reuse '" + Utils.lastNickNames.get(p.getUniqueId()) + "'") }));
					}

					if(FileUtils.cfg.getBoolean("AllowBookGUICustomName") && (p.hasPermission("nick.customnickname") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.customnickname")))
						NMSBookUtils.open(p, NMSBookBuilder.create("Name", new TextComponent("§0Alright, now you'll need to choose the §0§lNAME §0to use!\n\n"), option1, option2, option3, new TextComponent("\n§0To go back to being your usual self, type:\n§l/nick reset")));
					else
						NMSBookUtils.open(p, NMSBookBuilder.create("Name", new TextComponent("§0Alright, now you'll need to choose the §0§lNAME §0to use!\n\n"), option2, option3, new TextComponent("\n§0To go back to being your usual self, type:\n§l/nick reset")));
				} else {
					if(args[3].equalsIgnoreCase("RANDOM")) {
						String name = Utils.nickNames.get((new Random().nextInt(Utils.nickNames.size())));
						boolean nickNameIsInUse = false;
						
						for (String nickName : Utils.playerNicknames.values())
							if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
								nickNameIsInUse = true;
						
						while (nickNameIsInUse) {
							nickNameIsInUse = false;
							name = Utils.nickNames.get((new Random().nextInt(Utils.nickNames.size())));
							
							for (String nickName : Utils.playerNicknames.values())
								if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
									nickNameIsInUse = true;
						}
					
						TextComponent option1 = new TextComponent("     §a§nUSE NAME\n");
						option1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/booknick " + args[1] + " " + args[2]  + " " + name));
						option1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to use this name.") }));
						TextComponent option2 = new TextComponent("     §c§nTRY AGAIN\n\n");
						option2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " " + args[2]  + " RANDOM"));
						option2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to generate another name.") }));
						TextComponent option3 = new TextComponent("§0§nOr enter a name to\n§nuse.");
						option3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bookgui " + args[0] + " " + args[1] + " " + args[2]  + " ENTERNAME"));
						option3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { new TextComponent("§fClick here to enter a name") }));
						
						if(FileUtils.cfg.getBoolean("AllowBookGUICustomName") && (p.hasPermission("nick.customnickname") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.customnickname")))
							NMSBookUtils.open(p, NMSBookBuilder.create("RandomNick", new TextComponent("§0We've generated a random username for you:\n§l" + name + "\n\n"), option1, option2, option3));
						else
							NMSBookUtils.open(p, NMSBookBuilder.create("RandomNick", new TextComponent("§0We've generated a random username for you:\n§l" + name + "\n\n"), option1, option2));
					} else if(args[3].equalsIgnoreCase("ENTERNAME")) {
						if(FileUtils.cfg.getBoolean("AllowBookGUICustomName") && (p.hasPermission("nick.customnickname") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.customnickname"))) {
							if(FileUtils.cfg.getBoolean("UseSignGUIForCustomName")) {
								SignGUI.open(p, "", "^^^^^^^^^^^^^^^", "Enter your", "username here", new SignGUI.EditCompleteListener() {
									
									@Override
									public void onEditComplete(SignGUI.EditCompleteEvent e) {
										p.chat("/booknick " + args[1] + " " + args[2] + " " + e.getLines()[0]);
									}
								});
							} else {
								AnvilGUI gui = new AnvilGUI(p, new AnvilGUI.AnvilClickEventHandler() {
		
									@Override
									public void onAnvilClick(AnvilGUI.AnvilClickEvent e) {
										if (e.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
											e.setWillClose(true);
											e.setWillDestroy(true);
											
											p.chat("/booknick " + args[1] + " " + args[2] + " " + e.getName());
										} else {
											e.setWillClose(false);
											e.setWillDestroy(false);
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
					}
				}
			} else
				p.sendMessage(Utils.noPerm);
		} else
			Utils.sendConsole(Utils.notPlayer);
		
		return true;
	}
	
}
