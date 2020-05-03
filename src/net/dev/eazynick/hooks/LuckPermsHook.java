package net.dev.eazynick.hooks;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.Utils;

public class LuckPermsHook {

	private EazyNick eazyNick;
	private Utils utils;
	private FileUtils fileUtils;
	
	private Player p;
	
	public LuckPermsHook(Player p) {
		this.eazyNick = EazyNick.getInstance();
		this.utils = eazyNick.getUtils();
		this.fileUtils = eazyNick.getFileUtils();
		
		this.p = p;
	}
	
	public void updateNodes(String prefix, String suffix, String groupName) {
		net.luckperms.api.LuckPerms api = net.luckperms.api.LuckPermsProvider.get();
		net.luckperms.api.model.user.User user = api.getUserManager().getUser(p.getUniqueId());
		
		if(fileUtils.cfg.getBoolean("ChangeLuckPermsPrefixAndSufix")) {
			net.luckperms.api.node.NodeBuilderRegistry nodeFactory = api.getNodeBuilderRegistry();
			net.luckperms.api.node.Node prefixNode = nodeFactory.forPrefix().priority(99).prefix(prefix).expiry(24 * 30, TimeUnit.HOURS).build();
			net.luckperms.api.node.Node suffixNode = nodeFactory.forSuffix().priority(99).suffix(suffix).expiry(24 * 30, TimeUnit.HOURS).build();
			
			user.transientData().add(prefixNode);
			user.transientData().add(suffixNode);
			api.getUserManager().saveUser(user);
			
			utils.getLuckPermsPrefixes().put(p.getUniqueId(), prefixNode);
			utils.getLuckPermsSuffixes().put(p.getUniqueId(), suffixNode);
		}
		
		if(fileUtils.cfg.getBoolean("SwitchLuckPermsGroupByNicking") && !(groupName.equalsIgnoreCase("NONE"))) {
			if(!(utils.getOldLuckPermsGroups().containsKey(p.getUniqueId())))
				utils.getOldLuckPermsGroups().put(p.getUniqueId(), user.getPrimaryGroup());
			
			/*try {
				me.lucko.luckperms.bukkit.LPBukkitPlugin bukkitPlugin = (me.lucko.luckperms.bukkit.LPBukkitPlugin) eazyNick.getReflectUtils().getField(me.lucko.luckperms.bukkit.LPBukkitBootstrap.class, "plugin").get((me.lucko.luckperms.bukkit.LPBukkitBootstrap) Bukkit.getPluginManager().getPlugin("LuckPerms"));
				me.lucko.luckperms.common.model.User commonUser = new me.lucko.luckperms.common.model.User(p.getUniqueId(), bukkitPlugin);
				net.luckperms.api.context.ImmutableContextSet context = bukkitPlugin.getContextManager().getContext(p);
				List<net.luckperms.api.node.types.InheritanceNode> nodes = commonUser.normalData().immutableInheritance().get(context.immutableCopy()).stream().filter(net.luckperms.api.node.Node::getValue).distinct().collect(Collectors.toList());

				if(!(nodes.isEmpty()))
					commonUser.unsetNode(net.luckperms.api.model.data.DataType.NORMAL, nodes.get(0));
				
				commonUser.setNode(net.luckperms.api.model.data.DataType.NORMAL, me.lucko.luckperms.common.node.types.Inheritance.builder(groupName).withContext(context).build(), true);
				commonUser.getPrimaryGroup().setStoredValue(groupName);
			} catch (Exception e) {
			}*/
			
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " parent set " + groupName);
		}
	}

	public void resetNodes() {
		net.luckperms.api.LuckPerms api = net.luckperms.api.LuckPermsProvider.get();
		net.luckperms.api.model.user.User user = api.getUserManager().getUser(p.getUniqueId());
		
		if(fileUtils.cfg.getBoolean("ChangeLuckPermsPrefixAndSufix")) {
			if(utils.getLuckPermsPrefixes().containsKey(p.getUniqueId()) && utils.getLuckPermsSuffixes().containsKey(p.getUniqueId())) {
				user.transientData().remove((net.luckperms.api.node.Node) utils.getLuckPermsPrefixes().get(p.getUniqueId()));
				user.transientData().remove((net.luckperms.api.node.Node) utils.getLuckPermsSuffixes().get(p.getUniqueId()));
				api.getUserManager().saveUser(user);
				
				utils.getLuckPermsPrefixes().remove(p.getUniqueId());
				utils.getLuckPermsSuffixes().remove(p.getUniqueId());
			}
		}
		
		if(fileUtils.cfg.getBoolean("SwitchLuckPermsGroupByNicking")) {
			String groupName = utils.getOldLuckPermsGroups().get(p.getUniqueId());
			
			/*try {
				me.lucko.luckperms.bukkit.LPBukkitPlugin bukkitPlugin = (me.lucko.luckperms.bukkit.LPBukkitPlugin) eazyNick.getReflectUtils().getField(me.lucko.luckperms.bukkit.LPBukkitBootstrap.class, "plugin").get((me.lucko.luckperms.bukkit.LPBukkitBootstrap) Bukkit.getPluginManager().getPlugin("LuckPerms"));
				
				if(!(groupName.equalsIgnoreCase("NONE"))) {
					me.lucko.luckperms.common.model.User commonUser = new me.lucko.luckperms.common.model.User(p.getUniqueId(), bukkitPlugin);
					net.luckperms.api.context.ImmutableContextSet context = bukkitPlugin.getContextManager().getContext(p);
					List<net.luckperms.api.node.types.InheritanceNode> nodes = commonUser.normalData().immutableInheritance().get(context.immutableCopy()).stream().filter(net.luckperms.api.node.Node::getValue).distinct().collect(Collectors.toList());
					
					if(!(nodes.isEmpty())) {
						net.luckperms.api.node.types.InheritanceNode oldNode = nodes.get(0);
						
						commonUser.unsetNode(net.luckperms.api.model.data.DataType.NORMAL, oldNode);
					}
					
					commonUser.setNode(net.luckperms.api.model.data.DataType.NORMAL, me.lucko.luckperms.common.node.types.Inheritance.builder(groupName).withContext(context).build(), true);
					commonUser.getPrimaryGroup().setStoredValue(groupName);
				}
			} catch (Exception e) {
			}*/
			
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " parent set " + groupName);
			
			utils.getOldLuckPermsGroups().remove(p.getUniqueId());
		}
	}
	
}
