package com.justixdev.eazynick.utilities;

import com.google.common.primitives.Chars;
import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NameFormatting;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.api.NickedPlayerData;
import com.justixdev.eazynick.api.events.PlayerNickEvent;
import com.justixdev.eazynick.nms.ScoreboardTeamHandler;
import com.justixdev.eazynick.nms.guis.book.BookPage;
import com.justixdev.eazynick.nms.guis.book.NMSBookUtils;
import com.justixdev.eazynick.nms.netty.legacy.impl.OutgoingLegacyPacketInjector;
import com.justixdev.eazynick.nms.netty.modern.impl.OutgoingModernPacketInjector;
import com.justixdev.eazynick.sql.MySQLNickManager;
import com.justixdev.eazynick.sql.MySQLPlayerDataManager;
import com.justixdev.eazynick.utilities.AsyncTask.AsyncRunnable;
import com.justixdev.eazynick.utilities.configuration.yaml.GUIYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import com.justixdev.eazynick.utilities.mojang.MojangAPI;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Data;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.justixdev.eazynick.nms.ReflectionHelper.NMS_VERSION;
import static com.justixdev.eazynick.nms.ReflectionHelper.invoke;

@Data
public class Utils {

    private static final List<Character> ALLOWED_NAME_SYMBOLS =
            Chars.asList("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_".toCharArray());

    // Support mode status
    private boolean supportMode = false;

    /**
     * Used in
     * {@link OutgoingModernPacketInjector}
     * and
     * {@link OutgoingLegacyPacketInjector}
     * to avoid overwriting sent chat messages
     * */
    private String lastChatMessage = "NONE";

    // Store common used messages from the current language file in a variable
    private String prefix, noPerm, notPlayer;

    private final List<String>
            nickNames = new ArrayList<>(),
            blackList = new ArrayList<>(),
            worldsWithDisabledLobbyMode = new ArrayList<>(),
            worldsWithDisabledPrefixAndSuffix = new ArrayList<>(),
            worldsWithDisabledActionBar = new ArrayList<>(),
            replaceNameInCommandBlackList = new ArrayList<>(),
            worldBlackList = new ArrayList<>(),
            mineSkinUUIDs = new ArrayList<>();

    // NickOnWorldChange: true -> every player whose nick item is enabled
    private final List<UUID>
            nickOnWorldChangePlayers = new ArrayList<>();

    // Nicked player data handling
    private final Map<UUID, NickedPlayerData>
            nickedPlayers = new HashMap<>(),
            lastNickData = new HashMap<>();
    // NameTag prefix/suffix managers
    private final Map<UUID, ScoreboardTeamHandler>
            scoreboardTeamHandlers = new HashMap<>();

    // PermissionsEx old player data
    private final Map<UUID, String[]>
            oldPermissionsExGroups = new HashMap<>();
    private final Map<UUID, String>
            oldPermissionsExPrefixes = new HashMap<>(),
            oldPermissionsExSuffixes = new HashMap<>();

    // CloudNet old player data
    private final Map<UUID, String>
            oldCloudNETPrefixes = new HashMap<>(),
            oldCloudNETSuffixes = new HashMap<>();
    private final Map<UUID, Integer>
            oldCloudNETTagIDs = new HashMap<>();

    // LuckPerms old player data
    private final Map<UUID, Object>
            oldLuckPermsGroups = new HashMap<>();
    private final Map<UUID, Object>
            luckPermsPrefixes = new HashMap<>(),
            luckPermsSuffixes = new HashMap<>();

    //NameTagEdit old player data
    private final Map<UUID, String>
            nametagEditPrefixes = new HashMap<>(),
            nametagEditSuffixes = new HashMap<>();

    // Last selected options in the BookGUI/RankedNickGUI
    private final Map<UUID, String>
            lastNickNames = new HashMap<>(),
            lastSkinNames = new HashMap<>();

    // FakeExperienceLevel -> real experience level
    private final Map<UUID, Integer>
            oldExperienceLevels = new HashMap<>();

    // Current opened pages in the NickList gui
    private final Map<UUID, Integer>
            nickNameListPages = new HashMap<>();

    // Cooldown times for nicking
    private final Map<UUID, Long>
            canUseNick = new HashMap<>();

    // Current data for RankedNickGUI
    private final Map<UUID, String>
            lastGUITexts = new HashMap<>();

    // Chat inputs for BookGUI/RankedNickGUI
    private final Map<UUID, String>
            playersTypingNameInChat = new HashMap<>();

    // Texts read from PacketPlayInTextComplete in IncomingPacketInjector
    private final Map<Player, String>
            textsToComplete = new HashMap<>();

    // Players that will be nicked in a few milliseconds
    private final List<UUID>
            soonNickedPlayers = new ArrayList<>();

