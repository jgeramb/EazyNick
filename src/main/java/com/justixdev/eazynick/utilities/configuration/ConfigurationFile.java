package com.justixdev.eazynick.utilities.configuration;

import java.io.File;

public interface ConfigurationFile<T> {

    void initConfiguration();

    void setDefaults();

    void save();

    void reload();

    File getFile();

    T getConfiguration();

}
