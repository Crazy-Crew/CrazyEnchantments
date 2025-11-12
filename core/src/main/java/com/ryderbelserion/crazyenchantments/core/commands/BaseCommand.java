package com.ryderbelserion.crazyenchantments.core.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.ryderbelserion.crazyenchantments.api.CrazyEnchantmentsProvider;
import com.ryderbelserion.crazyenchantments.core.CrazyEnchantments;
import com.ryderbelserion.crazyenchantments.core.commands.player.ISource;
import com.ryderbelserion.crazyenchantments.core.enums.Mode;
import com.ryderbelserion.crazyenchantments.core.registry.UserRegistry;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class BaseCommand<S> extends LiteralArgumentBuilder<S> {

    protected final CrazyEnchantments plugin = (CrazyEnchantments) CrazyEnchantmentsProvider.getInstance();

    protected final UserRegistry userRegistry = this.plugin.getUserRegistry();

    private final Function<S, ISource> function;
    private final String permission;

    public BaseCommand(@NotNull final String description, @NotNull final String parent, @NotNull final Map<String, Boolean> children, @NotNull final String literal, @NotNull final Mode mode, @NotNull final Function<S, ISource> function) {
        super(literal);

        this.plugin.registerPermission(mode, this.permission = parent, description, children);

        this.function = function;

        requires(stack -> function.apply(stack).hasPermission(this.permission));

        executes(this::execute);
    }

    public BaseCommand(@NotNull final String description, @NotNull final String parent, @NotNull final String literal, @NotNull final Mode mode, @NotNull final Function<S, ISource> function) {
        this(description, parent, new HashMap<>(), literal, mode, function);
    }

    protected @NotNull final ISource getSource(@NotNull final CommandContext<S> context) {
        return this.function.apply(context.getSource());
    }

    protected abstract int execute(@NotNull final CommandContext<S> context);

    public @NotNull final String getPermission() {
        return this.permission;
    }
}