    // Active plugins on the server
    private final Map<String, List<String>>
            loadedPlugins = new HashMap<>();

    // Check plugin status
    public boolean isPluginInstalled(String name) {
        return this.loadedPlugins.containsKey(name);
    }

    public boolean isPluginInstalled(String name, String author) {
        if(this.loadedPlugins.containsKey(name))
            return this.loadedPlugins.get(name).contains(author);

        return false;
    }

    // Send a message to the console including the plugin prefix
    public void sendConsole(String msg) {
        if(msg != null)
            Bukkit.getConsoleSender().sendMessage(this.prefix + msg);
    }

    // Returns the online player count
    public int getOnlinePlayerCount() {
        return Bukkit.getOnlinePlayers().size();
    }

    // Reload all configuration files
    public void reloadConfigs() {
        EazyNick eazyNick = EazyNick.getInstance();
        eazyNick.getSetupYamlFile().reload();
        eazyNick.getLanguageYamlFile().reload();
        eazyNick.getGuiYamlFile().reload();
        eazyNick.getNickNameYamlFile().reload();
    }

    private void replaceRandomColorPlaceholder(NameFormatting nameFormatting) {
        String randomColor = "§" + "0123456789abcdef".charAt(new Random().nextInt(16));

        nameFormatting.setChatPrefix(nameFormatting.getChatPrefix()
                .replace("%randomColor%", randomColor)
                .replace("%randomcolor%", randomColor)
        );
        nameFormatting.setChatSuffix(nameFormatting.getChatSuffix()
                .replace("%randomColor%", randomColor)
                .replace("%randomcolor%", randomColor)
        );
        nameFormatting.setTabPrefix(nameFormatting.getTabPrefix()
                .replace("%randomColor%", randomColor)
                .replace("%randomcolor%", randomColor)
        );
        nameFormatting.setTabSuffix(nameFormatting.getTabSuffix()
                .replace("%randomColor%", randomColor)
                .replace("%randomcolor%", randomColor)
        );
        nameFormatting.setTagPrefix(nameFormatting.getTagPrefix()
                .replace("%randomColor%", randomColor)
                .replace("%randomcolor%", randomColor)
        );
        nameFormatting.setTagSuffix(nameFormatting.getTagSuffix()
                .replace("%randomColor%", randomColor)
                .replace("%randomcolor%", randomColor)
        );
    }

    // BookGUI & RankedNickGUI
    public void performRankedNick(Player player, String rankName, String skinType, String name) {
        EazyNick eazyNick = EazyNick.getInstance();
        SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
        LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
        GUIYamlFile guiYamlFile = eazyNick.getGuiYamlFile();
        NMSBookUtils nmsBookUtils = eazyNick.getNmsBookUtils();

        // Initialize nick data
        String nameWithoutColors = new StringUtils(name).getPureString();
        String skinName;
        int sortID = 9999,
                nameLengthMin = Math.max(Math.min(setupYamlFile.getConfiguration().getInt("Settings.NameLength.Min"), 16), 1),
                nameLengthMax = Math.max(Math.min(setupYamlFile.getConfiguration().getInt("Settings.NameLength.Max"), 16), 1);

        // Check if name length is valid
        if(nameWithoutColors.length() > nameLengthMax) {
            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.NickTooLong")
                            .replace("%prefix%", this.prefix)
            );
            return;
        }

