package net.dev.eazynick;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.mojang.authlib.GameProfile;

import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.commands.BookGUICommand;
import net.dev.eazynick.commands.BookNickCommand;
import net.dev.eazynick.commands.ChangeSkinCommand;
import net.dev.eazynick.commands.CommandNotAvaiableCommand;
import net.dev.eazynick.commands.FixSkinCommand;
import net.dev.eazynick.commands.NameCommand;
import net.dev.eazynick.commands.NickCommand;
import net.dev.eazynick.commands.NickGuiCommand;
import net.dev.eazynick.commands.NickHelpCommand;
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
import net.dev.eazynick.listeners.DeluxeChatHookListener;
import net.dev.eazynick.listeners.NickListener;
import net.dev.eazynick.placeholders.PlaceHolderExpansion;
import net.dev.eazynick.sql.MySQL;
import net.dev.eazynick.updater.SpigotUpdater;
import net.dev.eazynick.utils.BookGUIFileUtils;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.LanguageFileUtils;
import net.dev.eazynick.utils.NickNameFileUtils;
import net.dev.eazynick.utils.ReflectUtils;
import net.dev.eazynick.utils.Utils;

public class EazyNick extends JavaPlugin {

	public static File pluginFile;
	public static MySQL mysql;
	public static String version = "XX_XX_RXX";
	private static boolean isCancelled;
	private static EazyNick instance;
	
