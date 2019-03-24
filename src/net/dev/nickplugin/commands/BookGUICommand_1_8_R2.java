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
import net.dev.nickplugin.utils.bookutils.BookUtils_1_8_R2;
import net.dev.nickplugin.utils.bookutils.BookUtils_1_8_R2.ClickAction;
import net.dev.nickplugin.utils.bookutils.BookUtils_1_8_R2.HoverAction;
import net.dev.nickplugin.utils.bookutils.BookUtils_1_8_R2.PageBuilder;

public class BookGUICommand_1_8_R2 implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.gui") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.gui")) {
				if(new NickManager(p).isNicked()) {
					p.chat("/unnick");
				}
				
				if(args.length == 0) {
					BookUtils_1_8_R2 bu = new BookUtils_1_8_R2("Nick", "Ranks");
					PageBuilder page1 = bu.addPage();
					page1.add("§0Let's get you set up\nwith your nickname!\nFirst, you'll need to choose which §0§lRANK§0\nyou would like to be\nshown as when nicked.\n")
							.build();
					page1.add("\n").build();
					
					String arrow = "\u27A4";

					if((BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank1.Enabled"))) {
						page1.add(arrow + " " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank1.Rank")) + "\n")
								.clickEvent(ClickAction.Run_Command, "/bookgui " + BookGUIFileUtils.cfg.getString("BookGUI.Rank1.RankName"))
								.hoverEvent(HoverAction.Show_Text, "§fClick here to be show as " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank1.Rank")))
								.build();
					}
					if((BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank2.Enabled"))) {
						page1.add(arrow + " " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank2.Rank")) + "\n")
								.clickEvent(ClickAction.Run_Command, "/bookgui " + BookGUIFileUtils.cfg.getString("BookGUI.Rank2.RankName"))
								.hoverEvent(HoverAction.Show_Text, "§fClick here to be show as " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank2.Rank")))
								.build();
					}
					if((BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank3.Enabled"))) {
						page1.add(arrow + " " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank3.Rank")) + "\n")
								.clickEvent(ClickAction.Run_Command, "/bookgui " + BookGUIFileUtils.cfg.getString("BookGUI.Rank3.RankName"))
								.hoverEvent(HoverAction.Show_Text, "§fClick here to be show as " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank3.Rank")))
								.build();
					}
					if((BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank4.Enabled"))) {
						page1.add(arrow + " " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank4.Rank"))+ "\n")
								.clickEvent(ClickAction.Run_Command, "/bookgui " + BookGUIFileUtils.cfg.getString("BookGUI.Rank4.RankName"))
								.hoverEvent(HoverAction.Show_Text, "§fClick here to be show as " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank4.Rank")))
								.build();
					}
					if((BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank5.Enabled"))) {
						page1.add(arrow + " " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank5.Rank")) + "\n")
								.clickEvent(ClickAction.Run_Command, "/bookgui " + BookGUIFileUtils.cfg.getString("BookGUI.Rank5.RankName"))
								.hoverEvent(HoverAction.Show_Text, "§fClick here to be show as " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank5.Rank")))
								.build();
					}
					if((BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank6.Enabled"))) {
						page1.add(arrow + " " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank6.Rank")) + "\n")
								.clickEvent(ClickAction.Run_Command, "/bookgui " + BookGUIFileUtils.cfg.getString("BookGUI.Rank6.RankName"))
								.hoverEvent(HoverAction.Show_Text, "§fClick here to be show as " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank6.Rank")))
								.build();
					}
					if((BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank7.Enabled"))) {
						page1.add(arrow + " " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank7.Rank")) + "\n")
								.clickEvent(ClickAction.Run_Command, "/bookgui " + BookGUIFileUtils.cfg.getString("BookGUI.Rank7.RankName"))
								.hoverEvent(HoverAction.Show_Text, "§fClick here to be show as " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank7.Rank")))
								.build();
					}
					if((BookGUIFileUtils.cfg.getBoolean("BookGUI.Rank8.Enabled"))) {
						page1.add(arrow + " " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank8.Rank")) + "\n")
								.clickEvent(ClickAction.Run_Command, "/bookgui " + BookGUIFileUtils.cfg.getString("BookGUI.Rank8.RankName"))
								.hoverEvent(HoverAction.Show_Text, "§fClick here to be show as " + ChatColor.translateAlternateColorCodes('&', BookGUIFileUtils.cfg.getString("BookGUI.Rank8.Rank")))
								.build();
					}
					page1.build();
					
					BookUtils_1_8_R2.open(p, bu.build(), false);
				} else if(args.length == 1) {
					BookUtils_1_8_R2 bu = new BookUtils_1_8_R2("Nick", "Skin");
					PageBuilder page2 = bu.addPage();
					
					page2.add("Awesome! Now, wich").build();
					page2.add("\n§0§lSKIN §0would you like\nto have while nicked?\n").build();
					page2.add("\n").build();
					
					String arrow = "\u27A4";
					
					page2.add(arrow + " §0My normal skin\n")
							.clickEvent(ClickAction.Run_Command, "/bookgui " + args[0] + " DEFAULT")
							.hoverEvent(HoverAction.Show_Text, "§fClick here to use your normal skin")
							.build();
					page2.add(arrow + " §0Steven/Alex skin\n")
							.clickEvent(ClickAction.Run_Command, "/bookgui " + args[0] + " NORMAL")
							.hoverEvent(HoverAction.Show_Text, ChatColor.WHITE + "Click here to use a Steven/Alex skin")
							.build();
					page2.add(arrow + " §0Random skin\n")
							.clickEvent(ClickAction.Run_Command, "/bookgui " + args[0] + " RANDOM")
							.hoverEvent(HoverAction.Show_Text, ChatColor.WHITE + "Click here to use a random skin")
							.build();
					
					page2.build();
					
					BookUtils_1_8_R2.open(p, bu.build(), false);
				} else if(args.length == 2) {
					BookUtils_1_8_R2 bu = new BookUtils_1_8_R2("Nick", "Name");
					PageBuilder page3 = bu.addPage();
					
					page3.add("§0Alright, now you'll need\nto choose the §0§lNAME §0to use!\n").build();
					page3.add("\n").build();
					
					String arrow = "\u27A4";
					
					page3.add(arrow + " §0Enter a name\n")
							.clickEvent(ClickAction.Run_Command, "/bookgui " + args[0] + " " + args[1] + " ENTERNAME")
							.hoverEvent(HoverAction.Show_Text, "§fClick here to enter a name")
							.build();
					page3.add(arrow + " §0Use a random name\n")
							.clickEvent(ClickAction.Run_Command, "/bookgui " + args[0] + " " + args[1] + " RANDOM")
							.hoverEvent(HoverAction.Show_Text, "§fClick here to use a random name")
							.build();
					
					page3.add("\n").build();
					page3.add("§0To go back to being\nyour usual self, type:\n§0§l/unnick").build();
					page3.build();
					
					BookUtils_1_8_R2.open(p, bu.build(), false);
				} else {
					if(args[2].equalsIgnoreCase("RANDOM")) {
						BookUtils_1_8_R2 bu = new BookUtils_1_8_R2("Nick", "RandomName");
						PageBuilder page4 = bu.addPage();
						String name = Utils.nickNames.get((new Random().nextInt(Utils.nickNames.size())));
						boolean nickNameIsInUse = false;
						
						for (String nickName : Utils.playerNicknames.values()) {
							if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase())) {
								nickNameIsInUse = true;
							}
						}
						
						while (nickNameIsInUse ) {
							nickNameIsInUse = false;
							name = Utils.nickNames.get((new Random().nextInt(Utils.nickNames.size())));
							
							for (String nickName : Utils.playerNicknames.values()) {
								if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase())) {
									nickNameIsInUse = true;
								}
							}
						}
					
						
						page4.add("We've generated a\nrandom username for\nyou:\n§0§l" + name + "\n").build();
						page4.add("\n").build();
						
						page4.add(ChatColor.GREEN + "     §a§nUSE NAME\n")
								.clickEvent(ClickAction.Run_Command, "/booknick " + args[0] + " " + args[1] + " " + name)
								.hoverEvent(HoverAction.Show_Text,ChatColor.WHITE + "Click here to use this name.").build();
						page4.add(ChatColor.RED + "    §c§nTRY AGAIN\n")
								.clickEvent(ClickAction.Run_Command, "/bookgui " + args[0] + " " + args[1] + " " + "RANDOM")
								.hoverEvent(HoverAction.Show_Text, "§fClick here to generate another name")
								.build();
						page4.add("\n").build();
						page4.add("§0§nOr enter a name to use.")
								.clickEvent(ClickAction.Run_Command, "/bookgui " + args[0] + " " + args[1] + " ENTERNAME")
								.hoverEvent(HoverAction.Show_Text, "§fClick here to enter a name")
								.build();
						
						page4.build();
						
						BookUtils_1_8_R2.open(p, bu.build(), false);
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
