package com.justixdev.eazynick.commands;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.commands.parameters.ParameterCombination;
import com.justixdev.eazynick.commands.parameters.ParameterType;
import com.justixdev.eazynick.utilities.ClassFinder;
import com.justixdev.eazynick.utilities.Utils;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.stream.Stream;

public class CommandManager {

    @Getter
    private final LinkedHashMap<CommandMetaData, Command> commands = new LinkedHashMap<>();

    private final Utils utils;

    public CommandManager() {
        this.utils = EazyNick.getInstance().getUtils();

        ClassFinder.withAnnotation(getClass().getPackage().getName(), CustomCommand.class)
                .forEach(clazz -> {
                    CustomCommand annotation = clazz.getAnnotation(CustomCommand.class);

                    try {
                        if(clazz.getSuperclass() == Command.class)
                            this.commands.put(
                                    new CommandMetaData(annotation.name(), annotation.description(), annotation.playersOnly()),
                                    (Command) clazz.newInstance()
                            );
                    } catch (Exception ignore) {
                    }
                });
    }

    public boolean execute(CommandSender sender, String message) {
        String prefix = this.utils.getPrefix();
        String commandName = message.substring(0, message.contains(" ") ? message.indexOf(' ') : message.length());
        String[] args = Stream.of(message.substring(commandName.length()).trim().split("\\s+"))
                .filter(str -> !str.isEmpty())
                .toArray(String[]::new);

        return this.commands.entrySet().stream().anyMatch((commandEntry) -> {
            CommandMetaData metaData = commandEntry.getKey();
            Command command = commandEntry.getValue();

            if(metaData.getName().equalsIgnoreCase(commandName)
                    || command.getAliases().stream().anyMatch(commandName::equalsIgnoreCase)) {
                if(metaData.isPlayersOnly() && !(sender instanceof Player)) {
                    String notPlayerMessage = prefix + this.utils.getNotPlayer();

                    if (!notPlayerMessage.trim().isEmpty())
                        sender.sendMessage(notPlayerMessage);

                    return true;
                }

                Optional<ParameterCombination> validCombination = command.getCombinations()
                        .stream()
                        .filter(parameterCombination -> parameterCombination.matches(sender, args))
                        .findFirst();

                if(validCombination.isPresent() || command.getCombinations().isEmpty()) {
                    switch (command.execute(sender, validCombination.orElse(ParameterCombination.EMPTY))) {
                        case FAILURE_NO_PERMISSION:
                            String noPermissionMessage = this.utils.getNoPerm();

                            if (!noPermissionMessage.trim().isEmpty())
                                sender.sendMessage(noPermissionMessage);
                            break;
                        case FAILURE_OTHER:
                            sender.sendMessage(prefix + "§cFailed to execute command§7.");
                            break;
                        default:
                            break;
                    }
                } else {
                    sender.sendMessage(prefix + "§cUsage§7:");

                    command.getCombinations().forEach(combination -> {
                        StringBuilder combinationString = new StringBuilder();

                        Arrays.stream(combination.getParameters())
                                .map(parameter -> {
                                    StringBuilder parameterString = new StringBuilder();
                                    parameterString.append("§7<");
                                    parameterString.append(parameter.getName());

                                    if(!parameter.getType().equals(ParameterType.TEXT)) {
                                        if(!parameter.getType().equals(ParameterType.PLAYER)
                                                || !parameter.getName().toLowerCase().contains("player")) {
                                            parameterString.append(": §8");

                                            switch (parameter.getType()) {
                                                case NUMBER:
                                                    parameterString.append("number");
                                                    break;
                                                case DECIMAL:
                                                    parameterString.append("decimal");
                                                    break;
                                                case PLAYER:

                                                    parameterString.append("player");
                                                    break;
                                                case BOOL:
                                                    parameterString.append("true|false");
                                                    break;
                                            }
                                        }
                                    }

                                    parameterString.append("§7>");

                                    return parameterString.toString();
                                }).forEach(parameterString -> combinationString.append(' ').append(parameterString));

                        sender.sendMessage(prefix + "§a" + ((sender instanceof Player) ? "/" : "") + commandName.toLowerCase() + combinationString);
                    });
                }

                return true;
            }

            return false;
        });
    }

}
