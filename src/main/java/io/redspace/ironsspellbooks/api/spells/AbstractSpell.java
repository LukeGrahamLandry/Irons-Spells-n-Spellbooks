package io.redspace.ironsspellbooks.api.spells;

import com.google.common.util.concurrent.AtomicDouble;
import io.redspace.ironsspellbooks.api.magic.IMagicManager;
import net.minecraft.util.math.vector.Vector3f;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.events.SpellCastEvent;
import io.redspace.ironsspellbooks.api.item.IScroll;
import io.redspace.ironsspellbooks.api.item.ISpellbook;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.MagicHelper;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.api.item.curios.RingData;
import io.redspace.ironsspellbooks.damage.IndirectSpellDamageSource;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.network.ClientboundSyncMana;
import io.redspace.ironsspellbooks.network.ClientboundUpdateCastingState;
import io.redspace.ironsspellbooks.network.spell.ClientboundOnCastFinished;
import io.redspace.ironsspellbooks.network.spell.ClientboundOnCastStarted;
import io.redspace.ironsspellbooks.network.spell.ClientboundOnClientCast;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.player.ClientInputEvents;
import io.redspace.ironsspellbooks.player.ClientPlayerEvents;
import io.redspace.ironsspellbooks.player.ClientSpellCastHelper;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.setup.Messages;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.util.Log;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.util.text.TextFormatting;
import java.util.Arrays;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.IForgeRegistryEntry;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.redspace.ironsspellbooks.api.spells.SpellAnimations.*;

public abstract class AbstractSpell implements IForgeRegistryEntry<AbstractSpell> {
    private String spellID = null;
    private String deathMessageId = null;
    private String spellName = null;
    protected int baseManaCost;
    protected int manaCostPerLevel;
    protected int baseSpellPower;
    protected int spellPowerPerLevel;

    //All time values in ticks
    protected int castTime;
    //protected int cooldown;

    public AbstractSpell() {
    }

    public final String getSpellName() {
        if (spellName == null) {
            ResourceLocation resourceLocation = Objects.requireNonNull(getSpellResource());
            spellName = resourceLocation.getPath().intern();
        }

        //IronsSpellbooks.LOGGER.debug("AbstractSpell.getSpellName {}", spellName);

        return spellName;
    }

    public final String getSpellId() {
        if (spellID == null) {
            ResourceLocation resourceLocation = Objects.requireNonNull(getSpellResource());
            spellID = resourceLocation.toString().intern();
        }

        return spellID;
    }

    public final ResourceLocation getSpellIconResource() {
        return new ResourceLocation(getSpellResource().getNamespace(), "textures/gui/spell_icons/" + getSpellName() + ".png");
    }

    public int getMinRarity() {
        return ServerConfigs.getSpellConfig(this).minRarity().getValue();
    }

    public int getMaxLevel() {
        return ServerConfigs.getSpellConfig(this).maxLevel();
    }

    public int getMinLevel() {
        return 1;
    }

    public IFormattableTextComponent getDisplayName() {
        return new TranslationTextComponent(getComponentId());
    }

    public String getComponentId() {
        return String.format("spell.%s.%s", getSpellResource().getNamespace(), getSpellName());
    }

    protected abstract ResourceLocation getSpellResource();

    public abstract DefaultConfig getDefaultConfig();

    public abstract CastType getCastType();

    public SchoolType getSchoolType() {
        return ServerConfigs.getSpellConfig(this).school();
    }

    public Vector3f getTargetingColor() {
        return this.getSchoolType().getTargetingColor();
    }

    public int getLevel(int level, @Nullable LivingEntity caster) {
        int addition = 0;
        if (caster != null) {
            addition = CuriosApi.getCuriosHelper().findCurios(caster, this::filterCurios).size();
        }

        return level + addition;
    }

    private boolean filterCurios(ItemStack itemStack) {
        return RingData.hasRingData(itemStack) && RingData.getRingData(itemStack).getSpell().equals(this);
    }

