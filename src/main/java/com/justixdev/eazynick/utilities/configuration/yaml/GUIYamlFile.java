package com.justixdev.eazynick.utilities.configuration.yaml;

import com.justixdev.eazynick.EazyNick;

public class GUIYamlFile extends YamlFile {

    public GUIYamlFile(EazyNick eazyNick) {
        super(eazyNick, "", "guis");
    }

    @Override
    public void setDefaults() {
        super.setDefaults();

        String arrow = "&0➤";
        boolean newMaterials = this.eazyNick.getUtils().isVersion13OrLater();

        // Default NickGUI
        this.configuration.addDefault("NickGUI.InventoryTitle", "&5Nick&8:");
        this.configuration.addDefault("NickGUI.Nick.DisplayName", "&aNick");
        this.configuration.addDefault("NickGUI.Unnick.DisplayName", "&cUnnick");

        // NickList
        this.configuration.addDefault("NickNameGUI.InventoryTitle", "&5Nicknames &7[&aPage %currentPage%&7]&8:");
        this.configuration.addDefault("NickNameGUI.Previous.DisplayName", "&7Previous page");
        this.configuration.addDefault("NickNameGUI.Next.DisplayName", "&7Next page");
        this.configuration.addDefault("NickNameGUI.NickName.DisplayName", "&b%nickName%");

        // BookGUI
        this.configuration.addDefault("BookGUI.Page1.Title", "Info");
        this.configuration.addDefault("BookGUI.Page1.Text", "&0Nicknames allow you to play with a different username to not get recognized.%nl%%nl%All rules still apply.%nl%You can still be reported and all name history is stored.%nl%%nl%");
        this.configuration.addDefault("BookGUI.Page1.Enabled", true);
        this.configuration.addDefault("BookGUI.Page2.Title", "Ranks");
        this.configuration.addDefault("BookGUI.Page2.Text", "&0Let's get you set up%nl%with your nickname!%nl%First, you'll need to%nl%choose which &lRANK%nl%&0you would like to be%nl%shown as when nicked.%nl%%nl%");
        this.configuration.addDefault("BookGUI.Page3.Title", "Skin");
        this.configuration.addDefault("BookGUI.Page3.Text","&0Awesome! Now, which &lSKIN &0would you like to have while nicked?%nl%%nl%");
        this.configuration.addDefault("BookGUI.Page4.Title", "Name");
        this.configuration.addDefault("BookGUI.Page4.Text", "&0Alright, now you'll need to choose the &0&lNAME &0to use!%nl%%nl%");
        this.configuration.addDefault("BookGUI.Page5.Title", "RandomNick");
        this.configuration.addDefault("BookGUI.Page5.Text", "&0We've generated a random username for you:%nl%&l%name%%nl%");
        this.configuration.addDefault("BookGUI.Page6.Title", "Done");
        this.configuration.addDefault("BookGUI.Page6.Text.SingleServer", "&0You have finished setting up your nickname!%nl%%nl%You are now nicked as %name%&0.%nl%%nl%To go back to being your usual self, type:%nl%&l/nick reset");
        this.configuration.addDefault("BookGUI.Page6.Text.BungeeCord", "&0You have finished setting up your nickname!%nl%%nl%When you go into a game, you will be nicked as %name%&0.You will not be nicked in lobbies.%nl%%nl%To go back to being your usual self, type:%nl%&l/nick reset");
        this.configuration.addDefault("BookGUI.Page6.Enabled", true);

        this.configuration.addDefault("BookGUI.Accept.Text", arrow + " &nI understand, set up my nickname&r");
        this.configuration.addDefault("BookGUI.Accept.Hover", "&fClick here to proceed");
        this.configuration.addDefault("BookGUI.Rank.Text", arrow + " %rank%%nl%");
        this.configuration.addDefault("BookGUI.Rank.Hover", "&fClick here to be shown as %rank%");
        this.configuration.addDefault("BookGUI.NormalSkin.Text", arrow + " &0My normal skin%nl%");
        this.configuration.addDefault("BookGUI.NormalSkin.Hover", "&fClick here to use your normal skin");
        this.configuration.addDefault("BookGUI.SteveAlexSkin.Text", arrow + " &0Steve/Alex skin%nl%");
        this.configuration.addDefault("BookGUI.SteveAlexSkin.Hover", "&fClick here to use a Steve/Alex skin");
        this.configuration.addDefault("BookGUI.RandomSkin.Text", arrow + " &0Random skin%nl%");
        this.configuration.addDefault("BookGUI.RandomSkin.Hover", "&fClick here to use a random skin");
        this.configuration.addDefault("BookGUI.SkinFromName.Text", arrow + " &0Use skin of nickname%nl%");
        this.configuration.addDefault("BookGUI.SkinFromName.Hover", "&fClick here to use the skin of your nickname");
        this.configuration.addDefault("BookGUI.ReuseSkin.Text", arrow + " &0Reuse '%skin%'%nl%");
        this.configuration.addDefault("BookGUI.ReuseSkin.Hover", "&fClick here to reuse your previous skin");
        this.configuration.addDefault("BookGUI.EnterName.Text", arrow + " &0Enter a name%nl%");
        this.configuration.addDefault("BookGUI.EnterName.Hover", "&fClick here to enter a name");
        this.configuration.addDefault("BookGUI.RandomName.Text", arrow + " &0Use random name%nl%");
        this.configuration.addDefault("BookGUI.RandomName.Hover", "&fClick here to use a random name");
        this.configuration.addDefault("BookGUI.ReuseName.Text", arrow + " &0Reuse '%name%'%nl%");
        this.configuration.addDefault("BookGUI.ReuseName.Hover", "&fClick here to reuse '%name%'");
        this.configuration.addDefault("BookGUI.OptionUseName.Text", "     &a&nUSE NAME&r%nl%");
        this.configuration.addDefault("BookGUI.OptionUseName.Hover", "&fClick here to use this name.");
        this.configuration.addDefault("BookGUI.OptionTryAgain.Text", "     &c&nTRY AGAIN&r%nl%%nl%");
        this.configuration.addDefault("BookGUI.OptionTryAgain.Hover", "&fClick here to generate another name.");
        this.configuration.addDefault("BookGUI.OptionEnterName.Text", "&0&nOr enter a name to&r%nl%&0&nuse.");
        this.configuration.addDefault("BookGUI.OptionEnterName.Hover", "&fClick here to enter a name");

        this.configuration.addDefault("SignGUI.Line1", "");
        this.configuration.addDefault("SignGUI.Line2", "^^^^^^^^^^^^^^^");
        this.configuration.addDefault("SignGUI.Line3", "Enter your");
        this.configuration.addDefault("SignGUI.Line4", "username here");

        this.configuration.addDefault("AnvilGUI.Title", "Enter name here");

        // RankedNickGUI
        // Step 1
        this.configuration.addDefault("RankedNickGUI.Step1.InventoryTitle", "&5Choose a rank&8:");
        this.configuration.addDefault("RankedNickGUI.Step1.NoRankAvailable.DisplayName", "&cYou do not have permission to nick yourself with a specific rank");
        this.configuration.addDefault("RankedNickGUI.Step1.Rank1.ItemType", newMaterials ? "GRAY_WOOL" : "WOOL");
        this.configuration.addDefault("RankedNickGUI.Step1.Rank1.MetaData", newMaterials ? 0 : 7);
        this.configuration.addDefault("RankedNickGUI.Step1.Rank2.ItemType", newMaterials ? "LIME_WOOL" : "WOOL");
        this.configuration.addDefault("RankedNickGUI.Step1.Rank2.MetaData", newMaterials ? 0 : 5);
        this.configuration.addDefault("RankedNickGUI.Step1.Rank3.ItemType", newMaterials ? "LIME_WOOL" : "WOOL");
        this.configuration.addDefault("RankedNickGUI.Step1.Rank3.MetaData", newMaterials ? 0 : 5);
        this.configuration.addDefault("RankedNickGUI.Step1.Rank4.ItemType", newMaterials ? "LIGHT_BLUE_WOOL" : "WOOL");
        this.configuration.addDefault("RankedNickGUI.Step1.Rank4.MetaData", newMaterials ? 0 : 3);
        this.configuration.addDefault("RankedNickGUI.Step1.Rank5.ItemType", newMaterials ? "LIGHT_BLUE_WOOL" : "WOOL");
        this.configuration.addDefault("RankedNickGUI.Step1.Rank5.MetaData", newMaterials ? 0 : 3);
        this.configuration.addDefault("RankedNickGUI.Step1.Rank6.ItemType", newMaterials ? "ORANGE_WOOL" : "WOOL");
        this.configuration.addDefault("RankedNickGUI.Step1.Rank6.MetaData", newMaterials ? 0 : 1);

        for (int i = 7; i <= 18; i++) {
            this.configuration.addDefault("RankedNickGUI.Step1.Rank" + i + ".ItemType", newMaterials ? "LIGHT_GRAY_WOOL" : "WOOL");
            this.configuration.addDefault("RankedNickGUI.Step1.Rank" + i + ".MetaData", newMaterials ? 0 : 8);
        }

        // Step 2
        this.configuration.addDefault("RankedNickGUI.Step2.InventoryTitle", "&5Choose a skin&8:");
        this.configuration.addDefault("RankedNickGUI.Step2.Default.DisplayName", "&bMy normal skin");
        this.configuration.addDefault("RankedNickGUI.Step2.Normal.DisplayName", "&bSteve/Alex skin");
        this.configuration.addDefault("RankedNickGUI.Step2.Random.DisplayName", "&bRandom skin");
        this.configuration.addDefault("RankedNickGUI.Step2.SkinFromName.DisplayName", "&bUse skin of nickname");

        // Step 3
        this.configuration.addDefault("RankedNickGUI.Step3.InventoryTitle", "&5Choose a name&8:");
        this.configuration.addDefault("RankedNickGUI.Step3.Custom.DisplayName", "&bEnter a name");
        this.configuration.addDefault("RankedNickGUI.Step3.Random.DisplayName", "&bRandom name");

        // Step 4
        this.configuration.addDefault("RankedNickGUI.Step4.InventoryTitle", "&5%nickName%&8:");
        this.configuration.addDefault("RankedNickGUI.Step4.Use.DisplayName", "&bUse name");
        this.configuration.addDefault("RankedNickGUI.Step4.Retry.DisplayName", "&bTry again");
        this.configuration.addDefault("RankedNickGUI.Step4.Custom.DisplayName", "&bEnter a name");

        // Ranks
        this.configuration.addDefault("RankGUI.Rank1.Enabled", true);
        this.configuration.addDefault("RankGUI.Rank1.Rank", "&8DEFAULT");
        this.configuration.addDefault("RankGUI.Rank1.RankName", "DEFAULT");
        this.configuration.addDefault("RankGUI.Rank1.Permission", "NONE");
        this.configuration.addDefault("RankGUI.Rank2.Enabled", true);
        this.configuration.addDefault("RankGUI.Rank2.Rank", "&aVIP");
        this.configuration.addDefault("RankGUI.Rank2.RankName", "VIP");
        this.configuration.addDefault("RankGUI.Rank2.Permission", "nick.rank.vip");
        this.configuration.addDefault("RankGUI.Rank3.Enabled", true);
        this.configuration.addDefault("RankGUI.Rank3.Rank", "&aVIP&6+");
        this.configuration.addDefault("RankGUI.Rank3.RankName", "VIPPLUS");
        this.configuration.addDefault("RankGUI.Rank3.Permission", "nick.rank.vipplus");
        this.configuration.addDefault("RankGUI.Rank4.Enabled", true);
        this.configuration.addDefault("RankGUI.Rank4.Rank", "&bMVP");
        this.configuration.addDefault("RankGUI.Rank4.RankName", "MVP");
        this.configuration.addDefault("RankGUI.Rank4.Permission", "nick.rank.mvp");
        this.configuration.addDefault("RankGUI.Rank5.Enabled", true);
        this.configuration.addDefault("RankGUI.Rank5.Rank", "&bMVP&c+");
        this.configuration.addDefault("RankGUI.Rank5.RankName", "MVPPLUS");
        this.configuration.addDefault("RankGUI.Rank5.Permission", "nick.rank.mvpplus");
        this.configuration.addDefault("RankGUI.Rank6.Enabled", true);
        this.configuration.addDefault("RankGUI.Rank6.Rank", "&6MVP&c++");
        this.configuration.addDefault("RankGUI.Rank6.RankName", "MVPPLUSPLUS");
        this.configuration.addDefault("RankGUI.Rank6.Permission", "nick.rank.mvpplusplus");

        for (int i = 7; i <= 18; i++) {
            this.configuration.addDefault("RankGUI.Rank" + i + ".Enabled", false);
            this.configuration.addDefault("RankGUI.Rank" + i + ".Rank", "&dRank" + i);
            this.configuration.addDefault("RankGUI.Rank" + i + ".RankName", "RANK" + i);
            this.configuration.addDefault("RankGUI.Rank" + i + ".Permission", "nick.rank." + i);
        }

        // Rank-Formats
        this.configuration.addDefault("Settings.NickFormat.Rank1.Chat.Prefix", "&7");
        this.configuration.addDefault("Settings.NickFormat.Rank1.Chat.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.Rank1.PlayerList.Prefix", "&7");
        this.configuration.addDefault("Settings.NickFormat.Rank1.PlayerList.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.Rank1.NameTag.Prefix", "&7");
        this.configuration.addDefault("Settings.NickFormat.Rank1.NameTag.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.Rank1.SortID", 9997);
        this.configuration.addDefault("Settings.NickFormat.Rank1.GroupName", "Default");

        this.configuration.addDefault("Settings.NickFormat.Rank2.Chat.Prefix", "&a[VIP] ");
        this.configuration.addDefault("Settings.NickFormat.Rank2.Chat.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.Rank2.PlayerList.Prefix", "&a[VIP] ");
        this.configuration.addDefault("Settings.NickFormat.Rank2.PlayerList.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.Rank2.NameTag.Prefix", "&a[VIP] ");
        this.configuration.addDefault("Settings.NickFormat.Rank2.NameTag.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.Rank2.SortID", 9996);
        this.configuration.addDefault("Settings.NickFormat.Rank2.GroupName", "VIP");

        this.configuration.addDefault("Settings.NickFormat.Rank3.Chat.Prefix", "&a[VIP&6+&a] ");
        this.configuration.addDefault("Settings.NickFormat.Rank3.Chat.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.Rank3.PlayerList.Prefix", "&a[VIP&6+&a] ");
        this.configuration.addDefault("Settings.NickFormat.Rank3.PlayerList.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.Rank3.NameTag.Prefix", "&a[VIP&6+&a] ");
        this.configuration.addDefault("Settings.NickFormat.Rank3.NameTag.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.Rank3.SortID", 9995);
        this.configuration.addDefault("Settings.NickFormat.Rank3.GroupName", "VIPPlus");

        this.configuration.addDefault("Settings.NickFormat.Rank4.Chat.Prefix", "&b[MVP] ");
        this.configuration.addDefault("Settings.NickFormat.Rank4.Chat.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.Rank4.PlayerList.Prefix", "&b[MVP] ");
        this.configuration.addDefault("Settings.NickFormat.Rank4.PlayerList.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.Rank4.NameTag.Prefix", "&b[MVP] ");
        this.configuration.addDefault("Settings.NickFormat.Rank4.NameTag.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.Rank4.SortID", 9994);
        this.configuration.addDefault("Settings.NickFormat.Rank4.GroupName", "MVP");

        this.configuration.addDefault("Settings.NickFormat.Rank5.Chat.Prefix", "&b[MVP%randomColor%+&b] ");
        this.configuration.addDefault("Settings.NickFormat.Rank5.Chat.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.Rank5.PlayerList.Prefix", "&b[MVP%randomColor%+&b] ");
        this.configuration.addDefault("Settings.NickFormat.Rank5.PlayerList.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.Rank5.NameTag.Prefix", "&b[MVP%randomColor%+&b] ");
        this.configuration.addDefault("Settings.NickFormat.Rank5.NameTag.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.Rank5.SortID", 9993);
        this.configuration.addDefault("Settings.NickFormat.Rank5.GroupName", "MVPPlus");

        this.configuration.addDefault("Settings.NickFormat.Rank6.Chat.Prefix", "&6[MVP%randomColor%++&6] ");
        this.configuration.addDefault("Settings.NickFormat.Rank6.Chat.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.Rank6.PlayerList.Prefix", "&6[MVP%randomColor%++&6] ");
        this.configuration.addDefault("Settings.NickFormat.Rank6.PlayerList.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.Rank6.NameTag.Prefix", "&6[MVP%randomColor%++&6] ");
        this.configuration.addDefault("Settings.NickFormat.Rank6.NameTag.Suffix", "&r");
        this.configuration.addDefault("Settings.NickFormat.Rank6.SortID", 9992);
        this.configuration.addDefault("Settings.NickFormat.Rank6.GroupName", "MVPPlusPlus");

        for (int i = 7; i <= 18; i++) {
            String prefix = "&d[Rank" + i + "] ", suffix = "&r";

            this.configuration.addDefault("Settings.NickFormat.Rank" + i + ".Chat.Prefix", prefix);
            this.configuration.addDefault("Settings.NickFormat.Rank" + i + ".Chat.Suffix", suffix);
            this.configuration.addDefault("Settings.NickFormat.Rank" + i + ".PlayerList.Prefix", prefix);
            this.configuration.addDefault("Settings.NickFormat.Rank" + i + ".PlayerList.Suffix", suffix);
            this.configuration.addDefault("Settings.NickFormat.Rank" + i + ".NameTag.Prefix", prefix);
            this.configuration.addDefault("Settings.NickFormat.Rank" + i + ".NameTag.Suffix", suffix);
            this.configuration.addDefault("Settings.NickFormat.Rank" + i + ".SortID", 9997 - i);
            this.configuration.addDefault("Settings.NickFormat.Rank" + i + ".GroupName", "Rank" + i);
        }
    }

}
