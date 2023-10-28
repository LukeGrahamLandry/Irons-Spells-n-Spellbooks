package io.redspace.ironsspellbooks.player;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.spells.ender.TeleportSpell;
import io.redspace.ironsspellbooks.spells.holy.CloudOfRegenerationSpell;
import io.redspace.ironsspellbooks.spells.holy.FortifySpell;
import io.redspace.ironsspellbooks.spells.ice.FrostStepSpell;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.UUID;

import static io.redspace.ironsspellbooks.config.ClientConfigs.SHOW_FIRST_PERSON_ARMS;
import static io.redspace.ironsspellbooks.config.ClientConfigs.SHOW_FIRST_PERSON_ITEMS;

public class ClientSpellCastHelper {
    /**
     * Right Click Suppression
     */
    private static boolean suppressRightClicks;

    public static boolean shouldSuppressRightClicks() {
        return suppressRightClicks;
    }

    public static void setSuppressRightClicks(boolean suppressRightClicks) {
        //Ironsspellbooks.logger.debug("ClientSpellCastHelper.setSuppressRightClicks {}", suppressRightClicks);
        ClientSpellCastHelper.suppressRightClicks = suppressRightClicks;
    }

    /**
     * Handle Network Triggered Particles
     */
    public static void handleClientboundBloodSiphonParticles(Vector3d pos1, Vector3d pos2) {
        if (Minecraft.getInstance().player == null)
            return;
        World level = Minecraft.getInstance().player.level;
        Vector3d direction = pos2.subtract(pos1).scale(.1f);
        for (int i = 0; i < 40; i++) {
            Vector3d scaledDirection = direction.scale(1 + Utils.getRandomScaled(.35));
            Vector3d random = new Vector3d(Utils.getRandomScaled(.08f), Utils.getRandomScaled(.08f), Utils.getRandomScaled(.08f));
            level.addParticle(ParticleHelper.BLOOD, pos1.x, pos1.y, pos1.z, scaledDirection.x + random.x, scaledDirection.y + random.y, scaledDirection.z + random.z);
        }
    }

    public static void handleClientsideHealParticles(Vector3d pos) {
        //Copied from arrow because these particles use their motion for color??
        ClientPlayerEntity player = Minecraft.getInstance().player;

        if (player != null) {
            World level = Minecraft.getInstance().player.level;
            int i = PotionUtils.getColor(Potion.byName("healing"));
            double d0 = (double) (i >> 16 & 255) / 255.0D;
            double d1 = (double) (i >> 8 & 255) / 255.0D;
            double d2 = (double) (i >> 0 & 255) / 255.0D;

            for (int j = 0; j < 15; ++j) {
                level.addParticle(ParticleTypes.ENTITY_EFFECT, pos.x + Utils.getRandomScaled(0.25D), pos.y + Utils.getRandomScaled(1) + 1, pos.z + Utils.getRandomScaled(0.25D), d0, d1, d2);
            }
        }
    }

    public static void handleClientsideAbsorptionParticles(Vector3d pos) {
        //Copied from arrow because these particles use their motion for color??
        ClientPlayerEntity player = Minecraft.getInstance().player;

        if (player != null) {
            World level = Minecraft.getInstance().player.level;
            int i = 16239960;//Copied from fortify's MobEffect registration (this is the color)
            double d0 = (double) (i >> 16 & 255) / 255.0D;
            double d1 = (double) (i >> 8 & 255) / 255.0D;
            double d2 = (double) (i >> 0 & 255) / 255.0D;

            for (int j = 0; j < 15; ++j) {
                level.addParticle(ParticleTypes.ENTITY_EFFECT, pos.x + Utils.getRandomScaled(0.25D), pos.y + Utils.getRandomScaled(1), pos.z + Utils.getRandomScaled(0.25D), d0, d1, d2);
            }
        }
    }

    public static void handleClientboundOakskinParticles(Vector3d pos) {
        ClientPlayerEntity player = Minecraft.getInstance().player;

        RandomSource randomsource = player.getRandom();
        for (int i = 0; i < 50; ++i) {
            double d0 = MathHelper.randomBetween(randomsource, -0.5F, 0.5F);
            double d1 = MathHelper.randomBetween(randomsource, 0F, 2f);
            double d2 = MathHelper.randomBetween(randomsource, -0.5F, 0.5F);
            IParticleData particleType = randomsource.nextFloat() < .1f ? ParticleHelper.FIREFLY : new BlockParticleData(ParticleTypes.BLOCK, Blocks.OAK_WOOD.defaultBlockState());
            player.level.addParticle(particleType, pos.x + d0, pos.y + d1, pos.z + d2, d0 * .05, 0.05, d2 * .05);
        }
    }

    public static void handleClientsideRegenCloudParticles(Vector3d pos) {
        ClientPlayerEntity player = Minecraft.getInstance().player;

        if (player != null) {
            World level = player.level;
            int ySteps = 16;
            int xSteps = 48;
            float yDeg = 180f / ySteps * MathHelper.DEG_TO_RAD;
            float xDeg = 360f / xSteps * MathHelper.DEG_TO_RAD;
            for (int x = 0; x < xSteps; x++) {
                for (int y = 0; y < ySteps; y++) {
                    Vector3d offset = new Vector3d(0, 0, CloudOfRegenerationSpell.radius).yRot(y * yDeg).xRot(x * xDeg).zRot(-MathHelper.PI / 2).multiply(1, .85f, 1);
                    level.addParticle(RedstoneParticleData.REDSTONE, pos.x + offset.x, pos.y + offset.y, pos.z + offset.z, 0, 0, 0);
                }
            }
        }
    }

