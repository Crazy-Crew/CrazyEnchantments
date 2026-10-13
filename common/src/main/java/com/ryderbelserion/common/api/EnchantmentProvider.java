package com.ryderbelserion.common.api;

import com.ryderbelserion.common.CrazyEnchantmentsPlugin;
import org.jspecify.annotations.NonNull;

public class EnchantmentProvider {

    private static CrazyEnchantmentsPlugin instance;

    public static CrazyEnchantmentsPlugin api() {
        if (instance == null) {
            throw new IllegalStateException("CrazyEnchantments API is not loaded.");
        }

        return instance;
    }

    public static void register(@NonNull final CrazyEnchantmentsPlugin instance) {
        if (EnchantmentProvider.instance != null) return;

        EnchantmentProvider.instance = instance;
    }

    public static void unregister() {
        EnchantmentProvider.instance = null;
    }
}