package net.dev.eazynick.utilities.configuration.yaml;

import net.dev.eazynick.EazyNick;

public class GUIYamlFile extends YamlFile {

	public GUIYamlFile(EazyNick eazyNick, String fileName) {
		super(eazyNick, "", fileName);
	}

	@Override
	public void setDefaults() {
		String arrow = "&0\u27A4";
		boolean newMaterials = eazyNick.getUtils().isNewVersion();
		
		//Default NickGUI
		configuration.addDefault("NickGUI.InventoryTitle", "&5Nick&8:");
		configuration.addDefault("NickGUI.Nick.DisplayName", "&aNick");
		configuration.addDefault("NickGUI.Unnick.DisplayName", "&cUnnick");
		
		//NickList
		configuration.addDefault("NickNameGUI.InventoryTitle", "&5Nicknames &7[&aPage %currentPage%&7]&8:");
		configuration.addDefault("NickNameGUI.Previous.DisplayName", "&7Previous page");
		configuration.addDefault("NickNameGUI.Next.DisplayName", "&7Next page");
		configuration.addDefault("NickNameGUI.NickName.DisplayName", "&b%nickName%");
		
		//BookGUI
		configuration.addDefault("BookGUI.Page1.Title", "Info");
		configuration.addDefault("BookGUI.Page1.Text", "&0Nicknames allow you to play with a different%nl%username to not get recognized.%nl%%nl%All rules still apply. You can still be reported and all name history is stored.%nl%%nl%");
		configuration.addDefault("BookGUI.Page1.Enabled", true);
		configuration.addDefault("BookGUI.Page2.Title", "Ranks");
		configuration.addDefault("BookGUI.Page2.Text", "&0Let's get you set up%nl%with your nickname!%nl%First, you'll need to%nl%choose which &lRANK%nl%&0you would like to be%nl%shown as when nicked.%nl%%nl%");
		configuration.addDefault("BookGUI.Page3.Title", "Skin");
		configuration.addDefault("BookGUI.Page3.Text","&0Awesome! Now, which &lSKIN &0would you like to have while nicked?%nl%%nl%");
		configuration.addDefault("BookGUI.Page4.Title", "Name");
		configuration.addDefault("BookGUI.Page4.Text", "&0Alright, now you'll need to choose the &0&lNAME &0to use!%nl%%nl%");
		configuration.addDefault("BookGUI.Page5.Title", "RandomNick");
		configuration.addDefault("BookGUI.Page5.Text", "&0We've generated a random username for you:%nl%&l%name%%nl%");
		configuration.addDefault("BookGUI.Page5.Text2", "&0To go back to being your usual self, type:%nl%&l/nick reset");
		configuration.addDefault("BookGUI.Page6.Title", "Done");
		configuration.addDefault("BookGUI.Page6.Text.SingleServer", "&0You have finished setting up your nickname!%nl%%nl%You are now nicked as%nl%%name%&0.%nl%%nl%To go back to being your usual self, type:%nl%&l/nick reset");
		configuration.addDefault("BookGUI.Page6.Text.BungeeCord", "&0You have finished setting up your nickname!%nl%%nl%When you go into a game, you will be nicked as%nl%%name%&0.%nl%%nl%To go back to being your usual self, type:%nl%&l/nick reset");
		configuration.addDefault("BookGUI.Page6.Enabled", true);
		
		configuration.addDefault("BookGUI.Accept.Text", arrow + " &nI understand, set&r%nl%&0&nup my nickname&r");
		configuration.addDefault("BookGUI.Accept.Hover", "&fClick here to proceed");
		configuration.addDefault("BookGUI.Rank.Text", arrow + " %rank%%nl%");
		configuration.addDefault("BookGUI.Rank.Hover", "&fClick here to be shown as %rank%");
		configuration.addDefault("BookGUI.NormalSkin.Text", arrow + " &0My normal skin%nl%");
		configuration.addDefault("BookGUI.NormalSkin.Hover", "&fClick here to use your normal skin");
		configuration.addDefault("BookGUI.SteveAlexSkin.Text", arrow + " &0Steve/Alex skin%nl%");
		configuration.addDefault("BookGUI.SteveAlexSkin.Hover", "&fClick here to use a Steve/Alex skin");
		configuration.addDefault("BookGUI.RandomSkin.Text", arrow + " &0Random skin%nl%");
		configuration.addDefault("BookGUI.RandomSkin.Hover", "&fClick here to use a random skin");
		configuration.addDefault("BookGUI.SkinFromName.Text", arrow + " &0Use skin of nickname%nl%");
		configuration.addDefault("BookGUI.SkinFromName.Hover", "&fClick here to use the skin of your nickname");
		configuration.addDefault("BookGUI.ReuseSkin.Text", arrow + " &0Reuse %skin%%nl%");
		configuration.addDefault("BookGUI.ReuseSkin.Hover", "&fClick here to reuse your previous skin");
		configuration.addDefault("BookGUI.EnterName.Text", arrow + " &0Enter a name%nl%");
		configuration.addDefault("BookGUI.EnterName.Hover", "&fClick here to enter a name");
		configuration.addDefault("BookGUI.RandomName.Text", arrow + " &0Use random name%nl%");
		configuration.addDefault("BookGUI.RandomName.Hover", "&fClick here to use a random name");
		configuration.addDefault("BookGUI.ReuseName.Text", arrow + " &0Reuse '%name%'%nl%");
		configuration.addDefault("BookGUI.ReuseName.Hover", "&fClick here to reuse '%name%'");
		configuration.addDefault("BookGUI.OptionUseName.Text", "     &a&nUSE NAME&r%nl%");
		configuration.addDefault("BookGUI.OptionUseName.Hover", "&fClick here to use this name.");
		configuration.addDefault("BookGUI.OptionTryAgain.Text", "     &c&nTRY AGAIN&r%nl%%nl%");
		configuration.addDefault("BookGUI.OptionTryAgain.Hover", "&fClick here to generate another name.");
		configuration.addDefault("BookGUI.OptionEnterName.Text", "&0&nOr enter a name to&r%nl%&0&nuse.&r%nl%%nl%");
		configuration.addDefault("BookGUI.OptionEnterName.Hover", "&fClick here to enter a name");
		
		configuration.addDefault("SignGUI.Line1", "");
		configuration.addDefault("SignGUI.Line2", "^^^^^^^^^^^^^^^");
		configuration.addDefault("SignGUI.Line3", "Enter your");
		configuration.addDefault("SignGUI.Line4", "username here");
		
		configuration.addDefault("AnvilGUI.Title", "Enter name here");
		
		//RankedNickGUI
		//Step 1
		configuration.addDefault("RankedNickGUI.Step1.InventoryTitle", "&5Choose a rank&8:");
		configuration.addDefault("RankedNickGUI.Step1.NoRankAvailable.DisplayName", "&cYou do not have permission to nick yourself with a specific rank");
		configuration.addDefault("RankedNickGUI.Step1.Rank1.ItemType", newMaterials ? "GRAY_WOOL" : "WOOL");
		configuration.addDefault("RankedNickGUI.Step1.Rank1.MetaData", newMaterials ? 0 : 7);
		configuration.addDefault("RankedNickGUI.Step1.Rank2.ItemType", newMaterials ? "LIME_WOOL" : "WOOL");
		configuration.addDefault("RankedNickGUI.Step1.Rank2.MetaData", newMaterials ? 0 : 5);
		configuration.addDefault("RankedNickGUI.Step1.Rank3.ItemType", newMaterials ? "LIME_WOOL" : "WOOL");
		configuration.addDefault("RankedNickGUI.Step1.Rank3.MetaData", newMaterials ? 0 : 5);
		configuration.addDefault("RankedNickGUI.Step1.Rank4.ItemType", newMaterials ? "LIGHT_BLUE_WOOL" : "WOOL");
		configuration.addDefault("RankedNickGUI.Step1.Rank4.MetaData", newMaterials ? 0 : 3);
		configuration.addDefault("RankedNickGUI.Step1.Rank5.ItemType", newMaterials ? "LIGHT_BLUE_WOOL" : "WOOL");
		configuration.addDefault("RankedNickGUI.Step1.Rank5.MetaData", newMaterials ? 0 : 3);
		configuration.addDefault("RankedNickGUI.Step1.Rank6.ItemType", newMaterials ? "ORANGE_WOOL" : "WOOL");
		configuration.addDefault("RankedNickGUI.Step1.Rank6.MetaData", newMaterials ? 0 : 1);
		
		for (int i = 7; i <= 18; i++) {
			configuration.addDefault("RankedNickGUI.Step1.Rank" + i + ".ItemType", newMaterials ? "LIGHT_GRAY_WOOL" : "WOOL");
			configuration.addDefault("RankedNickGUI.Step1.Rank" + i + ".MetaData", newMaterials ? 0 : 8);
		}
		
		//Step 2
		configuration.addDefault("RankedNickGUI.Step2.InventoryTitle", "&5Choose a skin&8:");
		configuration.addDefault("RankedNickGUI.Step2.Default.DisplayName", "&bMy normal skin");
		configuration.addDefault("RankedNickGUI.Step2.Normal.DisplayName", "&bSteve/Alex skin");
		configuration.addDefault("RankedNickGUI.Step2.Random.DisplayName", "&bRandom skin");
		
		//Step 3
		configuration.addDefault("RankedNickGUI.Step3.InventoryTitle", "&5Choose a name&8:");
		configuration.addDefault("RankedNickGUI.Step3.Custom.DisplayName", "&bEnter a name");
		configuration.addDefault("RankedNickGUI.Step3.Random.DisplayName", "&bRandom name");
		
		//Step 4
		configuration.addDefault("RankedNickGUI.Step4.InventoryTitle", "&5%nickName%&8:");
		configuration.addDefault("RankedNickGUI.Step4.Use.DisplayName", "&bUse name");
		configuration.addDefault("RankedNickGUI.Step4.Retry.DisplayName", "&bTry again");
		configuration.addDefault("RankedNickGUI.Step4.Custom.DisplayName", "&bEnter a name");
		
		//Ranks
		configuration.addDefault("RankGUI.Rank1.Enabled", true);
		configuration.addDefault("RankGUI.Rank1.Rank", "&8DEFAULT");
		configuration.addDefault("RankGUI.Rank1.RankName", "DEFAULT");
		configuration.addDefault("RankGUI.Rank1.Permission", "NONE");
		configuration.addDefault("RankGUI.Rank2.Enabled", true);
		configuration.addDefault("RankGUI.Rank2.Rank", "&aVIP");
		configuration.addDefault("RankGUI.Rank2.RankName", "VIP");
		configuration.addDefault("RankGUI.Rank2.Permission", "nick.rank.vip");
		configuration.addDefault("RankGUI.Rank3.Enabled", true);
		configuration.addDefault("RankGUI.Rank3.Rank", "&aVIP&6+");
		configuration.addDefault("RankGUI.Rank3.RankName", "VIPPLUS");
		configuration.addDefault("RankGUI.Rank3.Permission", "nick.rank.vipplus");
		configuration.addDefault("RankGUI.Rank4.Enabled", true);
		configuration.addDefault("RankGUI.Rank4.Rank", "&bMVP");
		configuration.addDefault("RankGUI.Rank4.RankName", "MVP");
		configuration.addDefault("RankGUI.Rank4.Permission", "nick.rank.mvp");
		configuration.addDefault("RankGUI.Rank5.Enabled", true);
		configuration.addDefault("RankGUI.Rank5.Rank", "&bMVP&c+");
		configuration.addDefault("RankGUI.Rank5.RankName", "MVPPLUS");
		configuration.addDefault("RankGUI.Rank5.Permission", "nick.rank.mvpplus");
		configuration.addDefault("RankGUI.Rank6.Enabled", true);
		configuration.addDefault("RankGUI.Rank6.Rank", "&6MVP&c++");
		configuration.addDefault("RankGUI.Rank6.RankName", "MVPPLUSPLUS");
		configuration.addDefault("RankGUI.Rank6.Permission", "nick.rank.mvpplusplus");
		
		for (int i = 7; i <= 18; i++) {
			configuration.addDefault("RankGUI.Rank" + i + ".Enabled", false);
			configuration.addDefault("RankGUI.Rank" + i + ".Rank", "&dRank" + i);
			configuration.addDefault("RankGUI.Rank" + i + ".RankName", "RANK" + i);
			configuration.addDefault("RankGUI.Rank" + i + ".Permission", "nick.rank." + i);
		}

		//Rank-Formats
		configuration.addDefault("Settings.NickFormat.Rank1.ChatPrefix", "&7");
		configuration.addDefault("Settings.NickFormat.Rank1.ChatSuffix", "&7");
		configuration.addDefault("Settings.NickFormat.Rank1.TabPrefix", "&7");
		configuration.addDefault("Settings.NickFormat.Rank1.TabSuffix", "&7");
		configuration.addDefault("Settings.NickFormat.Rank1.TagPrefix", "&7");
		configuration.addDefault("Settings.NickFormat.Rank1.TagSuffix", "&7");
		configuration.addDefault("Settings.NickFormat.Rank1.SortID", 9997);
		configuration.addDefault("Settings.NickFormat.Rank1.GroupName", "Default");
		
		configuration.addDefault("Settings.NickFormat.Rank2.ChatPrefix", "&a[VIP] ");
		configuration.addDefault("Settings.NickFormat.Rank2.ChatSuffix", "&r");
		configuration.addDefault("Settings.NickFormat.Rank2.TabPrefix", "&a[VIP] ");
		configuration.addDefault("Settings.NickFormat.Rank2.TabSuffix", "&r");
		configuration.addDefault("Settings.NickFormat.Rank2.TagPrefix", "&a[VIP] ");
		configuration.addDefault("Settings.NickFormat.Rank2.TagSuffix", "&r");
		configuration.addDefault("Settings.NickFormat.Rank2.SortID", 9996);
		configuration.addDefault("Settings.NickFormat.Rank2.GroupName", "VIP");
		
		configuration.addDefault("Settings.NickFormat.Rank3.ChatPrefix", "&a[VIP&6+&a] ");
		configuration.addDefault("Settings.NickFormat.Rank3.ChatSuffix", "&r");
		configuration.addDefault("Settings.NickFormat.Rank3.TabPrefix", "&a[VIP&6+&a] ");
		configuration.addDefault("Settings.NickFormat.Rank3.TabSuffix", "&r");
		configuration.addDefault("Settings.NickFormat.Rank3.TagPrefix", "&a[VIP&6+&a] ");
		configuration.addDefault("Settings.NickFormat.Rank3.TagSuffix", "&r");
		configuration.addDefault("Settings.NickFormat.Rank3.SortID", 9995);
		configuration.addDefault("Settings.NickFormat.Rank3.GroupName", "VIPPlus");
		
		configuration.addDefault("Settings.NickFormat.Rank4.ChatPrefix", "&b[MVP] ");
		configuration.addDefault("Settings.NickFormat.Rank4.ChatSuffix", "&r");
		configuration.addDefault("Settings.NickFormat.Rank4.TabPrefix", "&b[MVP] ");
		configuration.addDefault("Settings.NickFormat.Rank4.TabSuffix", "&r");
		configuration.addDefault("Settings.NickFormat.Rank4.TagPrefix", "&b[MVP] ");
		configuration.addDefault("Settings.NickFormat.Rank4.TagSuffix", "&r");
		configuration.addDefault("Settings.NickFormat.Rank4.SortID", 9994);
		configuration.addDefault("Settings.NickFormat.Rank4.GroupName", "MVP");
		
		configuration.addDefault("Settings.NickFormat.Rank5.ChatPrefix", "&b[MVP%randomColor%+&b] ");
		configuration.addDefault("Settings.NickFormat.Rank5.ChatSuffix", "&r");
		configuration.addDefault("Settings.NickFormat.Rank5.TabPrefix", "&b[MVP%randomColor%+&b] ");
		configuration.addDefault("Settings.NickFormat.Rank5.TabSuffix", "&r");
		configuration.addDefault("Settings.NickFormat.Rank5.TagPrefix", "&b[MVP%randomColor%+&b] ");
		configuration.addDefault("Settings.NickFormat.Rank5.TagSuffix", "&r");
		configuration.addDefault("Settings.NickFormat.Rank5.SortID", 9993);
		configuration.addDefault("Settings.NickFormat.Rank5.GroupName", "MVPPlus");
		
		configuration.addDefault("Settings.NickFormat.Rank6.ChatPrefix", "&6[MVP%randomColor%++&6] ");
		configuration.addDefault("Settings.NickFormat.Rank6.ChatSuffix", "&r");
		configuration.addDefault("Settings.NickFormat.Rank6.TabPrefix", "&6[MVP%randomColor%++&6] ");
		configuration.addDefault("Settings.NickFormat.Rank6.TabSuffix", "&r");
		configuration.addDefault("Settings.NickFormat.Rank6.TagPrefix", "&6[MVP%randomColor%++&6] ");
		configuration.addDefault("Settings.NickFormat.Rank6.TagSuffix", "&r");
		configuration.addDefault("Settings.NickFormat.Rank6.SortID", 9992);
		configuration.addDefault("Settings.NickFormat.Rank6.GroupName", "MVPPlusPlus");
		
		for (int i = 7; i <= 18; i++) {
			String prefix = "&d[Rank" + i + "] ", suffix = "&r";
			
			configuration.addDefault("Settings.NickFormat.Rank" + i + ".ChatPrefix", prefix);
			configuration.addDefault("Settings.NickFormat.Rank" + i + ".ChatSuffix", suffix);
			configuration.addDefault("Settings.NickFormat.Rank" + i + ".TabPrefix", prefix);
			configuration.addDefault("Settings.NickFormat.Rank" + i + ".TabSuffix", suffix);
			configuration.addDefault("Settings.NickFormat.Rank" + i + ".TagPrefix", prefix);
			configuration.addDefault("Settings.NickFormat.Rank" + i + ".TagSuffix", suffix);
			configuration.addDefault("Settings.NickFormat.Rank" + i + ".SortID", 9997 - i);
			configuration.addDefault("Settings.NickFormat.Rank" + i + ".GroupName", "Rank" + i);
		}
	}
	
}
