package com.justixdev.eazynick;

import com.justixdev.eazynick.commands.CommandManager;
import com.justixdev.eazynick.hooks.PlaceHolderExpansion;
import com.justixdev.eazynick.listeners.*;
import com.justixdev.eazynick.nms.ScoreboardTeamHandler;
import com.justixdev.eazynick.nms.guis.SignGUI;
import com.justixdev.eazynick.nms.guis.book.NMSBookUtils;
import com.justixdev.eazynick.nms.netty.PacketInjectorManager;
import com.justixdev.eazynick.sql.MySQL;
import com.justixdev.eazynick.sql.MySQLNickManager;
import com.justixdev.eazynick.sql.MySQLPlayerDataManager;
import com.justixdev.eazynick.updater.Updater;
import com.justixdev.eazynick.utilities.*;
import com.justixdev.eazynick.utilities.configuration.BaseFileFactory;
import com.justixdev.eazynick.utilities.configuration.YamlFileFactory;
import com.justixdev.eazynick.utilities.configuration.yaml.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

import static com.justixdev.eazynick.nms.ReflectionHelper.NMS_VERSION;
import static com.justixdev.eazynick.nms.ReflectionHelper.getFieldValue;

public class EazyNick extends JavaPlugin {

    @Getter
    private static EazyNick instance;

    @Getter
    private ClassLoader pluginClassLoader;

    @Getter
    private MySQL mysql;
    @Getter
    private MySQLNickManager mysqlNickManager;
    @Getter
    private MySQLPlayerDataManager mysqlPlayerDataManager;

    @Getter
    private Updater updater;
    @Getter
    private Utils utils;
    @Getter
    private GUIManager guiManager;
    @Getter
    private ActionBarUtils actionBarUtils;
    @Getter
    private SetupYamlFile setupYamlFile;
    @Getter
    private NickNameYamlFile nickNameYamlFile;
    @Getter
    private SavedNickDataYamlFile savedNickDataYamlFile;
    @Getter
    private GUIYamlFile guiYamlFile;
    @Getter
    private LanguageYamlFile languageYamlFile;
    @Getter
    private NMSBookUtils nmsBookUtils;
    @Getter
    private SignGUI signGUI;
    @Getter
    private MineSkinAPI mineSkinAPI;
    @Getter
    private PacketInjectorManager packetInjectorManager;
    @Getter
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        instance = this;

        this.pluginClassLoader = this.getClassLoader();

        // Initialize class instances
        this.utils = new Utils();
        this.actionBarUtils = new ActionBarUtils();

        final BaseFileFactory<YamlConfiguration> configurationFactory = new YamlFileFactory();
        this.setupYamlFile = configurationFactory.createConfigurationFile(this, SetupYamlFile.class);
        this.nickNameYamlFile = configurationFactory.createConfigurationFile(this, NickNameYamlFile.class);
        this.savedNickDataYamlFile = configurationFactory.createConfigurationFile(this, SavedNickDataYamlFile.class);
        this.guiYamlFile = configurationFactory.createConfigurationFile(this, GUIYamlFile.class);
        this.languageYamlFile = configurationFactory.createConfigurationFile(this, LanguageYamlFile.class);

        this.updater = new Updater(this, this.getFile());
        this.mineSkinAPI = new MineSkinAPI();

        this.packetInjectorManager = new PacketInjectorManager();

        this.signGUI = new SignGUI(this);
        this.nmsBookUtils = new NMSBookUtils(this);
        this.guiManager = new GUIManager(this);

