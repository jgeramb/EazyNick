package com.justixdev.eazynick.commands.impl.disguise;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.api.events.PlayerUnnickEvent;
import com.justixdev.eazynick.commands.Command;
import com.justixdev.eazynick.commands.CommandResult;
import com.justixdev.eazynick.commands.CustomCommand;
import com.justixdev.eazynick.commands.parameters.CommandParameter;
import com.justixdev.eazynick.commands.parameters.ParameterCombination;
import com.justixdev.eazynick.commands.parameters.ParameterType;
import com.justixdev.eazynick.sql.MySQLNickManager;
import com.justixdev.eazynick.sql.MySQLPlayerDataManager;
import com.justixdev.eazynick.utilities.GUIManager;
import com.justixdev.eazynick.utilities.StringUtils;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import com.justixdev.eazynick.utilities.mojang.MojangAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.justixdev.eazynick.nms.ReflectionHelper.NMS_VERSION;

@CustomCommand(name = "nick", description = "Changes your identity (skin, name, nametag, player list name, chat name)")
public class NickCommand extends Command {

    @Override
    protected void initAliases() {
        super.initAliases();

        this.aliases.add("d");
        this.aliases.add("disguise");
    }

    @Override
    public List<ParameterCombination> getCombinations() {
        return Arrays.asList(
                new ParameterCombination(),
                new ParameterCombination(
                        new CommandParameter("name", ParameterType.TEXT))
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, ParameterCombination args) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();
        GUIManager guiManager = eazyNick.getGuiManager();
        SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
        LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
        MySQLNickManager mysqlNickManager = eazyNick.getMysqlNickManager();
        MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMysqlPlayerDataManager();

        String prefix = utils.getPrefix();
        Player player = (Player) sender;

        if(new NickManager(player).isNicked()) {
            if(player.hasPermission("eazynick.nick.reset"))
                Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(player));

            return CommandResult.SUCCESS;
        } else if((mysqlNickManager != null)
                && mysqlNickManager.isNicked(player.getUniqueId())
                && setupYamlFile.getConfiguration().getBoolean("LobbyMode")
                && setupYamlFile.getConfiguration().getBoolean("RemoveMySQLNickOnUnnickWhenLobbyModeEnabled")) {
            if(player.hasPermission("eazynick.nick.reset")) {
                mysqlNickManager.removePlayer(player.getUniqueId());
                mysqlPlayerDataManager.removeData(player.getUniqueId());

                languageYamlFile.sendMessage(
                        player,
                        languageYamlFile.getConfigString(player, "Messages.Unnick")
                                .replace("%prefix%", prefix)
                );
            }

            return CommandResult.SUCCESS;
        }

        if(!(player.hasPermission("eazynick.nick.random")
                || player.hasPermission("eazynick.nick.custom")))
            return CommandResult.FAILURE_NO_PERMISSION;

