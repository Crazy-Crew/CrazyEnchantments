package com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.enums;

import org.jspecify.annotations.NonNull;

public enum MigrationType {

    BROKEN_ENCHANTMENTS("broken_enchantments"),
    LEGACY_TO_MINIMESSAGE("legacy_to_minimessage"),
    TINKER("tinker");

    private final String name;

    MigrationType(@NonNull final String name) {
        this.name = name;
    }

    public @NonNull final String getName() {
        return this.name;
    }
}