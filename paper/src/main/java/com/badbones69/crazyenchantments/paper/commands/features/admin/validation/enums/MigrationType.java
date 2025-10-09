package com.badbones69.crazyenchantments.paper.commands.features.admin.validation.enums;

import org.jetbrains.annotations.NotNull;

public enum MigrationType {

    tinker_migration("tinker_migration"),
    enchant_migration("enchant_migration");

    private final String name;

    MigrationType(@NotNull final String name) {
        this.name = name;
    }

    public @NotNull final String getName() {
        return this.name;
    }

    public static MigrationType fromName(@NotNull final String name) {
        MigrationType type = null;

        for (final MigrationType key : MigrationType.values()) {
            if (key.getName().equalsIgnoreCase(name)) {
                type = key;

                break;
            }
        }

        return type;
    }
}