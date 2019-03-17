package net.dev.nickplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nametagedit.plugin.NametagEdit;

import net.dev.nickplugin.sql.MySQLPlayerDataManager;
import net.dev.nickplugin.utils.FileUtils;
import net.dev.nickplugin.utils.NickManager;
import net.dev.nickplugin.utils.Utils;
import net.dev.nickplugin.utils.scoreboard.ScoreboardTeamManager;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.storage.objects.User;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class UnnickCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.use") || Utils.hasLuckPermsPermission(p.getUniqueId(), "nick.use")) {
				NickManager api = new NickManager(p);
				
				if((Utils.canUseNick.get(p.getUniqueId()) == true)) {
					if(Utils.nickedPlayers.contains(p.getUniqueId())) {
						api.unnickPlayer();
						
						if(Utils.cloudNetStatus())
							api.resetCloudNET();
						
						if(Utils.luckPermsStatus()) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission unset prefix.99." + Utils.luckPermsPrefixes.get(p.getUniqueId()));
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission unset suffix.99." + Utils.luckPermsSuffixes.get(p.getUniqueId()));
						
							Utils.luckPermsPrefixes.remove(p.getUniqueId());
							Utils.luckPermsSuffixes.remove(p.getUniqueId());
						}
						
						if(Utils.nameTagEditStatus()) {
							NametagEdit.getApi().reloadNametag(p);
						}
						
						if(Utils.permissionsExStatus()) {
							PermissionUser user = PermissionsEx.getUser(p);
						
							if(FileUtils.cfg.getBoolean("SwitchPermissionsExGroupByNicking")) {
								if(Utils.oldPermissionsExGroups.containsKey(p.getUniqueId())) {
									user.setGroups(Utils.oldPermissionsExGroups.get(p.getUniqueId()));
								}
							} else {
								user.setPrefix(Utils.oldPermissionsExPrefixes.get(p.getUniqueId()), p.getWorld().getName());
								user.setSuffix(Utils.oldPermissionsExSuffixes.get(p.getUniqueId()), p.getWorld().getName());
							}
						}
						
						if(FileUtils.cfg.getBoolean("Settings.NameChangeOptions.NameTagColored") == true) {
							if(Utils.scoreboardTeamManagers.containsKey(p.getUniqueId())) {
								ScoreboardTeamManager sbtm = Utils.scoreboardTeamManagers.get(p.getUniqueId());
								
								sbtm.removePlayerFromTeam();
								sbtm.destroyTeam();
								sbtm.createTeam();
								
								Utils.scoreboardTeamManagers.remove(p.getUniqueId());
							}
						}
						
						if(FileUtils.cfg.getBoolean("BungeeCord") == true) {
							MySQLPlayerDataManager.removeData(p.getUniqueId());
						}
						
						if(Utils.ultraPermissionsStatus()) {
							User user = UltraPermissions.getAPI().getUsers().uuid(p.getUniqueId());
							
							user.setPrefix(Utils.ultraPermsPrefixes.get(p.getUniqueId()));
							user.setSuffix(Utils.ultraPermsSuffixes.get(p.getUniqueId()));
							user.save();
							
							Utils.ultraPermsPrefixes.remove(p.getUniqueId());
							Utils.ultraPermsSuffixes.remove(p.getUniqueId());
						}
						
						p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.Unnick")));
					} else {
						p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.NotNicked")));
					}
				} else {
					p.sendMessage(Utils.PREFIX + ChatColor.translateAlternateColorCodes('&', FileUtils.cfg.getString("Messages.NickDelay")));
				}
			} else {
				p.sendMessage(Utils.NO_PERM);
			}
		} else {
			Utils.sendConsole(Utils.NOT_PLAYER);
		}
		
		return true;
	}

}
