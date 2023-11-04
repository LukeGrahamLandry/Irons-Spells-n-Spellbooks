package io.redspace.ironsspellbooks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
//import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.ItemInput;
import net.minecraft.util.registry.Registry;
import java.util.Arrays;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

import java.util.Set;
import java.util.stream.Collectors;

public class CreateImbuedSwordCommand {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.irons_spellbooks.create_imbued_sword.failed"));

    private static final SuggestionProvider<CommandSource> SWORD_SUGGESTIONS = (p_180253_, p_180254_) -> {
        Set<ResourceLocation> resources = Registry.ITEM.stream().filter((k) -> k instanceof SwordItem).map(Registry.ITEM::getKey).collect(Collectors.toSet());
        return ISuggestionProvider.suggestResource(resources, p_180254_);
    };

    public static void register(CommandDispatcher<CommandSource> pDispatcher) {

        pDispatcher.register(Commands.literal("createImbuedSword").requires((commandSourceStack) -> {
            return commandSourceStack.hasPermission(2);
        }).then(Commands.argument("item", ItemArgument.item()).suggests(SWORD_SUGGESTIONS)
                .then(Commands.argument("spell", SpellArgument.spellArgument())
                        .then(Commands.argument("level", IntegerArgumentType.integer(1)).executes((ctx) -> {
                            return createImbuedSword(ctx.getSource(), ctx.getArgument("item", ItemInput.class), ctx.getArgument("spell", String.class), IntegerArgumentType.getInteger(ctx, "level"));
                        })))));
    }

    private static int createImbuedSword(CommandSource source, ItemInput itemInput, String spell, int spellLevel) throws CommandSyntaxException {
        if (!spell.contains(":")) {
            spell = IronsSpellbooks.MODID + ":" + spell;
        }

        AbstractSpell abstractSpell = SpellRegistry.REGISTRY.get().getValue(new ResourceLocation(spell));

        if (spellLevel > abstractSpell.getMaxLevel()) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("commands.irons_spellbooks.create_spell.failed_max_level", abstractSpell.getSpellName(), abstractSpell.getMaxLevel())).create();
        }

        var serverPlayer = source.getPlayer();
        if (serverPlayer != null) {
            ItemStack itemstack = new ItemStack(itemInput.getItem());
            if (itemstack.getItem() instanceof SwordItem) {
                SpellData.setSpellData(itemstack, abstractSpell, spellLevel);
                if (serverPlayer.getInventory().add(itemstack)) {
                    return 1;
                }
            }
        }

        throw ERROR_FAILED.create();
    }
}
