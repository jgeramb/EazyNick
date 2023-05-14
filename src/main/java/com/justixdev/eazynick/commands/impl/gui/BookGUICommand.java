package com.justixdev.eazynick.commands.impl.gui;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.api.NickManager;
import com.justixdev.eazynick.api.events.PlayerUnnickEvent;
import com.justixdev.eazynick.commands.Command;
import com.justixdev.eazynick.commands.CommandResult;
import com.justixdev.eazynick.commands.CustomCommand;
import com.justixdev.eazynick.commands.parameters.CommandParameter;
import com.justixdev.eazynick.commands.parameters.ParameterCombination;
import com.justixdev.eazynick.commands.parameters.ParameterType;
import com.justixdev.eazynick.nms.guis.book.BookComponentBuilder;
import com.justixdev.eazynick.nms.guis.book.BookPage;
import com.justixdev.eazynick.nms.guis.book.NMSBookUtils;
import com.justixdev.eazynick.sql.MySQLNickManager;
import com.justixdev.eazynick.sql.MySQLPlayerDataManager;
import com.justixdev.eazynick.utilities.Utils;
import com.justixdev.eazynick.utilities.configuration.yaml.GUIYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import com.justixdev.eazynick.utilities.configuration.yaml.SetupYamlFile;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.justixdev.eazynick.nms.ReflectionHelper.NMS_VERSION;

@CustomCommand(name = "bookgui", description = "Opens a Hypixel-like book interface")
public class BookGUICommand extends Command {

    @Override
    public List<ParameterCombination> getCombinations() {
        return Arrays.asList(
                // step 1
                new ParameterCombination(),
                // step 2
                new ParameterCombination(
                        new CommandParameter("accept", ParameterType.BOOL)),
                // step 3
                new ParameterCombination(
                        new CommandParameter("accept", ParameterType.BOOL),
                        new CommandParameter("rank", ParameterType.TEXT)),
                // step 4
                new ParameterCombination(
                        new CommandParameter("accept", ParameterType.BOOL),
                        new CommandParameter("rank", ParameterType.TEXT),
                        new CommandParameter("skin", ParameterType.TEXT)),
                // step 5
                new ParameterCombination(
                        new CommandParameter("accept", ParameterType.BOOL),
                        new CommandParameter("rank", ParameterType.TEXT),
                        new CommandParameter("skin", ParameterType.TEXT),
                        new CommandParameter("nickname", ParameterType.TEXT)));
    }

    @Override
    public CommandResult execute(CommandSender sender, ParameterCombination args) {
        EazyNick eazyNick = EazyNick.getInstance();
        Utils utils = eazyNick.getUtils();
        SetupYamlFile setupYamlFile = eazyNick.getSetupYamlFile();
        LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();
        GUIYamlFile guiYamlFile = eazyNick.getGuiYamlFile();
        NMSBookUtils nmsBookUtils = eazyNick.getNmsBookUtils();
        MySQLNickManager mysqlNickManager = eazyNick.getMysqlNickManager();
        MySQLPlayerDataManager mysqlPlayerDataManager = eazyNick.getMysqlPlayerDataManager();

        String prefix = utils.getPrefix();
        Player player = (Player) sender;

        if(NMS_VERSION.equals("v1_7_R4")) {
            player.chat("/rankednickgui");
            return CommandResult.SUCCESS;
        }

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

                languageYamlFile.sendMessage(player, languageYamlFile.getConfigString(player, "Messages.Unnick")
                        .replace("%prefix%", prefix));
            }

