package com.ryderbelserion.crazyenchantments.paper.commands.brigadier.types.admin;

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ryderbelserion.crazyenchantments.paper.CrazyEnchantments;
import com.ryderbelserion.crazyenchantments.paper.api.MessageKeys;
import com.ryderbelserion.crazyenchantments.paper.api.registry.MessageRegistry;
import com.ryderbelserion.crazyenchantments.paper.api.registry.EnchantmentRegistry;
import com.ryderbelserion.fusion.core.files.FileManager;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.api.commands.objects.AbstractPaperCommand;
import com.ryderbelserion.fusion.paper.api.commands.objects.AbstractPaperContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class CommandReload extends AbstractPaperCommand {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final EnchantmentRegistry enchantmentRegistry = this.plugin.getEnchantmentRegistry();

    private final MessageRegistry messageRegistry = this.plugin.getMessageRegistry();

    private final FileManager fileManager = this.plugin.getFileManager();

    private final FusionPaper fusion = this.plugin.getPaper();

    @Override
    public void execute(@NotNull final AbstractPaperContext context) {
        this.fusion.reload(false);

        this.fileManager.refresh(false);

        this.messageRegistry.populateMessages();

        this.enchantmentRegistry.reload();

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
                    execute(new AbstractPaperContext(context));

                    return com.mojang.brigadier.Command.SINGLE_SUCCESS;
                }).build();
    }

    @Override
    public @NotNull final PermissionDefault getPermissionMode() {
        return PermissionDefault.OP;
    }

    @Override
    public @NotNull final List<String> getPermissions() {
        return List.of("crazyenchantments.reload");
    }
}