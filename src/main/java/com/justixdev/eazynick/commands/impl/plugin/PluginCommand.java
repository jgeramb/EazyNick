package com.justixdev.eazynick.commands.impl.plugin;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickedPlayerData;
import com.justixdev.eazynick.commands.Command;
import com.justixdev.eazynick.commands.CommandResult;
import com.justixdev.eazynick.commands.CustomCommand;
import com.justixdev.eazynick.commands.parameters.CommandParameter;
import com.justixdev.eazynick.commands.parameters.ParameterCombination;
import com.justixdev.eazynick.commands.parameters.ParameterType;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.justixdev.eazynick.nms.ReflectionHelper.*;

@CustomCommand(name = "eazynick", description = "Main command of the plugin", playersOnly = false)
public class PluginCommand extends Command {

    @Override
    public List<ParameterCombination> getCombinations() {
        return Arrays.asList(
                new ParameterCombination(),
                // page
                new ParameterCombination(
                        new CommandParameter("page", ParameterType.NUMBER)),
                // debug
                new ParameterCombination(
                        new CommandParameter("action", ParameterType.TEXT),
                        new CommandParameter("player", ParameterType.PLAYER)),
                // reload + support
                new ParameterCombination(
                        new CommandParameter("action", ParameterType.TEXT)));
    }

    @Override
    public CommandResult execute(CommandSender sender, ParameterCombination args) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();
        LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
        SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();

        String prefix = utils.getPrefix();
        PluginDescriptionFile desc = eazyNick.getDescription();

        if(!sender.hasPermission("eazynick.help")) {
            sender.sendMessage(prefix + "§7§m-----§r§8 [ §5" + desc.getName() + " §8] §7§m-----§r");
            sender.sendMessage(prefix);
            sender.sendMessage(prefix + "§7Version§8: §a" + desc.getVersion());
            sender.sendMessage(prefix + "§7Authors§8: §a" + desc.getAuthors().toString().substring(1).replace("]", ""));
            sender.sendMessage(prefix + "§7Website/Source§8: §a" + desc.getWebsite());
            sender.sendMessage(prefix);
            sender.sendMessage(prefix + "§7§m-----§r§8 [ §5" + desc.getName() + " §8] §7§m-----§r");

            return CommandResult.SUCCESS;
        }

        String action = args.withName("action")
                .map(CommandParameter::asText)
                .orElse("none");

