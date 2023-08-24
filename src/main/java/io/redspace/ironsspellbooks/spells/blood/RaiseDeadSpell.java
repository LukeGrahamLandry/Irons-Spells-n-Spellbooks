package io.redspace.ironsspellbooks.spells.blood;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.mobs.SummonedSkeleton;
import io.redspace.ironsspellbooks.entity.mobs.SummonedZombie;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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

public class RaiseDeadSpell extends AbstractSpell {
    public RaiseDeadSpell() {
        this(1);
    }

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(LivingEntity caster) {
        return List.of(new TranslationTextComponent("ui.irons_spellbooks.summon_count", getLevel(caster)));
    }

    public static DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchool(SchoolType.BLOOD)
            .setMaxLevel(6)
            .setCooldownSeconds(150)
            .build();

    public RaiseDeadSpell(int level) {
        super(SpellType.RAISE_DEAD_SPELL);
        this.setLevel(level);
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 10;
        this.spellPowerPerLevel = 3;
        this.castTime = 30;
        this.baseManaCost = 50;

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
    public void onCast(World world, LivingEntity entity, PlayerMagicData playerMagicData) {
        int summonTime = 20 * 60 * 10;
        int level = getLevel(entity);
        for (int i = 0; i < level; i++) {
            boolean isSkeleton = world.random.nextDouble() < .3;
            var equipment = getEquipment(getSpellPower(entity), world.getRandom());

            MonsterEntity undead = isSkeleton ? new SummonedSkeleton(world, entity, true) : new SummonedZombie(world, entity, true);
            undead.finalizeSpawn((ServerWorld) world, world.getCurrentDifficultyAt(undead.getOnPos()), SpawnReason.MOB_SUMMONED, null, null);
            undead.addEffect(new EffectInstance(MobEffectRegistry.RAISE_DEAD_TIMER.get(), summonTime, 0, false, false, false));
            equip(undead, equipment);
            Vector3d spawn = entity.position();
            for (int j = 0; j < 4; j++) {
                //Going to try to spawn 3 times
                float distance = level / 4f + 1;
                distance *= (3 - j) / 3f;
                spawn = entity.getEyePosition().add(new Vector3d(0, 0, distance).yRot(((6.281f / level) * i)));
                spawn = new Vector3d(spawn.x, Utils.findRelativeGroundLevel(world, spawn, 5), spawn.z);
                if (!world.getBlockState(new BlockPos(spawn).below()).isAir())
                    break;
            }
            undead.moveTo(spawn.x, spawn.y, spawn.z, entity.getYRot(), 0);
            world.addFreshEntity(undead);
        }

        int effectAmplifier = level - 1;
        if(entity.hasEffect(MobEffectRegistry.RAISE_DEAD_TIMER.get()))
            effectAmplifier += entity.getEffect(MobEffectRegistry.RAISE_DEAD_TIMER.get()).getAmplifier() + 1;
        entity.addEffect(new EffectInstance(MobEffectRegistry.RAISE_DEAD_TIMER.get(), summonTime, effectAmplifier, false, false, true));

        super.onCast(world, entity, playerMagicData);
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
        int maxQuality = SpellType.RAISE_DEAD_SPELL.getMaxLevel() * spellPowerPerLevel + 15;

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
