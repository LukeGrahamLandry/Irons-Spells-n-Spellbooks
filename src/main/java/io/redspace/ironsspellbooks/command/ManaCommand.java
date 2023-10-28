package io.redspace.ironsspellbooks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.network.ClientboundSyncMana;
import io.redspace.ironsspellbooks.setup.Messages;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.Collection;

public class ManaCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> command = dispatcher.register(Commands.literal("mana")
                .requires((p) -> p.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes((context) -> changeMana(context.getSource(), EntityArgument.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "amount"), true)))))
                .then(Commands.literal("add")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes((context) -> changeMana(context.getSource(), EntityArgument.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "amount"), false)))))
        );
    }

    private static int changeMana(CommandSource source, Collection<ServerPlayerEntity> targets, int amount, boolean set) {
        targets.forEach((serverPlayer -> {
            MagicData pmg = MagicData.getPlayerMagicData(serverPlayer);
            var base = set ? 0 : pmg.getMana();
            pmg.setMana(amount + base);
            Messages.sendToPlayer(new ClientboundSyncMana(pmg), serverPlayer);
        }));
        String s = set ? "set" : "add";
        if (targets.size() == 1) {
            source.sendSuccess(ITextComponent.translatable("commands.mana." + s + ".success.single", amount, targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendSuccess(ITextComponent.translatable("commands.mana." + s + ".success.multiple", amount, targets.size()), true);
        }

        return targets.size();
    }


}