        if(action.equals("none")) {
            int page = args.withName("page")
                    .map(CommandParameter::asNumber)
                    .orElse(1);

            List<String> commandInfos = eazyNick.getCommandManager()
                    .getCommands()
                    .keySet()
                    .stream()
                    .filter(commandMetaData -> !commandMetaData.getDescription().equals("For internal purposes only"))
                    .map(commandMetaData -> ("§7" + (commandMetaData.isPlayersOnly() ? "/" : "(/)") + commandMetaData.getName() + " §8» §a" + commandMetaData.getDescription()))
                    .collect(Collectors.toList());
            int commandsPerPage = 8;
            int maxPage = (commandInfos.size() / commandsPerPage) + ((commandInfos.size() % commandsPerPage == 0) ? 0 : 1);

            if ((page > 0) && (page <= maxPage)) {
                sender.sendMessage(prefix + "§7§m-----§r§8 [ §5" + desc.getName() + " §8] §7§m-----§r");
                sender.sendMessage(prefix);

                for (int i = (page - 1) * commandsPerPage; i < page * commandsPerPage; i++) {
                    sender.sendMessage(prefix + commandInfos.get(i));

                    if (i == (commandInfos.size() - 1))
                        break;
                }

                if (page < maxPage) {
                    sender.sendMessage(prefix);
                    sender.sendMessage(prefix + "§7More help§8: §a/eazynick " + (page + 1));
                }

                sender.sendMessage(prefix);
                sender.sendMessage(prefix + "§7§m-----§r§8 [ §5" + desc.getName() + " §8] §7§m-----§r");
            } else
                sender.sendMessage(prefix + "§cUnknown page (available: 1 to " + maxPage + ")");
        } else {
            if(action.equalsIgnoreCase("debug")) {
                if(!sender.hasPermission("eazynick.debug"))
                    return CommandResult.FAILURE_NO_PERMISSION;

                Player targetPlayer = args.withName("player")
                        .map(CommandParameter::asPlayer)
                        .orElse(null);

                if(targetPlayer != null) {
                    sender.sendMessage(prefix + "§7§m-----§r§8 [ §5Debug Info §8] §7§m-----§r");
                    sender.sendMessage(prefix);
                    sender.sendMessage(prefix + "§5§lPlayer details");

                    NickedPlayerData nickedPlayerData = utils.getNickedPlayers().get(targetPlayer.getUniqueId());

                    if(nickedPlayerData != null) {
                        sender.sendMessage(prefix + "§8┣ §7Nicked §8» §aYes");
                        sender.sendMessage(prefix + "§8┣ §7Real name §8» §a" + nickedPlayerData.getRealName());
                        sender.sendMessage(prefix + "§8┣ §7Nickname §8» §a" + nickedPlayerData.getNickName());
                        sender.sendMessage(prefix + "§8┣ §7Skin §8» §a" + nickedPlayerData.getSkinName());
                        sender.sendMessage(prefix + "§8┣ §7UUID §8» §8'§f" + nickedPlayerData.getUniqueId() + "§8'");
                        sender.sendMessage(prefix + "§8┣ §7Spoofed UUID §8» §8'§f" + nickedPlayerData.getSpoofedUniqueId() + "§8'");
                        sender.sendMessage(prefix + "§8┣ §7Chat prefix §8» §8'§f" + nickedPlayerData.getChatPrefix().replace("§", "&") + "§8'");
                        sender.sendMessage(prefix + "§8┣ §7Chat suffix §8» §8'§f" + nickedPlayerData.getChatSuffix().replace("§", "&") + "§8'");
                        sender.sendMessage(prefix + "§8┣ §7Tab prefix §8» §8'§f" + nickedPlayerData.getTabPrefix().replace("§", "&") + "§8'");
                        sender.sendMessage(prefix + "§8┣ §7Tab suffix §8» §8'§f" + nickedPlayerData.getTabSuffix().replace("§", "&") + "§8'");
                        sender.sendMessage(prefix + "§8┣ §7Tag prefix §8» §8'§f" + nickedPlayerData.getTagPrefix().replace("§", "&") + "§8'");
                        sender.sendMessage(prefix + "§8┣ §7Tag suffix §8» §8'§f" + nickedPlayerData.getTagSuffix().replace("§", "&") + "§8'");
                        sender.sendMessage(prefix + "§8┣ §7Group name §8» §8'§f" + nickedPlayerData.getGroupName() + "§8'");
                        sender.sendMessage(prefix + "§8┣ §7SortID §8» §a" + nickedPlayerData.getSortID());
                        sender.sendMessage(prefix + "§8┣ §7Old chat name §8» §8'§f" + nickedPlayerData.getOldDisplayName().replace("§", "&") + "§8'");
                        sender.sendMessage(prefix + "§8┗ §7Old tab name §8» §8'§f" + nickedPlayerData.getOldPlayerListName().replace("§", "&") + "§8'");
                    } else
                        sender.sendMessage(prefix + "§8┗  §7Nicked §8» §cNo");

                    sender.sendMessage(prefix);
                    sender.sendMessage(prefix + "§7§m-----§r§8 [ §5Debug Info §8] §7§m-----§r");
                } else
                    languageYamlFile.sendMessage(sender,
                            languageYamlFile.getConfigString(null, "Messages.PlayerNotFound")
                                    .replace("%prefix%", prefix)
                    );
            } else if(action.equalsIgnoreCase("reload")) {
                if(!sender.hasPermission("eazynick.reload"))
                    return CommandResult.FAILURE_NO_PERMISSION;

                utils.reloadConfigs();

                languageYamlFile.sendMessage(sender,
                        languageYamlFile.getConfigString("Messages.ReloadConfig")
                                .replace("%prefix%", prefix)
                );
            } else if(action.equalsIgnoreCase("support")) {
                if(!sender.hasPermission("eazynick.support"))
                    return CommandResult.FAILURE_NO_PERMISSION;

                if (utils.isSupportMode()) {
                    utils.setSupportMode(false);

                    sender.sendMessage(prefix + "§cSupport mode was disabled");
                } else {
                    utils.setSupportMode(true);

                    sender.sendMessage(prefix + "§7§m-----§r§8 [ §5EazyNick Support §8] §7§m-----§r");
                    sender.sendMessage(prefix);

                    if (sender instanceof Player) {
                        if (NMS_VERSION.startsWith("v1_17") || NMS_VERSION.startsWith("v1_18") || NMS_VERSION.startsWith("v1_19"))
                            ((Player) sender).spigot().sendMessage(
                                    new ComponentBuilder(prefix + "§71. Join the discord server §8(discord.justix-dev.com)")
                                            .event(new ClickEvent(ClickEvent.Action.OPEN_URL,
                                                    "https://discord.justix-dev.com/"))
                                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                    Collections.singletonList(new net.md_5.bungee.api.chat.hover.content.Text(
                                                            "§7Click here to open the discord invitation link"))))
                                            .create()
                            );
                        else {
                            try {
                                ((Player) sender).spigot().sendMessage(
                                        new ComponentBuilder(prefix + "§71. Join the discord server §8(discord.justix-dev.com)")
                                                .event(new ClickEvent(ClickEvent.Action.OPEN_URL,
                                                        "https://discord.justix-dev.com/"))
                                                .event((HoverEvent) newInstance(
                                                        HoverEvent.class,
                                                        types(HoverEvent.Action.class, BaseComponent[].class),
                                                        HoverEvent.Action.SHOW_TEXT,
                                                        TextComponent.fromLegacyText(
                                                                "§7Click here to open the discord invitation link")))
                                                .create()
                                );
                            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException ignore) {
                            }
                        }
                    } else
                        sender.sendMessage(prefix + "§71. Join the discord server §a(discord.justix-dev.com)");

                    sender.sendMessage(prefix + "§72. Create a support ticket in the §a#create-tickets §7channel");
                    sender.sendMessage(prefix + "§73. Fill out the format specified in the bot message");
                    sender.sendMessage(prefix + "§74. Send a screenshot of the details below into the newly created ticket channel");
                    sender.sendMessage(prefix);
                    sender.sendMessage(prefix + "§5§lServer details");
                    sender.sendMessage(prefix + "§8┣ §7Operating system §8» §a" + System.getProperty("os.name") + " (" + System.getProperty("os.version") + ")");
                    sender.sendMessage(prefix + "§8┣ §7Java version §8» §a" + System.getProperty("java.version"));
                    sender.sendMessage(prefix + "§8┣ §7Server version §8» §a" + Bukkit.getVersion());
                    sender.sendMessage(prefix + "§8┣ §7Online mode §8» §a" + Bukkit.getOnlineMode());
                    sender.sendMessage(prefix + "§8┣ §7BungeeCord §8» §a" + setupYamlFile.getConfiguration().getBoolean("BungeeCord"));
                    sender.sendMessage(prefix + "§8┣ §7LobbyMode §8» §a" + setupYamlFile.getConfiguration().getBoolean("LobbyMode"));
                    sender.sendMessage(prefix + "§8┣ §7Spawn protection §8» §a" + Bukkit.getSpawnRadius());

                    StringBuilder plugins = new StringBuilder();

                    for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
                        plugins
                                .append(plugin.isEnabled() ? "§a" : "§c")
                                .append(plugin.getName())
                                .append(" v")
                                .append(plugin.getDescription().getVersion())
                                .append("§8, ");

                    if (plugins.length() > 0)
                        plugins = new StringBuilder(plugins.substring(0, plugins.length() - 4));

                    sender.sendMessage(prefix + "§8┗ §7Plugins §8» §a" + plugins);
                    sender.sendMessage(prefix);
                    sender.sendMessage(prefix + "§7§m-----§r§8 [ §5EazyNick Support §8] §7§m-----§r");
                }
            }
        }

        return CommandResult.SUCCESS;
    }

}
