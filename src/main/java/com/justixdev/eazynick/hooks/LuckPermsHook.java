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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.logging.Level;

public class LuckPermsHook {

    private final Player player;
    private final LuckPerms api;
    private final User user;

    private final Utils utils;
    private final SetupYamlFile setupYamlFile;

    public LuckPermsHook(Player player) {
        this.player = player;

        this.api = LuckPermsProvider.get();
        this.user = this.api.getUserManager().getUser(this.player.getUniqueId());

        if(this.user == null)
            Bukkit.getLogger().log(Level.SEVERE, "Could not load LuckPerms user '" + player.getName() + "'");

        EazyNick eazyNick = EazyNick.getInstance();
        this.utils = eazyNick.getUtils();
        this.setupYamlFile = eazyNick.getSetupYamlFile();
    }

    public void updateNodes(String prefix, String suffix, String groupName) {
        if(this.user == null)
            return;

        if (this.setupYamlFile.getConfiguration().getBoolean("ChangeLuckPermsPrefixAndSuffix")) {
            // Create new prefix and suffix nodes
            PrefixNode prefixNode = PrefixNode.builder().prefix(prefix).priority(100).build();
            SuffixNode suffixNode = SuffixNode.builder().suffix(suffix).priority(100).build();

            this.user.transientData().add(prefixNode);
            this.user.transientData().add(suffixNode);

            this.utils.getLuckPermsPrefixes().put(this.player.getUniqueId(), prefixNode);
            this.utils.getLuckPermsSuffixes().put(this.player.getUniqueId(), suffixNode);
        }

        if (this.setupYamlFile.getConfiguration().getBoolean("SwitchLuckPermsGroupByNicking")
                && !groupName.equalsIgnoreCase("NONE") && !user.getPrimaryGroup().isEmpty()) {
            // Update group nodes
            this.utils.getOldLuckPermsGroups().put(this.player.getUniqueId(), user.getNodes(NodeType.INHERITANCE));

            this.removeAllGroups(this.user);

            this.user.data().add(InheritanceNode.builder(groupName).build());
        }

        this.api.getUserManager().saveUser(this.user);
    }

    public void resetNodes() {
        if(this.user == null)
            return;

        if (this.setupYamlFile.getConfiguration().getBoolean("ChangeLuckPermsPrefixAndSuffix")
                && this.utils.getLuckPermsPrefixes().containsKey(this.player.getUniqueId())
                && this.utils.getLuckPermsSuffixes().containsKey(this.player.getUniqueId())) {
            // Remove prefix and suffix nodes
            this.user.transientData().remove((Node) this.utils.getLuckPermsPrefixes().get(this.player.getUniqueId()));
            this.user.transientData().remove((Node) this.utils.getLuckPermsSuffixes().get(this.player.getUniqueId()));

            this.utils.getLuckPermsPrefixes().remove(this.player.getUniqueId());
            this.utils.getLuckPermsSuffixes().remove(this.player.getUniqueId());
        }

        if (this.setupYamlFile.getConfiguration().getBoolean("SwitchLuckPermsGroupByNicking")
                && this.utils.getOldLuckPermsGroups().containsKey(this.player.getUniqueId())) {
            // Reset group nodes
            this.removeAllGroups(user);

            //noinspection unchecked
            ((Collection<InheritanceNode>) this.utils.getOldLuckPermsGroups().get(this.player.getUniqueId()))
                    .forEach(node -> user.data().add(node));

            this.utils.getOldLuckPermsGroups().remove(this.player.getUniqueId());
        }

        this.api.getUserManager().saveUser(user);
    }

    private void removeAllGroups(User user) {
        user.data().toMap().values().forEach(node ->
                node.stream()
                        .filter(node2 -> (node2 instanceof InheritanceNode))
                        .forEach(node2 -> user.data().remove(node2)));
    }

}
