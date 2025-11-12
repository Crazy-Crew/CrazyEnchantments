package com.ryderbelserion.crazyenchantments.paper.commands.brigadier.types.admin;

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ryderbelserion.crazyenchantments.paper.CrazyEnchantmentsPlatform;
import com.ryderbelserion.crazyenchantments.core.constants.MessageKeys;
import com.ryderbelserion.crazyenchantments.core.registry.MessageRegistry;
import com.ryderbelserion.fusion.paper.api.commands.objects.PaperCommand;
import com.ryderbelserion.fusion.paper.api.commands.objects.PaperCommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class CommandReload extends PaperCommand {

    private final CrazyEnchantmentsPlatform platform;
    private final MessageRegistry messageRegistry;

    public CommandReload(@NotNull final CrazyEnchantmentsPlatform platform) {
        this.platform = platform;
        this.messageRegistry = this.platform.getMessageRegistry();
    }

    @Override
    public void execute(@NotNull final PaperCommandContext context) {
        this.platform.reload();

        this.messageRegistry.getMessage(MessageKeys.reload_plugin).send(context.getCommandSender());
    }

    @Override
    public final boolean requirement(@NotNull final CommandSourceStack source) {
        return source.getSender().hasPermission(getPermissions().getFirst());
    }

    @Override
    public @NotNull final LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("reload")
                .requires(this::requirement)
                .executes(context -> {
                    execute(new PaperCommandContext(context));

                    return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                }).build();
    }

    @Override
    public @NotNull final List<String> getPermissions() {
        return List.of("crazyenchantments.reload");
    }
}