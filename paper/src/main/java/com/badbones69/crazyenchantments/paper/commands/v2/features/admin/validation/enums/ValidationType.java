package com.badbones69.crazyenchantments.paper.commands.v2.features.admin.validation.enums;

import org.jetbrains.annotations.NotNull;

public enum ValidationType {

    config_validator("ConfigValidator"),
    enchant_validator("EnchantValidator");

    private final String name;

    ValidationType(@NotNull final String name) {
        this.name = name;
    }

    public @NotNull final String getName() {
        return this.name;
    }

    public static ValidationType fromName(@NotNull final String name) {
        ValidationType type = null;

        for (final ValidationType key : ValidationType.values()) {
            if (key.getName().equalsIgnoreCase(name)) {
                type = key;

                break;
            }
        }

        return type;
    }
}