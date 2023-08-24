package io.redspace.ironsspellbooks.player;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
import io.redspace.ironsspellbooks.effect.AbyssalShroudEffect;
import io.redspace.ironsspellbooks.effect.AscensionEffect;
import io.redspace.ironsspellbooks.effect.InstantManaEffect;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.item.Scroll;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.PotionRegistry;
import io.redspace.ironsspellbooks.render.SpellRenderingHelper;
import io.redspace.ironsspellbooks.spells.CastSource;
import io.redspace.ironsspellbooks.spells.SpellType;
import io.redspace.ironsspellbooks.spells.blood.RayOfSiphoningSpell;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientPlayerEvents {
    //
    //  Handle (Client Side) cast duration
    //
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isClient() && event.phase == TickEvent.Phase.END && event.player == Minecraft.getInstance().player) {
            var level = Minecraft.getInstance().level;

            ClientMagicData.getCooldowns().tick(1);
            if (ClientMagicData.getCastDuration() > 0) {
                ClientMagicData.handleCastDuration();
            }

            if (level != null) {
                List<Entity> spellcasters = level.getEntities((Entity) null, event.player.getBoundingBox().inflate(64), (mob) -> mob instanceof PlayerEntity || mob instanceof AbstractSpellCastingMob);
                spellcasters.forEach((entity) -> {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    var spellData = ClientMagicData.getSyncedSpellData(livingEntity);
                    /*
                    Status Effect Visuals
                     */
                    if (spellData.hasEffect(SyncedSpellData.ABYSSAL_SHROUD)) {
                        AbyssalShroudEffect.ambientParticles(level, livingEntity);
                    }
                    if (spellData.hasEffect(SyncedSpellData.ASCENSION)) {
                        AscensionEffect.ambientParticles(level, livingEntity);
                    }
                    /*
                    Current Casting Spell Visuals
                     */
                    SpellType currentSpell = SpellType.getTypeFromValue(spellData.getCastingSpellId());
                    if (currentSpell == SpellType.RAY_OF_SIPHONING_SPELL) {
                        Vector3d impact = Utils.raycastForEntity(entity.level, entity, RayOfSiphoningSpell.getRange(0), true).getLocation().subtract(0, .25, 0);
                        for (int i = 0; i < 8; i++) {
                            Vector3d motion = new Vector3d(
                                    Utils.getRandomScaled(.2f),
                                    Utils.getRandomScaled(.2f),
                                    Utils.getRandomScaled(.2f)
                            );
                            entity.level.addParticle(ParticleHelper.SIPHON, impact.x + motion.x, impact.y + motion.y, impact.z + motion.z, motion.x, motion.y, motion.z);
                        }
                    }
                });
            }

        }
    }

    @SubscribeEvent
    public static void beforeLivingRender(RenderLivingEvent.Pre<? extends LivingEntity, ? extends EntityModel<? extends LivingEntity>> event) {
        var player = Minecraft.getInstance().player;
        if (player == null)
            return;

        var livingEntity = event.getEntity();
        if (livingEntity instanceof PlayerEntity || livingEntity instanceof AbstractSpellCastingMob) {

            var syncedData = ClientMagicData.getSyncedSpellData(livingEntity);
            if (syncedData.hasEffect(SyncedSpellData.TRUE_INVIS) && livingEntity.isInvisibleTo(player)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void afterLivingRender(RenderLivingEvent.Post<? extends LivingEntity, ? extends EntityModel<? extends LivingEntity>> event) {
        var livingEntity = event.getEntity();
        if (livingEntity instanceof PlayerEntity) {
            var syncedData = ClientMagicData.getSyncedSpellData(livingEntity);
            SpellRenderingHelper.renderSpellHelper(syncedData, livingEntity, event.getPoseStack(), event.getMultiBufferSource(), event.getPartialTick());
        }
    }

    @SubscribeEvent
    public static void imbuedWeaponTooltips(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        /*
        Universal info to display:
        - Unique Info
        - Cast Time
        - Mana Cost
        - Cooldown Time
        Scrolls show:
        - Level w/ rarity
        - School
        Spellbooks and Imbued weapons show:
        - [*name* *lvl*]
         */

        if (SpellData.getSpellData(stack).getSpellId() != 0) {
            var player = Minecraft.getInstance().player;
            if (player == null)
                return;
            //Scrolls take care of themselves
            if (!(stack.getItem() instanceof Scroll)) {
                var additionalLines = TooltipsUtils.formatActiveSpellTooltip(stack, CastSource.SWORD, player);
                //Add header to sword tooltip
                additionalLines.add(1, new TranslationTextComponent("tooltip.irons_spellbooks.imbued_tooltip").withStyle(TextFormatting.GRAY));
                //Indent the title because we have an additional header
                additionalLines.set(2, new StringTextComponent(" ").append(additionalLines.get(2)));
                //Make room for the stuff the advanced tooltips add to the tooltip
                if (event.getFlags().isAdvanced())
                    event.getToolTip().addAll(event.getToolTip().size() - 2, additionalLines);
                else
                    event.getToolTip().addAll(additionalLines);
                //event.getToolTip().add(Component.literal(additionalLines.size() + "").withStyle(ChatFormatting.BOLD));
            }

        }
    }

    @SubscribeEvent
    public static void customPotionTooltips(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        var mobEffects = PotionUtils.getMobEffects(stack);
        if (mobEffects.size() > 0) {
            for (EffectInstance mobEffectInstance : mobEffects) {
                if (mobEffectInstance.getEffect() == MobEffectRegistry.INSTANT_MANA.get()) {
                    int amp = mobEffectInstance.getAmplifier() + 1;
                    int addition = amp * InstantManaEffect.manaPerAmplifier;
                    int percent = (int) (amp * InstantManaEffect.manaPerAmplifierPercent * 100);
                    var description = new TranslationTextComponent("tooltip.irons_spellbooks.instant_mana_description", addition, percent).withStyle(TextFormatting.BLUE);

                    var header = new TranslationTextComponent("potion.whenDrank").withStyle(TextFormatting.DARK_PURPLE);
                    var tooltip = event.getToolTip();
                    var newLines = new ArrayList<ITextComponent>();
                    int i = tooltip.indexOf(header);

                    if (i < 0) {
                        newLines.add(StringTextComponent.EMPTY);
                        newLines.add(header);
                        newLines.add(description);
                        i = event.getFlags().isAdvanced() ? tooltip.size() - 2 : tooltip.size();
                    } else {
                        newLines.add(description);
                        i++;
                    }
                    tooltip.addAll(i, newLines);
                    return;
                }
            }
        }
    }
}