	public static EazyNick getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;
		
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			
			@Override
			public void run() {
				long startMillis = System.currentTimeMillis();

				pluginFile = getFile();

				FileUtils.setupFiles();
				FileUtils.saveFile();

				NickNameFileUtils.setupFiles();
				NickNameFileUtils.saveFile();
				
				BookGUIFileUtils.setupFiles();
				BookGUIFileUtils.saveFile();
				
				Utils.nickNames = NickNameFileUtils.cfg.getStringList("NickNames");
				Utils.blackList = FileUtils.cfg.getStringList("BlackList");
				Utils.worldBlackList = FileUtils.cfg.getStringList("AutoNickWorldBlackList");

				if (Utils.blackList.size() >= 1) {
					ArrayList<String> toAdd = new ArrayList<>();
					
					for (String blackListName : Utils.blackList)
						toAdd.add(blackListName.toUpperCase());
					
					Utils.blackList = new ArrayList<>(toAdd);
				}

				if (Utils.worldBlackList.size() >= 1) {
					ArrayList<String> toAdd = new ArrayList<>();
					
					for (String blackListWorld : Utils.worldBlackList)
						toAdd.add(blackListWorld.toUpperCase());
					
					Utils.worldBlackList = new ArrayList<>(toAdd);
				}

				Utils.prefix = LanguageFileUtils.getConfigString("Messages.Prefix");
				Utils.noPerm = LanguageFileUtils.getConfigString("Messages.NoPerm");
				Utils.notPlayer = LanguageFileUtils.getConfigString("Messages.NotPlayer");

				Utils.sendConsole("§7========== §8[ §5§lNickSystem §8] §7==========");
				Utils.sendConsole("	§7Starting system...");
				Utils.sendConsole("	§7");
				Utils.sendConsole("	§7Scanning active §3Minecraft-Version §7...");

				if (!(ReflectUtils.getVersion().equalsIgnoreCase("v1_7_R4")
						|| ReflectUtils.getVersion().equalsIgnoreCase("v1_8_R1")
						|| ReflectUtils.getVersion().equalsIgnoreCase("v1_8_R2")
						|| ReflectUtils.getVersion().equalsIgnoreCase("v1_8_R3")
						|| ReflectUtils.getVersion().equalsIgnoreCase("v1_9_R1")
						|| ReflectUtils.getVersion().equalsIgnoreCase("v1_9_R2")
						|| ReflectUtils.getVersion().equalsIgnoreCase("v1_10_R1")
						|| ReflectUtils.getVersion().equalsIgnoreCase("v1_11_R1")
						|| ReflectUtils.getVersion().equalsIgnoreCase("v1_12_R1")
						|| ReflectUtils.getVersion().equalsIgnoreCase("v1_13_R1")
						|| ReflectUtils.getVersion().equalsIgnoreCase("v1_13_R2")
						|| ReflectUtils.getVersion().equalsIgnoreCase("v1_14_R1")
						|| ReflectUtils.getVersion().equalsIgnoreCase("v1_15_R1"))) {
					Utils.sendConsole("	§cERROR§8: §eVersion is §4§lINCOMPATIBLE§e!");
					Utils.sendConsole("	§7");
					Utils.sendConsole("	§7Plugin by§8: §3" + getDescription().getAuthors().toString().replace("[", "").replace("]", ""));
					Utils.sendConsole("	§7Version§8: §3" + getDescription().getVersion());
					Utils.sendConsole("	§7Plugin-State§8: §cCANCELLED");

					isCancelled = true;
				} else {
					if (FileUtils.cfg.getBoolean("APIMode") == false) {
						if (ReflectUtils.getVersion().equalsIgnoreCase("v1_7_R4")) {
							Utils.nameField = ReflectUtils.getField(net.minecraft.util.com.mojang.authlib.GameProfile.class, "name");
							Utils.uuidField = ReflectUtils.getField(net.minecraft.util.com.mojang.authlib.GameProfile.class, "id");
						} else {
							Utils.nameField = ReflectUtils.getField(GameProfile.class, "name");
							Utils.uuidField = ReflectUtils.getField(GameProfile.class, "id");
						}
						
						version = ReflectUtils.getVersion().substring(1);
						
						getCommand("eazynick").setExecutor(new NickHelpCommand());
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
						
						Bukkit.getPluginManager().registerEvents(new NickListener(), instance);

						for (Player all : Bukkit.getOnlinePlayers()) {
							if ((all != null) && (all.getUniqueId() != null)) {
								if (!(Utils.canUseNick.containsKey(all.getUniqueId())))
									Utils.canUseNick.put(all.getUniqueId(), true);
							}
						}
					}
					
					Utils.sendConsole("	§7Loading §e" + version + " §7...");
					Utils.sendConsole("	§7Version §e" + version + " §7was loaded §asuccessfully§7!");

					if (FileUtils.cfg.getBoolean("BungeeCord")) {
						mysql = new MySQL(FileUtils.cfg.getString("BungeeMySQL.hostname"), FileUtils.cfg.getString("BungeeMySQL.port"), FileUtils.cfg.getString("BungeeMySQL.database"), FileUtils.cfg.getString("BungeeMySQL.username"), FileUtils.cfg.getString("BungeeMySQL.password"));
						mysql.connect();

						mysql.update("CREATE TABLE IF NOT EXISTS NickedPlayers (UUID varchar(64), NICKNAME varchar(64), SKINNAME varchar(64))");
						mysql.update("CREATE TABLE IF NOT EXISTS NickedPlayerDatas (UUID varchar(64), OldPermissionsExRank varchar(64), ChatPrefix varchar(64), ChatSuffix varchar(64), TabPrefix varchar(64), TabSuffix varchar(64), TagPrefix varchar(64), TagSuffix varchar(64))");
					
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
					
					Utils.sendConsole("	§7");
					Utils.sendConsole("	§7API-Mode§8: §3" + FileUtils.cfg.getBoolean("APIMode"));
					Utils.sendConsole("	§7Plugin by§8: §3" + getDescription().getAuthors().toString().replace("[", "").replace("]", ""));
					Utils.sendConsole("	§7Version§8: §3" + getDescription().getVersion());
					Utils.sendConsole("	§7Plugin-State§8: §aENABLED");
					Utils.sendConsole("	§7");
					Utils.sendConsole("	§7System started in §e" + (System.currentTimeMillis() - startMillis) + "ms");
				}

				Utils.sendConsole("§7========== §8[ §5§lNickSystem §8] §7==========");

				if (FileUtils.cfg.getBoolean("AutoUpdater"))
					SpigotUpdater.checkForUpdates();

				if (isCancelled)
					Bukkit.getPluginManager().disablePlugin(instance);
				
				if(Utils.placeholderAPIStatus()) {
					new PlaceHolderExpansion(instance).register();
					
					Utils.sendConsole("§7Placeholders loaded successfully!");
				}
				
				if(Utils.deluxeChatStatus()) {
					Bukkit.getPluginManager().registerEvents(new DeluxeChatHookListener(), instance);
					
					Utils.sendConsole("§7DeluxeChat hooked successfully!");
				}
			}
		}, 20);
	}

	@Override
	public void onDisable() {
		Utils.sendConsole("§7========== §8[ §5§lNickSystem §8] §7==========");
		Utils.sendConsole("	§7Disabling System...");

		if (FileUtils.cfg.getBoolean("BungeeCord"))
			mysql.disconnect();

		Utils.sendConsole("	§7System disabled §asuccessfully§7!");
		Utils.sendConsole("	§7");
		Utils.sendConsole("	§7Plugin by§8: §3" + getDescription().getAuthors().toString().replace("[", "").replace("]", ""));
		Utils.sendConsole("	§7Version§8: §3" + getDescription().getVersion());
		Utils.sendConsole("	§7Plugin-State§8: §cDISABLED");
		Utils.sendConsole("§7========== §8[ §5§lNickSystem §8] §7==========");
		
		for (Player all : Bukkit.getOnlinePlayers()) {
			NickManager apiAll = new NickManager(all);
			
			if(apiAll.isNicked())
				apiAll.unnickPlayerWithoutRemovingMySQL(false);
		}
	}

}
