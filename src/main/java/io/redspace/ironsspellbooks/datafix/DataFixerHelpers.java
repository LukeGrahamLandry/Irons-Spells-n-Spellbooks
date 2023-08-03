package io.redspace.ironsspellbooks.datafix;

import com.google.common.collect.ImmutableMap;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.spells.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
import io.redspace.ironsspellbooks.capabilities.spellbook.SpellBookData;
import io.redspace.ironsspellbooks.spells.blood.*;
import io.redspace.ironsspellbooks.spells.ender.*;
import io.redspace.ironsspellbooks.spells.evocation.*;
import io.redspace.ironsspellbooks.spells.fire.*;
import io.redspace.ironsspellbooks.spells.holy.*;
import io.redspace.ironsspellbooks.spells.ice.*;
import io.redspace.ironsspellbooks.spells.lightning.*;
import io.redspace.ironsspellbooks.spells.poison.*;
import io.redspace.ironsspellbooks.spells.void_school.AbyssalShroudSpell;
import io.redspace.ironsspellbooks.spells.void_school.BlackHoleSpell;
import io.redspace.ironsspellbooks.spells.void_school.VoidTentaclesSpell;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class DataFixerHelpers {
    static final Map<Integer, String> LEGACY_SPELL_MAPPING = ImmutableMap.<Integer, String>builder()
            .put(1, new FireballSpell().getSpellId())
            .put(2, new BurningDashSpell().getSpellId())
            .put(3, new TeleportSpell().getSpellId())
            .put(4, new MagicMissileSpell().getSpellId())
            .put(5, new ElectrocuteSpell().getSpellId())
            .put(6, new ConeOfColdSpell().getSpellId())
            .put(7, new HealSpell().getSpellId())
            .put(8, new BloodSlashSpell().getSpellId())
            .put(9, new SummonVexSpell().getSpellId())
            .put(10, new FireboltSpell().getSpellId())
            .put(11, new FireBreathSpell().getSpellId())
            .put(12, new IcicleSpell().getSpellId())
            .put(13, new FirecrackerSpell().getSpellId())
            .put(14, new SummonHorseSpell().getSpellId())
            .put(15, new AngelWingsSpell().getSpellId())
            .put(16, new ShieldSpell().getSpellId())
            .put(17, new WallOfFireSpell().getSpellId())
            .put(18, new WispSpell().getSpellId())
            .put(19, new FangStrikeSpell().getSpellId())
            .put(20, new FangWardSpell().getSpellId())
            .put(21, new EvasionSpell().getSpellId())
            .put(22, new HeartstopSpell().getSpellId())
            .put(23, new LightningLanceSpell().getSpellId())
            .put(24, new LightningBoltSpell().getSpellId())
            .put(25, new RaiseDeadSpell().getSpellId())
            .put(26, new WitherSkullSpell().getSpellId())
            .put(27, new GreaterHealSpell().getSpellId())
            .put(28, new CloudOfRegenerationSpell().getSpellId())
            .put(29, new RayOfSiphoningSpell().getSpellId())
            .put(30, new MagicArrowSpell().getSpellId())
            .put(31, new LobCreeperSpell().getSpellId())
            .put(32, new ChainCreeperSpell().getSpellId())
            .put(33, new BlazeStormSpell().getSpellId())
            .put(34, new FrostStepSpell().getSpellId())
            .put(35, new AbyssalShroudSpell().getSpellId())
            .put(36, new FrostbiteSpell().getSpellId())
            .put(37, new AscensionSpell().getSpellId())
            .put(38, new InvisibilitySpell().getSpellId())
            .put(39, new BloodStepSpell().getSpellId())
            .put(40, new SummonPolarBearSpell().getSpellId())
            .put(41, new BlessingOfLifeSpell().getSpellId())
            .put(42, new DragonBreathSpell().getSpellId())
            .put(43, new FortifySpell().getSpellId())
            .put(44, new CounterspellSpell().getSpellId())
            .put(45, new SpectralHammerSpell().getSpellId())
            .put(46, new ChargeSpell().getSpellId())
            .put(47, new VoidTentaclesSpell().getSpellId())
            .put(48, new IceBlockSpell().getSpellId())
            .put(49, new PoisonBreathSpell().getSpellId())
            .put(50, new PoisonArrowSpell().getSpellId())
            .put(51, new PoisonSplashSpell().getSpellId())
            .put(52, new AcidOrbSpell().getSpellId())
            .put(53, new SpiderAspectSpell().getSpellId())
            .put(54, new BlightSpell().getSpellId())
            .put(55, new RootSpell().getSpellId())
            .put(56, new BlackHoleSpell().getSpellId())
            .put(57, new BloodNeedlesSpell().getSpellId())
            .put(58, new AcupunctureSpell().getSpellId())
            .put(59, new MagmaBombSpell().getSpellId())
            .put(60, new StarfallSpell().getSpellId())
            .put(61, new HealingCircleSpell().getSpellId())
            .put(62, new GuidingBoltSpell().getSpellId())
            .put(63, new SunbeamSpell().getSpellId())
            .put(64, new GustSpell().getSpellId())
            .put(65, new ChainLightningSpell().getSpellId())
            .put(66, new DevourSpell().getSpellId())
            .build();

    /**
     * Returns true if data was updated
     */
    public static boolean doFixUps(CompoundTag tag) {
        var fix1 = fixIsbSpellbook(tag);
        var fix2 = fixIsbSpell(tag);
        return fix1 || fix2;
    }

    public static boolean fixIsbSpellbook(CompoundTag tag) {
        if (tag != null) {
            var spellBookTag = (CompoundTag) tag.get(SpellBookData.ISB_SPELLBOOK);
            if (spellBookTag != null) {
                ListTag listTagSpells = (ListTag) spellBookTag.get(SpellBookData.SPELLS);
                if (listTagSpells != null && !listTagSpells.isEmpty()) {
                    if (((CompoundTag) listTagSpells.get(0)).contains(SpellBookData.LEGACY_ID)) {
                        DataFixerHelpers.fixSpellbookData(listTagSpells);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean fixIsbSpell(CompoundTag tag) {
        if (tag != null) {
            var spellTag = (CompoundTag) tag.get(SpellData.ISB_SPELL);
            if (spellTag != null) {
                if (spellTag.contains(SpellData.LEGACY_SPELL_TYPE)) {
                    DataFixerHelpers.fixScrollData(spellTag);
                    return true;
                }
            }
        }

        return false;
    }

    public static void fixScrollData(CompoundTag tag) {
        var legacySpellId = tag.getInt(SpellData.LEGACY_SPELL_TYPE);
        tag.remove(SpellData.LEGACY_SPELL_TYPE);
        tag.putString(SpellData.SPELL_ID, LEGACY_SPELL_MAPPING.getOrDefault(legacySpellId, "irons_spellbooks:none"));
    }

    public static void fixSpellbookData(ListTag listTag) {
        listTag.forEach(tag -> {
            CompoundTag t = (CompoundTag) tag;
            int legacySpellId = t.getInt(SpellBookData.LEGACY_ID);
            t.putString(SpellBookData.ID, LEGACY_SPELL_MAPPING.getOrDefault(legacySpellId, "irons_spellbooks:none"));
            t.remove(SpellBookData.LEGACY_ID);
        });
    }
}