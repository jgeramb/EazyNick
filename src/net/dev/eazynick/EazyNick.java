package net.dev.eazynick;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.dev.eazynick.commands.*;
import net.dev.eazynick.hooks.DeluxeChatListener;
import net.dev.eazynick.hooks.PlaceHolderExpansion;
import net.dev.eazynick.listeners.*;
import net.dev.eazynick.nms.ReflectionHelper;
import net.dev.eazynick.nms.fakegui.book.NMSBookBuilder;
import net.dev.eazynick.nms.fakegui.book.NMSBookUtils;
import net.dev.eazynick.nms.fakegui.sign.SignGUI;
import net.dev.eazynick.nms.netty.PacketInjector;
import net.dev.eazynick.nms.netty.PacketInjector_1_7;
import net.dev.eazynick.sql.*;
import net.dev.eazynick.updater.SpigotUpdater;
import net.dev.eazynick.utilities.*;
import net.dev.eazynick.utilities.AsyncTask.AsyncRunnable;
import net.dev.eazynick.utilities.configuration.yaml.*;
import net.dev.eazynick.utilities.mojang.*;

public class EazyNick extends JavaPlugin {

	private static EazyNick instance;
	
	public static EazyNick getInstance() {
		return instance;
	}
	
	private File pluginFile;
	private String version = "XX_XX_RXX";
	private boolean isCancelled;

	private MySQL mysql;
	private MySQLNickManager mysqlNickManager;
	private MySQLPlayerDataManager mysqlPlayerDataManager;
	
	private SpigotUpdater spigotUpdater;
	private Utils utils;
	private GUIManager guiManager;
	private ActionBarUtils actionBarUtils;
	private SetupYamlFile setupYamlFile;
	private NickNameYamlFile nickNameYamlFile;
	private GUIYamlFile guiYamlFile;
	private LanguageYamlFile languageYamlFile;
	private ReflectionHelper reflectionHelper;
	private NMSBookBuilder nmsBookBuilder;
	private NMSBookUtils nmsBookUtils;
	private GameProfileBuilder_1_7 gameProfileBuilder_1_7;
	private GameProfileBuilder_1_8_R1 gameProfileBuilder_1_8_R1;
	private GameProfileBuilder gameProfileBuilder;
	private UUIDFetcher_1_7 uuidFetcher_1_7;
	private UUIDFetcher_1_8_R1 uuidFetcher_1_8_R1;
	private UUIDFetcher uuidFetcher;
	private SignGUI signGUI;
	private MineSkinAPI mineSkinAPI;
	
	@Override
	public void onEnable() {
		instance = this;
		
		reflectionHelper = new ReflectionHelper();
		
		version = reflectionHelper.getVersion().substring(1);
		pluginFile = getFile();
		
		utils = new Utils();
		guiManager = new GUIManager();
		actionBarUtils = new ActionBarUtils();
		setupYamlFile = new SetupYamlFileFactory().createConfigurationFile(this);
		nickNameYamlFile = new NickNameYamlFileFactory().createConfigurationFile(this);
		guiYamlFile = new GUIYamlFileFactory().createConfigurationFile(this);
		spigotUpdater = new SpigotUpdater();
		signGUI = new SignGUI();
		nmsBookBuilder = new NMSBookBuilder();
		nmsBookUtils = new NMSBookUtils();
		mineSkinAPI = new MineSkinAPI();
		
		if(utils.essentialsStatus())
			Bukkit.getScheduler().runTaskLater(this, () -> initiatePlugin(), 20);
		else
			initiatePlugin();
	}

	@Override
	public void onDisable() {
		utils.getNickedPlayers().keySet().forEach(uuid -> {
			Player player = Bukkit.getPlayer(uuid);
			
			if(utils.getScoreboardTeamManagers().containsKey(uuid))
				utils.getScoreboardTeamManagers().get(uuid).destroyTeam();
			
			player.kickPlayer("§cYou will need to reconnect in order to be able to play properly");
		});
		
		if (setupYamlFile.getConfiguration().getBoolean("BungeeCord"))
			mysql.disconnect();
		
		utils.sendConsole("§7========== §8[ §5§lEazyNick §8] §7==========");
		utils.sendConsole("");
		utils.sendConsole("§7Plugin by§8: §3" + getDescription().getAuthors().toString().replace("[", "").replace("]", ""));
		utils.sendConsole("§7Version§8: §3" + getDescription().getVersion());
		utils.sendConsole("");
		utils.sendConsole("§7========== §8[ §5§lEazyNick §8] §7==========");
	}
	
