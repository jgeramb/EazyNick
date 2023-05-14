package com.justixdev.eazynick.commands.parameters;

import com.justixdev.eazynick.EazyNick;
import com.justixdev.eazynick.utilities.configuration.yaml.LanguageYamlFile;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Data
@RequiredArgsConstructor
public class CommandParameter {

    private final String name;
    private final ParameterType type;
    private Object value;

    public boolean isValid(CommandSender sender, String rawValue) {
        switch (this.type) {
            case NUMBER:
                try {
                    this.value = Integer.parseInt(rawValue);

                    return true;
                } catch (NumberFormatException ignore) {
                }
                break;
            case DECIMAL:
                try {
                    this.value = Double.parseDouble(rawValue);

                    return true;
                } catch (NumberFormatException ignore) {
                }
                break;
            case PLAYER:
                Player targetPlayer = Bukkit.getPlayer(rawValue);

                if(targetPlayer != null) {
                    this.value = targetPlayer;

                    return true;
                } else if(!rawValue.isEmpty()) {
                    EazyNick eazyNick = EazyNick.getInstance();
                    LanguageYamlFile languageYamlFile = eazyNick.getLanguageYamlFile();

                    languageYamlFile.sendMessage(
                            sender,
                            languageYamlFile.getConfigString((sender instanceof Player) ? (Player) sender : null, "Messages.PlayerNotFound")
                                    .replace("%prefix%", eazyNick.getUtils().getPrefix())
                    );
                }
                break;
            case TEXT:
                if(!rawValue.isEmpty()) {
                    this.value = rawValue;

                    return true;
                }
                break;
            case BOOL:
                boolean isTrue = rawValue.equalsIgnoreCase("true");

                if(isTrue || rawValue.equalsIgnoreCase("false")) {
                    this.value = isTrue;

                    return true;
                }
                break;
        }

        return false;
    }

    public int asNumber() {
        return (int) this.value;
    }

    public double asDecimal() {
        return (double) this.value;
    }

    public Player asPlayer() {
        return (Player) this.value;
    }

    public String asText() {
        return (String) this.value;
    }

    public boolean asBool() {
        return (boolean) this.value;
    }

    @Override
    public String toString() {
        return "CommandParameter{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }

}
