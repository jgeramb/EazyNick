package net.dev.eazynick.utilities;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.nms.fakegui.anvil.AnvilGUI;
import net.dev.eazynick.nms.fakegui.sign.SignGUI;
import net.dev.eazynick.utilities.configuration.yaml.GUIYamlFile;
import net.dev.eazynick.utilities.configuration.yaml.SetupYamlFile;

public class GUIManager {
	
	private Utils utils;
	private GUIYamlFile guiYamlFile;
	private SetupYamlFile setupYamlFile;
	private SignGUI signGUI;
	
	public GUIManager(EazyNick eazyNick) {
		this.utils = eazyNick.getUtils();
		this.guiYamlFile = eazyNick.getGUIYamlFile();
		this.setupYamlFile = eazyNick.getSetupYamlFile();
		this.signGUI = eazyNick.getSignGUI();
	}

	public void openNickList(Player player, int page) {
		Inventory inv = Bukkit.createInventory(null, 45, guiYamlFile.getConfigString(player, "NickNameGUI.InventoryTitle").replace("%currentPage%", String.valueOf(page + 1)).replace("%currentpage%", String.valueOf(page + 1)));
		ArrayList<String> toShow = new ArrayList<>();
		
		player.openInventory(inv);
		
		for (int i = 36 * page; i < utils.getNickNames().size(); i++) {
			if(toShow.size() >= 36)
				break;
			
			toShow.add(utils.getNickNames().get(i));
		}
		
		int i = 0;
		
		for (String nickName : toShow) {
			inv.setItem(i, new ItemBuilder(1).setDisplayName(guiYamlFile.getConfigString(player, "NickNameGUI.NickName.DisplayName").replace("%nickName%", nickName).replace("%nickname%", nickName)).setSkullOwner((toShow.size() > 12) ? "MHF_Question" : nickName).build());
			i++;
		}
			
		if(page != 0)
			inv.setItem(36, new ItemBuilder(Material.ARROW).setDisplayName(guiYamlFile.getConfigString(player, "NickNameGUI.Previous.DisplayName")).build());
		
		if(utils.getNickNames().size() > ((page + 1) * 36))
			inv.setItem(44, new ItemBuilder(Material.ARROW).setDisplayName(guiYamlFile.getConfigString(player, "NickNameGUI.Next.DisplayName")).build());
		
		utils.getNickNameListPages().put(player.getUniqueId(), page);
	}
	