	private void initiatePlugin() {
		PluginManager pluginManager = Bukkit.getPluginManager();

		languageYamlFile = new LanguageYamlFileFactory(setupYamlFile.getConfiguration().getString("Language")).createConfigurationFile(this);
		
		utils.reloadConfigs();
		utils.sendConsole("§7========== §8[ §5§lEazyNick §8] §7==========");
		utils.sendConsole("");

		if (!(version.equals("1_7_R4") || version.equals("1_8_R1") || version.equals("1_8_R2")
				|| version.equals("1_8_R3") || version.equals("1_9_R1") || version.equals("1_9_R2")
				|| version.equals("1_10_R1") || version.equals("1_11_R1") || version.equals("1_12_R1")
				|| version.equals("1_13_R1") || version.equals("1_13_R2") || version.equals("1_14_R1")
				|| version.equals("1_15_R1") || version.equals("1_16_R1") || version.equals("1_16_R2")
				|| version.equals("1_16_R3"))) {
			utils.sendConsole("§cERROR§8: §eVersion is §4§lINCOMPATIBLE§e!");

			isCancelled = true;
		} else {
			if (version.equals("1_7_R4")) {
				gameProfileBuilder_1_7 = new GameProfileBuilder_1_7();
				uuidFetcher_1_7 = new UUIDFetcher_1_7();
			} else {
				if(version.equals("1_8_R1")) {
					gameProfileBuilder_1_8_R1 = new GameProfileBuilder_1_8_R1();
					uuidFetcher_1_8_R1 = new UUIDFetcher_1_8_R1();
				} else {
					gameProfileBuilder = new GameProfileBuilder();
					uuidFetcher = new UUIDFetcher();
				}
			}

			if(version.equals("1_7_R4"))
				new PacketInjector_1_7().init();
			else
				new PacketInjector().init();
			
			if (!(setupYamlFile.getConfiguration().getBoolean("APIMode"))) {
				getCommand("eazynick").setExecutor(new PluginCommand());
				getCommand("nickother").setExecutor(new NickOtherCommand());
				getCommand("changeskin").setExecutor(new ChangeSkinCommand());
				getCommand("nicklist").setExecutor(new NickListCommand());
				getCommand("nick").setExecutor(new NickCommand());
				getCommand("unnick").setExecutor(new UnnickCommand());
				getCommand("name").setExecutor(new NameCommand());
				getCommand("reloadconfig").setExecutor(new ReloadConfigCommand());
				getCommand("fixskin").setExecutor(new FixSkinCommand());
				getCommand("resetskin").setExecutor(new ResetSkinCommand());
				getCommand("resetname").setExecutor(new ResetNameCommand());
				getCommand("renick").setExecutor(new ReNickCommand());
				getCommand("nickedplayers").setExecutor(new NickedPlayersCommand());
				getCommand("nickupdatecheck").setExecutor(new NickUpdateCheckCommand());
				getCommand("togglebungeenick").setExecutor(new ToggleBungeeNickCommand());
				getCommand("realname").setExecutor(new RealNameCommand());
				getCommand("nickgui").setExecutor(setupYamlFile.getConfiguration().getBoolean("OpenRankedNickGUIOnNickGUICommand") ? new RankedNickGUICommand() : new NickGUICommand());
				getCommand("guinick").setExecutor(new GuiNickCommand());
				getCommand("bookgui").setExecutor(version.startsWith("1_7") ? new RankedNickGUICommand() : new BookGUICommand());
				
				pluginManager.registerEvents(new PlayerNickListener(), this);
				pluginManager.registerEvents(new PlayerUnnickListener(), this);
				
				pluginManager.registerEvents(new AsyncPlayerChatListener(), this);
				
				if(!(version.startsWith("1_13") || version.startsWith("1_14") || version.startsWith("1_15") || version.startsWith("1_16")))
					pluginManager.registerEvents(new PlayerChatTabCompleteListener(), this);
				
				if(!(version.startsWith("1_7") || version.startsWith("1_8") || version.startsWith("1_9")))
					pluginManager.registerEvents(new TabCompleteListener(), this);
					
				pluginManager.registerEvents(new PlayerCommandPreprocessListener(), this);
				pluginManager.registerEvents(new PlayerDropItemListener(), this);
				pluginManager.registerEvents(new InventoryClickListener(), this);
				pluginManager.registerEvents(new InventoryCloseListener(), this);
				pluginManager.registerEvents(new PlayerInteractListener(), this);
				pluginManager.registerEvents(new PlayerChangedWorldListener(), this);
				pluginManager.registerEvents(new PlayerDeathListener(), this);
				pluginManager.registerEvents(new PlayerRespawnListener(), this);
				pluginManager.registerEvents(new PlayerJoinListener(), this);
				pluginManager.registerEvents(new PlayerKickListener(), this);
				pluginManager.registerEvents(new PlayerQuitListener(), this);

				for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
					if ((currentPlayer != null) && (currentPlayer.getUniqueId() != null)) {
						if (!(utils.getCanUseNick().containsKey(currentPlayer.getUniqueId())))
							utils.getCanUseNick().put(currentPlayer.getUniqueId(), true);
					}
				}
				
				if(setupYamlFile.getConfiguration().getBoolean("NickActionBarMessage") && setupYamlFile.getConfiguration().getBoolean("ShowNickActionBarWhenMySQLNicked")) {
					new AsyncTask(new AsyncRunnable() {
						
						@Override
						public void run() {
							Bukkit.getOnlinePlayers().stream().filter(currentPlayer -> (mysqlNickManager.isPlayerNicked(currentPlayer.getUniqueId()) && !(utils.getNickedPlayers().containsKey(currentPlayer.getUniqueId())))).forEach(currentNickedPlayer -> {
								String nickName = mysqlNickManager.getNickName(currentNickedPlayer.getUniqueId()), prefix = mysqlPlayerDataManager.getChatPrefix(currentNickedPlayer.getUniqueId()), suffix = mysqlPlayerDataManager.getChatSuffix(currentNickedPlayer.getUniqueId());
								
								if(!(utils.getWorldsWithDisabledActionBar().contains(currentNickedPlayer.getWorld().getName().toUpperCase())))
									actionBarUtils.sendActionBar(currentNickedPlayer, languageYamlFile.getConfigString(currentNickedPlayer, currentNickedPlayer.hasPermission("nick.otheractionbarmessage") ? "NickActionBarMessageOther" : "NickActionBarMessage").replace("%nickName%", nickName).replace("%nickname%", nickName).replace("%nickPrefix%", prefix).replace("%nickprefix%", prefix).replace("%nickSuffix%", suffix).replace("%nicksuffix%", suffix).replace("%prefix%", utils.getPrefix()));
							});
						}
					}, 0, 1000).run();
				}
				
				if(utils.tabStatus())
					me.neznamy.tab.shared.config.Configs.unlimited_nametag_mode_not_enabled = "false";
			}
			
			pluginManager.registerEvents(new WorldInitListener(), this);
			pluginManager.registerEvents(new ServerListPingListener(), this);
			
			utils.sendConsole("§7Version §e" + version + " §7was loaded §asuccessfully§7!");

			if (setupYamlFile.getConfiguration().getBoolean("BungeeCord")) {
				mysql = new MySQL(setupYamlFile.getConfiguration().getString("BungeeMySQL.hostname"), setupYamlFile.getConfiguration().getString("BungeeMySQL.port"), setupYamlFile.getConfiguration().getString("BungeeMySQL.database"), setupYamlFile.getConfiguration().getString("BungeeMySQL.username"), setupYamlFile.getConfiguration().getString("BungeeMySQL.password"));
				mysql.connect();

				mysql.update("CREATE TABLE IF NOT EXISTS NickedPlayers (UUID varchar(64), NickName varchar(64), SkinName varchar(64))");
				mysql.update("CREATE TABLE IF NOT EXISTS NickedPlayerDatas (UUID varchar(64), GroupName varchar(64), ChatPrefix varchar(64), ChatSuffix varchar(64), TabPrefix varchar(64), TabSuffix varchar(64), TagPrefix varchar(64), TagSuffix varchar(64))");

				mysqlNickManager = new MySQLNickManager(mysql);
				mysqlPlayerDataManager = new MySQLPlayerDataManager(mysql);
				
				try {
					File file = new File("spigot.yml");
					
					if(!(file.exists()))
						file.createNewFile();
					
					YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
					configuration.set("settings.bungeecord", true);
					configuration.save(file);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			
				Bukkit.spigot().getConfig().set("settings.bungeecord", true);
			}
			
			utils.sendConsole("");
			utils.sendConsole("§7Plugin by§8: §3" + getDescription().getAuthors().toString().replace("[", "").replace("]", ""));
			utils.sendConsole("§7Version§8: §3" + getDescription().getVersion());
		}

		utils.sendConsole("");
		utils.sendConsole("§7========== §8[ §5§lEazyNick §8] §7==========");

		if (spigotUpdater.checkForUpdates())
			isCancelled = true;

		if (isCancelled) {
			pluginManager.disablePlugin(this);
			return;
		}
		
		if(utils.placeholderAPIStatus()) {
			new PlaceHolderExpansion(this).register();
			
			utils.sendConsole("§7Placeholders loaded successfully!");
		}
		
		if(utils.deluxeChatStatus()) {
			pluginManager.registerEvents(new DeluxeChatListener(), this);
			
			utils.sendConsole("§7DeluxeChat hooked successfully!");
		}
	}
	
