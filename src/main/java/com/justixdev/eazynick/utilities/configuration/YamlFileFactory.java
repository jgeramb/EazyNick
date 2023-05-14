package com.justixdev.eazynick.utilities.configuration;

import com.justixdev.eazynick.EazyNick;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import static com.justixdev.eazynick.nms.ReflectionHelper.newInstance;
import static com.justixdev.eazynick.nms.ReflectionHelper.types;

public class YamlFileFactory implements BaseFileFactory<YamlConfiguration> {

    @SuppressWarnings("unchecked")
    @Override
    public <V extends ConfigurationFile<YamlConfiguration>> V createConfigurationFile(EazyNick eazyNick, Class<V> type) {
        try {
            return (V) newInstance(type, types(eazyNick.getClass()), eazyNick);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not create configuration:");
            ex.printStackTrace();

            return null;
        }
    }

}
