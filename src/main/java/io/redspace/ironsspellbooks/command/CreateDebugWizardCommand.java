package io.redspace.ironsspellbooks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.entity.mobs.debug_wizard.DebugWizard;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import java.util.Arrays;
import net.minecraft.util.text.TranslationTextComponent;

public class CreateDebugWizardCommand {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.irons_spellbooks.create_debug_wizard.failed"));
    private static final SimpleCommandExceptionType ERROR_FAILED_MAX_LEVEL = new SimpleCommandExceptionType(new TranslationTextComponent("commands.irons_spellbooks.create_debug_wizard.failed_max_level"));

    public static void register(CommandDispatcher<CommandSource> pDispatcher) {
        pDispatcher.register(Commands.literal("createDebugWizard").requires((commandSourceStack) -> {
            return commandSourceStack.hasPermission(2);
        }).then(Commands.argument("spell", SpellArgument.spellArgument())
                .then(Commands.argument("spellLevel", IntegerArgumentType.integer(1))
                        .then(Commands.argument("targetsPlayer", BoolArgumentType.bool())
                                .then(Commands.argument("cancelAfterTicks", IntegerArgumentType.integer(0))
                                        .executes((ctx) -> {
                                            return createDebugWizard(
                                                    ctx.getSource(),
                                                    ctx.getArgument("spell", String.class),
                                                    IntegerArgumentType.getInteger(ctx, "spellLevel"),
                                                    BoolArgumentType.getBool(ctx, "targetsPlayer"),
                                                    IntegerArgumentType.getInteger(ctx, "cancelAfterTicks"));
                                        }))))));
    }

    private static int createDebugWizard(CommandSource source, String spellId, int spellLevel, boolean targetsPlayer, int cancelAfterTicks) throws CommandSyntaxException {
        if (!spellId.contains(":")) {
            spellId = IronsSpellbooks.MODID + ":" + spellId;
        }

        AbstractSpell spell = SpellRegistry.getSpell(spellId);

        if (spellLevel > spell.getMaxLevel()) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("commands.irons_spellbooks.create_spell.failed_max_level", spell.getSpellName(), spell.getMaxLevel())).create();
        }

        var serverPlayer = source.getPlayer();
        if (serverPlayer != null) {
            DebugWizard debugWizard = new DebugWizard(EntityRegistry.DEBUG_WIZARD.get(), serverPlayer.level, spell, spellLevel, targetsPlayer, cancelAfterTicks);
            debugWizard.moveTo(serverPlayer.position());
            if (serverPlayer.level.addFreshEntity(debugWizard)) {
                return 1;
            }
        }

        throw ERROR_FAILED.create();
    }
}
