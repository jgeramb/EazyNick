package com.justixdev.eazynick.commands;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommandMetaData {

    private final String name;
    private final String description;
    private final boolean playersOnly;

}
