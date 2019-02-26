package net.dev.nickplugin.main;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.mojang.authlib.GameProfile;

import net.dev.nickplugin.commands.BookGUICommand_1_10_R1;
import net.dev.nickplugin.commands.BookGUICommand_1_11_R1;
import net.dev.nickplugin.commands.BookGUICommand_1_12_R1;
import net.dev.nickplugin.commands.BookGUICommand_1_13_R1;
import net.dev.nickplugin.commands.BookGUICommand_1_13_R2;
import net.dev.nickplugin.commands.BookGUICommand_1_8_R1;
import net.dev.nickplugin.commands.BookGUICommand_1_8_R2;
import net.dev.nickplugin.commands.BookGUICommand_1_8_R3;
import net.dev.nickplugin.commands.BookGUICommand_1_9_R1;
import net.dev.nickplugin.commands.BookGUICommand_1_9_R2;
import net.dev.nickplugin.commands.BookNickCommand;
import net.dev.nickplugin.commands.ChangeNameCommand;
import net.dev.nickplugin.commands.ChangeSkinCommand;
import net.dev.nickplugin.commands.CommandNotAvaiableCommand;
import net.dev.nickplugin.commands.FixSkinCommand;
import net.dev.nickplugin.commands.NameCommand;
import net.dev.nickplugin.commands.NickCommand;
import net.dev.nickplugin.commands.NickGuiCommand;
import net.dev.nickplugin.commands.NickGuiCommand_1_13;
import net.dev.nickplugin.commands.NickGuiCommand_1_7_R4;
import net.dev.nickplugin.commands.NickHelpCommand;
import net.dev.nickplugin.commands.NickListCommand;
import net.dev.nickplugin.commands.NickOtherCommand;
import net.dev.nickplugin.commands.NickUpdateCheckCommand;
import net.dev.nickplugin.commands.NickedPlayersCommand;
import net.dev.nickplugin.commands.ReNickCommand;
import net.dev.nickplugin.commands.RealNameCommand;
import net.dev.nickplugin.commands.RealNameCommand_1_7;
import net.dev.nickplugin.commands.RealNameCommand_1_8_R1;
import net.dev.nickplugin.commands.ReloadConfigCommand;
import net.dev.nickplugin.commands.ResetNameCommand;
import net.dev.nickplugin.commands.ResetSkinCommand;
import net.dev.nickplugin.commands.UnnickCommand;
import net.dev.nickplugin.listeners.NickListener;
import net.dev.nickplugin.listeners.NickListener_1_13;
import net.dev.nickplugin.sql.MySQL;
import net.dev.nickplugin.updater.SpigotUpdater;
import net.dev.nickplugin.utils.BookGUIFileUtils;
import net.dev.nickplugin.utils.FileUtils;
import net.dev.nickplugin.utils.NickNameFileUtils;
import net.dev.nickplugin.utils.ReflectUtils;
import net.dev.nickplugin.utils.Utils;
import net.dev.nickplugin.utils.nickutils.NickManager_1_10_R1;
import net.dev.nickplugin.utils.nickutils.NickManager_1_11_R1;
import net.dev.nickplugin.utils.nickutils.NickManager_1_12_R1;
import net.dev.nickplugin.utils.nickutils.NickManager_1_13_R1;
import net.dev.nickplugin.utils.nickutils.NickManager_1_13_R2;
import net.dev.nickplugin.utils.nickutils.NickManager_1_7_R4;
import net.dev.nickplugin.utils.nickutils.NickManager_1_8_R1;
import net.dev.nickplugin.utils.nickutils.NickManager_1_8_R2;
import net.dev.nickplugin.utils.nickutils.NickManager_1_8_R3;
import net.dev.nickplugin.utils.nickutils.NickManager_1_9_R1;
import net.dev.nickplugin.utils.nickutils.NickManager_1_9_R2;

public class Main extends JavaPlugin {