	public void openCustomGUI(Player player, String rankName, String skinType) {
		if(setupYamlFile.getConfiguration().getBoolean("UseSignGUIForCustomName")) {
			signGUI.open(player, guiYamlFile.getConfigString(player, "SignGUI.Line1"), guiYamlFile.getConfigString(player, "SignGUI.Line2"), guiYamlFile.getConfigString(player, "SignGUI.Line3"), guiYamlFile.getConfigString(player, "SignGUI.Line4"), new SignGUI.EditCompleteListener() {
				
				@Override
				public void onEditComplete(SignGUI.EditCompleteEvent event) {
					String name = event.getLines()[0];
					int nameLengthMin = setupYamlFile.getConfiguration().getInt("Settings.NameLength.Min"), nameLengthMax = setupYamlFile.getConfiguration().getInt("Settings.NameLength.Max");
					
					if(nameLengthMin > 16)
						nameLengthMin = 16;
					
					if(nameLengthMin < 1)
						nameLengthMin = 1;
					
					if(nameLengthMax > 16)
						nameLengthMax = 16;
					
					if(nameLengthMax < 1)
						nameLengthMax = 1;
					
					if(!(name.isEmpty()) && (name.length() <= nameLengthMax) && (name.length() >= nameLengthMin))
						utils.performRankedNick(player, rankName, skinType, name);
				}
			});
		} else {
			AnvilGUI gui = new AnvilGUI(player, new AnvilGUI.AnvilClickEventHandler() {

				@Override
				public void onAnvilClick(AnvilGUI.AnvilClickEvent event) {
					if (event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
						event.setWillClose(true);
						event.setWillDestroy(true);
						
						utils.performRankedNick(player, rankName, skinType, event.getName());
					} else {
						event.setWillClose(false);
						event.setWillDestroy(false);
					}
				}
			});
			
			gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, new ItemBuilder(Material.PAPER).setDisplayName(guiYamlFile.getConfigString(player, "AnvilGUI.Title")).build());

			try {
				gui.open();
			} catch (IllegalAccessException | InvocationTargetException | InstantiationException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void openRankedNickGUI(Player player, String text) {
		EazyNick eazyNick = EazyNick.getInstance();
		GUIYamlFile guiYamlFile = eazyNick.getGUIYamlFile();
		
		utils.getLastGUITexts().put(player.getUniqueId(), text);
		
		boolean newVersion = utils.isVersion13OrLater();
		String[] args = text.isEmpty() ? new String[0] : text.split(" ");
		
		if(args.length == 0) {
			Inventory inv = Bukkit.createInventory(null, 27, guiYamlFile.getConfigString(player, "RankedNickGUI.Step1.InventoryTitle"));
			
			for (int i = 0; i < inv.getSize(); i++)
				inv.setItem(i, new ItemBuilder(Material.getMaterial(newVersion ? "BLACK_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE"), 1, newVersion ? 0 : 15).setDisplayName("§r").build());
			
			ArrayList<ItemStack> availableRanks = new ArrayList<>();
			
			for (int i = 1; i <= 18; i++) {
				String permission = guiYamlFile.getConfigString(player, "RankGUI.Rank" + i + ".Permission");
				
				if(guiYamlFile.getConfiguration().getBoolean("RankGUI.Rank" + i + ".Enabled") && (permission.equalsIgnoreCase("NONE") || player.hasPermission(permission)))
					availableRanks.add(new ItemBuilder(Material.valueOf(guiYamlFile.getConfigString(player, "RankedNickGUI.Step1.Rank" + i + ".ItemType")), 1, guiYamlFile.getConfiguration().getInt("RankedNickGUI.Step1.Rank" + i + ".MetaData")).setDisplayName(guiYamlFile.getConfigString(player, "RankGUI.Rank" + i + ".Rank")).build());
			}
			
			switch (availableRanks.size()) {
				case 1:
					inv.setItem(13, availableRanks.get(0));
					break;
				case 2:
					inv.setItem(11, availableRanks.get(0));
					inv.setItem(15, availableRanks.get(1));
					break;
				case 3:
					inv.setItem(10, availableRanks.get(0));
					inv.setItem(13, availableRanks.get(1));
					inv.setItem(16, availableRanks.get(2));
					break;
				case 4:
					inv.setItem(10, availableRanks.get(0));
					inv.setItem(12, availableRanks.get(1));
					inv.setItem(14, availableRanks.get(2));
					inv.setItem(16, availableRanks.get(3));
					break;
				case 5:
					inv.setItem(9, availableRanks.get(0));
					inv.setItem(11, availableRanks.get(1));
					inv.setItem(13, availableRanks.get(2));
					inv.setItem(15, availableRanks.get(3));
					inv.setItem(17, availableRanks.get(4));
					break;
				case 6:
					inv.setItem(4, availableRanks.get(0));
					inv.setItem(9, availableRanks.get(1));
					inv.setItem(11, availableRanks.get(2));
					inv.setItem(15, availableRanks.get(3));
					inv.setItem(17, availableRanks.get(4));
					inv.setItem(22, availableRanks.get(5));
					break;
				case 7:
					inv.setItem(2, availableRanks.get(0));
					inv.setItem(6, availableRanks.get(1));
					inv.setItem(10, availableRanks.get(2));
					inv.setItem(13, availableRanks.get(3));
					inv.setItem(16, availableRanks.get(4));
					inv.setItem(20, availableRanks.get(5));
					inv.setItem(24, availableRanks.get(6));
					break;
				case 8:
					inv.setItem(2, availableRanks.get(0));
					inv.setItem(6, availableRanks.get(1));
					inv.setItem(10, availableRanks.get(2));
					inv.setItem(12, availableRanks.get(3));
					inv.setItem(14, availableRanks.get(4));
					inv.setItem(16, availableRanks.get(5));
					inv.setItem(20, availableRanks.get(6));
					inv.setItem(24, availableRanks.get(7));
					break;
				case 9:
					inv.setItem(2, availableRanks.get(0));
					inv.setItem(6, availableRanks.get(1));
					inv.setItem(9, availableRanks.get(2));
					inv.setItem(11, availableRanks.get(3));
					inv.setItem(13, availableRanks.get(4));
					inv.setItem(15, availableRanks.get(5));
					inv.setItem(17, availableRanks.get(6));
					inv.setItem(20, availableRanks.get(7));
					inv.setItem(24, availableRanks.get(8));
					break;
				case 10:
					inv.setItem(2, availableRanks.get(0));
					inv.setItem(4, availableRanks.get(1));
					inv.setItem(6, availableRanks.get(2));
					inv.setItem(10, availableRanks.get(3));
					inv.setItem(12, availableRanks.get(4));
					inv.setItem(14, availableRanks.get(5));
					inv.setItem(16, availableRanks.get(6));
					inv.setItem(20, availableRanks.get(7));
					inv.setItem(22, availableRanks.get(8));
					inv.setItem(24, availableRanks.get(9));
					break;
				case 11:
					inv.setItem(1, availableRanks.get(0));
					inv.setItem(3, availableRanks.get(1));
					inv.setItem(5, availableRanks.get(2));
					inv.setItem(7, availableRanks.get(3));
					inv.setItem(11, availableRanks.get(4));
					inv.setItem(13, availableRanks.get(5));
					inv.setItem(15, availableRanks.get(6));
					inv.setItem(19, availableRanks.get(7));
					inv.setItem(21, availableRanks.get(8));
					inv.setItem(23, availableRanks.get(9));
					inv.setItem(25, availableRanks.get(10));
					break;
				case 12:
					inv.setItem(2, availableRanks.get(0));
					inv.setItem(3, availableRanks.get(1));
					inv.setItem(5, availableRanks.get(2));
					inv.setItem(6, availableRanks.get(3));
					inv.setItem(10, availableRanks.get(4));
					inv.setItem(12, availableRanks.get(5));
					inv.setItem(14, availableRanks.get(6));
					inv.setItem(16, availableRanks.get(7));
					inv.setItem(20, availableRanks.get(8));
					inv.setItem(21, availableRanks.get(9));
					inv.setItem(23, availableRanks.get(10));
					inv.setItem(24, availableRanks.get(11));
					break;
				case 13:
					inv.setItem(1, availableRanks.get(0));
					inv.setItem(3, availableRanks.get(1));
					inv.setItem(5, availableRanks.get(2));
					inv.setItem(7, availableRanks.get(3));
					inv.setItem(9, availableRanks.get(4));
					inv.setItem(11, availableRanks.get(5));
					inv.setItem(13, availableRanks.get(6));
					inv.setItem(15, availableRanks.get(7));
					inv.setItem(17, availableRanks.get(8));
					inv.setItem(19, availableRanks.get(9));
					inv.setItem(21, availableRanks.get(10));
					inv.setItem(23, availableRanks.get(11));
					inv.setItem(25, availableRanks.get(12));
					break;
				case 14:
					inv.setItem(0, availableRanks.get(0));
					inv.setItem(2, availableRanks.get(1));
					inv.setItem(4, availableRanks.get(2));
					inv.setItem(6, availableRanks.get(3));
					inv.setItem(8, availableRanks.get(4));
					inv.setItem(10, availableRanks.get(5));
					inv.setItem(12, availableRanks.get(6));
					inv.setItem(14, availableRanks.get(7));
					inv.setItem(16, availableRanks.get(8));
					inv.setItem(18, availableRanks.get(9));
					inv.setItem(20, availableRanks.get(10));
					inv.setItem(22, availableRanks.get(11));
					inv.setItem(24, availableRanks.get(12));
					inv.setItem(26, availableRanks.get(13));
					break;
				case 15:
					inv.setItem(0, availableRanks.get(0));
					inv.setItem(2, availableRanks.get(1));
					inv.setItem(4, availableRanks.get(2));
					inv.setItem(6, availableRanks.get(3));
					inv.setItem(8, availableRanks.get(4));
					inv.setItem(9, availableRanks.get(5));
					inv.setItem(11, availableRanks.get(6));
					inv.setItem(13, availableRanks.get(7));
					inv.setItem(15, availableRanks.get(8));
					inv.setItem(17, availableRanks.get(9));
					inv.setItem(18, availableRanks.get(10));
					inv.setItem(20, availableRanks.get(11));
					inv.setItem(22, availableRanks.get(12));
					inv.setItem(24, availableRanks.get(13));
					inv.setItem(26, availableRanks.get(14));
					break;
				case 16:
					inv.setItem(0, availableRanks.get(0));
					inv.setItem(2, availableRanks.get(1));
					inv.setItem(4, availableRanks.get(2));
					inv.setItem(6, availableRanks.get(3));
					inv.setItem(8, availableRanks.get(4));
					inv.setItem(9, availableRanks.get(5));
					inv.setItem(11, availableRanks.get(6));
					inv.setItem(12, availableRanks.get(7));
					inv.setItem(14, availableRanks.get(8));
					inv.setItem(15, availableRanks.get(9));
					inv.setItem(17, availableRanks.get(10));
					inv.setItem(18, availableRanks.get(11));
					inv.setItem(20, availableRanks.get(12));
					inv.setItem(22, availableRanks.get(13));
					inv.setItem(24, availableRanks.get(14));
					inv.setItem(26, availableRanks.get(15));
					break;
				case 17:
					inv.setItem(0, availableRanks.get(0));
					inv.setItem(2, availableRanks.get(1));
					inv.setItem(4, availableRanks.get(2));
					inv.setItem(6, availableRanks.get(3));
					inv.setItem(8, availableRanks.get(4));
					inv.setItem(9, availableRanks.get(5));
					inv.setItem(10, availableRanks.get(6));
					inv.setItem(12, availableRanks.get(7));
					inv.setItem(13, availableRanks.get(8));
					inv.setItem(14, availableRanks.get(9));
					inv.setItem(16, availableRanks.get(10));
					inv.setItem(17, availableRanks.get(11));
					inv.setItem(18, availableRanks.get(12));
					inv.setItem(20, availableRanks.get(13));
					inv.setItem(22, availableRanks.get(14));
					inv.setItem(24, availableRanks.get(15));
					inv.setItem(26, availableRanks.get(16));
					break;
				case 18:
					inv.setItem(0, availableRanks.get(0));
					inv.setItem(2, availableRanks.get(1));
					inv.setItem(4, availableRanks.get(2));
					inv.setItem(6, availableRanks.get(3));
					inv.setItem(8, availableRanks.get(4));
					inv.setItem(9, availableRanks.get(5));
					inv.setItem(10, availableRanks.get(6));
					inv.setItem(11, availableRanks.get(7));
					inv.setItem(12, availableRanks.get(8));
					inv.setItem(14, availableRanks.get(9));
					inv.setItem(15, availableRanks.get(10));
					inv.setItem(16, availableRanks.get(11));
					inv.setItem(17, availableRanks.get(12));
					inv.setItem(18, availableRanks.get(13));
					inv.setItem(20, availableRanks.get(14));
					inv.setItem(22, availableRanks.get(15));
					inv.setItem(24, availableRanks.get(16));
					inv.setItem(26, availableRanks.get(17));
					break;
				default:
					inv.setItem(13, new ItemBuilder(Material.valueOf(newVersion ? "RED_STAINED_GLASS" : "GLASS"), 1, newVersion ? 0 : 14).setDisplayName(guiYamlFile.getConfigString(player, "RankedNickGUI.Step1.NoRankAvailable.DisplayName")).build());
					break;
			}
			
			player.openInventory(inv);
		} else if(args.length == 1) {
			if(setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.Skin")) {
				Inventory inv = Bukkit.createInventory(null, 27, guiYamlFile.getConfigString(player, "RankedNickGUI.Step2.InventoryTitle"));
				
				for (int i = 0; i < inv.getSize(); i++)
					inv.setItem(i, new ItemBuilder(Material.getMaterial(newVersion ? "BLACK_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE"), 1, newVersion ? 0 : 15).setDisplayName("§r").build());
				
				inv.setItem(10, new ItemBuilder(1).setDisplayName(guiYamlFile.getConfigString(player, "RankedNickGUI.Step2.Default.DisplayName")).setSkullOwner(player.getName()).build());
				inv.setItem(12, new ItemBuilder(1).setDisplayName(guiYamlFile.getConfigString(player, "RankedNickGUI.Step2.Normal.DisplayName")).build());
				inv.setItem(14, new ItemBuilder(1).setDisplayName(guiYamlFile.getConfigString(player, "RankedNickGUI.Step2.Random.DisplayName")).setSkullOwner("MHF_Question").build());
				inv.setItem(16, new ItemBuilder(1).setDisplayName(guiYamlFile.getConfigString(player, "RankedNickGUI.Step2.SkinFromName.DisplayName")).setSkullOwner("Steve").build());
				
				player.openInventory(inv);
			} else
				openRankedNickGUI(player, text + " DEFAULT");
		} else if(args.length == 2) {
			if(player.hasPermission("eazynick.nick.custom")) {
				Inventory inv = Bukkit.createInventory(null, 27, guiYamlFile.getConfigString(player, "RankedNickGUI.Step3.InventoryTitle"));
				
				for (int i = 0; i < inv.getSize(); i++)
					inv.setItem(i, new ItemBuilder(Material.getMaterial(newVersion ? "BLACK_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE"), 1, newVersion ? 0 : 15).setDisplayName("§r").build());
				
				inv.setItem(12, new ItemBuilder(Material.valueOf((newVersion && !(eazyNick.getVersion().startsWith("1_13"))) ? "OAK_SIGN" : "SIGN")).setDisplayName(guiYamlFile.getConfigString(player, "RankedNickGUI.Step3.Custom.DisplayName")).build());
				inv.setItem(14, new ItemBuilder(1).setDisplayName(guiYamlFile.getConfigString(player, "RankedNickGUI.Step3.Random.DisplayName")).setSkullOwner("MHF_Question").build());
				
				player.openInventory(inv);
			} else
				openRankedNickGUI(player, text + " RANDOM");
		} else {
			Inventory inv = Bukkit.createInventory(null, 27, guiYamlFile.getConfigString(player, "RankedNickGUI.Step4.InventoryTitle").replace("%nickName%", args[2]).replace("%nickname%", args[2]));
			
			for (int i = 0; i < inv.getSize(); i++)
				inv.setItem(i, new ItemBuilder(Material.getMaterial(newVersion ? "BLACK_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE"), 1, newVersion ? 0 : 15).setDisplayName("§r").build());
			
			inv.setItem(11, new ItemBuilder(Material.valueOf(newVersion ? "LIME_WOOL" : "WOOL"), 1, newVersion ? 0 : 5).setDisplayName(guiYamlFile.getConfigString(player, "RankedNickGUI.Step4.Use.DisplayName")).build());
			inv.setItem(13, new ItemBuilder(1).setDisplayName(guiYamlFile.getConfigString(player, "RankedNickGUI.Step4.Retry.DisplayName")).build());
			inv.setItem(15, new ItemBuilder(Material.valueOf((newVersion && !(eazyNick.getVersion().startsWith("1_13"))) ? "OAK_SIGN" : "SIGN")).setDisplayName(guiYamlFile.getConfigString(player, "RankedNickGUI.Step4.Custom.DisplayName")).build());
			
			player.openInventory(inv);
		}
	}
	
}
