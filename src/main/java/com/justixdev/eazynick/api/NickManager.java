package com.justixdev.eazynick.api;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.hooks.TABHook;
import com.justixdev.eazynick.nms.NMSNickManager;
import com.justixdev.eazynick.nms.ReflectionHelper;
import com.justixdev.eazynick.nms.ScoreboardTeamHandler;
import com.justixdev.eazynick.sql.MySQLNickManager;
import com.justixdev.eazynick.utilities.AsyncTask;
import com.justixdev.eazynick.utilities.AsyncTask.AsyncRunnable;
import com.justixdev.eazynick.utilities.MineSkinAPI;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.nametagedit.plugin.NametagEdit;
import com.nametagedit.plugin.api.INametagApi;
import com.nametagedit.plugin.api.data.Nametag;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.chat.Chat;
import net.skinsrestorer.api.PlayerWrapper;
import net.skinsrestorer.api.SkinsRestorerAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("unused")
public class NickManager extends ReflectionHelper {

    private final Player player;
    private final NMSNickManager nmsNickManager;

    private final EazyNick eazyNick;
    private final SetupYamlFile setupYamlFile;
    private final Utils utils;

    public NickManager(Player player) {
        this.player = player;
        this.nmsNickManager = new NMSNickManager(player);

        this.eazyNick = EazyNick.getInstance();
        this.setupYamlFile = eazyNick.getSetupYamlFile();
        this.utils = eazyNick.getUtils();
    }

