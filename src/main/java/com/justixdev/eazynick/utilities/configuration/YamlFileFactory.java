package com.justixdev.eazynick.utilities.configuration;

import com.justixdev.eazynick.EazyNick;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class YamlFileFactory implements BaseFileFactory<YamlConfiguration> {

    @Override
    public <V extends ConfigurationFile<YamlConfiguration>> V createConfigurationFile(EazyNick eazyNick, Class<V> type) {
        try {
            return type.getConstructor(eazyNick.getClass()).newInstance(eazyNick);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not create configuration: " + ex.getMessage());

            return null;
        }
    }

}
