package net.dev.eazynick.hooks;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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
		
		if(fileUtils.getConfig().getBoolean("ChangeLuckPermsPrefixAndSufix")) {
			net.luckperms.api.node.NodeBuilderRegistry nodeFactory = api.getNodeBuilderRegistry();
			net.luckperms.api.node.Node prefixNode = nodeFactory.forPrefix().priority(99).prefix(prefix).expiry(24 * 30, TimeUnit.HOURS).build();
			net.luckperms.api.node.Node suffixNode = nodeFactory.forSuffix().priority(99).suffix(suffix).expiry(24 * 30, TimeUnit.HOURS).build();
			
			user.transientData().add(prefixNode);
			user.transientData().add(suffixNode);
			
			utils.getLuckPermsPrefixes().put(p.getUniqueId(), prefixNode);
			utils.getLuckPermsSuffixes().put(p.getUniqueId(), suffixNode);
		}
		
		if(fileUtils.getConfig().getBoolean("SwitchLuckPermsGroupByNicking") && !(groupName.equalsIgnoreCase("NONE")) && (user.getPrimaryGroup() != null) && !(user.getPrimaryGroup().isEmpty())) {
			utils.getOldLuckPermsGroups().put(p.getUniqueId(), user.getPrimaryGroup());
			
			removeAllGroups(user);
			
			user.data().add(net.luckperms.api.node.types.InheritanceNode.builder(groupName).build());
		}
		
		api.getUserManager().saveUser(user);
	}

	public void resetNodes() {
		net.luckperms.api.LuckPerms api = net.luckperms.api.LuckPermsProvider.get();
		net.luckperms.api.model.user.User user = api.getUserManager().getUser(p.getUniqueId());
		
		if(fileUtils.getConfig().getBoolean("ChangeLuckPermsPrefixAndSufix")) {
			if(utils.getLuckPermsPrefixes().containsKey(p.getUniqueId()) && utils.getLuckPermsSuffixes().containsKey(p.getUniqueId())) {
				user.transientData().remove((net.luckperms.api.node.Node) utils.getLuckPermsPrefixes().get(p.getUniqueId()));
				user.transientData().remove((net.luckperms.api.node.Node) utils.getLuckPermsSuffixes().get(p.getUniqueId()));
				
				utils.getLuckPermsPrefixes().remove(p.getUniqueId());
				utils.getLuckPermsSuffixes().remove(p.getUniqueId());
			}
		}
		
		if(fileUtils.getConfig().getBoolean("SwitchLuckPermsGroupByNicking")) {
			if(utils.getOldLuckPermsGroups().containsKey(p.getUniqueId())) {
				removeAllGroups(user);
				
				user.data().add(net.luckperms.api.node.types.InheritanceNode.builder(utils.getOldLuckPermsGroups().get(p.getUniqueId())).build());
				
				utils.getOldLuckPermsGroups().remove(p.getUniqueId());
			}
		}
		
		api.getUserManager().saveUser(user);
	}

	private void removeAllGroups(net.luckperms.api.model.user.User user) {
		ArrayList<net.luckperms.api.node.Node> toRemove = new ArrayList<>();
		
		user.data().toMap().values().forEach(node -> node.stream().filter(node2 -> (node2 instanceof net.luckperms.api.node.types.InheritanceNode)).forEach(toRemove::add));
		toRemove.forEach(node -> user.data().remove(node));
	}
	
}