        if(nameWithoutColors.length() < nameLengthMin) {
            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.NickTooShort")
                            .replace("%prefix%", this.prefix)
            );
            return;
        }

        if(this.containsSpecialChars(nameWithoutColors)
                && !(setupYamlFile.getConfiguration().getBoolean("AllowSpecialCharactersInCustomName"))) {
            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.NickContainsSpecialCharacters")
                            .replace("%prefix%", this.prefix)
            );
            return;
        }

        if(this.containsBlackListEntry(nameWithoutColors)) {
            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.NameNotAllowed")
                            .replace("%prefix%", this.prefix)
            );
            return;
        }

        if(this.nickedPlayers
                .values()
                .stream()
                .anyMatch(currentNickedPlayerData -> currentNickedPlayerData.getNickName().equalsIgnoreCase(name))
                && !setupYamlFile.getConfiguration().getBoolean("AllowPlayersToUseSameNickName")) {
            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.NickNameAlreadyInUse")
                            .replace("%prefix%", this.prefix)
            );
            return;
        }

        if(
                (Bukkit.getOnlinePlayers()
                        .stream()
                        .anyMatch(currentPlayer -> currentPlayer.getName().equalsIgnoreCase(name))
                        || Stream.of(Bukkit.getOfflinePlayers())
                                .anyMatch(currentOfflinePlayer -> name.equalsIgnoreCase(currentOfflinePlayer.getName())))
                && !setupYamlFile.getConfiguration().getBoolean("AllowPlayersToNickAsKnownPlayers")) {
            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.PlayerWithThisNameIsKnown")
                            .replace("%prefix%", this.prefix)
            );
            return;
        }

        // Import rank data from 'guis.yml'
        NameFormatting nameFormatting = null;
        String groupName = null;

        for (int i = 1; i <= 18; i++) {
            String permission = guiYamlFile.getConfigString("RankGUI.Rank" + i + ".Permission");

            if(rankName.equalsIgnoreCase(guiYamlFile.getConfiguration().getString("RankGUI.Rank" + i + ".RankName"))
                    && guiYamlFile.getConfiguration().getBoolean("RankGUI.Rank" + i + ".Enabled")
                    && (permission.equalsIgnoreCase("NONE") || player.hasPermission(permission))) {
                nameFormatting = new NameFormatting(
                        guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".Chat.Prefix"),
                        guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".Chat.Suffix"),
                        guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".PlayerList.Prefix"),
                        guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".PlayerList.Suffix"),
                        guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".NameTag.Prefix"),
                        guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".NameTag.Suffix")
                );
                groupName = guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".GroupName");
                sortID = guiYamlFile.getConfiguration().getInt("Settings.NickFormat.Rank" + i + ".SortID");
                break;
            }
        }

        if(nameFormatting == null)
            return;

        // Add support for %randomColor% placeholder
        this.replaceRandomColorPlaceholder(nameFormatting);

        // Set skin name depending on skin type
        if(skinType.equalsIgnoreCase("DEFAULT"))
            skinName = player.getName();
        else if(skinType.equalsIgnoreCase("NORMAL"))
            skinName = new Random().nextBoolean() ? "Steve" : "Alex";
        else if(skinType.equalsIgnoreCase("RANDOM"))
            skinName = setupYamlFile.getConfiguration().getBoolean("UseMineSkinAPI")
                    ? ("MINESKIN:" + getRandomStringFromList(this.mineSkinUUIDs))
                    : this.nickNames.get(new Random().nextInt(getNickNames().size()));
        else if(skinType.equalsIgnoreCase("SKINFROMNAME"))
            skinName = name;
        else
            skinName = skinType;

        // Update last name & skin cache
        this.lastSkinNames.put(player.getUniqueId(), skinName.startsWith("MINESKIN:") ? "RANDOM" : skinName);
        this.lastNickNames.put(player.getUniqueId(), name);

        // Fix book still in inventory bug
        for (ItemStack item : player.getInventory().getContents()) {
            if((item != null) && item.getType().equals(Material.WRITTEN_BOOK) && (item.getItemMeta() != null)) {
                if(Objects.equals(((BookMeta) item.getItemMeta()).getAuthor(), eazyNick.getDescription().getName()))
                    player.getInventory().remove(item);
            }
        }

        //Add nick data to mysql or nick player
        new NickManager(player).setGroupName(groupName);

        if(setupYamlFile.getConfiguration().getBoolean("BungeeCord")
                && !(player.hasPermission("eazynick.bypasslobbymode")
                && setupYamlFile.getConfiguration().getBoolean("EnableBypassLobbyModePermission"))
                && setupYamlFile.getConfiguration().getBoolean("LobbyMode")
                && !(this.worldsWithDisabledLobbyMode.contains(player.getWorld().getName().toUpperCase()))) {
            eazyNick.getMysqlNickManager().addPlayer(player.getUniqueId(), name, skinName);
            eazyNick.getMysqlPlayerDataManager().insertData(
                    player.getUniqueId(),
                    "NONE",
                    nameFormatting.getChatPrefix(),
                    nameFormatting.getChatSuffix(),
                    nameFormatting.getTabPrefix(),
                    nameFormatting.getTabSuffix(),
                    nameFormatting.getTagPrefix(),
                    nameFormatting.getTagSuffix()
            );

            if(guiYamlFile.getConfiguration().getBoolean("BookGUI.Page6.Enabled")
                    && !NMS_VERSION.equals("v1_7_R4")) {
                ArrayList<TextComponent> textComponents = new ArrayList<>();

                Arrays.asList(guiYamlFile.getConfigString(player, "BookGUI.Page6.Text.BungeeCord")
                                .replace("%name%", nameFormatting.getChatPrefix() + name + nameFormatting.getChatSuffix())
                                .split("%nl%"))
                        .forEach(s -> textComponents.add(new TextComponent(s + "\n")));

                nmsBookUtils.open(player, nmsBookUtils.create("Done", new BookPage(textComponents)));
            }
        } else {
            final NameFormatting finalNameFormatting = nameFormatting;
            final String finalGroupName = groupName;
            final int finalSortID = sortID;

            new Thread(() -> {
                UUID uniqueId = setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.UUID")
                        ? MojangAPI.getUniqueId(name)
                        : player.getUniqueId();

                Bukkit.getScheduler().runTask(eazyNick, () -> {
                    PlayerNickEvent playerNickEvent = new PlayerNickEvent(
                            player,
                            name,
                            skinName,
                            uniqueId,
                            finalNameFormatting.getChatPrefix(),
                            finalNameFormatting.getChatSuffix(),
                            finalNameFormatting.getTabPrefix(),
                            finalNameFormatting.getTabSuffix(),
                            finalNameFormatting.getTagPrefix(),
                            finalNameFormatting.getTagSuffix(),
                            false,
                            false,
                            finalSortID,
                            finalGroupName
                    );

                    Bukkit.getPluginManager().callEvent(playerNickEvent);

                    if (!playerNickEvent.isCancelled()) {
                        new AsyncTask(new AsyncRunnable() {

                            @Override
                            public void run() {
                                Bukkit.getScheduler().runTask(eazyNick, () -> {
                                    if (guiYamlFile.getConfiguration().getBoolean("BookGUI.Page6.Enabled")
                                            && !NMS_VERSION.equals("v1_7_R4")) {
                                        nmsBookUtils.open(player, nmsBookUtils.create("Done", new BookPage(
                                                Arrays.stream(guiYamlFile.getConfigString(player, "BookGUI.Page6.Text.SingleServer")
                                                                .replace("%name%", finalNameFormatting.getChatPrefix() + name + finalNameFormatting.getChatSuffix())
                                                                .split("%nl%"))
                                                        .map(s -> new TextComponent(s + "\n"))
                                                        .collect(Collectors.toList())))
                                        );
                                    }
                                });
                            }
                        }, 400 + (
                                setupYamlFile.getConfiguration().getBoolean("RandomDisguiseDelay")
                                        ? 2000
                                        : 0
                        )).run();
                    }
                });
            }).start();
        }
    }

    // Command 'nick', 'nickother', 'nickgui' -> Nick & nick item
    public void performNick(Player player, String customNickName) {
        EazyNick eazyNick = EazyNick.getInstance();
        SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();

        // Process name
        String name = customNickName.equals("RANDOM")
                ? this.nickNames.get(new Random().nextInt(this.nickNames.size()))
                : customNickName;

        // Make sure that random nick is not being used
        if(customNickName.equals("RANDOM") && !(setupYamlFile.getConfiguration().getBoolean("AllowPlayersToUseSameNickName"))) {
            AtomicReference<String> currentName = new AtomicReference<>();

            do {
                currentName.set(this.nickNames.get(new Random().nextInt(this.nickNames.size())));
            } while (this.nickedPlayers
                    .values()
                    .stream()
                    .anyMatch(currentNickedPlayerData -> currentNickedPlayerData.getNickName().equalsIgnoreCase(currentName.get()))
            );
        }

        // Collect nick data
        boolean serverFull = getOnlinePlayerCount() >= Bukkit.getMaxPlayers();
        String nameWithoutColors = ChatColor.stripColor(name);
        String[] prefixSuffix = new String[0];
        String chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix;

        try {
            prefixSuffix = name.split(nameWithoutColors);
        } catch (Exception ignore) {
        }

        if(prefixSuffix.length >= 1) {
            chatPrefix = ChatColor.translateAlternateColorCodes('&', prefixSuffix[0]);

            if(chatPrefix.length() > 16)
                chatPrefix = chatPrefix.substring(0, 16);

            if(prefixSuffix.length >= 2) {
                chatSuffix = ChatColor.translateAlternateColorCodes('&', prefixSuffix[1]);

                if(chatSuffix.length() > 16)
                    chatSuffix = chatSuffix.substring(0, 16);
            } else
                chatSuffix = "§r";

            tabPrefix = chatPrefix;
            tabSuffix = chatSuffix;
            tagPrefix = chatPrefix;
            tagSuffix = chatSuffix;
        } else {
            chatPrefix = serverFull
                    ? setupYamlFile.getConfigString("Settings.NickFormat.ServerFullRank.Chat.Prefix")
                    : setupYamlFile.getConfigString("Settings.NickFormat.Chat.Prefix");
            chatSuffix = serverFull
                    ? setupYamlFile.getConfigString("Settings.NickFormat.ServerFullRank.Chat.Suffix")
                    : setupYamlFile.getConfigString("Settings.NickFormat.Chat.Suffix");
            tabPrefix = serverFull
                    ? setupYamlFile.getConfigString("Settings.NickFormat.ServerFullRank.PlayerList.Prefix")
                    : setupYamlFile.getConfigString("Settings.NickFormat.PlayerList.Prefix");
            tabSuffix = serverFull
                    ? setupYamlFile.getConfigString("Settings.NickFormat.ServerFullRank.PlayerList.Suffix")
                    : setupYamlFile.getConfigString("Settings.NickFormat.PlayerList.Suffix");
            tagPrefix = serverFull
                    ? setupYamlFile.getConfigString("Settings.NickFormat.ServerFullRank.NameTag.Prefix")
                    : setupYamlFile.getConfigString("Settings.NickFormat.NameTag.Prefix");
            tagSuffix = serverFull
                    ? setupYamlFile.getConfigString("Settings.NickFormat.ServerFullRank.NameTag.Suffix")
                    : setupYamlFile.getConfigString("Settings.NickFormat.NameTag.Suffix");
        }

        // Nick player
        new NickManager(player).setGroupName(serverFull
                ? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.GroupName")
                : setupYamlFile.getConfigString(player, "Settings.NickFormat.GroupName"));

        final String finalChatPrefix = chatPrefix;
        final String finalChatSuffix = chatSuffix;

        new Thread(() -> {
            UUID uniqueId = setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.UUID")
                    ? MojangAPI.getUniqueId(name)
                    : player.getUniqueId();

            Bukkit.getScheduler().runTask(eazyNick, () -> Bukkit.getPluginManager().callEvent(
                    new PlayerNickEvent(
                            player,
                            nameWithoutColors,
                            setupYamlFile.getConfiguration().getBoolean("UseMineSkinAPI")
                                    ? "MINESKIN:" + getRandomStringFromList(this.mineSkinUUIDs)
                                    : nameWithoutColors,
                            uniqueId,
                            finalChatPrefix,
                            finalChatSuffix,
                            tabPrefix,
                            tabSuffix,
                            tagPrefix,
                            tagSuffix,
                            false,
                            false,
                            serverFull
                                    ? setupYamlFile.getConfiguration().getInt("Settings.NickFormat.ServerFullRank.SortID")
                                    : setupYamlFile.getConfiguration().getInt("Settings.NickFormat.SortID"),
                            serverFull
                                    ? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.GroupName")
                                    : setupYamlFile.getConfigString(player, "Settings.NickFormat.GroupName"))
                    )
            );
        }).start();
    }

    public void performReNick(Player player) {
        // Nick player
        performReNick(
                player,
                this.nickOnWorldChangePlayers.contains(player.getUniqueId())
                        ? this.nickNames.get(new Random().nextInt(this.nickNames.size()))
                        : EazyNick.getInstance().getMysqlNickManager().getNickName(player.getUniqueId()));
    }

    public void performReNick(Player player, String name) {
        if(!new NickManager(player).isNicked()) {
            EazyNick eazyNick = EazyNick.getInstance();
            SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
            GUIYamlFile guiYamlFile = eazyNick.getGuiYamlFile();
            LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
            MySQLNickManager mysqlNickManager = eazyNick.getMysqlNickManager();
            MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMysqlPlayerDataManager();

            if(this.nickedPlayers
                    .values()
                    .stream()
                    .noneMatch(currentNickedPlayerData -> currentNickedPlayerData.getNickName().equalsIgnoreCase(name))
                    || setupYamlFile.getConfiguration().getBoolean("AllowPlayersToUseSameNickName")) {
                // Check if player is known on the server (in usercache.json)
                if(setupYamlFile.getConfiguration().getBoolean("AllowPlayersToNickAsKnownPlayers")
                        || Bukkit.getOnlinePlayers().stream()
                                .noneMatch(currentPlayer -> currentPlayer.getName().equalsIgnoreCase(name))
                                && Stream.of(Bukkit.getOfflinePlayers())
                                        .noneMatch(currentOfflinePlayer -> name.equalsIgnoreCase(currentOfflinePlayer.getName()))) {
                    UUID uniqueId = player.getUniqueId();
                    UUID spoofedUniqueId = setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.UUID")
                            ? MojangAPI.getUniqueId(name)
                            : uniqueId;

                    // Check if the nickname is different from the real name
                    if(!name.equalsIgnoreCase(player.getName())) {
                        if((mysqlPlayerDataManager != null) && mysqlPlayerDataManager.isRegistered(uniqueId)) {
                            if(setupYamlFile.getConfiguration().contains("UseLocalRankPrefixes")) {
                                // Use MySQL data as fallback data
                                NameFormatting nameFormatting = new NameFormatting(
                                        mysqlPlayerDataManager.getChatPrefix(uniqueId),
                                        mysqlPlayerDataManager.getChatSuffix(uniqueId),
                                        mysqlPlayerDataManager.getTabPrefix(uniqueId),
                                        mysqlPlayerDataManager.getTabSuffix(uniqueId),
                                        mysqlPlayerDataManager.getTagPrefix(uniqueId),
                                        mysqlPlayerDataManager.getTagSuffix(uniqueId)
                                );
                                String groupName = mysqlPlayerDataManager.getGroupName(uniqueId);
                                int sortID = 9999;

                                // Import from 'setup.yml'
                                if(groupName.equalsIgnoreCase(setupYamlFile.getConfigString("Settings.NickFormat.GroupName"))
                                        || groupName.equalsIgnoreCase(setupYamlFile.getConfigString("Settings.NickFormat.ServerFullRank.GroupName"))) {
                                    String serverFullRank = (getOnlinePlayerCount() >= Bukkit.getMaxPlayers()) ? ".ServerFullRank" : "";

                                    nameFormatting = new NameFormatting(
                                            setupYamlFile.getConfigString("Settings.NickFormat" + serverFullRank + ".Chat.Prefix"),
                                            setupYamlFile.getConfigString("Settings.NickFormat" + serverFullRank + ".Chat.Suffix"),
                                            setupYamlFile.getConfigString("Settings.NickFormat" + serverFullRank + ".PlayerList.Prefix"),
                                            setupYamlFile.getConfigString("Settings.NickFormat" + serverFullRank + ".PlayerList.Suffix"),
                                            setupYamlFile.getConfigString("Settings.NickFormat" + serverFullRank + ".NameTag.Prefix"),
                                            setupYamlFile.getConfigString("Settings.NickFormat" + serverFullRank + ".NameTag.Suffix")
                                    );
                                    sortID = setupYamlFile.getConfiguration().getInt("Settings.NickFormat" + serverFullRank + ".SortID");
                                } else {
                                    for (int i = 1; i <= 18; i++) {
                                        if(groupName.equalsIgnoreCase(guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".GroupName"))) {
                                            nameFormatting = new NameFormatting(
                                                    guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".Chat.Prefix"),
                                                    guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".Chat.Suffix"),
                                                    guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".PlayerList.Prefix"),
                                                    guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".PlayerList.Suffix"),
                                                    guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".NameTag.Prefix"),
                                                    guiYamlFile.getConfigString("Settings.NickFormat.Rank" + i + ".NameTag.Suffix")
                                            );
                                            sortID = guiYamlFile.getConfiguration().getInt("Settings.NickFormat.Rank" + i + ".SortID");
                                            break;
                                        }
                                    }
                                }

                                new NickManager(player).setGroupName(groupName);

                                this.replaceRandomColorPlaceholder(nameFormatting);

                                // Nick player
                                Bukkit.getPluginManager().callEvent(
                                        new PlayerNickEvent(
                                                player,
                                                name,
                                                mysqlNickManager.getSkinName(player.getUniqueId()),
                                                spoofedUniqueId,
                                                nameFormatting.getChatPrefix(),
                                                nameFormatting.getChatSuffix(),
                                                nameFormatting.getTabPrefix(),
                                                nameFormatting.getTabSuffix(),
                                                nameFormatting.getTagPrefix(),
                                                nameFormatting.getTagSuffix(),
                                                true,
                                                false,
                                                sortID,
                                                mysqlPlayerDataManager.getGroupName(player.getUniqueId()))
                                );
                            } else
                                // Nick player using MySQL data
                                Bukkit.getPluginManager().callEvent(new PlayerNickEvent(
                                        player,
                                        name,
                                        mysqlNickManager.getSkinName(player.getUniqueId()),
                                        spoofedUniqueId,
                                        mysqlPlayerDataManager.getChatPrefix(player.getUniqueId()),
                                        mysqlPlayerDataManager.getChatSuffix(player.getUniqueId()),
                                        mysqlPlayerDataManager.getTabPrefix(player.getUniqueId()),
                                        mysqlPlayerDataManager.getTabSuffix(player.getUniqueId()),
                                        mysqlPlayerDataManager.getTagPrefix(player.getUniqueId()),
                                        mysqlPlayerDataManager.getTagSuffix(player.getUniqueId()),
                                        true,
                                        false,
                                        9999,
                                        mysqlPlayerDataManager.getGroupName(player.getUniqueId()))
                                );
                        } else {
                            // Import data from 'setup.yml'
                            boolean serverFull = getOnlinePlayerCount() >= Bukkit.getMaxPlayers();
                            String prefix = serverFull
                                    ? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.NameTag.Prefix")
                                    : setupYamlFile.getConfigString(player, "Settings.NickFormat.NameTag.Prefix");
                            String suffix = serverFull
                                    ? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.NameTag.Suffix")
                                    : setupYamlFile.getConfigString(player, "Settings.NickFormat.NameTag.Suffix");

                            // Nick player
                            new NickManager(player).setGroupName(
                                    serverFull
                                            ? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.GroupName")
                                            : setupYamlFile.getConfigString(player, "Settings.NickFormat.GroupName")
                            );

                            Bukkit.getPluginManager().callEvent(
                                    new PlayerNickEvent(
                                            player,
                                            name,
                                            mysqlNickManager.getSkinName(player.getUniqueId()),
                                            spoofedUniqueId,
                                            serverFull
                                                    ? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.Chat.Prefix")
                                                    : setupYamlFile.getConfigString(player, "Settings.NickFormat.Chat.Prefix"),
                                            serverFull
                                                    ? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.Chat.Suffix")
                                                    : setupYamlFile.getConfigString(player, "Settings.NickFormat.Chat.Suffix"),
                                            serverFull
                                                    ? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.PlayerList.Prefix")
                                                    : setupYamlFile.getConfigString(player, "Settings.NickFormat.PlayerList.Prefix"),
                                            serverFull
                                                    ? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.PlayerList.Suffix")
                                                    : setupYamlFile.getConfigString(player, "Settings.NickFormat.PlayerList.Suffix"),
                                            prefix,
                                            suffix,
                                            true,
                                            false,
                                            getOnlinePlayerCount() >= Bukkit.getMaxPlayers()
                                                    ? setupYamlFile.getConfiguration().getInt("Settings.NickFormat.ServerFullRank.SortID")
                                                    : setupYamlFile.getConfiguration().getInt("Settings.NickFormat.GrSortIDoupName"),
                                            getOnlinePlayerCount() >= Bukkit.getMaxPlayers()
                                                    ? setupYamlFile.getConfigString(player, "Settings.NickFormat.ServerFullRank.GroupName")
                                                    : setupYamlFile.getConfigString(player, "Settings.NickFormat.GroupName")
                                    )
                            );
                        }
                    } else
                        languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.CanNotNickAsSelf")
                                .replace("%prefix%", this.prefix)
                        );
                } else
                    languageYamlFile.sendMessage(
                            player,
                            languageYamlFile.getConfigString(player, "Messages.PlayerWithThisNameIsKnown")
                                    .replace("%prefix%", this.prefix)
                    );
            } else
                languageYamlFile.sendMessage(
                        player,
                        languageYamlFile.getConfigString(player, "Messages.NickNameAlreadyInUse")
                                .replace("%prefix%", this.prefix)
                );
        }
    }

    // Command 'togglebungeenick', nick item
    public void toggleBungeeNick(Player player) {
        EazyNick eazyNick = EazyNick.getInstance();
        SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
        LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
        MySQLNickManager mysqlNickManager = eazyNick.getMysqlNickManager();
        MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMysqlPlayerDataManager();

        try {
            // Check if the player has a nick item
            ItemStack itemInHand = NMS_VERSION.startsWith("1_8") || NMS_VERSION.startsWith("1_7")
                    ? (ItemStack) invoke(player, "getItemInHand")
                    : player.getInventory().getItemInMainHand();
            boolean hasItem = (itemInHand != null)
                    && (itemInHand.getType() != Material.AIR)
                    && (itemInHand.getItemMeta() != null)
                    && (itemInHand.getItemMeta().getDisplayName().equalsIgnoreCase(
                            languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Disabled"))
                            || itemInHand.getItemMeta().getDisplayName().equalsIgnoreCase(
                                    languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Enabled")));

            if(setupYamlFile.getConfiguration().getBoolean("NeedItemToToggleNick") && !hasItem)
                return;

            if(mysqlNickManager.isNicked(player.getUniqueId())) {
                // Remove the player from MySQL
                mysqlNickManager.removePlayer(player.getUniqueId());
                mysqlPlayerDataManager.removeData(player.getUniqueId());

                // Update the nick item
                if(hasItem)
                    player.getInventory().setItem(
                            player.getInventory().getHeldItemSlot(),
                            new ItemBuilder(
                                    Material.getMaterial(setupYamlFile.getConfigString("NickItem.ItemType.Disabled")),
                                    setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Disabled"),
                                    setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Disabled")
                            )
                                    .setDisplayName(languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Disabled"))
                                    .setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Disabled").split("&n"))
                                    .setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Disabled"))
                                    .build()
                    );

                languageYamlFile.sendMessage(
                        player,
                        languageYamlFile.getConfigString(player, "Messages.BungeeAutoNickDisabled").replace("%prefix%", this.prefix));
            } else {
                String name = this.nickNames.get((new Random().nextInt(this.nickNames.size())));

                // Add the player to MySQL
                mysqlNickManager.addPlayer(player.getUniqueId(), name, name);

                // Update the nick item
                if(hasItem)
                    player.getInventory().setItem(
                            player.getInventory().getHeldItemSlot(),
                            new ItemBuilder(
                                    Material.getMaterial(setupYamlFile.getConfigString("NickItem.ItemType.Enabled")),
                                    setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Enabled"),
                                    setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Enabled"))
                                    .setDisplayName(languageYamlFile.getConfigString(player, "NickItem.BungeeCord.DisplayName.Enabled"))
                                    .setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Enabled").split("&n"))
                                    .setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Enabled"))
                                    .build());

                languageYamlFile.sendMessage(
                        player,
                        languageYamlFile.getConfigString(player, "Messages.BungeeAutoNickEnabled").replace("%prefix%", this.prefix));
            }
        } catch (Exception ignore) {
        }
    }

    // Get a random string from a list
    public String getRandomStringFromList(List<String> list) {
        return list.isEmpty()
                ? ""
                : list.get(new Random().nextInt(list.size()));
    }

    // String contains characters that are not allowed in minecraft usernames (allowed: a-z, A-Z, 0-9, _)
    public boolean containsSpecialChars(String str) {
        return !new HashSet<>(ALLOWED_NAME_SYMBOLS)
                .containsAll(Chars.asList(str.toCharArray()));
    }

    public boolean containsBlackListEntry(String str) {
        String lowerCaseString = str.toLowerCase();

        return this.blackList.stream().anyMatch(currentEntry -> currentEntry.contains(lowerCaseString));
    }

    //Default game profile (Steve)
    public GameProfile getDefaultGameProfile() {
        GameProfile gameProfile = new GameProfile(getDefaultUniqueId(), getDefaultName());
        gameProfile.getProperties().put(
                "textures",
                new Property("textures", getDefaultSkinValue(), getDefaultSkinSignature())
        );

        return gameProfile;
    }

    //Default 1.7 game profile ("Steve")
    public net.minecraft.util.com.mojang.authlib.GameProfile getDefaultGameProfile_1_7() {
        net.minecraft.util.com.mojang.authlib.GameProfile gameProfile =
                new net.minecraft.util.com.mojang.authlib.GameProfile(getDefaultUniqueId(), getDefaultName());
        gameProfile.getProperties().put(
                "textures",
                new net.minecraft.util.com.mojang.authlib.properties.Property(
                        "textures",
                        getDefaultSkinValue(),
                        getDefaultSkinSignature())
        );

        return gameProfile;
    }

    //"Steve" unique id
    public UUID getDefaultUniqueId() {
        return UUID.fromString("8667ba71-b85a-4004-af54-457a9734eed7");
    }

    //"Steve" name
    public String getDefaultName() {
        return "Steve";
    }

    //"Steve" skin value
    public String getDefaultSkinValue() {
        return "ewogICJ0aW1lc3RhbXAiIDogMTU4OTU2NzM1NzQyMSwKICAicHJvZmlsZUlkIiA6ICI5MzRiMmFhOGEyODQ0Yzc3ODg2NDhiNDBiY2IzYjAzMSIsCiAgInByb2ZpbGVOYW1lIiA6ICI0Z2wiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQzYjA2YzM4NTA0ZmZjMDIyOWI5NDkyMTQ3YzY5ZmNmNTlmZDJlZDc4ODVmNzg1MDIxNTJmNzdiNGQ1MGRlMSIKICAgIH0KICB9Cn0=";
    }

    //"Steve" skin signature
    public String getDefaultSkinSignature() {
        return "WMx2gG2+sM8D4qLHAjiiEWMNwxR6hT0H1uhqoiM1g2IJuM9dODDpgQOEEn7+9K1GxHr45Y7NQ4s9tFk7a5M1BL+IpLUNuZ3PIH2qAuoqVvaYrcYX05e9SBTuHLJCuSo+RjDqyT6AWkE4nYpD6vTLoOQS8Ku+ZXyPdFh2ALW95zQPHh2ZXrlaY+Ktdwf2TEq0vqr8agxzhDaksBxQBgbntu5VS4Z9yJ2hTeftZZALadewYegDI7Dkf/9yWr+6PEcCczhCyrf4xhHzBOjBLMDjg6ZRQhGGscv6dLP7hdqbIRPwvZ/tH0NW1GE1UWqof5TspCmlHNI592djxo2MqDhA0LrT4jbQfsnfZze0urQwMQG1V3fDDrf8kfZdD+7H29UmFAaTvfqMkwqKalPExm75oqeFole4qzxifl1Rv1i3bJy8ZZlgixZzxhl3idDIP5IkPnQHt9YFOEpOQLWtJV8dTixCP5TvYVQRtXkgFtzIUljDNkrqUmqlkeXPlZR27lEykuLIGPrV4U/bXanNVpHKcCMsD7vzFC1wu1XS0JWozN8SFZdwmVTCFpmBgqeKHBPbIuTqdOF0+YZ7xoxc7W869vgFJSaJ7jdlcTsHFt+AQcWUxlSqoV1n1kyQ7hF/zcjoi2YtAMy9XGh1IODS+UPl/edqs7Sq+fA054/ivaqzeh4=";
    }

}