        Runnable initiatePlugin = () -> {
            PluginManager pluginManager = Bukkit.getPluginManager();

            this.utils.reloadConfigs();

            int versionNumber = Integer.parseInt(NMS_VERSION.substring(1).split("_")[1]);

            // Check if bukkit version is compatible
            if ((versionNumber < 7) || (versionNumber > 19)) {
                this.utils.sendConsole("§cYour server version is currently not supported§7.");

                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }

            // Check for plugin updates
            if (updater.checkForUpdates() && setupYamlFile.getConfiguration().getBoolean("AutoUpdater")) {
                Bukkit.getScheduler().runTask(instance, () -> pluginManager.disablePlugin(instance));
                return;
            }

            new AsyncTask(new AsyncTask.AsyncRunnable() {

                @Override
                public void run() {
                    // Set up packet injectors
                    Bukkit.getOnlinePlayers().forEach(packetInjectorManager::inject);

                    // Cache loaded plugins
                    Stream.of(Bukkit.getPluginManager().getPlugins())
                            .filter(Plugin::isEnabled)
                            .forEach(currentPlugin -> utils.getLoadedPlugins().put(currentPlugin.getName(), currentPlugin.getDescription().getAuthors()));

                    Bukkit.getScheduler().runTask(instance, () -> {
                        // Prepare PlaceholderAPI placeholders
                        if(utils.isPluginInstalled("PlaceholderAPI"))
                            new PlaceHolderExpansion(instance).register();

                        if(utils.isPluginInstalled("SkinsRestorer")
                                && setupYamlFile.getConfiguration().getBoolean("ChangeSkinsRestorerSkin")) {
                            Plugin skinsRestorer = Bukkit.getPluginManager().getPlugin("SkinsRestorer");

                            if ((boolean) getFieldValue(Objects.requireNonNull(skinsRestorer), "proxyMode"))
                                Bukkit.getMessenger().registerOutgoingPluginChannel(instance, "sr:messagechannel");
                        }
                    });
                }
            }, 50).run();

            // Check if plugin features should be enabled -> APIMode: false
            if (!this.setupYamlFile.getConfiguration().getBoolean("APIMode")) {
                // Register listeners for plugin events
                pluginManager.registerEvents(new PlayerNickListener(), this);
                pluginManager.registerEvents(new PlayerUnnickListener(), this);

                // Register other event listeners
                pluginManager.registerEvents(new AsyncPlayerChatListener(), this);
                pluginManager.registerEvents(new PlayerCommandPreprocessListener(), this);
                pluginManager.registerEvents(new PlayerDropItemListener(), this);
                pluginManager.registerEvents(new InventoryClickListener(), this);
                pluginManager.registerEvents(new InventoryCloseListener(), this);
                pluginManager.registerEvents(new PlayerInteractListener(), this);
                pluginManager.registerEvents(new PlayerChangedWorldListener(), this);
                pluginManager.registerEvents(new PlayerDeathListener(), this);
                pluginManager.registerEvents(new PlayerRespawnListener(), this);
                pluginManager.registerEvents(new PlayerLoginListener(), this);
                pluginManager.registerEvents(new ServerCommandListener(), this);

                // Register all plugin commands
                this.commandManager = new CommandManager();

                // Start action bar scheduler
                if(this.setupYamlFile.getConfiguration().getBoolean("NickActionBarMessage")
                        && this.setupYamlFile.getConfiguration().getBoolean("ShowNickActionBarWhenMySQLNicked")
                        && this.setupYamlFile.getConfiguration().getBoolean("BungeeCord")
                        && this.setupYamlFile.getConfiguration().getBoolean("LobbyMode")) {
                    new AsyncTask(new AsyncTask.AsyncRunnable() {

                        @Override
                        public void run() {
                            // Display action bar to players that are nicked in mysql
                            Bukkit.getOnlinePlayers()
                                    .stream()
                                    .filter(currentPlayer -> (
                                            mysqlNickManager.isNicked(currentPlayer.getUniqueId())
                                                    && !utils.getNickedPlayers().containsKey(currentPlayer.getUniqueId())))
                                    .forEach(currentNickedPlayer -> {
                                        String nickName = mysqlNickManager.getNickName(currentNickedPlayer.getUniqueId()),
                                                prefix = mysqlPlayerDataManager.getChatPrefix(currentNickedPlayer.getUniqueId()),
                                                suffix = mysqlPlayerDataManager.getChatSuffix(currentNickedPlayer.getUniqueId());

                                        if(utils.getWorldsWithDisabledActionBar()
                                                .stream()
                                                .noneMatch(world -> world.equalsIgnoreCase(currentNickedPlayer.getWorld().getName())))
                                            actionBarUtils.sendActionBar(
                                                    currentNickedPlayer,
                                                    languageYamlFile.getConfigString(currentNickedPlayer,
                                                            currentNickedPlayer.hasPermission("eazynick.actionbar.other")
                                                                    ? "NickActionBarMessageOther"
                                                                    : "NickActionBarMessage")
                                                            .replace("%nickName%", nickName)
                                                            .replace("%nickname%", nickName)
                                                            .replace("%nickPrefix%", prefix)
                                                            .replace("%nickprefix%", prefix)
                                                            .replace("%nickSuffix%", suffix)
                                                            .replace("%nicksuffix%", suffix)
                                                            .replace("%prefix%", utils.getPrefix()));
                                    });
                        }
                    }, 1000, 1000).run();
                }
            }

            // Register important event listeners
            pluginManager.registerEvents(new PlayerJoinListener(), this);
            pluginManager.registerEvents(new PlayerKickListener(), this);
            pluginManager.registerEvents(new PlayerQuitListener(), this);
            pluginManager.registerEvents(new ServerListPingListener(), this);

            // Prepare BungeeCord/MySQL mode
            if (this.setupYamlFile.getConfiguration().getBoolean("BungeeCord")) {
                // Open mysql connection
                this.mysql = new MySQL(
                        this.setupYamlFile.getConfiguration().getString("BungeeMySQL.hostname"),
                        this.setupYamlFile.getConfiguration().getString("BungeeMySQL.port"),
                        this.setupYamlFile.getConfiguration().getString("BungeeMySQL.database"),
                        this.setupYamlFile.getConfiguration().getString("BungeeMySQL.username"),
                        this.setupYamlFile.getConfiguration().getString("BungeeMySQL.password"));
                this.mysql.connect();

                // Create default tables
                this.mysql.update("CREATE TABLE IF NOT EXISTS NickedPlayers (unique_id varchar(36), nickname varchar(32), skin_name varchar(32))");
                this.mysql.update("CREATE TABLE IF NOT EXISTS NickedPlayerData (unique_id varchar(36), group varchar(64), chat_prefix varchar(64), chat_suffix varchar(64), tab_prefix varchar(64), tab_suffix varchar(64), tag_prefix varchar(64), tag_suffix varchar(64))");

                // Initialize mysql managers
                this.mysqlNickManager = new MySQLNickManager(this.mysql);
                this.mysqlPlayerDataManager = new MySQLPlayerDataManager(this.mysql);
            }

            // Initialize bStats
            BStatsMetrics bStatsMetrics = new BStatsMetrics(this, 11663);
            bStatsMetrics.addCustomChart(new BStatsMetrics.SimplePie(
                    "mysql",
                    () -> this.setupYamlFile.getConfiguration().getBoolean("BungeeCord")
                            ? "yes"
                            : "no"
            ));

            this.printLogo();
        };

