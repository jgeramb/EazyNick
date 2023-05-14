package com.justixdev.eazynick.utilities.configuration.yaml;

import com.justixdev.eazynick.EazyNick;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

public class NickNameYamlFile extends YamlFile {

    public NickNameYamlFile(EazyNick eazyNick) {
        super(eazyNick, "", "nickNames");
    }

    @Override
    public void setDefaults() {
        if(!this.configuration.contains("NickNames")) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/nickNames.txt"))))) {
                this.configuration.set("NickNames", reader.lines().collect(Collectors.toList()));
            } catch (Exception ignore) {
            }
        }
    }

    @Override
    public void reload() {
        super.reload();

        if(this.utils != null) {
            this.utils.getNickNames().clear();
            this.utils.getNickNames().addAll(this.configuration.getStringList("NickNames"));
        }
    }

}