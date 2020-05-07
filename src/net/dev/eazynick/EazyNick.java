package net.dev.eazynick;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.mojang.authlib.GameProfile;

import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.commands.BookGUICommand;
import net.dev.eazynick.commands.BookNickCommand;
import net.dev.eazynick.commands.ChangeSkinCommand;
import net.dev.eazynick.commands.CommandNotAvaiableCommand;
import net.dev.eazynick.commands.FixSkinCommand;
import net.dev.eazynick.commands.HelpCommand;
import net.dev.eazynick.commands.NameCommand;
import net.dev.eazynick.commands.NickCommand;
import net.dev.eazynick.commands.NickGuiCommand;
import net.dev.eazynick.commands.NickListCommand;
import net.dev.eazynick.commands.NickOtherCommand;
import net.dev.eazynick.commands.NickUpdateCheckCommand;
import net.dev.eazynick.commands.NickedPlayersCommand;
import net.dev.eazynick.commands.ReNickCommand;
import net.dev.eazynick.commands.RealNameCommand;
import net.dev.eazynick.commands.ReloadConfigCommand;
import net.dev.eazynick.commands.ResetNameCommand;
import net.dev.eazynick.commands.ResetSkinCommand;
import net.dev.eazynick.commands.ToggleBungeeNickCommand;
import net.dev.eazynick.commands.UnnickCommand;
import net.dev.eazynick.hooks.DeluxeChatListener;
import net.dev.eazynick.hooks.PlaceHolderExpansion;
import net.dev.eazynick.listeners.AsyncPlayerChatListener;
import net.dev.eazynick.listeners.InventoryClickListener;
import net.dev.eazynick.listeners.PlayerChangedWorldListener;
import net.dev.eazynick.listeners.PlayerCommandPreprocessListener;
import net.dev.eazynick.listeners.PlayerDeathListener;
import net.dev.eazynick.listeners.PlayerDropItemListener;
import net.dev.eazynick.listeners.PlayerInteractListener;
import net.dev.eazynick.listeners.PlayerJoinListener;
import net.dev.eazynick.listeners.PlayerKickListener;
import net.dev.eazynick.listeners.PlayerNickListener;
import net.dev.eazynick.listeners.PlayerQuitListener;
import net.dev.eazynick.listeners.PlayerUnnickListener;
import net.dev.eazynick.sql.MySQL;
import net.dev.eazynick.sql.MySQLNickManager;
import net.dev.eazynick.sql.MySQLPlayerDataManager;
import net.dev.eazynick.updater.SpigotUpdater;
import net.dev.eazynick.utils.ActionBarUtils;
import net.dev.eazynick.utils.BookGUIFileUtils;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.LanguageFileUtils;
import net.dev.eazynick.utils.MineSkinAPI;
import net.dev.eazynick.utils.NickNameFileUtils;
import net.dev.eazynick.utils.PacketInjector;
import net.dev.eazynick.utils.PacketInjector_1_7;
import net.dev.eazynick.utils.ReflectUtils;
import net.dev.eazynick.utils.Utils;
import net.dev.eazynick.utils.bookutils.NMSBookBuilder;
import net.dev.eazynick.utils.bookutils.NMSBookUtils;
import net.dev.eazynick.utils.nickutils.GameProfileBuilder;
import net.dev.eazynick.utils.nickutils.GameProfileBuilder_1_7;
import net.dev.eazynick.utils.nickutils.GameProfileBuilder_1_8_R1;
import net.dev.eazynick.utils.nickutils.NMSNickManager;
import net.dev.eazynick.utils.nickutils.UUIDFetcher;
import net.dev.eazynick.utils.nickutils.UUIDFetcher_1_7;
import net.dev.eazynick.utils.nickutils.UUIDFetcher_1_8_R1;
import net.dev.eazynick.utils.signutils.SignGUI;

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
	private ActionBarUtils actionBarUtils;
	private FileUtils fileUtils;
	private NickNameFileUtils nickNameFileUtils;
	private BookGUIFileUtils bookGUIFileUtils;
	private LanguageFileUtils languageFileUtils;
	private ReflectUtils reflectUtils;
	private NMSBookBuilder nmsBookBuilder;
	private NMSBookUtils nmsBookUtils;
	private GameProfileBuilder_1_7 gameProfileBuilder_1_7;
	private GameProfileBuilder_1_8_R1 gameProfileBuilder_1_8_R1;
	private GameProfileBuilder gameProfileBuilder;
	private NMSNickManager nmsNickManager;
	private UUIDFetcher_1_7 uuidFetcher_1_7;
	private UUIDFetcher_1_8_R1 uuidFetcher_1_8_R1;
	private UUIDFetcher uuidFetcher;
	private SignGUI signGUI;
	private MineSkinAPI mineSkinAPI;
	
	@Override
	public void onEnable() {
		instance = this;
		
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			
			@Override
			public void run() {
				long startMillis = System.currentTimeMillis();

				PluginManager pm = Bukkit.getPluginManager();
				
				pluginFile = getFile();

				utils = new Utils();
				actionBarUtils = new ActionBarUtils();
				fileUtils = new FileUtils();
				nickNameFileUtils = new NickNameFileUtils();
				bookGUIFileUtils = new BookGUIFileUtils();
				spigotUpdater = new SpigotUpdater();
				reflectUtils = new ReflectUtils();
				signGUI = new SignGUI();
				nmsBookBuilder = new NMSBookBuilder();
				nmsBookUtils = new NMSBookUtils();
				mineSkinAPI = new MineSkinAPI();
				nmsNickManager = new NMSNickManager();
				
				utils.reloadConfigs();
				
				utils.sendConsole("§7========== §8[ §5§lNickSystem §8] §7==========");
				utils.sendConsole("	§7Starting system...");
				utils.sendConsole("	§7");
				utils.sendConsole("	§7Scanning active §3Minecraft-Version §7...");

				String reflectVersion = reflectUtils.getVersion();
				
				if (!(reflectVersion.equals("v1_7_R4") || reflectVersion.equals("v1_8_R1")
						|| reflectVersion.equals("v1_8_R2") || reflectVersion.equals("v1_8_R3")
						|| reflectVersion.equals("v1_9_R1") || reflectVersion.equals("v1_9_R2")
						|| reflectVersion.equals("v1_10_R1") || reflectVersion.equals("v1_11_R1")
						|| reflectVersion.equals("v1_12_R1") || reflectVersion.equals("v1_13_R1")
						|| reflectVersion.equals("v1_13_R2") || reflectVersion.equals("v1_14_R1")
						|| reflectVersion.equals("v1_15_R1"))) {
					utils.sendConsole("	§cERROR§8: §eVersion is §4§lINCOMPATIBLE§e!");
					utils.sendConsole("	§7");
					utils.sendConsole("	§7Plugin by§8: §3" + getDescription().getAuthors().toString().replace("[", "").replace("]", ""));
					utils.sendConsole("	§7Version§8: §3" + getDescription().getVersion());
					utils.sendConsole("	§7Plugin-State§8: §cCANCELLED");

					isCancelled = true;
				} else {
					if (reflectVersion.equals("v1_7_R4")) {
						utils.setNameField(reflectUtils.getField(net.minecraft.util.com.mojang.authlib.GameProfile.class, "name"));
						utils.setUUIDField(reflectUtils.getField(net.minecraft.util.com.mojang.authlib.GameProfile.class, "id"));
						
						gameProfileBuilder_1_7 = new GameProfileBuilder_1_7();
						uuidFetcher_1_7 = new UUIDFetcher_1_7();
					} else {
						utils.setNameField(reflectUtils.getField(GameProfile.class, "name"));
						utils.setUUIDField(reflectUtils.getField(GameProfile.class, "id"));
						
						if(reflectVersion.equals("v1_8_R1")) {
							gameProfileBuilder_1_8_R1 = new GameProfileBuilder_1_8_R1();
							uuidFetcher_1_8_R1 = new UUIDFetcher_1_8_R1();
						} else {
							gameProfileBuilder = new GameProfileBuilder();
							uuidFetcher = new UUIDFetcher();
						}
					}

					version = reflectVersion.substring(1);
					
					if(version.equals("1_7_R4"))
						new PacketInjector_1_7().init();
					else
						new PacketInjector().init();
					
					if (fileUtils.cfg.getBoolean("APIMode") == false) {
						getCommand("eazynick").setExecutor(new HelpCommand());
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
						getCommand("nickgui").setExecutor(new NickGuiCommand());

						if(!(version.equalsIgnoreCase("1_7_R4"))) {
							getCommand("bookgui").setExecutor(new BookGUICommand());
							getCommand("booknick").setExecutor(new BookNickCommand());
						} else {
							getCommand("bookgui").setExecutor(new CommandNotAvaiableCommand());
							getCommand("booknick").setExecutor(new CommandNotAvaiableCommand());
						}
						
						pm.registerEvents(new PlayerNickListener(), instance);
						pm.registerEvents(new PlayerUnnickListener(), instance);
						
						pm.registerEvents(new AsyncPlayerChatListener(), instance);
						pm.registerEvents(new PlayerCommandPreprocessListener(), instance);
						pm.registerEvents(new PlayerDropItemListener(), instance);
						pm.registerEvents(new InventoryClickListener(), instance);
						pm.registerEvents(new PlayerInteractListener(), instance);
						pm.registerEvents(new PlayerChangedWorldListener(), instance);
						pm.registerEvents(new PlayerDeathListener(), instance);
						pm.registerEvents(new PlayerJoinListener(), instance);
						pm.registerEvents(new PlayerKickListener(), instance);
						pm.registerEvents(new PlayerQuitListener(), instance);

						for (Player all : Bukkit.getOnlinePlayers()) {
							if ((all != null) && (all.getUniqueId() != null)) {
								if (!(utils.getCanUseNick().containsKey(all.getUniqueId())))
									utils.getCanUseNick().put(all.getUniqueId(), true);
							}
						}
					}
					
					utils.sendConsole("	§7Loading §e" + version + " §7...");
					utils.sendConsole("	§7Version §e" + version + " §7was loaded §asuccessfully§7!");

					if (fileUtils.cfg.getBoolean("BungeeCord")) {
						mysql = new MySQL(fileUtils.cfg.getString("BungeeMySQL.hostname"), fileUtils.cfg.getString("BungeeMySQL.port"), fileUtils.cfg.getString("BungeeMySQL.database"), fileUtils.cfg.getString("BungeeMySQL.username"), fileUtils.cfg.getString("BungeeMySQL.password"));
						mysql.connect();

						mysql.update("CREATE TABLE IF NOT EXISTS NickedPlayers (UUID varchar(64), NickName varchar(64), SkinName varchar(64))");
						mysql.update("CREATE TABLE IF NOT EXISTS NickedPlayerDatas (UUID varchar(64), OldRank varchar(64), ChatPrefix varchar(64), ChatSuffix varchar(64), TabPrefix varchar(64), TabSuffix varchar(64), TagPrefix varchar(64), TagSuffix varchar(64))");
					
						mysqlNickManager = new MySQLNickManager(mysql);
						mysqlPlayerDataManager = new MySQLPlayerDataManager(mysql);
						
						try {
							File file = new File("spigot.yml");
							
							if(!(file.exists()))
								file.createNewFile();
							
							YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
							cfg.set("settings.bungeecord", true);
							cfg.save(file);
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					
						Bukkit.spigot().getConfig().set("settings.bungeecord", true);
					}
					
					utils.sendConsole("	§7");
					utils.sendConsole("	§7API-Mode§8: §3" + fileUtils.cfg.getBoolean("APIMode"));
					utils.sendConsole("	§7Plugin by§8: §3" + getDescription().getAuthors().toString().replace("[", "").replace("]", ""));
					utils.sendConsole("	§7Version§8: §3" + getDescription().getVersion());
					utils.sendConsole("	§7Plugin-State§8: §aENABLED");
					utils.sendConsole("	§7");
					utils.sendConsole("	§7System started in §e" + (System.currentTimeMillis() - startMillis) + "ms");
				}

				utils.sendConsole("§7========== §8[ §5§lNickSystem §8] §7==========");

				if (spigotUpdater.checkForUpdates())
					isCancelled = true;

				if (isCancelled) {
					pm.disablePlugin(instance);
					return;
				}
				
				if(utils.placeholderAPIStatus()) {
					new PlaceHolderExpansion(instance).register();
					
					utils.sendConsole("§7Placeholders loaded successfully!");
				}
				
				if(utils.deluxeChatStatus()) {
					pm.registerEvents(new DeluxeChatListener(), instance);
					
					utils.sendConsole("§7DeluxeChat hooked successfully!");
				}
			}
		}, 10);
	}

	@Override
	public void onDisable() {
		utils.sendConsole("§7========== §8[ §5§lNickSystem §8] §7==========");
		utils.sendConsole("	§7Disabling System...");

		if (fileUtils.cfg.getBoolean("BungeeCord"))
			mysql.disconnect();

		utils.sendConsole("	§7System disabled §asuccessfully§7!");
		utils.sendConsole("	§7");
		utils.sendConsole("	§7Plugin by§8: §3" + getDescription().getAuthors().toString().replace("[", "").replace("]", ""));
		utils.sendConsole("	§7Version§8: §3" + getDescription().getVersion());
		utils.sendConsole("	§7Plugin-State§8: §cDISABLED");
		utils.sendConsole("§7========== §8[ §5§lNickSystem §8] §7==========");
		
		for (Player all : Bukkit.getOnlinePlayers()) {
			NickManager apiAll = new NickManager(all);
			
			if(apiAll.isNicked())
				apiAll.unnickPlayerWithoutRemovingMySQL(false);
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
	
	public ActionBarUtils getActionBarUtils() {
		return actionBarUtils;
	}
	
	public FileUtils getFileUtils() {
		return fileUtils;
	}
	
	public NickNameFileUtils getNickNameFileUtils() {
		return nickNameFileUtils;
	}
	
	public BookGUIFileUtils getBookGUIFileUtils() {
		return bookGUIFileUtils;
	}
	
	public LanguageFileUtils getLanguageFileUtils() {
		return languageFileUtils;
	}
	
	public ReflectUtils getReflectUtils() {
		return reflectUtils;
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
	
	public NMSNickManager getNMSNickManager() {
		return nmsNickManager;
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
	
	public void setLanguageFileUtils(LanguageFileUtils languageFileUtils) {
		this.languageFileUtils = languageFileUtils;
	}

}