    public int getManaCost(int level, @Nullable LivingEntity caster) {
        return (int) ((baseManaCost + manaCostPerLevel * (getLevel(level, caster) - 1)) * ServerConfigs.getSpellConfig(this).manaMultiplier());
    }

    public int getSpellCooldown() {
        return ServerConfigs.getSpellConfig(this).cooldownInTicks();
    }

    public int getCastTime(int spellLevel) {
        return this.castTime;
    }

    public ICastDataSerializable getEmptyCastData() {
        return null;
    }

    public abstract Optional<SoundEvent> getCastStartSound();

    public abstract Optional<SoundEvent> getCastFinishSound();

    /**
     * Default Animations Based on Cast Type. Override for specific spell-based animations
     */
    public AnimationHolder getCastStartAnimation() {
        switch (getCastType()) {
            case INSTANT:
                return ANIMATION_INSTANT_CAST;
            case CONTINUOUS:
                return ANIMATION_CONTINUOUS_CAST;
            case LONG:
                return ANIMATION_LONG_CAST;
            default:
                return AnimationHolder.none();
        }
    }

    /**
     * Default Animations Based on Cast Type. Override for specific spell-based animations
     */
    public AnimationHolder getCastFinishAnimation() {
        switch (getCastType()) {
            case LONG:
                return ANIMATION_LONG_CAST_FINISH;
            default:
                return AnimationHolder.none();
        }
    }

    public float getSpellPower(int spellLevel, @Nullable Entity sourceEntity) {

        double entitySpellPowerModifier = 1;
        double entitySchoolPowerModifier = 1;

        float configPowerModifier = (float) ServerConfigs.getSpellConfig(this).powerMultiplier();
        int level = getLevel(spellLevel, null);
        if (sourceEntity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) sourceEntity;
            level = getLevel(spellLevel, livingEntity);
            entitySpellPowerModifier = (float) livingEntity.getAttributeValue(AttributeRegistry.SPELL_POWER.get());
            entitySchoolPowerModifier = this.getSchoolType().getPowerFor(livingEntity);
        }


