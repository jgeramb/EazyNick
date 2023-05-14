package com.justixdev.eazynick.api;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.utilities.MineSkinAPI;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import com.justixdev.eazynick.utilities.mojang.MojangAPI;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import static com.justixdev.eazynick.nms.ReflectionHelper.NMS_VERSION;
import static com.justixdev.eazynick.nms.ReflectionHelper.invoke;

public class NickedPlayerData {

    @Getter
    private final UUID uniqueId;
    @Getter
    private final int sortID;
    @Getter
    private Object skinProfile;
    @Getter
    private final String realName;
    @Getter @Setter
    private UUID spoofedUniqueId;
    @Getter @Setter
    private String oldDisplayName,
            oldPlayerListName,
            nickName,
            skinName,
            chatPrefix,
            chatSuffix,
            tabPrefix,
            tabSuffix,
            tagPrefix,
            tagSuffix,
            groupName;

    public NickedPlayerData(UUID uniqueId,
                            UUID spoofedUniqueId,
                            String oldDisplayName,
                            String oldPlayerListName,
                            String realName,
                            String nickName,
                            String skinName,
                            String chatPrefix,
                            String chatSuffix,
                            String tabPrefix,
                            String tabSuffix,
                            String tagPrefix,
                            String tagSuffix,
                            String groupName,
                            int sortID) {
        this.uniqueId = uniqueId;
        this.spoofedUniqueId = spoofedUniqueId;
        this.oldDisplayName = oldDisplayName;
        this.oldPlayerListName = oldPlayerListName;
        this.realName = realName;
        this.nickName = nickName;
        this.skinName = skinName;
        this.chatPrefix = chatPrefix;
        this.chatSuffix = chatSuffix;
        this.tabPrefix = tabPrefix;
        this.tabSuffix = tabSuffix;
        this.tagPrefix = tagPrefix;
        this.tagSuffix = tagSuffix;
        this.groupName = groupName;
        this.sortID = sortID;

        //Load skin
        prepareSkinProfile();
    }

    public Object getFakeGameProfile(boolean spoofUniqueId) {
        EazyNick eazyNick = EazyNick.getInstance();

        if(this.spoofedUniqueId == null)
            this.spoofedUniqueId = this.uniqueId;

        boolean changeNameTag = eazyNick.getSetupYamlFile().getConfiguration().getBoolean("Settings.ChangeOptions.NameTag");

        // Create and return new game profile
        if(NMS_VERSION.startsWith("v1_7")) {
            net.minecraft.util.com.mojang.authlib.GameProfile gameProfile = new net.minecraft.util.com.mojang.authlib.GameProfile(spoofUniqueId ? spoofedUniqueId : uniqueId, changeNameTag ? nickName : realName);
            net.minecraft.util.com.mojang.authlib.properties.PropertyMap properties = gameProfile.getProperties();
            properties.removeAll("textures");

            if(this.skinProfile == null)
                this.skinProfile = eazyNick.getUtils().getDefaultGameProfile_1_7();

            properties.putAll("textures", ((net.minecraft.util.com.mojang.authlib.GameProfile) this.skinProfile).getProperties().get("textures"));

            return gameProfile;
        } else {
            GameProfile gameProfile = new GameProfile(spoofUniqueId ? spoofedUniqueId : uniqueId, changeNameTag ? nickName : realName);
            PropertyMap properties = gameProfile.getProperties();
            properties.removeAll("textures");

            if(this.skinProfile == null)
                this.skinProfile = eazyNick.getUtils().getDefaultGameProfile();

            properties.putAll("textures", ((GameProfile) this.skinProfile).getProperties().get("textures"));

            return gameProfile;
        }
    }

    private void prepareSkinProfile() {
        new Thread(() -> {
            EazyNick eazyNick = EazyNick.getInstance();
            Utils utils = eazyNick.getUtils();
            MineSkinAPI mineSkinAPI = eazyNick.getMineSkinAPI();
            SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();

            if(!setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.Skin")) {
                Player player = Bukkit.getPlayer(this.uniqueId);

                if(player != null) {
                    try {
                        this.skinProfile = invoke(player,"getProfile");
                    } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException ignore) {
                    }
                }

                return;
            }

            this.skinProfile = NMS_VERSION.startsWith("v1_7")
                    ? utils.getDefaultGameProfile_1_7()
                    : utils.getDefaultGameProfile();

            try {
                Object profile = null;

                if(skinName.startsWith("MINESKIN:")) {
                    // Load skin from mineskin.org
                    if(NMS_VERSION.startsWith("v1_7")) {
                        ((net.minecraft.util.com.mojang.authlib.GameProfile) this.skinProfile).getProperties().removeAll("textures");
                        ((net.minecraft.util.com.mojang.authlib.GameProfile) this.skinProfile).getProperties().putAll(
                                "textures",
                                mineSkinAPI.getTextureProperties_1_7(skinName.equals("MINESKIN:RANDOM")
                                        ? utils.getRandomStringFromList(utils.getMineSkinUUIDs())
                                        : this.skinName.split(":")[1])
                        );
                    } else {
                        ((GameProfile) this.skinProfile).getProperties().removeAll("textures");
                        ((GameProfile) this.skinProfile).getProperties().putAll(
                                "textures",
                                mineSkinAPI.getTextureProperties(this.skinName.equals("MINESKIN:RANDOM")
                                        ? utils.getRandomStringFromList(utils.getMineSkinUUIDs())
                                        : this.skinName.split(":")[1])
                        );
                    }
                } else
                    profile = MojangAPI.getGameProfile(this.skinName);

                if(profile != null)
                    this.skinProfile = profile;
            } catch (IOException ex) {
                if(setupYamlFile.getConfiguration().getBoolean("ShowProfileErrorMessages")) {
                    if(utils.isSupportMode()) {
                        utils.sendConsole("§cAn error occurred while preparing skin profile§7:");

                        ex.printStackTrace();
                    } else
                        utils.sendConsole("§cAn error occurred while preparing skin profile§7, §cthis is NOT a plugin error§7!");
                }

                this.skinProfile = NMS_VERSION.startsWith("v1_7")
                        ? utils.getDefaultGameProfile_1_7()
                        : utils.getDefaultGameProfile();
            }
        }).start();
    }

    public void setSkinName(String skinName) {
        this.skinName = skinName;

        // Reload skin
        prepareSkinProfile();
    }

    public NickedPlayerData copy() {
        return new NickedPlayerData(
                uniqueId,
                spoofedUniqueId,
                oldDisplayName,
                oldPlayerListName,
                realName,
                nickName,
                skinName,
                chatPrefix,
                chatSuffix,
                tabPrefix,
                tabSuffix,
                tagPrefix,
                tagSuffix,
                groupName,
                sortID
        );
    }

    @Override
    public String toString() {
        return "NickedPlayerData={" +
                "realUniqueId=" + uniqueId + " | " +
                "spoofedUniqueId=" + spoofedUniqueId + " | " +
                "oldDisplayName=" + oldDisplayName + " | " +
                "oldPlayerListName=" + oldPlayerListName+ " | " +
                "realName=" + realName+ " | " +
                "nickName=" + nickName + " | " +
                "skinName=" + skinName + " | " +
                "chatPrefix=" + chatPrefix + " | " +
                "chatSuffix=" + chatSuffix + " | " +
                "tabPrefix=" + tabPrefix + " | " +
                "tabSuffix=" + tabSuffix + " | " +
                "tagPrefix=" + tagPrefix + " | " +
                "tagSuffix=" + tagSuffix + " | " +
                "groupName=" + groupName + " | " +
                "sortID=" + sortID +
        "}";
    }

}
