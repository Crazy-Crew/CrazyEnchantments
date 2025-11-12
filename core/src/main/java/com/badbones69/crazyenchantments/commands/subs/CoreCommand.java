package com.badbones69.crazyenchantments.commands.subs;

import com.badbones69.crazyenchantments.commands.BaseCommand;
import us.crazycrew.crazyenchantments.enums.Mode;
import com.badbones69.crazyenchantments.commands.player.ISource;
import com.mojang.brigadier.context.CommandContext;
import org.jetbrains.annotations.NotNull;
import java.util.function.Function;

public class CoreCommand<S> extends BaseCommand<S> {

    public CoreCommand(@NotNull final String permission,
                       @NotNull final String literal,
                       @NotNull final Function<S, ISource> function) {
        super("The base command for the plugin!", permission, literal, Mode.OP, function);

        then(new ReloadCommand<>(function));
    }

    @Override
    protected int execute(@NotNull CommandContext<S> context) {
        return 1;
    }
}