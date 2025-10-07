package com.badbones69.crazyenchantments.paper.api.objects;

import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;

public record CEOption(ItemBuilder itemBuilder, int slot, boolean inGUI, int cost, Currency currency) {}