        if(utils.getCanUseNick().getOrDefault(player.getUniqueId(), System.currentTimeMillis()) > System.currentTimeMillis()) {
            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.NickDelay")
                            .replace("%prefix%", prefix)
            );
            return CommandResult.SUCCESS;
        }

        if(setupYamlFile.getConfiguration().getBoolean("OpenBookGUIOnNickCommand")
                && !(NMS_VERSION.startsWith("v1_7"))) {
            if(player.hasPermission("eazynick.gui.book"))
                player.chat("/bookgui");
            else {
                PermissionAttachment pa = player.addAttachment(eazyNick);
                pa.setPermission("eazynick.gui.book", true);
                player.recalculatePermissions();

                player.chat("/bookgui");

                player.removeAttachment(pa);
                player.recalculatePermissions();
            }

            return CommandResult.SUCCESS;
        }

        if(setupYamlFile.getConfiguration().getBoolean("OpenNickListGUIOnNickCommand")) {
            guiManager.openNickList(player, 0);
            return CommandResult.SUCCESS;
        }

        if(setupYamlFile.getConfiguration().getBoolean("OpenRankedNickGUIOnNickCommand")) {
            guiManager.openRankedNickGUI(player, "");
            return CommandResult.SUCCESS;
        }

        String name = args.withName("name")
                .map(CommandParameter::asText)
                .orElse(null);

        if(name == null) {
            if(!player.hasPermission("eazynick.nick.random"))
                return CommandResult.FAILURE_NO_PERMISSION;

            if(setupYamlFile.getConfiguration().getStringList("DisabledNickWorlds")
                    .contains(player.getWorld().getName())) {
                languageYamlFile.sendMessage(
                        player,
                        languageYamlFile.getConfigString(player, "Messages.DisabledWorld")
                                .replace("%prefix%", prefix)
                );
                return CommandResult.SUCCESS;
            }

            utils.performNick(player, "RANDOM");
            return CommandResult.SUCCESS;
        }

        if(!player.hasPermission("eazynick.nick.custom"))
            return CommandResult.FAILURE_NO_PERMISSION;

        final String finalName = name.replace("\"", "");

        String nameWithoutColors = new StringUtils(name).getPureString();
        int nameLengthMin = Math.max(Math.min(setupYamlFile.getConfiguration().getInt("Settings.NameLength.Min"), 16), 1),
                nameLengthMax = Math.max(Math.min(setupYamlFile.getConfiguration().getInt("Settings.NameLength.Max"), 16), 1);

        if(nameWithoutColors.length() > nameLengthMax) {
            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.NickTooLong")
                            .replace("%prefix%", prefix)
            );
            return CommandResult.SUCCESS;
        }

        if(nameWithoutColors.length() < nameLengthMin) {
            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.NickTooShort")
                            .replace("%prefix%", prefix)
            );
            return CommandResult.SUCCESS;
        }

        if(utils.containsSpecialChars(nameWithoutColors)
                && !(setupYamlFile.getConfiguration().getBoolean("AllowSpecialCharactersInCustomName"))) {
            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.NickContainsSpecialCharacters")
                            .replace("%prefix%", prefix)
            );
            return CommandResult.SUCCESS;
        }

        if(utils.containsBlackListEntry(nameWithoutColors)) {
            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.NameNotAllowed")
                            .replace("%prefix%", prefix)
            );
            return CommandResult.SUCCESS;
        }

        if(utils.getNickedPlayers()
                .values()
                .stream()
                .anyMatch(currentNickedPlayerData -> currentNickedPlayerData.getNickName().equalsIgnoreCase(finalName))
                && !(setupYamlFile.getConfiguration().getBoolean("AllowPlayersToUseSameNickName"))) {
            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.NickNameAlreadyInUse")
                            .replace("%prefix%", prefix)
            );
            return CommandResult.SUCCESS;
        }

        if(
                (Bukkit.getOnlinePlayers()
                        .stream()
                        .anyMatch(currentPlayer -> currentPlayer.getName().equalsIgnoreCase(finalName))
                        || Stream.of(Bukkit.getOfflinePlayers())
                                .anyMatch(currentOfflinePlayer -> finalName.equalsIgnoreCase(currentOfflinePlayer.getName())))
                && !setupYamlFile.getConfiguration().getBoolean("AllowPlayersToNickAsKnownPlayers")
        ) {
            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.PlayerWithThisNameIsKnown")
                            .replace("%prefix%", prefix)
            );
            return CommandResult.SUCCESS;
        }

        if(name.equalsIgnoreCase(player.getName())) {
            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.CanNotNickAsSelf")
                            .replace("%prefix%", prefix)
            );
            return CommandResult.SUCCESS;
        }

        if(setupYamlFile.getConfiguration().getStringList("DisabledNickWorlds").contains(player.getWorld().getName())) {
            languageYamlFile.sendMessage(
                    player,
                    languageYamlFile.getConfigString(player, "Messages.DisabledWorld")
                            .replace("%prefix%", prefix)
            );
            return CommandResult.SUCCESS;
        }

        utils.performNick(player, new StringUtils(MojangAPI.correctName(name)).getColoredString());

        return CommandResult.SUCCESS;
    }

}
