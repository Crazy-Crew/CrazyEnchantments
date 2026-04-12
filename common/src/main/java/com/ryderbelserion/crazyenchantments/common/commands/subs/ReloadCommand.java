package com.ryderbelserion.crazyenchantments.common.commands.subs;

import com.mojang.brigadier.context.CommandContext;
import com.ryderbelserion.crazyenchantments.api.constants.Messages;
import com.ryderbelserion.crazyenchantments.common.commands.BaseCommand;
import com.ryderbelserion.crazyenchantments.common.commands.player.ISource;
import com.ryderbelserion.crazyenchantments.common.api.enums.Mode;
import org.jetbrains.annotations.NotNull;
import java.util.function.Function;

public class ReloadCommand<S> extends BaseCommand<S> {

    public ReloadCommand(@NotNull final Function<S, ISource> function) {
        super("Gives access to reloading the plugin", "crazyenchantments.reload", "reload", Mode.OP, function);
    }

    @Override
    protected int execute(@NotNull CommandContext<S> context) {
        final ISource source = getSource(context);

        this.plugin.reload();

        this.adapter.sendMessage(source.getAudience(), Messages.reload_plugin);

        return 1;
    }
}