	public static File pluginFile;
	public static MySQL mysql;
	public static String version = "XX_XX_RX";
	public boolean isCancelled;

	@Override
	public void onEnable() {
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
			
			for (String blackListName : Utils.blackList) {
				toAdd.add(blackListName.toUpperCase());
			}
			
			Utils.blackList = new ArrayList<>(toAdd);
		}

		if (Utils.worldBlackList.size() >= 1) {
			ArrayList<String> toAdd = new ArrayList<>();
			
			for (String blackListWorld : Utils.worldBlackList) {
				toAdd.add(blackListWorld.toUpperCase());
			}
			
			Utils.worldBlackList = new ArrayList<>(toAdd);
		}

		Utils.PREFIX = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.Prefix")) + " ";
		Utils.NO_PERM = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.NoPerm"));
		Utils.NOT_PLAYER = ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.NotPlayer"));

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
				|| ReflectUtils.getVersion().equalsIgnoreCase("v1_13_R2"))) {
			Utils.sendConsole("	§cERROR§8: §eVersion is §4§lINCOMPATIBLE§e!");
			Utils.sendConsole("	§7");
			Utils.sendConsole("	§7Plugin by§8: §3"
					+ getDescription().getAuthors().toString().replace("[", "").replace("]", ""));
			Utils.sendConsole("	§7Version§8: §3" + getDescription().getVersion());
			Utils.sendConsole("	§7Plugin-State§8: §cCANCELLED");

			this.isCancelled = true;
		} else {
			if (FileUtils.cfg.getBoolean("APIMode") == false) {
				if (ReflectUtils.getVersion().equalsIgnoreCase("v1_7_R4")) {
					getCommand("nickgui").setExecutor(new NickGuiCommand_1_7_R4());
					getCommand("bookgui").setExecutor(new CommandNotAvaiableCommand());
					getCommand("booknick").setExecutor(new CommandNotAvaiableCommand());
					getCommand("realname").setExecutor(new RealNameCommand_1_7());
					Bukkit.getPluginManager().registerEvents(new NickListener(), this);

					version = "1_7_R4";
					Utils.field = NickManager_1_7_R4.getField(net.minecraft.util.com.mojang.authlib.GameProfile.class,
							"name");
				} else if (ReflectUtils.getVersion().equalsIgnoreCase("v1_8_R1")) {
					getCommand("nickgui").setExecutor(new NickGuiCommand());
					getCommand("bookgui").setExecutor(new BookGUICommand_1_8_R1());
					getCommand("booknick").setExecutor(new BookNickCommand());
					getCommand("realname").setExecutor(new RealNameCommand_1_8_R1());
					Bukkit.getPluginManager().registerEvents(new NickListener(), this);

					version = "1_8_R1";
					Utils.field = NickManager_1_8_R1.getField(GameProfile.class, "name");
				} else if (ReflectUtils.getVersion().equalsIgnoreCase("v1_8_R2")) {
					getCommand("nickgui").setExecutor(new NickGuiCommand());
					getCommand("bookgui").setExecutor(new BookGUICommand_1_8_R2());
					getCommand("booknick").setExecutor(new BookNickCommand());
					getCommand("realname").setExecutor(new RealNameCommand());
					Bukkit.getPluginManager().registerEvents(new NickListener(), this);

					version = "1_8_R2";
					Utils.field = NickManager_1_8_R2.getField(GameProfile.class, "name");
				} else if (ReflectUtils.getVersion().equalsIgnoreCase("v1_8_R3")) {
					getCommand("nickgui").setExecutor(new NickGuiCommand());
					getCommand("bookgui").setExecutor(new BookGUICommand_1_8_R3());
					getCommand("booknick").setExecutor(new BookNickCommand());
					getCommand("realname").setExecutor(new RealNameCommand());
					Bukkit.getPluginManager().registerEvents(new NickListener(), this);

					version = "1_8_R3";
					Utils.field = NickManager_1_8_R3.getField(GameProfile.class, "name");
				} else if (ReflectUtils.getVersion().equalsIgnoreCase("v1_9_R1")) {
					getCommand("nickgui").setExecutor(new NickGuiCommand());
					getCommand("bookgui").setExecutor(new BookGUICommand_1_9_R1());
					getCommand("booknick").setExecutor(new BookNickCommand());
					getCommand("realname").setExecutor(new RealNameCommand());
					Bukkit.getPluginManager().registerEvents(new NickListener(), this);

					version = "1_9_R1";
					Utils.field = NickManager_1_9_R1.getField(GameProfile.class, "name");
				} else if (ReflectUtils.getVersion().equalsIgnoreCase("v1_9_R2")) {
					getCommand("nickgui").setExecutor(new NickGuiCommand());
					getCommand("bookgui").setExecutor(new BookGUICommand_1_9_R2());
					getCommand("booknick").setExecutor(new BookNickCommand());
					getCommand("realname").setExecutor(new RealNameCommand());
					Bukkit.getPluginManager().registerEvents(new NickListener(), this);

					version = "1_9_R2";
					Utils.field = NickManager_1_9_R2.getField(GameProfile.class, "name");
				} else if (ReflectUtils.getVersion().equalsIgnoreCase("v1_10_R1")) {
					getCommand("nickgui").setExecutor(new NickGuiCommand());
					getCommand("bookgui").setExecutor(new BookGUICommand_1_10_R1());
					getCommand("booknick").setExecutor(new BookNickCommand());
					getCommand("realname").setExecutor(new RealNameCommand());
					Bukkit.getPluginManager().registerEvents(new NickListener(), this);

					version = "1_10_R1";
					Utils.field = NickManager_1_10_R1.getField(GameProfile.class, "name");
				} else if (ReflectUtils.getVersion().equalsIgnoreCase("v1_11_R1")) {
					getCommand("nickgui").setExecutor(new NickGuiCommand());
					getCommand("bookgui").setExecutor(new BookGUICommand_1_11_R1());
					getCommand("booknick").setExecutor(new BookNickCommand());
					getCommand("realname").setExecutor(new RealNameCommand());
					Bukkit.getPluginManager().registerEvents(new NickListener(), this);

					version = "1_11_R1";
					Utils.field = NickManager_1_11_R1.getField(GameProfile.class, "name");
				} else if (ReflectUtils.getVersion().equalsIgnoreCase("v1_12_R1")) {
					getCommand("nickgui").setExecutor(new NickGuiCommand());
					getCommand("bookgui").setExecutor(new BookGUICommand_1_12_R1());
					getCommand("booknick").setExecutor(new BookNickCommand());
					getCommand("realname").setExecutor(new RealNameCommand());
					Bukkit.getPluginManager().registerEvents(new NickListener(), this);

					version = "1_12_R1";
					Utils.field = NickManager_1_12_R1.getField(GameProfile.class, "name");
				} else if (ReflectUtils.getVersion().equalsIgnoreCase("v1_13_R1")) {
					getCommand("nickgui").setExecutor(new NickGuiCommand_1_13());
					getCommand("bookgui").setExecutor(new BookGUICommand_1_13_R1());
					getCommand("booknick").setExecutor(new BookNickCommand());
					getCommand("realname").setExecutor(new RealNameCommand());
					Bukkit.getPluginManager().registerEvents(new NickListener_1_13(), this);

					version = "1_13_R1";
					Utils.field = NickManager_1_13_R1.getField(GameProfile.class, "name");
				} else if (ReflectUtils.getVersion().equalsIgnoreCase("v1_13_R2")) {
					getCommand("nickgui").setExecutor(new NickGuiCommand_1_13());
					getCommand("bookgui").setExecutor(new BookGUICommand_1_13_R2());
					getCommand("booknick").setExecutor(new BookNickCommand());
					getCommand("realname").setExecutor(new RealNameCommand());
					Bukkit.getPluginManager().registerEvents(new NickListener_1_13(), this);

					version = "1_13_R2";
					Utils.field = NickManager_1_13_R2.getField(GameProfile.class, "name");
				}
				
				getCommand("eazynick").setExecutor(new NickHelpCommand());
				getCommand("nickother").setExecutor(new NickOtherCommand());
				getCommand("changeskin").setExecutor(new ChangeSkinCommand());
				getCommand("changename").setExecutor(new ChangeNameCommand());
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

				for (Player all : Bukkit.getOnlinePlayers()) {
					if ((all != null) && (all.getUniqueId() != null)) {
						if (!(Utils.canUseNick.containsKey(all.getUniqueId()))) {
							Utils.canUseNick.put(all.getUniqueId(), true);
						}
					}
				}
			}

			Utils.sendConsole("	§7Loading §e" + version + " §7...");
			Utils.sendConsole("	§7Version §e" + version + " §7was loaded §asuccessfully§7!");

			if (FileUtils.cfg.getBoolean("BungeeCord") == true) {
				mysql = new MySQL(FileUtils.cfg.getString("BungeeMySQL.hostname"),
						FileUtils.cfg.getString("BungeeMySQL.port"), FileUtils.cfg.getString("BungeeMySQL.database"),
						FileUtils.cfg.getString("BungeeMySQL.username"),
						FileUtils.cfg.getString("BungeeMySQL.password"));
				mysql.connect();

				mysql.update("CREATE TABLE IF NOT EXISTS NickedPlayers (UUID varchar(64), NAME varchar(64))");
				mysql.update(
						"CREATE TABLE IF NOT EXISTS NickedPlayerDatas (UUID varchar(64), OldPermissionsExRank varchar(64), "
								+ "ChatPrefix varchar(64), ChatSuffix varchar(64), "
								+ "TabPrefix varchar(64), TabSuffix varchar(64), "
								+ "TagPrefix varchar(64), TagSuffix varchar(64))");
			}

			if (Bukkit.getWorld("nickWorld") == null)
				Bukkit.createWorld(new WorldCreator("nickWorld"));
			
			Utils.sendConsole("	§7");
			Utils.sendConsole("	§7API-Mode§8: §3" + FileUtils.cfg.getBoolean("APIMode"));
			Utils.sendConsole("	§7Plugin by§8: §3"
					+ getDescription().getAuthors().toString().replace("[", "").replace("]", ""));
			Utils.sendConsole("	§7Version§8: §3" + getDescription().getVersion());
			Utils.sendConsole("	§7Plugin-State§8: §aENABLED");
			Utils.sendConsole("	§7");
			Utils.sendConsole("	§7System started in §e" + (System.currentTimeMillis() - startMillis) + "ms");
		}

		Utils.sendConsole("§7========== §8[ §5§lNickSystem §8] §7==========");

		if (FileUtils.cfg.getBoolean("AutoUpdater") == true) {
			SpigotUpdater.checkForUpdates();
		}

		if (isCancelled == true) {
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}

	@Override
	public void onDisable() {
		Utils.sendConsole("§7========== §8[ §5§lNickSystem §8] §7==========");
		Utils.sendConsole("	§7Disabling System...");

		if (FileUtils.cfg.getBoolean("BungeeCord") == true) {
			mysql.disconnect();
		}

		Utils.sendConsole("	§7System disabled §asuccessfully§7!");
		Utils.sendConsole("	§7");
		Utils.sendConsole(
				"	§7Plugin by§8: §3" + getDescription().getAuthors().toString().replace("[", "").replace("]", ""));
		Utils.sendConsole("	§7Version§8: §3" + getDescription().getVersion());
		Utils.sendConsole("	§7Plugin-State§8: §cDISABLED");
		Utils.sendConsole("§7========== §8[ §5§lNickSystem §8] §7==========");
	}

}