    public static void handleClientsideFortifyAreaParticles(Vector3d pos) {
        ClientPlayerEntity player = Minecraft.getInstance().player;

        if (player != null) {
            World level = player.level;
            int ySteps = 128;
            float yDeg = 180f / ySteps * MathHelper.DEG_TO_RAD;
            for (int y = 0; y < ySteps; y++) {
                Vector3d offset = new Vector3d(0, 0, FortifySpell.radius).yRot(y * yDeg);
                Vector3d motion = new Vector3d(
                        Math.random() - .5,
                        Math.random() - .5,
                        Math.random() - .5
                ).scale(.1);
                level.addParticle(ParticleHelper.WISP, pos.x + offset.x, 1 + pos.y + offset.y, pos.z + offset.z, motion.x, motion.y, motion.z);
            }
        }
    }

    /**
     * Animation Helper
     */

    private static boolean didModify = false;

    private static void animatePlayerStart(PlayerEntity player, ResourceLocation resourceLocation) {
        //IronsSpellbooks.LOGGER.debug("animatePlayerStart {} {}", player, resourceLocation);
        KeyframeAnimation keyframeAnimation = PlayerAnimationRegistry.getAnimation(resourceLocation);
        if (keyframeAnimation != null) {
            //noinspection unchecked
            ModifierLayer<IAnimation> animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData((AbstractClientPlayerEntity) player).get(SpellAnimations.ANIMATION_RESOURCE);
            if (animation != null) {
                KeyframeAnimationPlayer castingAnimationPlayer = new KeyframeAnimationPlayer(keyframeAnimation);
                ClientMagicData.castingAnimationPlayerLookup.put(player.getUUID(), castingAnimationPlayer);
                Boolean armsFlag = SHOW_FIRST_PERSON_ARMS.get();
                Boolean itemsFlag = SHOW_FIRST_PERSON_ITEMS.get();

                if (armsFlag || itemsFlag) {
                    castingAnimationPlayer.setFirstPersonMode(/*resourceLocation.getPath().equals("charge_arrow") ? FirstPersonMode.VANILLA : */FirstPersonMode.THIRD_PERSON_MODEL);
                    castingAnimationPlayer.setFirstPersonConfiguration(new FirstPersonConfiguration(armsFlag, armsFlag, itemsFlag, itemsFlag));
                } else {
                    castingAnimationPlayer.setFirstPersonMode(FirstPersonMode.DISABLED);
                }

                //You might use  animation.replaceAnimationWithFade(); to create fade effect instead of sudden change
                animation.setAnimation(castingAnimationPlayer);
            }
        }
    }

    /**
     * Network Handling Wrapper
     */
    public static void handleClientboundOnClientCast(String spellId, int level, CastSource castSource, ICastData castData) {
        AbstractSpell spell = SpellRegistry.getSpell(spellId);
        spell.onClientCast(Minecraft.getInstance().player.level, level, Minecraft.getInstance().player, castData);
    }

    public static void handleClientboundTeleport(Vector3d pos1, Vector3d pos2) {
        ClientPlayerEntity player = Minecraft.getInstance().player;

        if (player != null) {
            World level = Minecraft.getInstance().player.level;
            TeleportSpell.particleCloud(level, pos1);
            TeleportSpell.particleCloud(level, pos2);
        }
    }


    public static void handleClientboundFrostStep(Vector3d pos1, Vector3d pos2) {
        ClientPlayerEntity player = Minecraft.getInstance().player;

        if (player != null) {
            World level = Minecraft.getInstance().player.level;
            FrostStepSpell.particleCloud(level, pos1);
            FrostStepSpell.particleCloud(level, pos2);
        }
    }

    public static void handleClientBoundOnCastStarted(UUID castingEntityId, String spellId, int spellLevel) {
        PlayerEntity player = Minecraft.getInstance().player.level.getPlayerByUUID(castingEntityId);
        AbstractSpell spell = SpellRegistry.getSpell(spellId);
        spell.getCastStartAnimation().getForPlayer().ifPresent((resourceLocation -> animatePlayerStart(player, resourceLocation)));
        spell.onClientPreCast(player.level, spellLevel, player, player.getUsedItemHand(), null);
    }

    public static void handleClientBoundOnCastFinished(UUID castingEntityId, String spellId, boolean cancelled) {
        ClientMagicData.resetClientCastState(castingEntityId);
        PlayerEntity player = Minecraft.getInstance().player.level.getPlayerByUUID(castingEntityId);

        AbstractSpell spell = SpellRegistry.getSpell(spellId);
        spell.getCastFinishAnimation()
                .getForPlayer()
                .ifPresent((resourceLocation -> {
                    if (!cancelled) {
                        animatePlayerStart(player, resourceLocation);
                    }
                }));

        if (castingEntityId.equals(Minecraft.getInstance().player.getUUID()) && ClientInputEvents.isUseKeyDown) {
            if (spell.getCastType().holdToCast()) {
                ClientSpellCastHelper.setSuppressRightClicks(true);
            }
            ClientInputEvents.hasReleasedSinceCasting = false;
        }
    }
}
