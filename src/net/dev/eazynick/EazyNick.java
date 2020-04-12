package net.dev.eazynick;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.mojang.authlib.GameProfile;

import net.dev.eazynick.api.*;
import net.dev.eazynick.commands.*;
import net.dev.eazynick.listeners.*;
import net.dev.eazynick.placeholders.*;
import net.dev.eazynick.sql.*;
import net.dev.eazynick.updater.*;
import net.dev.eazynick.utils.*;
import net.dev.eazynick.utils.bookutils.*;
import net.dev.eazynick.utils.nickutils.*;
import net.dev.eazynick.utils.signutils.*;

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
				nmsNickManager = new NMSNickManager();
				
				utils.setNickNames(nickNameFileUtils.cfg.getStringList("NickNames"));

				List<String> blackList = fileUtils.cfg.getStringList("BlackList");
				List<String> worldBlackList = fileUtils.cfg.getStringList("AutoNickWorldBlackList");
				
				if (blackList.size() >= 1) {
					ArrayList<String> toAdd = new ArrayList<>();
					
					for (String blackListName : blackList)
						toAdd.add(blackListName.toUpperCase());
					
					utils.setBlackList(new ArrayList<>(toAdd));
				}

				if (worldBlackList.size() >= 1) {
					ArrayList<String> toAdd = new ArrayList<>();
					
					for (String blackListWorld : worldBlackList)
						toAdd.add(blackListWorld.toUpperCase());
					
					utils.setWorldBlackList(new ArrayList<>(toAdd));
				}

				utils.setPrefix(languageFileUtils.getConfigString("Messages.Prefix"));
				utils.setNoPerm(languageFileUtils.getConfigString("Messages.NoPerm"));
				utils.setNotPlayer(languageFileUtils.getConfigString("Messages.NotPlayer"));

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

				if (fileUtils.cfg.getBoolean("AutoUpdater"))
					spigotUpdater.checkForUpdates();

				if (isCancelled)
					pm.disablePlugin(instance);
				
				if(utils.placeholderAPIStatus()) {
					new PlaceHolderExpansion(instance).register();
					
					utils.sendConsole("§7Placeholders loaded successfully!");
				}
				
				if(utils.deluxeChatStatus()) {
					pm.registerEvents(new DeluxeChatHookListener(), instance);
					
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
	
	public void setLanguageFileUtils(LanguageFileUtils languageFileUtils) {
		this.languageFileUtils = languageFileUtils;
	}

}
