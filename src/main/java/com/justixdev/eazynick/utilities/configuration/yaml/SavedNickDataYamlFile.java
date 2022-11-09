package com.justixdev.eazynick.utilities.configuration.yaml;

import com.justixdev.eazynick.EazyNick;

public class SavedNickDataYamlFile extends YamlFile {

    public SavedNickDataYamlFile(EazyNick eazyNick) {
        super(eazyNick, "", "savedNickData");
    }

    @Override
    public void initConfiguration() {
        setDefaults();
    }

    @Override
    public void setDefaults() {
    }

}