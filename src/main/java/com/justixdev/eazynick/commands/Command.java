package com.justixdev.eazynick.commands;

import com.justixdev.eazynick.commands.parameters.ParameterCombination;
import lombok.Data;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public abstract class Command {

    protected List<String> aliases;

    public Command() {
        this.initAliases();
    }

    protected void initAliases() {
        this.aliases = new ArrayList<>();
    }

    public List<ParameterCombination> getCombinations() {
        return Collections.emptyList();
    }

    public abstract CommandResult execute(CommandSender sender, ParameterCombination args);

}