package com.justixdev.eazynick.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NameFormatting {

    private String chatPrefix, chatSuffix, tabPrefix, tabSuffix, tagPrefix, tagSuffix;

}
