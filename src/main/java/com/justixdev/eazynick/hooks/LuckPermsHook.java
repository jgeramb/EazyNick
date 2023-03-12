package com.justixdev.eazynick.hooks;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;

public class LuckPermsHook {

    private final Utils utils;
    private final SetupYamlFile setupYamlFile;

    private final Player player;

    public LuckPermsHook(Player player) {
        EazyNick eazyNick = EazyNick.getInstance();
        this.utils = eazyNick.getUtils();
        this.setupYamlFile = eazyNick.getSetupYamlFile();

        this.player = player;
    }

    public void updateNodes(String prefix, String suffix, String groupName) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getUserManager().getUser(player.getUniqueId());

        if(user == null) return;

        if (setupYamlFile.getConfiguration().getBoolean("ChangeLuckPermsPrefixAndSuffix")) {
            // Create new prefix and suffix nodes
            PrefixNode prefixNode = PrefixNode.builder().prefix(prefix).priority(100).build();
            SuffixNode suffixNode = SuffixNode.builder().suffix(suffix).priority(100).build();

            user.transientData().add(prefixNode);
            user.transientData().add(suffixNode);

            utils.getLuckPermsPrefixes().put(player.getUniqueId(), prefixNode);
            utils.getLuckPermsSuffixes().put(player.getUniqueId(), suffixNode);
        }

        if (setupYamlFile.getConfiguration().getBoolean("SwitchLuckPermsGroupByNicking") && !(groupName.equalsIgnoreCase("NONE")) && !(user.getPrimaryGroup().isEmpty())) {
            // Update group nodes
            utils.getOldLuckPermsGroups().put(player.getUniqueId(), user.getNodes(NodeType.INHERITANCE));

            removeAllGroups(user);

            user.data().add(InheritanceNode.builder(groupName).build());
        }

        api.getUserManager().saveUser(user);
    }

    public void resetNodes() {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getUserManager().getUser(player.getUniqueId());

        if(user == null) return;

        if (setupYamlFile.getConfiguration().getBoolean("ChangeLuckPermsPrefixAndSuffix") && utils.getLuckPermsPrefixes().containsKey(player.getUniqueId()) && utils.getLuckPermsSuffixes().containsKey(player.getUniqueId())) {
            // Remove prefix and suffix nodes
            user.transientData().remove((Node) utils.getLuckPermsPrefixes().get(player.getUniqueId()));
            user.transientData().remove((Node) utils.getLuckPermsSuffixes().get(player.getUniqueId()));

            utils.getLuckPermsPrefixes().remove(player.getUniqueId());
            utils.getLuckPermsSuffixes().remove(player.getUniqueId());
        }

        if (setupYamlFile.getConfiguration().getBoolean("SwitchLuckPermsGroupByNicking") && utils.getOldLuckPermsGroups().containsKey(player.getUniqueId())) {
            // Reset group nodes
            removeAllGroups(user);

            //noinspection unchecked
            ((Collection<InheritanceNode>) utils.getOldLuckPermsGroups().get(player.getUniqueId())).forEach(node -> user.data().add(node));

            utils.getOldLuckPermsGroups().remove(player.getUniqueId());
        }

        api.getUserManager().saveUser(user);
    }

    private void removeAllGroups(User user) {
        new HashSet<>(user.data().toMap().values()).forEach(node ->
                node.stream()
                        .filter(node2 -> (node2 instanceof InheritanceNode))
                        .forEach(node2 -> user.data().remove(node2)));
    }

}
