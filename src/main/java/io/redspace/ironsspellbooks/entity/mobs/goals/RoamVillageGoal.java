package io.redspace.ironsspellbooks.entity.mobs.goals;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RoamVillageGoal extends PatrolNearLocationGoal {

    //    boolean wantsToBeInVillage;
    GlobalPos villagePoi;
    int searchCooldown;

    public RoamVillageGoal(CreatureEntity pMob, float radius, double pSpeedModifier) {
        super(pMob, radius, pSpeedModifier);
    }

    @Override
    protected @Nullable Vector3d getPosition() {
        if (villagePoi != null) {
            IronsSpellbooks.LOGGER.debug("RoamVillageGoal: finding position ({})", villagePoi.pos());

            return Vector3d.atBottomCenterOf(villagePoi.pos());
        }
        IronsSpellbooks.LOGGER.debug("RoamVillageGoal: village poi null. going to: {}", super.getPosition());

        return super.getPosition();
    }

    @Override
    public boolean canUse() {
        if (villagePoi == null && searchCooldown-- <= 0) {
            //IronsSpellbooks.LOGGER.debug("RoamVillageGoal.trying to find village (expensive?)");
            findVillagePoi();
            searchCooldown = 200;
        }
        //TODO: distance check too?

        var canUse = (this.mob.level.isDay() || isDuringRaid()) && villagePoi != null && super.canUse();
        //IronsSpellbooks.LOGGER.debug("RoamVillageGoal.canuse: {}", canUse);

        return canUse;
    }

    private boolean isDuringRaid() {
        //TODO: find out if a current raid is going on
        return false;
    }

    protected void findVillagePoi() {
        if (mob.level instanceof ServerWorld serverLevel) {
//            MinecraftServer minecraftserver = serverLevel.getServer();
//            ServerLevel serverlevel = minecraftserver.getLevel(serverLevel.dimension());
            Optional<BlockPos> optional1 = serverLevel.getPoiManager().find((poiTypeHolder) -> poiTypeHolder.is(PoiTypes.MEETING),
                    (x) -> true, mob.blockPosition(), 100, PointOfInterestManager.Status.ANY);
            optional1.ifPresent((blockPos -> this.villagePoi = GlobalPos.of(serverLevel.dimension(), blockPos)));

        }
    }
}