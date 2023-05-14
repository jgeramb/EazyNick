package com.justixdev.eazynick.commands.parameters;

import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Optional;

public class ParameterCombination {

    public static final ParameterCombination EMPTY = new ParameterCombination();

    @Getter
    private final CommandParameter[] parameters;

    public ParameterCombination(CommandParameter... parameters) {
        this.parameters = parameters;
    }

    public boolean matches(CommandSender sender, String[] args) {
        if(this.parameters.length != args.length)
            return false;

        for (int i = 0; i < args.length; i++) {
            if(!this.parameters[i].isValid(sender, args[i].trim()))
                return false;
        }

        return true;
    }

    public Optional<CommandParameter> withName(String name) {
        return Arrays.stream(this.parameters)
                .filter(parameter -> parameter.getName().equals(name))
                .findFirst();
    }

}
