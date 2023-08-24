package io.redspace.ironsspellbooks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
import io.redspace.ironsspellbooks.spells.SpellType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.ItemInput;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraftforge.server.command.EnumArgument;

import java.util.stream.Collectors;

public class CreateImbuedSwordCommand {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(ITextComponent.translatable("commands.irons_spellbooks.create_imbued_sword.failed"));
    private static final SimpleCommandExceptionType ERROR_FAILED_MAX_LEVEL = new SimpleCommandExceptionType(ITextComponent.translatable("commands.irons_spellbooks.create_imbued_sword.failed_max_level"));

    private static final SuggestionProvider<CommandSource> SWORD_SUGGESTIONS = (p_180253_, p_180254_) -> {
        var resources = Registry.ITEM.stream().filter((k) -> k instanceof SwordItem).map(Registry.ITEM::getKey).collect(Collectors.toSet());
        return ISuggestionProvider.suggestResource(resources, p_180254_);
    };

    public static void register(CommandDispatcher<CommandSource> pDispatcher, CommandBuildContext context) {

        pDispatcher.register(Commands.literal("createImbuedSword").requires((commandSourceStack) -> {
            return commandSourceStack.hasPermission(2);
        }).then(Commands.argument("item", ItemArgument.item(context)).suggests(SWORD_SUGGESTIONS)
                .then(Commands.argument("spellType", EnumArgument.enumArgument(SpellType.class))
                        .then(Commands.argument("level", IntegerArgumentType.integer(1)).executes((ctx) -> {
                            return createImbuedSword(ctx.getSource(), ctx.getArgument("item", ItemInput.class), ctx.getArgument("spellType", SpellType.class), IntegerArgumentType.getInteger(ctx, "level"));
                        })))));
    }

    private static int createImbuedSword(CommandSource source, ItemInput itemInput, SpellType spellType, int spellLevel) throws CommandSyntaxException {
        if (spellLevel > spellType.getMaxLevel()) {
            throw new SimpleCommandExceptionType(ITextComponent.translatable("commands.irons_spellbooks.create_spell.failed_max_level", spellType, spellType.getMaxLevel())).create();
        }

        var serverPlayer = source.getPlayer();
        if (serverPlayer != null) {
            ItemStack itemstack = new ItemStack(itemInput.getItem());
            if (itemstack.getItem() instanceof SwordItem) {
                SpellData.setSpellData(itemstack, spellType, spellLevel);
                if (serverPlayer.getInventory().add(itemstack)) {
                    return 1;
                }
            }
        }

        throw ERROR_FAILED.create();
    }
}