            return CommandResult.SUCCESS;
        }

        if(!player.hasPermission("eazynick.gui.book"))
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

        Optional<Boolean> optionalAccept = args.withName("accept")
                .map(CommandParameter::asBool);
        Optional<String> optionalRank = args.withName("rank")
                .map(CommandParameter::asText);
        Optional<String> optionalSkin = args.withName("skin")
                .map(CommandParameter::asText);
        Optional<String> optionalNickName = args.withName("nickname")
                .map(CommandParameter::asText);

        // Step 1
        if(!optionalAccept.isPresent() && guiYamlFile.getConfiguration().getBoolean("BookGUI.Page1.Enabled")) {
            ArrayList<TextComponent> textComponents = new ArrayList<>();

            for(String lineText : guiYamlFile.getConfigString(player, "BookGUI.Page1.Text").split("%nl%"))
                textComponents.add(new TextComponent(lineText + "\n"));

            for(String lineText : guiYamlFile.getConfigString(player, "BookGUI.Accept.Text").split("%nl%")) {
                textComponents.add(new BookComponentBuilder(lineText + "\n")
                        .clickEvent(ClickEvent.Action.RUN_COMMAND,
                                "/bookgui true")
                        .hoverEvent(HoverEvent.Action.SHOW_TEXT,
                                guiYamlFile.getConfigString(player, "BookGUI.Accept.Hover"))
                        .build()
                );
            }

            nmsBookUtils.open(
                    player,
                    nmsBookUtils.create(
                            guiYamlFile.getConfigString(player, "BookGUI.Page1.Title"),
                            new BookPage(textComponents))
            );

            return CommandResult.SUCCESS;
        }

        boolean accept = optionalAccept.isPresent();

        if(!accept)
            return CommandResult.FAILURE_OTHER;

        // Step 2
        if(!optionalRank.isPresent()) {
            List<String> ranks = new ArrayList<>();
            List<TextComponent> textComponentsOfFirstPage = new ArrayList<>(),
                    textComponentsOfSecondPage = new ArrayList<>();

            for(String lineText : guiYamlFile.getConfigString(player, "BookGUI.Page2.Text").split("%nl%"))
                textComponentsOfFirstPage.add(new TextComponent(lineText + "\n"));

            for (int i = 1; i <= 18; i++) {
                String permission = guiYamlFile.getConfigString(player, "RankGUI.Rank" + i + ".Permission");

                if(!(guiYamlFile.getConfiguration().getBoolean("RankGUI.Rank" + i + ".Enabled")
                        && (permission.equalsIgnoreCase("NONE") || player.hasPermission(permission))))
                    continue;

                String rank = guiYamlFile.getConfigString(player, "RankGUI.Rank" + i + ".Rank"),
                        rankName = guiYamlFile.getConfigString(player, "RankGUI.Rank" + i + ".RankName");

                TextComponent textComponent = new BookComponentBuilder(
                        guiYamlFile.getConfigString(player, "BookGUI.Rank.Text")
                                .replace("%rank%", rank)
                                .replace("%nl%", "\n")
                )
                        .clickEvent(ClickEvent.Action.RUN_COMMAND,
                                "/bookgui true " + rankName)
                        .hoverEvent(HoverEvent.Action.SHOW_TEXT,
                                guiYamlFile.getConfigString(player, "BookGUI.Rank.Hover")
                                        .replace("%rank%", rank))
                        .build();

                if(textComponentsOfFirstPage.size() < 14)
                    textComponentsOfFirstPage.add(textComponent);
                else
                    textComponentsOfSecondPage.add(textComponent);

                ranks.add(rankName);
            }

            if(ranks.size() > 1)
                nmsBookUtils.open(
                        player,
                        nmsBookUtils.create(
                                guiYamlFile.getConfigString(player, "BookGUI.Page2.Title"),
                                new BookPage(textComponentsOfFirstPage),
                                new BookPage(textComponentsOfSecondPage)
                        )
                );
            else if(!ranks.isEmpty())
                player.chat("/bookgui true " + ranks.get(0));

            return CommandResult.SUCCESS;
        }

        // Step 3
        String rank = optionalRank.get();

        if(!optionalSkin.isPresent()) {
            if(!(setupYamlFile.getConfiguration().getBoolean("Settings.ChangeOptions.Skin"))) {
                player.chat("/bookgui true " + rank + " DEFAULT");
                return CommandResult.SUCCESS;
            }

            ArrayList<TextComponent> textComponents = new ArrayList<>();

            for(String lineText : guiYamlFile.getConfigString(player, "BookGUI.Page3.Text").split("%nl%"))
                textComponents.add(new TextComponent(lineText + "\n"));

            textComponents.add(new BookComponentBuilder(
                    guiYamlFile.getConfigString(player, "BookGUI.NormalSkin.Text")
                            .replace("%nl%", "\n"))
                    .clickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/bookgui true " + rank + " DEFAULT")
                    .hoverEvent(HoverEvent.Action.SHOW_TEXT,
                            guiYamlFile.getConfigString(player, "BookGUI.NormalSkin.Hover"))
                    .build()
            );

            textComponents.add(new BookComponentBuilder(
                    guiYamlFile.getConfigString(player, "BookGUI.SteveAlexSkin.Text")
                            .replace("%nl%", "\n"))
                    .clickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/bookgui true " + rank + " NORMAL")
                    .hoverEvent(HoverEvent.Action.SHOW_TEXT,
                            guiYamlFile.getConfigString(player, "BookGUI.SteveAlexSkin.Hover"))
                    .build()
            );

            textComponents.add(new BookComponentBuilder(
                    guiYamlFile.getConfigString(player, "BookGUI.RandomSkin.Text")
                            .replace("%nl%", "\n"))
                    .clickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/bookgui true " + rank + " RANDOM")
                    .hoverEvent(HoverEvent.Action.SHOW_TEXT,
                            guiYamlFile.getConfigString(player, "BookGUI.RandomSkin.Hover"))
                    .build()
            );

            if(setupYamlFile.getConfiguration().getBoolean("AllowBookGUISkinFromName")) {
                textComponents.add(new BookComponentBuilder(
                        guiYamlFile.getConfigString(player, "BookGUI.SkinFromName.Text")
                                .replace("%nl%", "\n"))
                        .clickEvent(ClickEvent.Action.RUN_COMMAND,
                                "/bookgui true " + rank + " SKINFROMNAME")
                        .hoverEvent(HoverEvent.Action.SHOW_TEXT,
                                guiYamlFile.getConfigString(player, "BookGUI.SkinFromName.Hover"))
                        .build()
                );
            }

            if(utils.getLastSkinNames().containsKey(player.getUniqueId())) {
                textComponents.add(new BookComponentBuilder(
                        utils.getLastSkinNames().containsKey(player.getUniqueId())
                                ? guiYamlFile.getConfigString(player, "BookGUI.ReuseSkin.Text")
                                        .replace("%skin%", utils.getLastSkinNames().get(player.getUniqueId()))
                                        .replace("%nl%", "\n")
                                : "")
                        .clickEvent(ClickEvent.Action.RUN_COMMAND,
                                "/bookgui true " + rank + " " + utils.getLastSkinNames().get(player.getUniqueId()))
                        .hoverEvent(HoverEvent.Action.SHOW_TEXT,
                                guiYamlFile.getConfigString(player, "BookGUI.ReuseSkin.Hover"))
                        .build()
                );
            }

            nmsBookUtils.open(
                    player,
                    nmsBookUtils.create(
                            guiYamlFile.getConfigString(player, "BookGUI.Page3.Title"),
                            new BookPage(textComponents)
                    )
            );

            return CommandResult.SUCCESS;
        }

        // Step 4
        String skin = optionalSkin.get();

        if(!optionalNickName.isPresent()) {
            if(!(setupYamlFile.getConfiguration().getBoolean("AllowBookGUICustomName")
                    && player.hasPermission("eazynick.nick.custom"))) {
                player.chat("/bookgui true " + rank + " " + skin + " RANDOM");
                return CommandResult.SUCCESS;
            }

            ArrayList<TextComponent> textComponents = new ArrayList<>();

            for(String lineText : guiYamlFile.getConfigString(player, "BookGUI.Page4.Text").split("%nl%"))
                textComponents.add(new TextComponent(lineText + "\n"));

            textComponents.add(new BookComponentBuilder(
                    guiYamlFile.getConfigString(player, "BookGUI.EnterName.Text")
                            .replace("%nl%", "\n"))
                    .clickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/bookgui true " + rank + " " + skin + " ENTERNAME")
                    .hoverEvent(HoverEvent.Action.SHOW_TEXT,
                            guiYamlFile.getConfigString(player, "BookGUI.EnterName.Hover"))
                    .build()
            );

            textComponents.add(new BookComponentBuilder(
                    guiYamlFile.getConfigString(player, "BookGUI.RandomName.Text")
                            .replace("%nl%", "\n"))
                    .clickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/bookgui true " + rank + " " + skin + " RANDOM")
                    .hoverEvent(HoverEvent.Action.SHOW_TEXT,
                            guiYamlFile.getConfigString(player, "BookGUI.RandomName.Hover"))
                    .build()
            );

            if(utils.getLastNickNames().containsKey(player.getUniqueId()))
                textComponents.add(new BookComponentBuilder(
                        guiYamlFile.getConfigString(player, "BookGUI.ReuseName.Text")
                                .replace("%name%", utils.getLastNickNames().get(player.getUniqueId()))
                                .replace("%nl%", "\n"))
                        .clickEvent(ClickEvent.Action.RUN_COMMAND,
                                "/guinick " + rank + " " + skin + " " + utils.getLastNickNames().get(player.getUniqueId()))
                        .hoverEvent(HoverEvent.Action.SHOW_TEXT,
                                guiYamlFile.getConfigString(player, "BookGUI.ReuseName.Hover")
                                        .replace("%name%", utils.getLastNickNames().get(player.getUniqueId())))
                        .build()
                );

            nmsBookUtils.open(
                    player,
                    nmsBookUtils.create(
                            guiYamlFile.getConfigString(player, "BookGUI.Page4.Title"),
                            new BookPage(textComponents)
                    )
            );

            return CommandResult.SUCCESS;
        }

        // Step 5
        String nickName = optionalNickName.get();

        if(nickName.equals("RANDOM")) {
            AtomicReference<String> currentName = new AtomicReference<>();

            do {
                currentName.set(utils.getNickNames().get((new Random().nextInt(utils.getNickNames().size()))));
            } while (utils.getNickedPlayers()
                    .values()
                    .stream()
                    .anyMatch(currentNickedPlayerData -> currentNickedPlayerData.getNickName().equalsIgnoreCase(currentName.get())));

            String name = currentName.get();
            ArrayList<TextComponent> textComponents = new ArrayList<>();

            for(String lineText : guiYamlFile.getConfigString(player, "BookGUI.Page5.Text")
                    .replace("%name%", name).split("%nl%"))
                textComponents.add(new TextComponent(lineText + "\n"));

            textComponents.add(new BookComponentBuilder(
                    guiYamlFile.getConfigString(player, "BookGUI.OptionUseName.Text")
                            .replace("%nl%", "\n"))
                    .clickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/guinick " + rank + " " + skin + " " + name)
                    .hoverEvent(HoverEvent.Action.SHOW_TEXT,
                            guiYamlFile.getConfigString(player, "BookGUI.OptionUseName.Hover"))
                    .build()
            );

            textComponents.add(new BookComponentBuilder(
                    guiYamlFile.getConfigString(player, "BookGUI.OptionTryAgain.Text")
                            .replace("%nl%", "\n"))
                    .clickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/bookgui true " + rank + " " + skin + " RANDOM")
                    .hoverEvent(HoverEvent.Action.SHOW_TEXT,
                            guiYamlFile.getConfigString(player, "BookGUI.OptionTryAgain.Hover"))
                    .build()
            );

            if(setupYamlFile.getConfiguration().getBoolean("AllowBookGUICustomName")
                    && player.hasPermission("eazynick.nick.custom"))
                textComponents.add(new BookComponentBuilder(
                        guiYamlFile.getConfigString(player, "BookGUI.OptionEnterName.Text")
                                .replace("%nl%", "\n"))
                        .clickEvent(ClickEvent.Action.RUN_COMMAND,
                                "/bookgui true " + rank + " " + skin + " ENTERNAME")
                        .hoverEvent(HoverEvent.Action.SHOW_TEXT,
                                guiYamlFile.getConfigString(player, "BookGUI.OptionEnterName.Hover"))
                        .build()
                );

            nmsBookUtils.open(
                    player,
                    nmsBookUtils.create(
                            guiYamlFile.getConfigString(player, "BookGUI.Page5.Title"),
                            new BookPage(textComponents)
                    )
            );
        } else if(nickName.equalsIgnoreCase("ENTERNAME")) {
            if(!(setupYamlFile.getConfiguration().getBoolean("AllowBookGUICustomName")
                    && (player.hasPermission("eazynick.nick.custom"))))
                return CommandResult.FAILURE_NO_PERMISSION;

            if(
                    NMS_VERSION.equals("v1_8_R1")
                    || !(setupYamlFile.getConfiguration().getBoolean("UseSignGUIForCustomName")
                            || setupYamlFile.getConfiguration().getBoolean("UseAnvilGUIForCustomName"))
            ) {
                utils.getPlayersTypingNameInChat().put(player.getUniqueId(), rank + " " + skin);

                player.closeInventory();

                languageYamlFile.sendMessage(
                        player,
                        languageYamlFile.getConfigString(player, "Messages.TypeNameInChat")
                                .replace("%prefix%", prefix)
                );
            } else
                eazyNick.getGuiManager().openCustomGUI(player, rank, skin);
        }

        return CommandResult.SUCCESS;
    }

}
