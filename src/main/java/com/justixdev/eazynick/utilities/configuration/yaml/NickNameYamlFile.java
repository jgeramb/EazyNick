package com.justixdev.eazynick.utilities.configuration.yaml;

import com.justixdev.eazynick.EazyNick;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class NickNameYamlFile extends YamlFile {

    public NickNameYamlFile(EazyNick eazyNick) {
        super(eazyNick, "", "nickNames");
    }

    @Override
    public void setDefaults() {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/nickNames.txt"))))) {
            configuration.addDefault("NickNames", reader.lines().collect(Collectors.toList()));
        } catch (Exception ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load nicknames: " + ex.getMessage());
        }
    }

}