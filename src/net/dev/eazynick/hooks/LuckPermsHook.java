package net.dev.eazynick.hooks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utilities.Utils;
import net.dev.eazynick.utilities.configuration.yaml.SetupYamlFile;

public class LuckPermsHook {

	private EazyNick eazyNick;
	private Utils utils;
	private SetupYamlFile setupYamlFile;
	
	private Player player;
	
	public LuckPermsHook(Player player) {
		this.eazyNick = EazyNick.getInstance();
		this.utils = eazyNick.getUtils();
		this.setupYamlFile = eazyNick.getSetupYamlFile();
		
		this.player = player;
	}
	
	public void updateNodes(String prefix, String suffix, String groupName) {
		net.luckperms.api.LuckPerms api = net.luckperms.api.LuckPermsProvider.get();
		net.luckperms.api.model.user.User user = api.getUserManager().getUser(player.getUniqueId());
		
		if(setupYamlFile.getConfiguration().getBoolean("ChangeLuckPermsPrefixAndSufix")) {
			//Create new prefix and suffix nodes
			net.luckperms.api.node.NodeBuilderRegistry nodeFactory = api.getNodeBuilderRegistry();
			net.luckperms.api.node.Node prefixNode = nodeFactory.forPrefix().priority(99).prefix(prefix).expiry(24 * 30, TimeUnit.HOURS).build();
			net.luckperms.api.node.Node suffixNode = nodeFactory.forSuffix().priority(99).suffix(suffix).expiry(24 * 30, TimeUnit.HOURS).build();
			
			user.transientData().add(prefixNode);
			user.transientData().add(suffixNode);
			
			utils.getLuckPermsPrefixes().put(player.getUniqueId(), prefixNode);
			utils.getLuckPermsSuffixes().put(player.getUniqueId(), suffixNode);
		}
		
		if(setupYamlFile.getConfiguration().getBoolean("SwitchLuckPermsGroupByNicking") && !(groupName.equalsIgnoreCase("NONE")) && (user.getPrimaryGroup() != null) && !(user.getPrimaryGroup().isEmpty())) {
			//Update group nodes
			utils.getOldLuckPermsGroups().put(player.getUniqueId(), user.getNodes(net.luckperms.api.node.NodeType.INHERITANCE));
			
			removeAllGroups(user);
			
			user.data().add(net.luckperms.api.node.types.InheritanceNode.builder(groupName).build());
		}
		
		api.getUserManager().saveUser(user);
	}

	public void resetNodes() {
		net.luckperms.api.LuckPerms api = net.luckperms.api.LuckPermsProvider.get();
		net.luckperms.api.model.user.User user = api.getUserManager().getUser(player.getUniqueId());
		
		if(setupYamlFile.getConfiguration().getBoolean("ChangeLuckPermsPrefixAndSufix")) {
			if(utils.getLuckPermsPrefixes().containsKey(player.getUniqueId()) && utils.getLuckPermsSuffixes().containsKey(player.getUniqueId())) {
				//Remove prefix and suffix nodes
				user.transientData().remove((net.luckperms.api.node.Node) utils.getLuckPermsPrefixes().get(player.getUniqueId()));
				user.transientData().remove((net.luckperms.api.node.Node) utils.getLuckPermsSuffixes().get(player.getUniqueId()));
				
				utils.getLuckPermsPrefixes().remove(player.getUniqueId());
				utils.getLuckPermsSuffixes().remove(player.getUniqueId());
			}
		}
		
		if(setupYamlFile.getConfiguration().getBoolean("SwitchLuckPermsGroupByNicking")) {
			if(utils.getOldLuckPermsGroups().containsKey(player.getUniqueId())) {
				//Reset group nodes
				removeAllGroups(user);
				
				((Collection<net.luckperms.api.node.types.InheritanceNode>) utils.getOldLuckPermsGroups().get(player.getUniqueId())).forEach(node -> user.data().add(node));
				
				utils.getOldLuckPermsGroups().remove(player.getUniqueId());
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
