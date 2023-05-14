package com.justixdev.eazynick.listeners;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.api.events.PlayerUnnickEvent;
import com.justixdev.eazynick.hooks.LuckPermsHook;
import com.justixdev.eazynick.hooks.TABHook;
import com.justixdev.eazynick.utilities.AsyncTask;
import com.justixdev.eazynick.utilities.AsyncTask.AsyncRunnable;
import com.justixdev.eazynick.utilities.ItemBuilder;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.SavedNickDataYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import com.nametagedit.plugin.NametagEdit;
import com.nametagedit.plugin.api.INametagApi;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.Objects;

public class PlayerUnnickListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerUnnick(PlayerUnnickEvent event) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();
        SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
        SavedNickDataYamlFile savedNickDataYamlFile = eazyNick.getSavedNickDataYamlFile();
        LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();

        if(!(event.isCancelled())) {
            Player player = event.getPlayer();
            NickManager api = new NickManager(player);
            String nickName = api.getNickName(),
                    name = api.getRealName(),
                    oldDisplayName = api.getOldDisplayName(),
                    uniqueIdString = player.getUniqueId().toString().replace("-", "");

            if(setupYamlFile.getConfiguration().getBoolean("NickCommands.OnUnnick")) {
                if(utils.isPluginInstalled("PlaceholderAPI"))
                    setupYamlFile.getConfiguration().getStringList("NickCommands.Unnick")
                            .forEach(command -> Bukkit.dispatchCommand(
                                    setupYamlFile.getConfiguration().getBoolean("NickCommands.SendAsConsole")
                                            ? Bukkit.getConsoleSender()
                                            : player,
                                    PlaceholderAPI.setPlaceholders(
                                            player,
                                            command
                                                    .replace("%player%", player.getName())
                                                    .replace("%nickName%", nickName)
                                    )
                            ));
                else
                    setupYamlFile.getConfiguration().getStringList("NickCommands.Unnick")
                            .forEach(command -> Bukkit.dispatchCommand(
                                    setupYamlFile.getConfiguration().getBoolean("NickCommands.SendAsConsole")
                                            ? Bukkit.getConsoleSender()
                                            : player,
                                    command
                                            .replace("%player%", player.getName())
                                            .replace("%nickName%", nickName)
                            ));
            }

            if(savedNickDataYamlFile.getConfiguration().contains(uniqueIdString))
                savedNickDataYamlFile.getConfiguration().set(uniqueIdString, null);

            api.unnickPlayer();

            if(setupYamlFile.getConfiguration().getBoolean("LogNicknames"))
                utils.sendConsole("§a" + nickName + " §8(" + player.getUniqueId() + ") §7reset his nickname to §d" + name);

            if(setupYamlFile.getConfiguration().getBoolean("NickMessage.OnUnnick"))
                Bukkit.getOnlinePlayers().forEach(currentPlayer -> languageYamlFile.sendMessage(
                        currentPlayer,
                        setupYamlFile.getConfigString(player, "NickMessage.Unnick.Quit")
                                .replace("%displayName%", player.getDisplayName())
                                .replace("%displayname%", player.getDisplayName())
                                .replace("%name%", nickName)
                ));

            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.Unnick")
                            .replace("%prefix%", utils.getPrefix()));

            new AsyncTask(new AsyncRunnable() {

                @Override
                public void run() {
                    if(setupYamlFile.getConfiguration().getBoolean("NickMessage.OnUnnick"))
                        Bukkit.getOnlinePlayers().forEach(currentPlayer -> languageYamlFile.sendMessage(
                                currentPlayer,
                                setupYamlFile.getConfigString(player, "NickMessage.Unnick.Join")
                                        .replace("%displayName%", oldDisplayName)
                                        .replace("%displayname%", oldDisplayName)
                                        .replace("%name%", name)
                        ));
                }
            }, 50L * 3).run();

            // Update nick item
            if(setupYamlFile.getConfiguration().getBoolean("NickItem.getOnJoin")
                    && player.hasPermission("eazynick.item")) {
                for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
                    ItemStack item = player.getInventory().getItem(slot);

                    if((item != null)
                            && (item.getType() != Material.AIR)
                            && (item.getItemMeta() != null)
                            && item.getItemMeta().hasDisplayName()) {
                        if(item.getItemMeta()
                                .getDisplayName()
                                .equalsIgnoreCase(eazyNick.getLanguageYamlFile().getConfigString(player, "NickItem.DisplayName.Enabled")))
                            player.getInventory().setItem(
                                    slot,
                                    new ItemBuilder(Material.getMaterial(Objects.requireNonNull(setupYamlFile.getConfiguration().getString("NickItem.ItemType.Disabled"))),
                                            setupYamlFile.getConfiguration().getInt("NickItem.ItemAmount.Disabled"),
                                            setupYamlFile.getConfiguration().getInt("NickItem.MetaData.Disabled"))
                                            .setDisplayName(languageYamlFile.getConfigString(player, "NickItem.DisplayName.Disabled"))
                                            .setLore(languageYamlFile.getConfigString(player, "NickItem.ItemLore.Disabled").split("&n"))
                                            .setEnchanted(setupYamlFile.getConfiguration().getBoolean("NickItem.Enchanted.Disabled"))
                                            .build()
                            );
                    }
                }
            }

            // Plugin integrations

            if(utils.isPluginInstalled("LuckPerms"))
                new LuckPermsHook(player).resetNodes();

            if(utils.isPluginInstalled("TAB", "NEZNAMY")
                    && setupYamlFile.getConfiguration().getBoolean("ChangeGroupAndPrefixAndSuffixInTAB")) {
                new AsyncTask(new AsyncRunnable() {

                    @Override
                    public void run() {
                        new TABHook(player).reset();
                    }
                }, 400 + (setupYamlFile.getConfiguration().getBoolean("RandomDisguiseDelay")
                        ? 2000
                        : 0))
                        .run();
            }

            if(utils.isPluginInstalled("PermissionsEx")) {
                PermissionUser user = PermissionsEx.getUser(player);

                if(setupYamlFile.getConfiguration().getBoolean("SwitchPermissionsExGroupByNicking")) {
                    if(utils.getOldPermissionsExGroups().containsKey(player.getUniqueId())) {
                        for(String group : utils.getOldPermissionsExGroups().get(player.getUniqueId()))
                            user.addGroup(group);

                        utils.getOldPermissionsExGroups().remove(player.getUniqueId());
                    }
                } else if(utils.getOldPermissionsExPrefixes().containsKey(player.getUniqueId())
                        && utils.getOldPermissionsExSuffixes().containsKey(player.getUniqueId())) {
                    user.setPrefix(
                            utils.getOldPermissionsExPrefixes().get(player.getUniqueId()),
                            player.getWorld().getName()
                    );
                    user.setSuffix(
                            utils.getOldPermissionsExSuffixes().get(player.getUniqueId()),
                            player.getWorld().getName()
                    );
                }
            }

            if(utils.isPluginInstalled("NametagEdit")
                    && (utils.getNametagEditPrefixes().containsKey(player.getUniqueId())
                    || utils.getNametagEditSuffixes().containsKey(player.getUniqueId()))) {
                String prefix = utils.getNametagEditPrefixes().get(player.getUniqueId()),
                        suffix = utils.getNametagEditSuffixes().get(player.getUniqueId());
                INametagApi nametagEditAPI = NametagEdit.getApi();

                if((prefix != null) && !prefix.isEmpty())
                    nametagEditAPI.setPrefix(player, prefix);

                if((suffix != null) && !suffix.isEmpty())
                    nametagEditAPI.setSuffix(player, suffix);

                nametagEditAPI.reloadNametag(player);

                utils.getNametagEditPrefixes().remove(player.getUniqueId());
                utils.getNametagEditSuffixes().remove(player.getUniqueId());
            }
        }
    }

}