        // Fix essentials '/nick' command bug
        if(this.utils.isPluginInstalled("Essentials"))
            Bukkit.getScheduler().runTaskLater(this, initiatePlugin, 20 * 3);
        else
            initiatePlugin.run();
    }

    @Override
    public void onDisable() {
        // Save nicked players
        this.savedNickDataYamlFile.save();

        // Kick all nicked players
        this.utils.getNickedPlayers()
                .keySet()
                .stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(currentPlayer -> currentPlayer.kickPlayer("§cYou must reconnect to be able to play properly§7."));

        this.utils.getScoreboardTeamHandlers().values().forEach(ScoreboardTeamHandler::destroyTeam);
        this.packetInjectorManager.removeAll();

        // Disconnect MySQL
        if (this.setupYamlFile.getConfiguration().getBoolean("BungeeCord"))
            this.mysql.disconnect();

        this.printLogo();
    }

    private void printLogo() {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        console.sendMessage("");

        String version = this.getDescription().getVersion();
        String[] logoLines =
                (
                        "§5 ___              _  _ _    _   \n" +
                        "§5| __|__ _ ____  _| \\| (_)__| |__\n" +
                        "§5| _|/ _` |_ / || | .` | / _| / /\n" +
                        "§5|___\\__,_/__|\\_, |_|\\_|_\\__|_\\_\\\n" +
                        "            §5|__/       " + new StringUtils(" ").repeat(8 - version.length()) + "§8v§7" + version + "\n"
                ).split("\n");
        int logoLength = Stream.of(logoLines)
                .max(Comparator.comparingInt(String::length))
                .map(String::length)
                .orElse(0);

        for (String line : logoLines)
            console.sendMessage("§5" + line + new StringUtils(" ").repeat(logoLength - line.length()));

        console.sendMessage("");
    }

}
