package io.redspace.ironsspellbooks.capabilities.magic;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.network.ClientboundSyncEntityData;
import io.redspace.ironsspellbooks.network.ClientboundSyncPlayerData;
import io.redspace.ironsspellbooks.player.SpinAttackType;
import io.redspace.ironsspellbooks.setup.Messages;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;

public class SyncedSpellData {
    //syncedEffectFlags
    public static final long ANGEL_WINGS = 1;
    public static final long EVASION = 2;
    public static final long HEARTSTOP = 4;
    public static final long ABYSSAL_SHROUD = 8;
    public static final long ASCENSION = 16;
    public static final long TRUE_INVIS = 32;
    public static final long CHARGED = 64;

    //localEffectFlags
    public static final long HEAL_TARGET = 1;

    //TODO: may want to switch this to ServerPlayer.UUID
    private final int serverPlayerId;
    private @Nullable LivingEntity livingEntity;

    private boolean isCasting;
    private String castingSpellId;
    private int castingSpellLevel;
    private long syncedEffectFlags;
    private long localEffectFlags;
    private float heartStopAccumulatedDamage;
    private int evasionHitsRemaining;
    private SpinAttackType spinAttackType;

    //Use this on the client
    public SyncedSpellData(int serverPlayerId) {
        this.livingEntity = null;
        this.serverPlayerId = serverPlayerId;
        this.isCasting = false;
        this.castingSpellId = "";
        this.castingSpellLevel = 0;
        this.syncedEffectFlags = 0;
        this.localEffectFlags = 0;
        this.heartStopAccumulatedDamage = 0f;
        this.evasionHitsRemaining = 0;
        this.spinAttackType = SpinAttackType.RIPTIDE;
    }

    //Use this on the server
    public SyncedSpellData(LivingEntity livingEntity) {
        this(livingEntity == null ? -1 : livingEntity.getId());
        this.livingEntity = livingEntity;
    }

    public static final IDataSerializer<SyncedSpellData> SYNCED_SPELL_DATA = new IDataSerializer.ForValueType<SyncedSpellData>() {
        public void write(PacketBuffer buffer, SyncedSpellData data) {
            buffer.writeInt(data.serverPlayerId);
            buffer.writeBoolean(data.isCasting);
            buffer.writeUtf(data.castingSpellId);
            buffer.writeInt(data.castingSpellLevel);
            buffer.writeLong(data.syncedEffectFlags);
            buffer.writeFloat(data.heartStopAccumulatedDamage);
            buffer.writeInt(data.evasionHitsRemaining);
            buffer.writeEnum(data.spinAttackType);
        }

        public SyncedSpellData read(PacketBuffer buffer) {
            var data = new SyncedSpellData(buffer.readInt());
            data.isCasting = buffer.readBoolean();
            data.castingSpellId = buffer.readUtf();
            data.castingSpellLevel = buffer.readInt();
            data.syncedEffectFlags = buffer.readLong();
            data.heartStopAccumulatedDamage = buffer.readFloat();
            data.evasionHitsRemaining = buffer.readInt();
            data.spinAttackType = buffer.readEnum(SpinAttackType.class);
            return data;
        }
    };

    public void saveNBTData(CompoundNBT compound) {
        compound.putBoolean("isCasting", this.isCasting);
        compound.putString("castingSpellId", this.castingSpellId);
        compound.putInt("castingSpellLevel", this.castingSpellLevel);
        compound.putLong("effectFlags", this.syncedEffectFlags);
        compound.putFloat("heartStopAccumulatedDamage", this.heartStopAccumulatedDamage);
        compound.putFloat("evasionHitsRemaining", this.evasionHitsRemaining);
        //SpinAttack not saved
    }

    public void loadNBTData(CompoundNBT compound) {
        this.isCasting = compound.getBoolean("isCasting");
        this.castingSpellId = compound.getString("castingSpellId");
        this.castingSpellLevel = compound.getInt("castingSpellLevel");
        this.syncedEffectFlags = compound.getLong("effectFlags");
        this.heartStopAccumulatedDamage = compound.getFloat("heartStopAccumulatedDamage");
        this.evasionHitsRemaining = compound.getInt("evasionHitsRemaining");
        //SpinAttack not saved

    }

    public int getServerPlayerId() {
        return serverPlayerId;
    }

    public boolean hasEffect(long effectFlags) {
        return (this.syncedEffectFlags & effectFlags) == effectFlags;
    }

    public boolean hasLocalEffect(long effectFlags) {
        return (this.localEffectFlags & effectFlags) == effectFlags;
    }

    public void addLocalEffect(long effectFlags) {
        this.localEffectFlags |= effectFlags;
    }

    public void removeLocalEffect(long effectFlags) {
        this.localEffectFlags &= ~effectFlags;
    }

    public float getHeartstopAccumulatedDamage() {
        return heartStopAccumulatedDamage;
    }

    public boolean hasDodgeEffect() {
        return hasEffect(EVASION) || hasEffect(ABYSSAL_SHROUD);
    }

    public void setHeartstopAccumulatedDamage(float damage) {
        heartStopAccumulatedDamage = damage;
        doSync();
    }

    public SpinAttackType getSpinAttackType() {
        return spinAttackType;
    }

    public void setSpinAttackType(SpinAttackType spinAttackType) {
        this.spinAttackType = spinAttackType;
        doSync();
    }

    public int getEvasionHitsRemaining() {
        return evasionHitsRemaining;
    }

    public void subtractEvasionHit() {
        evasionHitsRemaining--;
        doSync();
    }

    public void setEvasionHitsRemaining(int hitsRemaining) {
        evasionHitsRemaining = hitsRemaining;
        doSync();
    }

    public void addHeartstopDamage(float damage) {
        heartStopAccumulatedDamage += damage;
        doSync();
    }

    public void addEffects(long effectFlags) {
        this.syncedEffectFlags |= effectFlags;
        doSync();
    }

    public void removeEffects(long effectFlags) {
        this.syncedEffectFlags &= ~effectFlags;
        doSync();
    }

    public void doSync() {
        if (livingEntity instanceof ServerPlayerEntity serverPlayer) {
            Messages.sendToPlayer(new ClientboundSyncPlayerData(this), serverPlayer);
            Messages.sendToPlayersTrackingEntity(new ClientboundSyncPlayerData(this), serverPlayer);
        } else if (livingEntity instanceof AbstractSpellCastingMob abstractSpellCastingMob) {
            Messages.sendToPlayersTrackingEntity(new ClientboundSyncEntityData(this, abstractSpellCastingMob), abstractSpellCastingMob);
        }
    }

    public void syncToPlayer(ServerPlayerEntity serverPlayer) {
        Messages.sendToPlayer(new ClientboundSyncPlayerData(this), serverPlayer);
    }

    public void setIsCasting(boolean isCasting, String castingSpellId, int castingSpellLevel) {
        this.isCasting = isCasting;
        this.castingSpellId = castingSpellId;
        this.castingSpellLevel = castingSpellLevel;
        doSync();
    }

    public boolean isCasting() {
        return isCasting;
    }

    public String getCastingSpellId() {
        return castingSpellId;
    }

    public int getCastingSpellLevel() {
        return castingSpellLevel;
    }

    @Override
    protected SyncedSpellData clone() {
        return new SyncedSpellData(this.livingEntity);
    }

    @Override
    public String toString() {
        return String.format("isCasting:%s, spellID:%s, spellLevel:%d, effectFlags:%d",
                isCasting,
                castingSpellId,
                castingSpellLevel,
                syncedEffectFlags);
    }
}
