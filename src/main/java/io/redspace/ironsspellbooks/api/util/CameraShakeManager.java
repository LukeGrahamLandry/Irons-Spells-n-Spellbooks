package io.redspace.ironsspellbooks.api.util;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.spells.EarthquakeAoe;
import io.redspace.ironsspellbooks.network.ClientboundSyncCameraShake;
import io.redspace.ironsspellbooks.setup.Messages;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

//TODO: make IManager and shit
@Mod.EventBusSubscriber
public class CameraShakeManager {
    public static final ArrayList<CameraShakeData> cameraShakeData = new ArrayList<>();
    public static ArrayList<CameraShakeData> clientCameraShakeData = new ArrayList<>();

//    @SubscribeEvent
//    public static void onLevelTick(TickEvent.LevelTickEvent event) {
//        if (event.phase != TickEvent.Phase.START && event.side == LogicalSide.SERVER) {
//            ServerLevel serverLevel = (ServerLevel) event.level;
//            ArrayList<CameraShakeData> complete = new ArrayList<>();
//            //IronsSpellbooks.LOGGER.debug("CameraShakeManager.onWorldTick: tick, side:{} phase:{} level:{}", event.side, event.phase, event.level.dimension());
//            for (CameraShakeData data : cameraShakeData) {
//                IronsSpellbooks.LOGGER.debug("{}/{}", data.tickCount, data.duration);
//                if (data.tickCount++ >= data.duration) {
//                    complete.add(data);
//                }
//            }
//            if (!complete.isEmpty()) {
//                IronsSpellbooks.LOGGER.debug("CameraShakeManager.onWorldTick: removing complete data");
//                cameraShakeData.removeAll(complete);
//                doSync();
//            }
//        }
//    }

    public static void addCameraShake(CameraShakeData data) {
        cameraShakeData.add(data);
        doSync();
    }

    public static void removeCameraShake(CameraShakeData data) {
        if (cameraShakeData.remove(data)) {
            doSync();
        }
    }

    private static void doSync() {
        Messages.sendToAllPlayers(new ClientboundSyncCameraShake(cameraShakeData));
    }

    public static void doSync(ServerPlayerEntity player) {
        Messages.sendToPlayer(new ClientboundSyncCameraShake(cameraShakeData), player);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void handleCameraShake(ViewportEvent.ComputeCameraAngles event) {
        if (clientCameraShakeData.isEmpty()) {
            return;
        }

        var player = event.getCamera().getEntity();
        List<CameraShakeData> closestPositions = clientCameraShakeData.stream().sorted((o1, o2) -> (int) (o1.origin.distanceToSqr(player.position()) - o2.origin.distanceToSqr(player.position()))).toList();
        var closestPos = closestPositions.get(0).origin;
        //.0039f is 1/15^2
        float intensity = (float) MathHelper.clampedLerp(1, 0, closestPos.distanceToSqr(player.position()) * 0.0039f);
        float f = (float) (player.tickCount + event.getPartialTick());
        float yaw = MathHelper.cos(f * 1.5f) * intensity * .35f;
        float pitch = MathHelper.cos(f * 2f) * intensity * .35f;
        float roll = MathHelper.sin(f * 2.2f) * intensity * .35f;
        event.setYaw(event.getYaw() + yaw);
        event.setRoll(event.getRoll() + roll);
        event.setPitch(event.getPitch() + pitch);
    }
}
