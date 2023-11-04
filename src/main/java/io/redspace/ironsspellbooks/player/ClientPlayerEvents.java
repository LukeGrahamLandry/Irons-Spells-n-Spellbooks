package io.redspace.ironsspellbooks.player;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
import io.redspace.ironsspellbooks.effect.AbyssalShroudEffect;
import io.redspace.ironsspellbooks.effect.AscensionEffect;
import io.redspace.ironsspellbooks.effect.CustomDescriptionMobEffect;
import io.redspace.ironsspellbooks.effect.InstantManaEffect;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.item.Scroll;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.render.SpellRenderingHelper;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.spells.blood.RayOfSiphoningSpell;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.EntityModel;
import java.util.Arrays;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
            ClientWorld level = Minecraft.getInstance().level;

            ClientMagicData.getCooldowns().tick(1);
            if (ClientMagicData.getCastDuration() > 0) {
                ClientMagicData.handleCastDuration();
            }

            if (level != null) {
                List<Entity> spellcasters = level.getEntities((Entity) null, event.player.getBoundingBox().inflate(64), (mob) -> mob instanceof PlayerEntity || mob instanceof AbstractSpellCastingMob);
                spellcasters.forEach((entity) -> {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    SyncedSpellData spellData = ClientMagicData.getSyncedSpellData(livingEntity);
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
                    if (spellData.isCasting() && spellData.getCastingSpellId().equals(SpellRegistry.RAY_OF_SIPHONING_SPELL.get().getSpellId())) {
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
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null)
            return;

        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof PlayerEntity || livingEntity instanceof AbstractSpellCastingMob) {

            SyncedSpellData syncedData = ClientMagicData.getSyncedSpellData(livingEntity);
            if (syncedData.hasEffect(SyncedSpellData.TRUE_INVIS) && livingEntity.isInvisibleTo(player)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void afterLivingRender(RenderLivingEvent.Post<? extends LivingEntity, ? extends EntityModel<? extends LivingEntity>> event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof PlayerEntity) {
            SyncedSpellData syncedData = ClientMagicData.getSyncedSpellData(livingEntity);
            if (syncedData.isCasting()) {
                SpellRenderingHelper.renderSpellHelper(syncedData, livingEntity, event.getMatrixStack(), event.getBuffers(), event.getPartialRenderTick());
            }
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

        if (SpellData.getSpellData(stack) != SpellData.EMPTY) {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player == null)
                return;
            //Scrolls take care of themselves
            if (!(stack.getItem() instanceof Scroll)) {
                List<ITextComponent> additionalLines = TooltipsUtils.formatActiveSpellTooltip(stack, CastSource.SWORD, player);
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
        List<EffectInstance> mobEffects = PotionUtils.getMobEffects(stack);
        if (mobEffects.size() > 0) {
            for (EffectInstance mobEffectInstance : mobEffects) {
                if (mobEffectInstance.getEffect() instanceof CustomDescriptionMobEffect) {
                    CustomDescriptionMobEffect customDescriptionMobEffect = (CustomDescriptionMobEffect) mobEffectInstance.getEffect();
                    CustomDescriptionMobEffect.handleCustomPotionTooltip(stack, event.getToolTip(), event.getFlags().isAdvanced(), mobEffectInstance, customDescriptionMobEffect);
                }
            }
        }
    }
}