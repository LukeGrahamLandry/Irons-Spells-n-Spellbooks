package io.redspace.ironsspellbooks.player;

import io.redspace.ironsspellbooks.api.events.ChangeManaEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.capabilities.magic.UpgradeData;
import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
import io.redspace.ironsspellbooks.compat.tetra.TetraProxy;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.datafix.IronsWorldUpgrader;
import io.redspace.ironsspellbooks.effect.AbyssalShroudEffect;
import io.redspace.ironsspellbooks.effect.EvasionEffect;
import io.redspace.ironsspellbooks.effect.SpiderAspectEffect;
import io.redspace.ironsspellbooks.effect.SummonTimer;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.spells.root.PreventDismount;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.curios.LurkerRing;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.util.ModTags;
import io.redspace.ironsspellbooks.util.UpgradeUtils;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.potion.Effects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DrinkHelper;
import net.minecraft.item.Items;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.storage.SaveFormat;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber
public class ServerPlayerEvents {

//    @SubscribeEvent
//    public static void onPlayerAttack(AttackEntityEvent event) {
//        TODO: this only gets called when the player successfully hits something. we want it to cancel if they even try.
//              granted, the input even should be cancelled already, but better combat skips that due to custom weapon handling.
//        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
//            if (PlayerMagicData.getPlayerMagicData(serverPlayer).isCasting()) {
//                Utils.serverSideCancelCast(serverPlayer);
//            }
//        }
//    }

