package io.redspace.ironsspellbooks.spells.evocation;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.entity.spells.ExtendedFireworkRocket;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@AutoSpellConfig
public class FirecrackerSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(IronsSpellbooks.MODID, "firecracker");

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(new TranslationTextComponent("ui.irons_spellbooks.damage", Utils.stringTruncation(getSpellPower(spellLevel, caster), 1)));
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
            .setMaxLevel(10)
            .setCooldownSeconds(1.5)
            .build();

    public FirecrackerSpell() {
        this.manaCostPerLevel = 2;
        this.baseSpellPower = 4;
        this.spellPowerPerLevel = 1;
        this.castTime = 0;
        this.baseManaCost = 20;
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
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
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public void onCast(World world, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        Vector3d shootAngle = entity.getLookAngle().normalize();
        float speed = 2.5f;

        Vector3d hitPos = Utils.raycastForEntity(world, entity, getRange(spellLevel, entity), true).getLocation();
        Vector3d spawn = hitPos.subtract(shootAngle.scale(.5f));

        ExtendedFireworkRocket firework = new ExtendedFireworkRocket(world, randomFireworkRocket(), entity, spawn.x, spawn.y, spawn.z, true, getDamage(spellLevel, entity));
        world.addFreshEntity(firework);
        firework.shoot(shootAngle.x, shootAngle.y, shootAngle.z, speed, 0);
        super.onCast(world, spellLevel, entity, playerMagicData);
    }

    private int getRange(int spellLevel, LivingEntity entity) {
        return 15 + (int) (getSpellPower(spellLevel, entity) * 2);
    }

    private float getDamage(int spellLevel, LivingEntity entity) {
        return getSpellPower(spellLevel, entity) * .5f;
    }

    private ItemStack randomFireworkRocket() {
        Random random = new Random();
        ItemStack rocket = new ItemStack(Items.FIREWORK_ROCKET);
        CompoundNBT properties = new CompoundNBT();
        //https://minecraft.fandom.com/wiki/Firework_Rocket#Data_values
        ListNBT explosions = new ListNBT();
        CompoundNBT explosion = new CompoundNBT();
        explosion.putByte("Type", (byte) (random.nextInt(3) * 2));
        if (random.nextInt(3) == 0)
            explosion.putByte("Trail", (byte) 1);
        if (random.nextInt(3) == 0)
            explosion.putByte("Flicker", (byte) 1);

        explosion.putIntArray("Colors", randomColors());

        explosions.add(explosion);

        properties.put("Explosions", explosions);
        properties.putByte("Flight", (byte) -1);

        rocket.addTagElement("Fireworks", properties);

        return rocket;
    }

    private int[] randomColors() {
        int[] colors = new int[3];
        Random random = new Random();

        for (int i = 0; i < colors.length; i++) {
            colors[i] = DYE_COLORS[random.nextInt(DYE_COLORS.length)];
        }
        return colors;
    }

    //https://minecraft.fandom.com/wiki/Dye#Color_values
    private static final int[] DYE_COLORS = {
            //1908001,
            11546150,
            6192150,
            //8606770,
            3949738,
            8991416,
            1481884,
            //10329495,
            //4673362,
            15961002,
            8439583,
            16701501,
            3847130,
            13061821,
            16351261,
            16383998
    };
//    @Override
//    public MutableComponent getUniqueInfo() {
//        return uniqueText;
//    }
}
