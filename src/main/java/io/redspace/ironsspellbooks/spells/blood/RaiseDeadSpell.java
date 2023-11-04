package io.redspace.ironsspellbooks.spells.blood;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.entity.mobs.SummonedSkeleton;
import io.redspace.ironsspellbooks.entity.mobs.SummonedZombie;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.potion.EffectInstance;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class RaiseDeadSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(IronsSpellbooks.MODID, "raise_dead");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
            .setMaxLevel(6)
            .setCooldownSeconds(150)
            .build();

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(new TranslationTextComponent("ui.irons_spellbooks.summon_count", getLevel(spellLevel, caster)));
    }

    public RaiseDeadSpell() {
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 10;
        this.spellPowerPerLevel = 3;
        this.castTime = 30;
        this.baseManaCost = 50;

    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundRegistry.RAISE_DEAD_START.get());
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.RAISE_DEAD_FINISH.get());
    }

    @Override
    public void onCast(World world, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        int summonTime = 20 * 60 * 10;
        int level = getLevel(spellLevel, entity);
        float radius = 1.5f + .185f * level;
        for (int i = 0; i < level; i++) {
            boolean isSkeleton = Utils.random.nextDouble() < .3;
            ItemStack[] equipment = getEquipment(getSpellPower(spellLevel, entity), Utils.random);

            MonsterEntity undead = isSkeleton ? new SummonedSkeleton(world, entity, true) : new SummonedZombie(world, entity, true);
            undead.finalizeSpawn((ServerWorld) world, world.getCurrentDifficultyAt(undead.getOnPos()), SpawnReason.MOB_SUMMONED, null, null);
            undead.addEffect(new EffectInstance(MobEffectRegistry.RAISE_DEAD_TIMER.get(), summonTime, 0, false, false, false));
            equip(undead, equipment);
            var yrot = 6.281f / level * i + entity.yRot * Utils.DEG_TO_RAD;
            Vector3d spawn = Utils.moveToRelativeGroundLevel(world, entity.getEyePosition(0).add(new Vector3d(radius * MathHelper.cos(yrot), 0, radius * MathHelper.sin(yrot))), 10);
            undead.setPos(spawn.x, spawn.y, spawn.z);
            undead.setYRot(entity.yRot);
            undead.setOldPosAndRot();
            world.addFreshEntity(undead);
        }

        int effectAmplifier = level - 1;
        if (entity.hasEffect(MobEffectRegistry.RAISE_DEAD_TIMER.get()))
            effectAmplifier += entity.getEffect(MobEffectRegistry.RAISE_DEAD_TIMER.get()).getAmplifier() + 1;
        entity.addEffect(new EffectInstance(MobEffectRegistry.RAISE_DEAD_TIMER.get(), summonTime, effectAmplifier, false, false, true));

        super.onCast(world, spellLevel, entity, playerMagicData);
    }

    private void equip(MobEntity mob, ItemStack[] equipment) {
        mob.setItemSlot(EquipmentSlotType.FEET, equipment[0]);
        mob.setItemSlot(EquipmentSlotType.LEGS, equipment[1]);
        mob.setItemSlot(EquipmentSlotType.CHEST, equipment[2]);
        mob.setItemSlot(EquipmentSlotType.HEAD, equipment[3]);
        mob.setDropChance(EquipmentSlotType.FEET, 0.0F);
        mob.setDropChance(EquipmentSlotType.LEGS, 0.0F);
        mob.setDropChance(EquipmentSlotType.CHEST, 0.0F);
        mob.setDropChance(EquipmentSlotType.HEAD, 0.0F);
    }

    private ItemStack[] getEquipment(float power, RandomSource random) {
        Item[] leather = {Items.LEATHER_BOOTS, Items.LEATHER_LEGGINGS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET};
        Item[] chain = {Items.CHAINMAIL_BOOTS, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_HELMET};
        Item[] iron = {Items.IRON_BOOTS, Items.IRON_LEGGINGS, Items.IRON_CHESTPLATE, Items.IRON_HELMET};

        int minQuality = 12;
        int maxQuality = getMaxLevel() * spellPowerPerLevel + 15;

        ItemStack[] result = new ItemStack[4];
        for (int i = 0; i < 4; i++) {
            float quality = MathHelper.clamp((power + (random.nextIntBetweenInclusive(-3, 8)) - minQuality) / (maxQuality - minQuality), 0, .95f);
            if (random.nextDouble() < quality * quality) {
                if (quality > .85) {
                    result[i] = new ItemStack(iron[i]);
                } else if (quality > .65) {
                    result[i] = new ItemStack(chain[i]);
                } else if (quality > .15) {
                    result[i] = new ItemStack(leather[i]);
                } else {
                    result[i] = ItemStack.EMPTY;
                }
            } else {
                result[i] = ItemStack.EMPTY;
            }
        }
        return result;
    }
}
