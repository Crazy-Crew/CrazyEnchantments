package com.badbones69.crazyenchantments.paper.commands.simple;

import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.commands.wrapper.BaseCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.kyori.permissions.PermissionContext;
import com.ryderbelserion.fusion.kyori.permissions.enums.PermissionType;
import com.ryderbelserion.fusion.paper.builders.commands.context.PaperCommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/*@net.strokkur.commands.Command("blacksmith")
@Aliases({"bs", "bsmith"})
@Description("Opens the BlackSmith menu!")*/
public class BlackSmithCommand extends BaseCommand {

    /*@Executes
    void execute(CommandSender sender, Player player) {
        if (!sender.hasPermission("crazyenchantments.blacksmith.others")) {
            execute(sender);

            return;
        }
    }

    @Executes
    void execute(CommandSender sender) {

    }*/

    @Override
    public void run(@NotNull final PaperCommandContext context) {
        final CommandContext<CommandSourceStack> origin = context.getContext();

        final CommandSender sender = context.getSender();

        if (context.hasArgument("player") && sender.hasPermission("crazyenchantments.blacksmith.others")) {
            try {
                final PlayerSelectorArgumentResolver resolver = origin.getArgument("player", PlayerSelectorArgumentResolver.class);

                final Player target = resolver.resolve(origin.getSource()).getFirst();

                MenuManager.openBlackSmithMenu(target);
            } catch (final CommandSyntaxException exception) {
                this.fusion.log(Level.ERROR, "Failed to execute /blacksmith [player] command!", exception);
            }

            return;
        }

        if (!context.isPlayer()) {
            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());

            return;
        }

        MenuManager.openBlackSmithMenu(context.getPlayer());
    }

    @Override
    public @NotNull LiteralCommandNode<CommandSourceStack> literal() {
        final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("blacksmith").requires(this::requirement)
                .executes(context -> {
                    run(new PaperCommandContext(context));

                    return Command.SINGLE_SUCCESS;
                });

        final LiteralArgumentBuilder<CommandSourceStack> arg1 = root.then(Commands.argument("player", ArgumentTypes.player()))
                .requires(context -> context.getSender().hasPermission(getPermissions().getLast().getPermission()))
                .executes(context -> {
                    final PaperCommandContext origin = new PaperCommandContext(context);

                    origin.addArgument("player");

                    run(origin);

                    return Command.SINGLE_SUCCESS;
                });

        return root.then(arg1).build();
    }

    @Override
    public @NotNull final List<PermissionContext> getPermissions() {
        return List.of(
                new PermissionContext(
                        "crazyenhcantments.blacksmith",
                        "Gives access to /blacksmith!",
                        PermissionType.TRUE
                ),
                new PermissionContext(
                        "crazyenhcantments.blacksmith.others",
                        "Gives access to /blacksmith [player]!",
                        PermissionType.OP
                )
        );
    }

    @Override
    public final boolean requirement(@NotNull final CommandSourceStack context) {
        return context.getSender().hasPermission(getPermissions().getFirst().getPermission());
    }

    @Override
    public @NotNull final Collection<String> getAliases() {
        return Set.of("bsmith", "bs");
    }

    @Override
    public @NotNull final String getDescription() {
        return "Opens the BlackSmith menu!";
    }
}