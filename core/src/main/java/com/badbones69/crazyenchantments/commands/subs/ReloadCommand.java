package com.badbones69.crazyenchantments.commands.subs;

import com.badbones69.crazyenchantments.commands.BaseCommand;
import us.crazycrew.crazyenchantments.enums.Mode;
import com.badbones69.crazyenchantments.commands.player.ISource;
import com.mojang.brigadier.context.CommandContext;
import org.jetbrains.annotations.NotNull;
import java.util.function.Function;

public class ReloadCommand<S> extends BaseCommand<S> {

    public ReloadCommand(@NotNull final Function<S, ISource> function) {
        super("Gives access to reloading the plugin", "crazyenchantments.reload", "reload", Mode.OP, function);
    }

    @Override
    protected int execute(@NotNull CommandContext<S> context) {
        this.plugin.reload(getSource(context).getAudience());

        return 1;
    }
}