    public void changeSkin(String skinName) {
        if(skinName == null)
            return;

        MySQLNickManager mysqlNickManager = eazyNick.getMysqlNickManager();
        UUID uniqueId = player.getUniqueId();

        if(utils.getNickedPlayers().containsKey(uniqueId))
            utils.getNickedPlayers().get(uniqueId).setSkinName(skinName);
        else
            utils.getNickedPlayers().put(
                    uniqueId,
                    new NickedPlayerData(
                            uniqueId,
                            uniqueId,
                            player.getDisplayName(),
                            player.getPlayerListName(),
                            player.getName(),
                            player.getName(),
                            skinName,
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "default",
                            9999
                    )
            );

        new Thread(() -> {
            if(utils.isPluginInstalled("SkinsRestorer") && setupYamlFile.getConfiguration().getBoolean("ChangeSkinsRestorerSkin")) {
                // Update skins restorer data
                try {
                    Plugin skinsRestorer = Bukkit.getPluginManager().getPlugin("SkinsRestorer");

                    if ((boolean) getFieldValue(Objects.requireNonNull(skinsRestorer), "proxyMode")) {
                        if(!(skinName.startsWith("MINESKIN:"))) {
                            try {
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                DataOutputStream out = new DataOutputStream(byteArrayOutputStream);
                                out.writeUTF("setSkin");
                                out.writeUTF(player.getName());
                                out.writeUTF(skinName);

                                player.sendPluginMessage(eazyNick, "sr:messagechannel", byteArrayOutputStream.toByteArray());
                            } catch (IOException ignore) {
                            }
                        }
                    } else {
                        SkinsRestorerAPI skinsRestorerAPI = SkinsRestorerAPI.getApi();
                        Object skinProfile = utils.getNickedPlayers().get(player.getUniqueId()).getSkinProfile();

                        if(skinProfile == null)
                            return;

                        String skinValue = utils.getDefaultSkinValue(), skinSignature = utils.getDefaultSkinSignature();

                        if (skinName.startsWith("MINESKIN:")) {
                            // Load skin from mineskin.org
                            MineSkinAPI mineSkinAPI = eazyNick.getMineSkinAPI();

                            if (NMS_VERSION.startsWith("v1_7")) {
                                Optional<?> texturesPropertyOptional = mineSkinAPI.getTextureProperties_1_7(
                                        skinName.equals("MINESKIN:RANDOM")
                                                ? utils.getRandomStringFromList(utils.getMineSkinUUIDs())
                                                : skinName.split(":")[1]
                                ).stream().findAny();

                                if (texturesPropertyOptional.isPresent()) {
                                    net.minecraft.util.com.mojang.authlib.properties.Property texturesProperty = (net.minecraft.util.com.mojang.authlib.properties.Property) texturesPropertyOptional.get();
                                    skinValue = texturesProperty.getValue();
                                    skinSignature = texturesProperty.getSignature();
                                }
                            } else {
                                Optional<?> texturesPropertyOptional = mineSkinAPI.getTextureProperties(
                                        skinName.equals("MINESKIN:RANDOM")
                                                ? utils.getRandomStringFromList(utils.getMineSkinUUIDs()) :
                                                skinName.split(":")[1]
                                ).stream().findAny();

                                if (texturesPropertyOptional.isPresent()) {
                                    Property texturesProperty = (Property) texturesPropertyOptional.get();
                                    skinValue = texturesProperty.getValue();
                                    skinSignature = texturesProperty.getSignature();
                                }
                            }
                        } else if (NMS_VERSION.startsWith("v1_7")) {
                            Optional<?> texturesPropertyOptional = ((net.minecraft.util.com.mojang.authlib.GameProfile) skinProfile).getProperties().get("textures").stream().findAny();

                            if (texturesPropertyOptional.isPresent()) {
                                net.minecraft.util.com.mojang.authlib.properties.Property texturesProperty = (net.minecraft.util.com.mojang.authlib.properties.Property) texturesPropertyOptional.get();
                                skinValue = texturesProperty.getValue();
                                skinSignature = texturesProperty.getSignature();
                            }
                        } else {
                            Optional<?> texturesPropertyOptional = ((GameProfile) skinProfile).getProperties().get("textures").stream().findAny();

                            if (texturesPropertyOptional.isPresent()) {
                                Property texturesProperty = (Property) texturesPropertyOptional.get();
                                skinValue = texturesProperty.getValue();
                                skinSignature = texturesProperty.getSignature();
                            }
                        }

                        Class<?> genericPropertyClass = Objects.requireNonNull(findClass("net.skinsrestorer.api.property.GenericProperty"));

                        invoke(
                                skinsRestorerAPI,
                                "setSkinData",
                                types(String.class, genericPropertyClass),
                                "custom",
                                newInstance(
                                        genericPropertyClass,
                                        types(String.class, String.class, String.class),
                                        "textures",
                                        skinValue,
                                        skinSignature
                                ));
                        skinsRestorerAPI.setSkin(
                                this.player.getName(),
                                skinName.startsWith("MINESKIN:")
                                        ? this.player.getName()
                                        : skinName);
                        skinsRestorerAPI.applySkin(new PlayerWrapper(this.player));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            if (this.setupYamlFile.getConfiguration().getBoolean("BungeeCord")) {
                String nickName = mysqlNickManager.getNickName(uniqueId);

                if((nickName == null) || !(nickName.equals("NaN"))) {
                    mysqlNickManager.removePlayer(uniqueId);
                    mysqlNickManager.addPlayer(uniqueId, nickName, skinName);
                } else
                    mysqlNickManager.addPlayer(uniqueId, this.player.getName(), skinName);
            }

            // Respawn player and update list
            Bukkit.getScheduler().runTask(this.eazyNick, this.nmsNickManager::updatePlayer);
        }).start();
    }

    public void changeSkinToMineSkinId(String mineSkinId) {
        this.changeSkin("MINESKIN:" + mineSkinId);
    }

    public void setName(String nickName) {
        UUID uniqueId = this.player.getUniqueId();

        if(this.utils.getNickedPlayers().containsKey(uniqueId))
            this.utils.getNickedPlayers().get(uniqueId).setNickName(nickName);
        else {
            this.utils.getNickedPlayers().put(
                    uniqueId,
                    new NickedPlayerData(
                            uniqueId,
                            uniqueId,
                            this.player.getDisplayName(),
                            this.player.getPlayerListName(),
                            this.player.getName(),
                            nickName,
                            this.player.getName(),
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "default",
                            9999));
        }

        // Respawn player and update list
        this.nmsNickManager.updatePlayer();
    }

    public void nickPlayer(String nickName) {
        // Use nickname as skin name
        this.nickPlayer(nickName, nickName);
    }

    public void nickPlayer(String nickName, String skinName) {
        if (!NMS_VERSION.equalsIgnoreCase("v1_7_R4"))
            this.player.setCustomName(nickName);

        if(this.setupYamlFile.getConfiguration().getBoolean("BungeeCord"))
            this.eazyNick.getMysqlNickManager().addPlayer(this.player.getUniqueId(), nickName, skinName);

        int fakeExperienceLevel = this.setupYamlFile.getConfiguration().getInt("FakeExperienceLevel");

        if(fakeExperienceLevel != -1) {
            this.utils.getOldExperienceLevels().put(this.player.getUniqueId(), this.player.getLevel());

            this.player.setLevel(fakeExperienceLevel);
        }

        if(this.utils.isPluginInstalled("SkinsRestorer")
                && this.setupYamlFile.getConfiguration().getBoolean("ChangeSkinsRestorerSkin")) {
            new Thread(() -> {
                long terminateMillis = System.currentTimeMillis() + 1500;

                //noinspection StatementWithEmptyBody
                while((System.currentTimeMillis() < terminateMillis)
                        || (this.utils.getNickedPlayers().get(this.player.getUniqueId()).getSkinProfile() == null));

                if(this.utils.getNickedPlayers().get(this.player.getUniqueId()).getSkinProfile() == null) {
                    // Respawn player and update skin
                    this.changeSkin(skinName);
                }
            }).start();
        } else {
            // Respawn player
            this.nmsNickManager.updatePlayer();
        }
    }

    public void unnickPlayer() {
        // Remove MySQL data
        if(this.setupYamlFile.getConfiguration().getBoolean("BungeeCord")) {
            this.eazyNick.getMysqlNickManager().removePlayer(this.player.getUniqueId());
            this.eazyNick.getMysqlPlayerDataManager().removeData(this.player.getUniqueId());
        }

        // Unnick player
        this.unnickPlayerWithoutRemovingMySQL(false, true);
    }

    public void unnickPlayerWithoutRemovingMySQL(boolean keepNick, boolean respawnPlayer) {
        NickedPlayerData nickedPlayerData = this.utils.getNickedPlayers().get(this.player.getUniqueId());
        String realName = getRealName();

        if (!NMS_VERSION.equals("v1_7_R4"))
            this.player.setCustomName(realName);


        // Reset fake scoreboard team
        if(this.setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.NameTag")
                && this.utils.getScoreboardTeamHandlers().containsKey(this.player.getUniqueId()))
            this.utils.getScoreboardTeamHandlers().remove(this.player.getUniqueId()).destroyTeam();

        // Respawn player
        if(respawnPlayer) {
            if(this.utils.isPluginInstalled("SkinsRestorer")
                    && this.setupYamlFile.getConfiguration().getBoolean("ChangeSkinsRestorerSkin")) {
                // Respawn player and update skin
                changeSkin(this.player.getName());
            } else {
                // Respawn player
                this.nmsNickManager.updatePlayer();
            }
        }

        if(nickedPlayerData != null) {
            String nickName = nickedPlayerData.getNickName();

            new AsyncTask(new AsyncRunnable() {

                @Override
                public void run() {
                    utils.getNickedPlayers().remove(player.getUniqueId());
                }
            }, 50L * 3).run();

            if (keepNick)
                this.utils.getLastNickData().put(this.player.getUniqueId(), nickedPlayerData.copy());
            else
                this.utils.getLastNickData().remove(this.player.getUniqueId());

            // Reset chat and list name
            new AsyncTask(new AsyncRunnable() {

                @Override
                public void run() {
                    if (!player.isOnline())
                        return;

                    String oldDisplayName = nickedPlayerData.getOldDisplayName(),
                            oldPlayerListName = nickedPlayerData.getOldPlayerListName();
                    boolean replaceInDisplayName = (oldDisplayName != null) && oldDisplayName.equals("NONE"),
                            replaceInPlayerListName = (oldPlayerListName != null) && oldPlayerListName.equals("NONE");

                    if (
                            utils.getWorldsWithDisabledPrefixAndSuffix()
                                    .stream()
                                    .noneMatch(world -> world.equalsIgnoreCase(player.getWorld().getName()))
                            || replaceInDisplayName
                            || replaceInPlayerListName
                    ) {
                        player.setDisplayName(
                                replaceInDisplayName
                                        ? player.getDisplayName().replace(nickName, player.getName())
                                        : oldDisplayName
                        );

                        if (!utils.isPluginInstalled("LuckTab")) {
                            nmsNickManager.setPlayerListName(
                                    replaceInPlayerListName
                                            ? player.getPlayerListName().replace(nickName, player.getName())
                                            : oldDisplayName
                            );
                        }
                    }

                    if (utils.getOldExperienceLevels().containsKey(player.getUniqueId())) {
                        player.setLevel(utils.getOldExperienceLevels().get(player.getUniqueId()));

                        utils.getOldExperienceLevels().remove(player.getUniqueId());
                    }
                }
            }, 1000 +
                    (setupYamlFile.getConfiguration().getBoolean("RandomDisguiseDelay")
                            ? 2000
                            : 0)
            ).run();
        }
    }

    public String getRealName() {
        return this.player.getName();
    }

    private void updateChatTab() {
        if(NMS_VERSION.equals("v1_7_R4")) {
            String nameFormatChat = getChatPrefix() + getNickName() + getChatSuffix();
            String nameFormatTab = getTabPrefix() + getNickName() + getTabSuffix();

            if(nameFormatTab.length() <= 16) {
                this.player.setDisplayName(nameFormatChat);
                this.nmsNickManager.setPlayerListName(nameFormatTab);
            } else {
                this.player.setDisplayName(nameFormatChat.substring(0, 16));
                this.nmsNickManager.setPlayerListName(getNickName());
            }
        } else {
            this.player.setDisplayName(getChatPrefix() + getNickName() + getChatSuffix());
            this.nmsNickManager.setPlayerListName(getTabPrefix() + getNickName() + getTabSuffix());
        }
    }

    public String getChatPrefix() {
        return this.utils.getNickedPlayers().containsKey(this.player.getUniqueId())
                ? this.utils.getNickedPlayers().get(this.player.getUniqueId()).getChatPrefix()
                : (this.utils.isPluginInstalled("Vault")
                        && this.setupYamlFile.getConfiguration().getBoolean("ServerIsUsingVaultPrefixesAndSuffixes")
                        ? Objects.requireNonNull(Bukkit.getServer().getServicesManager().getRegistration(Chat.class))
                                .getProvider()
                                .getPlayerPrefix(this.player)
                        : "");
    }

    public void setChatPrefix(String chatPrefix) {
        if (this.isNicked())
            this.utils.getNickedPlayers().get(this.player.getUniqueId()).setChatPrefix(chatPrefix);

        this.updateChatTab();
    }

    public String getChatSuffix() {
        return this.utils.getNickedPlayers().containsKey(this.player.getUniqueId())
                ? this.utils.getNickedPlayers().get(this.player.getUniqueId()).getChatSuffix()
                : (this.utils.isPluginInstalled("Vault")
                        && this.setupYamlFile.getConfiguration().getBoolean("ServerIsUsingVaultPrefixesAndSuffixes")
                        ? Objects.requireNonNull(Bukkit.getServer().getServicesManager().getRegistration(Chat.class))
                                .getProvider()
                                .getPlayerSuffix(this.player)
                        : "");
    }

    public void setChatSuffix(String chatSuffix) {
        if(this.isNicked())
            this.utils.getNickedPlayers().get(this.player.getUniqueId()).setChatSuffix(chatSuffix);

        this.updateChatTab();
    }

    public String getTabPrefix() {
        return this.utils.getNickedPlayers().containsKey(this.player.getUniqueId())
                ? this.utils.getNickedPlayers().get(this.player.getUniqueId()).getTabPrefix()
                : (this.utils.isPluginInstalled("Vault")
                        && this.setupYamlFile.getConfiguration().getBoolean("ServerIsUsingVaultPrefixesAndSuffixes")
                        ? Objects.requireNonNull(Bukkit.getServer().getServicesManager().getRegistration(Chat.class))
                                .getProvider()
                                .getPlayerPrefix(this.player)
                        : "");
    }

    public void setTabPrefix(String tabPrefix) {
        if(this.isNicked())
            this.utils.getNickedPlayers().get(this.player.getUniqueId()).setTabPrefix(tabPrefix);

        this.updateChatTab();
    }

    public String getTabSuffix() {
        return this.utils.getNickedPlayers().containsKey(this.player.getUniqueId())
                ? this.utils.getNickedPlayers().get(this.player.getUniqueId()).getTabSuffix()
                : (this.utils.isPluginInstalled("Vault")
                        && this.setupYamlFile.getConfiguration().getBoolean("ServerIsUsingVaultPrefixesAndSuffixes")
                        ? Objects.requireNonNull(Bukkit.getServer().getServicesManager().getRegistration(Chat.class))
                                .getProvider()
                                .getPlayerSuffix(this.player)
                        : "");
    }

    public void setTabSuffix(String tabSuffix) {
        if(this.isNicked())
            this.utils.getNickedPlayers().get(this.player.getUniqueId()).setTabSuffix(tabSuffix);

        this.updateChatTab();
    }

    public String getTagPrefix() {
        return this.utils.getNickedPlayers().containsKey(this.player.getUniqueId())
                ? this.utils.getNickedPlayers().get(this.player.getUniqueId()).getTagPrefix()
                : (this.utils.isPluginInstalled("Vault")
                        && this.setupYamlFile.getConfiguration().getBoolean("ServerIsUsingVaultPrefixesAndSuffixes")
                        ? Objects.requireNonNull(Bukkit.getServer().getServicesManager().getRegistration(Chat.class))
                                .getProvider()
                                .getPlayerPrefix(this.player)
                        : "");
    }

    public void setTagPrefix(String tagPrefix) {
        if(this.utils.getScoreboardTeamHandlers().containsKey(this.player.getUniqueId()))
            this.utils.getScoreboardTeamHandlers().get(this.player.getUniqueId()).setPrefix(tagPrefix);

        if(this.isNicked())
            this.utils.getNickedPlayers().get(this.player.getUniqueId()).setTagPrefix(tagPrefix);
    }

    public String getTagSuffix() {
        return this.utils.getNickedPlayers().containsKey(this.player.getUniqueId())
                ? this.utils.getNickedPlayers().get(this.player.getUniqueId()).getTagSuffix()
                : (this.utils.isPluginInstalled("Vault")
                && this.setupYamlFile.getConfiguration().getBoolean("ServerIsUsingVaultPrefixesAndSuffixes")
                        ? Objects.requireNonNull(Bukkit.getServer().getServicesManager().getRegistration(Chat.class))
                                .getProvider()
                                .getPlayerSuffix(this.player)
                        : "");
    }

    public void setTagSuffix(String tagSuffix) {
        if(this.utils.getScoreboardTeamHandlers().containsKey(this.player.getUniqueId()))
            this.utils.getScoreboardTeamHandlers().get(this.player.getUniqueId()).setSuffix(tagSuffix);

        if(this.isNicked())
            this.utils.getNickedPlayers().get(this.player.getUniqueId()).setTagSuffix(tagSuffix);
    }

    public boolean isNicked() {
        return this.utils.getNickedPlayers().containsKey(this.player.getUniqueId());
    }

    public String getRandomName() {
        return this.utils.getRandomStringFromList(this.utils.getNickNames());
    }

    public String getNickName() {
        return this.isNicked()
                ? this.utils.getNickedPlayers().get(this.player.getUniqueId()).getNickName()
                : this.player.getName();
    }

    public String getNickFormat() {
        return this.getChatPrefix() + this.getNickName() + this.getChatSuffix();
    }

    public String getOldDisplayName() {
        return this.isNicked()
                ? this.utils.getNickedPlayers().get(this.player.getUniqueId()).getOldDisplayName()
                : this.player.getName();
    }

    public String getOldPlayerListName() {
        return this.isNicked()
                ? this.utils.getNickedPlayers().get(this.player.getUniqueId()).getOldPlayerListName()
                : this.player.getName();
    }

    public String getGroupName() {
        return this.isNicked()
                ? this.utils.getNickedPlayers().get(this.player.getUniqueId()).getGroupName()
                : "NONE";
    }

    public void setGroupName(String groupName) {
        if(this.isNicked())
            this.utils.getNickedPlayers().get(this.player.getUniqueId()).setGroupName(groupName);
    }

    public void updatePrefixSuffix(String nickName,
                                   String realName,
                                   String tagPrefix,
                                   String tagSuffix,
                                   String chatPrefix,
                                   String chatSuffix,
                                   String tabPrefix,
                                   String tabSuffix,
                                   int sortID,
                                   String groupName) {
        String finalTabPrefix = tabPrefix, finalTabSuffix = tabSuffix;

        if(this.setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.NameTag")) {
            // Reset fake scoreboard team
            if(this.utils.getScoreboardTeamHandlers().containsKey(this.player.getUniqueId())) {
                this.utils.getScoreboardTeamHandlers().get(this.player.getUniqueId()).destroyTeam();
                this.utils.getScoreboardTeamHandlers().remove(this.player.getUniqueId());
            }

            // Create new fake scoreboard team
            if(!(utils.isPluginInstalled("TAB", "NEZNAMY")
                    && this.setupYamlFile.getConfiguration().getBoolean("ChangeGroupAndPrefixAndSuffixInTAB")))
                this.utils.getScoreboardTeamHandlers().put(
                        this.player.getUniqueId(),
                        new ScoreboardTeamHandler(
                                this.player,
                                nickName,
                                realName,
                                tagPrefix,
                                tagSuffix,
                                sortID,
                                groupName
                        ));
        }

        new AsyncTask(
                new AsyncRunnable() {

                    @Override
                    public void run() {
                        if(!eazyNick.isEnabled() || !player.isOnline() || !isNicked()) {
                            cancel();
                            return;
                        }

                        UUID uuid = player.getUniqueId();

                        // Update fake scoreboard team
                        if(utils.getScoreboardTeamHandlers().containsKey(uuid)) {
                            ScoreboardTeamHandler scoreboardTeamHandler = utils.getScoreboardTeamHandlers().get(player.getUniqueId());
                            scoreboardTeamHandler.destroyTeam();
                            scoreboardTeamHandler.createTeam();
                        }

                        boolean tabGroupPrefixSuffixChange = utils.isPluginInstalled("TAB", "NEZNAMY")
                                && setupYamlFile.getConfiguration().getBoolean("ChangeGroupAndPrefixAndSuffixInTAB");

                        // Update TAB list prefix and suffix and nametag prefix, suffix and group
                        if(tabGroupPrefixSuffixChange)
                            new TABHook(player).update(finalTabPrefix, finalTabSuffix, tagPrefix, tagSuffix, groupName);

                        if(!setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.PlayerListName"))
                            return;

                        // Update list name
                        if(!(tabGroupPrefixSuffixChange || utils.isPluginInstalled("LuckTab"))) {
                            String tmpTabPrefix = finalTabPrefix,
                                    tmpTabSuffix = finalTabSuffix;

                            // Replace PlaceholderAPI placeholders
                            if(utils.isPluginInstalled("PlaceholderAPI")) {
                                tmpTabPrefix = PlaceholderAPI.setPlaceholders(player, tmpTabPrefix);
                                tmpTabSuffix = PlaceholderAPI.setPlaceholders(player, tmpTabSuffix);
                            }

                            nmsNickManager.setPlayerListName(tmpTabPrefix + nickName + tmpTabSuffix);
                        }
                    }
                },
                400 +
                        (setupYamlFile.getConfiguration().getBoolean("RandomDisguiseDelay")
                                ? 2000
                                : 0),
                setupYamlFile.getConfiguration().getInt("PrefixSuffixUpdateDelay") * 50L
        ).run();

        // Replace PlaceholderAPI placeholders
        if(this.utils.isPluginInstalled("PlaceholderAPI")) {
            chatPrefix = PlaceholderAPI.setPlaceholders(this.player, chatPrefix);
            chatSuffix = PlaceholderAPI.setPlaceholders(this.player, chatSuffix);
            tabPrefix = PlaceholderAPI.setPlaceholders(this.player, tabPrefix);
            tabSuffix = PlaceholderAPI.setPlaceholders(this.player, tabSuffix);
        }

        // Set chat name
        if(this.setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.DisplayName"))
            this.player.setDisplayName(chatPrefix + nickName + chatSuffix);

        Bukkit.getScheduler().runTask(this.eazyNick, () -> {
            String tmpTagPrefix = tagPrefix, tmpTagSuffix = tagSuffix;

            if (this.utils.isPluginInstalled("PlaceholderAPI")) {
                tmpTagPrefix = PlaceholderAPI.setPlaceholders(this.player, tmpTagPrefix);
                tmpTagSuffix = PlaceholderAPI.setPlaceholders(this.player, tmpTagSuffix);
            }

			// Update NametagEdit prefix and suffix synchronously
			if (this.utils.isPluginInstalled("NametagEdit")) {
                this.utils.getNametagEditPrefixes().remove(this.player.getUniqueId());
                this.utils.getNametagEditSuffixes().remove(this.player.getUniqueId());

				INametagApi nametagEditAPI = NametagEdit.getApi();
				Nametag nametag = nametagEditAPI.getNametag(this.player);

                this.utils.getNametagEditPrefixes().put(this.player.getUniqueId(), nametag.getPrefix());
                this.utils.getNametagEditSuffixes().put(this.player.getUniqueId(), nametag.getSuffix());

				nametagEditAPI.setPrefix(this.player, tmpTagPrefix);
				nametagEditAPI.setSuffix(this.player, tmpTagSuffix);
				nametagEditAPI.reloadNametag(this.player);
			}

            // Update CloudNet v2 prefix and suffix
            if (this.utils.isPluginInstalled("CloudNetAPI")
                    && this.setupYamlFile.getConfiguration().getBoolean("ServerIsUsingCloudNETPrefixesAndSuffixes")) {
                CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(this.player.getUniqueId());
                PermissionEntity entity = cloudPlayer.getPermissionEntity();

                this.utils.getOldCloudNETPrefixes().put(this.player.getUniqueId(), entity.getPrefix());
                this.utils.getOldCloudNETSuffixes().put(this.player.getUniqueId(), entity.getSuffix());

                entity.setPrefix(tmpTagPrefix);
                entity.setSuffix(tmpTagSuffix);
            }
        });

        // Update PermissionsEx prefix, suffix and group
        if(this.utils.isPluginInstalled("PermissionsEx")) {
            PermissionUser user = PermissionsEx.getUser(this.player);

            if(this.setupYamlFile.getConfiguration().getBoolean("SwitchPermissionsExGroupByNicking") && !(groupName.equalsIgnoreCase("NONE"))) {
                List<String> groupNames = new ArrayList<>();

                for (PermissionGroup group : user.getParents()) {
                    groupNames.add(group.getName());

                    user.removeGroup(group);
                }

                this.utils.getOldPermissionsExGroups().putIfAbsent(this.player.getUniqueId(), groupNames.toArray(new String[0]));
            } else {
                this.utils.getOldPermissionsExPrefixes().put(this.player.getUniqueId(), user.getPrefix());
                this.utils.getOldPermissionsExSuffixes().put(this.player.getUniqueId(), user.getSuffix());

                user.setPrefix(tabPrefix, this.player.getWorld().getName());
                user.setSuffix(tabSuffix, this.player.getWorld().getName());
            }
        }
    }

}