	public File getPluginFile() {
		return pluginFile;
	}
	
	public MySQL getMySQL() {
		return mysql;
	}
	
	public MySQLNickManager getMySQLNickManager() {
		return mysqlNickManager;
	}
	
	public MySQLPlayerDataManager getMySQLPlayerDataManager() {
		return mysqlPlayerDataManager;
	}
	
	public String getVersion() {
		return version;
	}
	
	public SpigotUpdater getSpigotUpdater() {
		return spigotUpdater;
	}
	
	public Utils getUtils() {
		return utils;
	}
	
	public GUIManager getGUIManager() {
		return guiManager;
	}
	
	public ActionBarUtils getActionBarUtils() {
		return actionBarUtils;
	}
	
	public SetupYamlFile getSetupYamlFile() {
		return setupYamlFile;
	}
	
	public NickNameYamlFile getNickNameYamlFile() {
		return nickNameYamlFile;
	}
	
	public GUIYamlFile getGUIYamlFile() {
		return guiYamlFile;
	}
	
	public LanguageYamlFile getLanguageYamlFile() {
		return languageYamlFile;
	}
	
	public ReflectionHelper getReflectUtils() {
		return reflectionHelper;
	}
	
	public NMSBookBuilder getNMSBookBuilder() {
		return nmsBookBuilder;
	}
	
	public NMSBookUtils getNMSBookUtils() {
		return nmsBookUtils;
	}
	
	public GameProfileBuilder_1_7 getGameProfileBuilder_1_7() {
		return gameProfileBuilder_1_7;
	}
	
	public GameProfileBuilder_1_8_R1 getGameProfileBuilder_1_8_R1() {
		return gameProfileBuilder_1_8_R1;
	}
	
	public GameProfileBuilder getGameProfileBuilder() {
		return gameProfileBuilder;
	}
	
	public UUIDFetcher_1_7 getUUIDFetcher_1_7() {
		return uuidFetcher_1_7;
	}
	
	public UUIDFetcher_1_8_R1 getUUIDFetcher_1_8_R1() {
		return uuidFetcher_1_8_R1;
	}
	
	public UUIDFetcher getUUIDFetcher() {
		return uuidFetcher;
	}
	
	public SignGUI getSignGUI() {
		return signGUI;
	}
	
	public MineSkinAPI getMineSkinAPI() {
		return mineSkinAPI;
	}

}