    @SubscribeEvent
    public static void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        if (ServerConfigs.RUN_WORLD_UPGRADER.get()) {
            MinecraftServer server = event.getServer();
            SaveFormat.LevelSave storageSource = server.storageSource;
            new IronsWorldUpgrader(storageSource, server.getWorldData().worldGenSettings()).runUpgrade();
        }
    }

    @SubscribeEvent
    public static void onLivingEquipmentChangeEvent(LivingEquipmentChangeEvent event) {

        if (event.getEntity().level.isClientSide) {
            return;
        }

        if (event.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getEntity();
            MagicData playerMagicData = MagicData.getPlayerMagicData(serverPlayer);
            if (playerMagicData.isCasting()
                    && (event.getSlot().getIndex() == 0 || event.getSlot().getIndex() == 1)
                    && (event.getFrom().getItem() instanceof SpellBook || SpellData.hasSpellData(event.getFrom()))) {
                Utils.serverSideCancelCast(serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerOpenContainer(PlayerContainerEvent.Open event) {
        if (event.getEntity().level.isClientSide) {
            return;
        }
        //Ironsspellbooks.logger.debug("onPlayerOpenContainer {} {}", event.getEntity().getName().getString(), event.getContainer().getType());

        if (event.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getEntity();
            MagicData playerMagicData = MagicData.getPlayerMagicData(serverPlayer);
            if (playerMagicData.isCasting()) {
                Utils.serverSideCancelCast(serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public static void handleUpgradeModifiers(ItemAttributeModifierEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (!UpgradeData.hasUpgradeData(itemStack))
            return;
        UpgradeData upgradeData = UpgradeData.getUpgradeData(itemStack);

        EquipmentSlotType slot = event.getSlotType();
        if (upgradeData.getUpgradedSlot() != slot)
            return;

        UpgradeUtils.handleAttributeEvent(event, upgradeData);
    }

    @SubscribeEvent
    public static void onExperienceDroppedEvent(LivingExperienceDropEvent event) {
        PlayerEntity player = event.getAttackingPlayer();
        if (player != null) {
            int ringCount = CuriosApi.getCuriosHelper().findCurios(player, ItemRegistry.EMERALD_STONEPLATE_RING.get()).size();
            for (int i = 0; i < ringCount; i++) {
                event.setDroppedExperience((int) (event.getDroppedExperience() * 1.25));
            }
        }
    }

    @SubscribeEvent
    public static void onStartTracking(final PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof ServerPlayerEntity && event.getTarget() instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getEntity();
            ServerPlayerEntity targetPlayer = (ServerPlayerEntity) event.getTarget();
            MagicData.getPlayerMagicData(serverPlayer).getSyncedData().syncToPlayer(targetPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getEntity();
            MagicData playerMagicData = MagicData.getPlayerMagicData(serverPlayer);
            playerMagicData.getPlayerCooldowns().syncToPlayer(serverPlayer);
            playerMagicData.getSyncedData().syncToPlayer(serverPlayer);
            CameraShakeManager.doSync(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getEntity();
            Utils.serverSideCancelCast(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            if (event.getEntity() instanceof ServerPlayerEntity) {
                ServerPlayerEntity newServerPlayer = (ServerPlayerEntity) event.getEntity();
                //Persist summon timers across death
                event.getOriginal().getActiveEffects().forEach((effect -> {
                    //IronsSpellbooks.LOGGER.debug("{}", effect.getEffect().getDisplayName().getString());
                    if (effect.getEffect() instanceof SummonTimer) {
                        newServerPlayer.addEffect(effect);
                    }
                }));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        //Ironsspellbooks.logger.debug("PlayerChangedDimension: {}", event.getEntity().getName().getString());
        if (event.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getEntity();
            //Ironsspellbooks.logger.debug("onPlayerLoggedIn syncing cooldowns to {}", serverPlayer.getName().getString());
            Utils.serverSideCancelCast(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getEntity();

            //Clear fire
            // Frozen isn't a thing in 1.16
            serverPlayer.clearFire();
            serverPlayer.connection.send(new SEntityMetadataPacket(serverPlayer.getId(), serverPlayer.getEntityData(), true));

            //Cancel casting
            Utils.serverSideCancelCast(serverPlayer);

            //Sync effects
            serverPlayer.getActiveEffects().forEach((effect -> {
                if (effect.getEffect() instanceof SummonTimer) {
                    serverPlayer.connection.send(new SPlayEntityEffectPacket(serverPlayer.getId(), effect));
                }
            }));

            //Set respawn mana
            MagicData.getPlayerMagicData(serverPlayer).setMana((int) (serverPlayer.getAttributeValue(AttributeRegistry.MAX_MANA.get()) * ServerConfigs.MANA_SPAWN_PERCENT.get()));
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity livingEntity = event.getEntityLiving();
        //irons_spellbooks.LOGGER.debug("onLivingAttack.1: {}", livingEntity);

        if ((livingEntity instanceof ServerPlayerEntity) || (livingEntity instanceof AbstractSpellCastingMob)) {
            MagicData playerMagicData = MagicData.getPlayerMagicData(livingEntity);
            if (playerMagicData.getSyncedData().hasEffect(SyncedSpellData.EVASION)) {
                if (EvasionEffect.doEffect(livingEntity, event.getSource())) {
                    event.setCanceled(true);
                }
            } else if (playerMagicData.getSyncedData().hasEffect(SyncedSpellData.ABYSSAL_SHROUD)) {
                if (AbyssalShroudEffect.doEffect(livingEntity, event.getSource())) {
                    event.setCanceled(true);
                }
            }
        }

        TetraProxy.PROXY.handleLivingAttackEvent(event);
    }
//
//    @SubscribeEvent
//    public static void onMobTarget(LivingChangeTargetEvent event) {
//        var newTarget = event.getNewTarget();
//        var oldTarget = event.getOriginalTarget();
//        if (newTarget == null || oldTarget == null)
//            return;
//
//        if (newTarget.hasEffect(MobEffectRegistry.TRUE_INVISIBILITY.get()))
//            event.setNewTarget(oldTarget);
//    }
//    @SubscribeEvent
//    public static void onPlayerHurt(LivingHurtEvent event) {
//        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
//            var playerMagicData = PlayerMagicData.getPlayerMagicData(serverPlayer);
//            if (playerMagicData.getSyncedData().getHasEvasion()) {
//                if (EvasionEffect.doEffect(serverPlayer, event.getSource())) {
//                    event.setCanceled(true);
//                }
//            }
//        }
//    }

    @SubscribeEvent
    public static void onLivingChangeTarget(LivingSetAttackTargetEvent event) {
        LivingEntity newTarget = event.getTarget();
        if (newTarget != null && newTarget.getType().is(ModTags.VILLAGE_ALLIES) && event.getEntity().getType().is(ModTags.VILLAGE_ALLIES)
        ) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void testManaEvent(ChangeManaEvent event) {
        if (event.getEntityLiving().hasEffect(Effects.REGENERATION))
            event.setCanceled(true);
        else if (event.getEntityLiving().hasEffect(Effects.POISON) ){
            float diff = event.getNewMana() - event.getOldMana();
            if (diff < 0) {
                diff *= 2;
                event.setNewMana(event.getOldMana() + diff);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTakeDamage(LivingDamageEvent event) {
        /*
        Damage Increasing Effects
         */
        Entity attacker = event.getSource().getEntity();
        if (attacker instanceof LivingEntity) {
            LivingEntity livingAttacker = (LivingEntity) attacker;
            //IronsSpellbooks.LOGGER.debug("onLivingTakeDamage: attacker: {} target:{}", livingAttacker.getName().getString(), event.getEntity());
            //TODO: subscribe in effect class?
            /**
             * Spider aspect handling
             */
            if (livingAttacker.hasEffect(MobEffectRegistry.SPIDER_ASPECT.get())) {
                if (event.getEntityLiving().hasEffect(Effects.POISON)) {
                    int lvl = livingAttacker.getEffect(MobEffectRegistry.SPIDER_ASPECT.get()).getAmplifier() + 1;
                    float before = event.getAmount();
                    float multiplier = 1 + SpiderAspectEffect.DAMAGE_PER_LEVEL * lvl;
                    event.setAmount(event.getAmount() * multiplier);
                    //IronsSpellbooks.LOGGER.debug("spider mode {}->{}", before, event.getAmount());

                }
            }
            /**
             * Lurker Ring handling
             */
            if (livingAttacker.isInvisible() && ItemRegistry.LURKER_RING.get().isEquippedBy(livingAttacker)) {
                if (livingAttacker instanceof PlayerEntity && !((PlayerEntity) livingAttacker).getCooldowns().isOnCooldown(ItemRegistry.LURKER_RING.get())) {
                    PlayerEntity player = (PlayerEntity) livingAttacker;
                    event.setAmount(event.getAmount() * LurkerRing.MULTIPLIER);
                    player.getCooldowns().addCooldown(ItemRegistry.LURKER_RING.get(), LurkerRing.COOLDOWN_IN_TICKS);
                }
            }
        }
        /*
        Damage Reducing Effects
         */
        MagicData playerMagicData = MagicData.getPlayerMagicData(event.getEntityLiving());
        if (playerMagicData.getSyncedData().hasEffect(SyncedSpellData.HEARTSTOP)) {
            playerMagicData.getSyncedData().addHeartstopDamage(event.getAmount() * .5f);
            //Ironsspellbooks.logger.debug("Accumulated damage: {}", playerMagicData.getSyncedData().getHeartstopAccumulatedDamage());
            event.setCanceled(true);
            return;
        }

        if (event.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getEntity();
            if (playerMagicData.isCasting() &&
                    playerMagicData.getCastingSpell().getSpell().getCastType() == CastType.LONG &&
                    !ItemRegistry.CONCENTRATION_AMULET.get().isEquippedBy(serverPlayer) &&
                    playerMagicData.getCastDurationRemaining() > 0 &&
                    event.getSource() != DamageSource.STARVE &&
                    event.getSource() != DamageSource.ON_FIRE &&
                    event.getSource() != DamageSource.WITHER) {
                Utils.serverSideCancelCast(serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityMountEvent(EntityMountEvent event) {
        if (event.getEntity().level.isClientSide) {
            return;
        }

        if (event.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) event.getEntity();
            MagicData playerMagicData = MagicData.getPlayerMagicData(serverPlayer);
            if (playerMagicData.isCasting()) {
                Utils.serverSideCancelCast(serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public static void preventDismount(EntityMountEvent event) {
        if (!event.getEntity().level.isClientSide && event.getEntityBeingMounted() instanceof PreventDismount && event.isDismounting() && event.getEntityBeingMounted().isAlive()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        Entity projectile = event.getEntity();
        Entity owner = projectile instanceof ProjectileEntity ? ((ProjectileEntity) projectile).getOwner() : null;
        if (event.getRayTraceResult() instanceof EntityRayTraceResult) {
            EntityRayTraceResult entityHitResult = (EntityRayTraceResult) event.getRayTraceResult();
            Entity victim = entityHitResult.getEntity();
            if (victim instanceof AbstractSpellCastingMob || victim instanceof PlayerEntity) {
                LivingEntity livingEntity = (LivingEntity) victim;
                MagicData playerMagicData = MagicData.getPlayerMagicData(livingEntity);
                if (playerMagicData.getSyncedData().hasEffect(SyncedSpellData.EVASION)) {
                    if (EvasionEffect.doEffect(livingEntity, new IndirectEntityDamageSource("noop", projectile, owner))) {
                        event.setCanceled(true);
                    }
                } else if (playerMagicData.getSyncedData().hasEffect(SyncedSpellData.ABYSSAL_SHROUD)) {
                    if (AbyssalShroudEffect.doEffect(livingEntity, new IndirectEntityDamageSource("noop", projectile, owner))) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void useOnEntityEvent(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getTarget() instanceof CreeperEntity) {
            CreeperEntity creeper = (CreeperEntity) event.getTarget();
            PlayerEntity player = event.getPlayer();
            ItemStack useItem = player.getItemInHand(event.getHand());
            if (useItem.getItem() == Items.GLASS_BOTTLE && creeper.isPowered()) {
                creeper.hurt(DamageSource.GENERIC.bypassMagic(), 5);
                player.level.playSound((PlayerEntity) null, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                player.swing(event.getHand());
                event.setCancellationResult(ActionResult.sidedSuccess(DrinkHelper.createFilledResult(useItem, player, new ItemStack(ItemRegistry.LIGHTNING_BOTTLE.get())), player.level.isClientSide).getResult());
            }
        }
    }

//    @SubscribeEvent
//    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
//        var entity = event.getEntity();
//        var level = entity.level;
//        if (!level.isClientSide) {
    // TODO: when add back cauldron
//            if (entity.tickCount % 20 == 0) {
//                BlockPos pos = entity.blockPosition();
//                BlockState blockState = entity.getLevel().getBlockState(pos);
//                if (blockState.is(Blocks.CAULDRON)) {
//                    BloodCauldronBlock.attemptCookEntity(blockState, entity.getLevel(), pos, entity, () -> {
//                        level.setBlockAndUpdate(pos, BlockRegistry.BLOOD_CAULDRON_BLOCK.get().defaultBlockState());
//                        level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
//                    });
//                }

//                //TODO: Citadel reimplementation
////                //boolean inStructure = MAGIC_AURA_PREDICATE.matches(serverPlayer.getLevel(), serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ());
////                //boolean inStructure = serverPlayer.getLevel().structureManager().get
////                //var structure = serverPlayer.getLevel().structureManager().getStructureAt(serverPlayer.blockPosition());
////                var structureManager = serverPlayer.getLevel().structureManager();
////                Structure structureKey = structureManager.registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY).get(ModTags.MAGIC_AURA_TEMP);
////                var structure = structureManager.getStructureAt(serverPlayer.blockPosition(), structureKey);
////                boolean inStructure = structure != StructureStart.INVALID_START /*&& structure.getBoundingBox().isInside(serverPlayer.blockPosition())*/;
////                //IronsSpellbooks.LOGGER.debug("ServerPlayerEvents: In citadel: {}", inStructure);
////                if (inStructure && !ItemRegistry.ENCHANTED_WARD_AMULET.get().isEquippedBy(serverPlayer))
////                    serverPlayer.addEffect(new MobEffectInstance(MobEffectRegistry.ENCHANTED_WARD.get(), 40, 0, false, false, false));
//
//            }
//        }
//    }
}