        return (float) ((baseSpellPower + spellPowerPerLevel * (level - 1)) * entitySpellPowerModifier * entitySchoolPowerModifier * configPowerModifier);
    }

    public float getEntityPowerMultiplier(@Nullable LivingEntity entity) {
        if (entity == null) {
            return 1f;
        }
        float entitySpellPowerModifier = (float) entity.getAttributeValue(AttributeRegistry.SPELL_POWER.get());
        double entitySchoolPowerModifier = this.getSchoolType().getPowerFor(entity);
        return (float) (entitySpellPowerModifier * entitySchoolPowerModifier);
    }

    public int getEffectiveCastTime(int spellLevel, @Nullable LivingEntity entity) {
        double entityCastTimeModifier = 1;
        if (entity != null) {
            /*
        Long/Charge casts trigger faster while continuous casts last longer.
        */
            if (getCastType() != CastType.CONTINUOUS)
                entityCastTimeModifier = 2 - Utils.softCapFormula(entity.getAttributeValue(AttributeRegistry.CAST_TIME_REDUCTION.get()));
            else
                entityCastTimeModifier = entity.getAttributeValue(AttributeRegistry.CAST_TIME_REDUCTION.get());
        }

        return Math.round(this.getCastTime(spellLevel) * (float) entityCastTimeModifier);
    }

    /**
     * returns true/false for success/failure to cast
     */
    public boolean attemptInitiateCast(ItemStack stack, int spellLevel, World level, PlayerEntity player, CastSource castSource, boolean triggerCooldown) {
        if (Log.SPELL_DEBUG) {
            IronsSpellbooks.LOGGER.debug("AbstractSpell.attemptInitiateCast isClient:{}, spell{}({})", level.isClientSide, this.getSpellId(), spellLevel);
        }

        if (level.isClientSide) {
            return false;
        }

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        MagicData playerMagicData = MagicData.getPlayerMagicData(serverPlayer);

        if (!playerMagicData.isCasting()) {
            int playerMana = playerMagicData.getMana();

            boolean hasEnoughMana = playerMana - getManaCost(spellLevel, serverPlayer) >= 0;
            boolean isSpellOnCooldown = playerMagicData.getPlayerCooldowns().isOnCooldown(this);

            if ((castSource == CastSource.SPELLBOOK || castSource == CastSource.SWORD) && isSpellOnCooldown) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(new TranslationTextComponent("ui.irons_spellbooks.cast_error_cooldown", getDisplayName()).withStyle(TextFormatting.RED)));
                return false;
            }

            if (castSource.consumesMana() && !hasEnoughMana) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(new TranslationTextComponent("ui.irons_spellbooks.cast_error_mana", getDisplayName()).withStyle(TextFormatting.RED)));
                return false;
            }

            if (MinecraftForge.EVENT_BUS.post(new SpellCastEvent(player, this.getSpellId(), spellLevel, getSchoolType(), castSource)))
                return false;

            if (!checkPreCastConditions(level, serverPlayer, playerMagicData))
                return false;


            CastType castType = getCastType();
            if (castType == CastType.INSTANT) {
                /*
                 * Immediately cast spell
                 */
                castSpell(level, spellLevel, serverPlayer, castSource, triggerCooldown);
            } else if (castType == CastType.LONG || castType == CastType.CONTINUOUS) {
                /*
                 * Prepare to cast spell (magic manager will pick it up by itself)
                 */
                int effectiveCastTime = getEffectiveCastTime(spellLevel, player);
                playerMagicData.initiateCast(this, getLevel(spellLevel, player), effectiveCastTime, castSource);
                onServerPreCast(player.level, spellLevel, player, playerMagicData);
                Messages.sendToPlayer(new ClientboundUpdateCastingState(getSpellId(), getLevel(spellLevel, player), effectiveCastTime, castSource), serverPlayer);
            }

            Messages.sendToPlayersTrackingEntity(new ClientboundOnCastStarted(serverPlayer.getUUID(), getSpellId(), spellLevel), serverPlayer, true);

            return true;
        } else {
            Utils.serverSideCancelCast(serverPlayer);
            return false;
        }
    }

    public void castSpell(World world, int spellLevel, ServerPlayerEntity serverPlayer, CastSource castSource, boolean triggerCooldown) {
        if (Log.SPELL_DEBUG) {
            IronsSpellbooks.LOGGER.debug("AbstractSpell.castSpell isClient:{}, spell{}({})", world.isClientSide, getSpellId(), spellLevel);
        }

        IMagicManager magicManager = MagicHelper.MAGIC_MANAGER;
        MagicData playerMagicData = MagicData.getPlayerMagicData(serverPlayer);

        if (castSource.consumesMana()) {
            int newMana = playerMagicData.getMana() - getManaCost(spellLevel, serverPlayer);
            magicManager.setPlayerCurrentMana(serverPlayer, newMana);
            Messages.sendToPlayer(new ClientboundSyncMana(playerMagicData), serverPlayer);
        }

        if (triggerCooldown) {
            magicManager.addCooldown(serverPlayer, this, castSource);
        }

        onCast(world, spellLevel, serverPlayer, playerMagicData);
        Messages.sendToPlayer(new ClientboundOnClientCast(this.getSpellId(), this.getLevel(spellLevel, serverPlayer), castSource, playerMagicData.getAdditionalCastData()), serverPlayer);

        if (getCastType() == CastType.INSTANT) {
            onServerCastComplete(world, spellLevel, serverPlayer, playerMagicData, false);
        }

        if (serverPlayer.getMainHandItem().getItem() instanceof ISpellbook || serverPlayer.getMainHandItem().getItem() instanceof IScroll)
            playerMagicData.setPlayerCastingItem(serverPlayer.getMainHandItem());
        else
            playerMagicData.setPlayerCastingItem(serverPlayer.getOffhandItem());

    }

    /**
     * The primary spell effect sound and particle handling goes here. Called Client Side only
     */
    public void onClientCast(World level, int spellLevel, LivingEntity entity, ICastData castData) {
        if (Log.SPELL_DEBUG) {
            IronsSpellbooks.LOGGER.debug("AbstractSpell.onClientCast isClient:{}, spell{}({})", level.isClientSide, getSpellId(), spellLevel);
        }
        playSound(getCastFinishSound(), entity, true);
    }

    /**
     * The primary spell effect handling goes here. Called Server Side
     */
    public void onCast(World level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        if (Log.SPELL_DEBUG) {
            IronsSpellbooks.LOGGER.debug("AbstractSpell.onCast isClient:{}, spell{}({}), pmd:{}", level.isClientSide, getSpellId(), spellLevel, playerMagicData);
        }

        playSound(getCastFinishSound(), entity, true);
    }

    protected void playSound(Optional<SoundEvent> sound, Entity entity, boolean playDefaultSound) {
        if (sound.isPresent()) {
            entity.playSound(sound.get(), 2.0f, .9f + Utils.random.nextFloat() * .2f);
        } else if (playDefaultSound) {
            entity.playSound(defaultCastSound(), 2.0f, .9f + Utils.random.nextFloat() * .2f);
        }
    }

    /**
     * Server Side. Used to see if a spell is allowed to be cast, such as if it requires a target but finds none
     */
    public boolean checkPreCastConditions(World level, LivingEntity entity, MagicData playerMagicData) {
        return true;
    }

    protected void playSound(Optional<SoundEvent> sound, Entity entity) {
        playSound(sound, entity, false);
    }

    private SoundEvent defaultCastSound() {
        return this.getSchoolType().getCastSound();
    }

    /**
     * Called on the server when a spell finishes casting or is cancelled, used for any cleanup or extra functionality
     */
    public void onServerCastComplete(World level, int spellLevel, LivingEntity entity, MagicData playerMagicData, boolean cancelled) {
        if (Log.SPELL_DEBUG) {
            IronsSpellbooks.LOGGER.debug("AbstractSpell.onServerCastComplete isClient:{}, spell{}({}), pmd:{}, cancelled:{}", level.isClientSide, getSpellId(), spellLevel, playerMagicData, cancelled);
        }

        playerMagicData.resetCastingState();
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) entity;
            Messages.sendToPlayersTrackingEntity(new ClientboundOnCastFinished(serverPlayer.getUUID(), getSpellId(), cancelled), serverPlayer, true);
        }
    }

    /**
     * Called once just before executing onCast. Can be used for client side sounds and particles
     */
    public void onClientPreCast(World level, int spellLevel, LivingEntity entity, Hand hand, @Nullable MagicData playerMagicData) {
        if (Log.SPELL_DEBUG) {
            IronsSpellbooks.LOGGER.debug("AbstractSpell.onClientPreCast isClient:{}, spell{}({}), pmd:{}", level.isClientSide, getSpellId(), spellLevel, playerMagicData);
        }
        if (this.getCastType().immediatelySuppressRightClicks()) {
            if (ClientInputEvents.isUseKeyDown) {
                ClientSpellCastHelper.setSuppressRightClicks(true);
            }
        }
        playSound(getCastStartSound(), entity);
    }

    /**
     * Called once just before executing onCast. Can be used for server side sounds and particles
     */
    public void onServerPreCast(World level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
        if (Log.SPELL_DEBUG) {
            IronsSpellbooks.LOGGER.debug("AbstractSpell.onServerPreCast isClient:{}, spell{}({}), pmd:{}", level.isClientSide, getSpellId(), level, playerMagicData);
        }
        playSound(getCastStartSound(), entity);
    }

    /**
     * Called on the server each tick while casting.
     */
    public void onServerCastTick(World level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {

    }

    /**
     * Used by AbstractSpellCastingMob to determine if the cast is no longer valid (ie player out of range of a particular spell). Override to create spell-specific criteria
     */
    public boolean shouldAIStopCasting(int spellLevel, MobEntity mob, LivingEntity target) {
        return false;
    }

    public List<IFormattableTextComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return Arrays.asList();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof AbstractSpell) {
            AbstractSpell other = (AbstractSpell) obj;
            return this.getSpellResource().equals(other.getSpellResource());
        }

        return false;
    }

    private volatile List<Double> rarityWeights;

    private void initializeRarityWeights() {
        synchronized (SpellRegistry.none()) {
            if (rarityWeights == null) {
                int minRarity = getMinRarity();
                int maxRarity = getMaxRarity();
                List<Double> rarityRawConfig = SpellRarity.getRawRarityConfig();
                List<Double> rarityConfig = SpellRarity.getRarityConfig();
                //IronsSpellbooks.LOGGER.debug("rarityRawConfig: {} rarityConfig:{}, {}, {}", rarityRawConfig.size(), rarityConfig.size(), this.hashCode(), this.name());

                List<Double> rarityRawWeights;
                if (minRarity != 0) {
                    //Must balance remaining weights

                    List<Double> subList = rarityRawConfig.subList(minRarity, maxRarity + 1);
                    double subtotal = subList.stream().reduce(0d, Double::sum);
                    rarityRawWeights = subList.stream().map(item -> ((item / subtotal) * (1 - subtotal)) + item).collect(Collectors.toList());

                    AtomicDouble counter = new AtomicDouble();
                    rarityWeights = new ArrayList<>();
                    rarityRawWeights.forEach(item -> {
                        rarityWeights.add(counter.addAndGet(item));
                    });
                } else {
                    //rarityRawWeights = rarityRawConfig;
                    rarityWeights = rarityConfig;
                }
            }
        }
    }

    private final int maxRarity = SpellRarity.LEGENDARY.getValue();

    public SpellRarity getRarity(int level) {
        if (rarityWeights == null) {
            initializeRarityWeights();
        }

        int maxLevel = getMaxLevel();
        int maxRarity = getMaxRarity();
        if (maxLevel == 1)
            return SpellRarity.values()[getMinRarity()];
        double percentOfMaxLevel = (double) level / (double) maxLevel;

        //irons_spellbooks.LOGGER.debug("getRarity: {} {} {} {} {} {}", this.toString(), rarityRawWeights, rarityWeights, percentOfMaxLevel, minRarity, maxRarity);

        int lookupOffset = maxRarity + 1 - rarityWeights.size();

        for (int i = 0; i < rarityWeights.size(); i++) {
            if (percentOfMaxLevel <= rarityWeights.get(i)) {
                return SpellRarity.values()[i + lookupOffset];
            }
        }

        return SpellRarity.COMMON;
    }

    public String getDeathMessageId() {
        if (deathMessageId == null) {
            deathMessageId = getSpellId().replace(':', '.');
        }

        return deathMessageId;
    }

    public final DamageSource getDamageSource(Entity attacker) {
        return getDamageSource(null, attacker);
    }

    public DamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
        return projectile == null ? new SpellDamageSource(attacker, this) : new IndirectSpellDamageSource(projectile, attacker, this);
    }

    public boolean isEnabled() {
        return ServerConfigs.getSpellConfig(this).enabled();
    }

    public int getMaxRarity() {
        return maxRarity;
    }

    public int getMinLevelForRarity(SpellRarity rarity) {
        if (rarityWeights == null) {
            initializeRarityWeights();
        }

        int minRarity = getMinRarity();
        int maxLevel = getMaxLevel();
        if (rarity.getValue() < minRarity) {
            return 0;
        }

        if (rarity.getValue() == minRarity) {
            return 1;
        }

        return (int) (rarityWeights.get(rarity.getValue() - (1 + minRarity)) * maxLevel) + 1;
    }

    @Override
    public AbstractSpell setRegistryName(ResourceLocation name) {
        assert name.equals(this.getSpellResource());
        return this;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return getSpellResource();
    }

    @Override
    public Class<AbstractSpell> getRegistryType() {
        return AbstractSpell.class;
    }
}
