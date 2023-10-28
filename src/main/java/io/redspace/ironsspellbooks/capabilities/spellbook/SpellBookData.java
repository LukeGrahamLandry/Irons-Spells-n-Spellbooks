package io.redspace.ironsspellbooks.capabilities.spellbook;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.item.UniqueSpellBook;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SpellBookData {

    public static final String ISB_SPELLBOOK = "ISB_spellbook";
    public static final String SPELL_SLOTS = "spellSlots";
    public static final String ACTIVE_SPELL_INDEX = "activeSpellIndex";
    public static final String SPELLS = "spells";
    public static final String LEGACY_ID = "id";
    public static final String ID = "sid";
    public static final String LEVEL = "level";
    public static final String SLOT = "slot";

    private SpellData[] transcribedSpells;
    private int activeSpellIndex = -1;
    private int spellSlots;
    private int spellCount = 0;

    public SpellBookData(ItemStack stack, CompoundNBT tag) {
        loadFromNBT(stack, tag);
    }

    public SpellBookData(int spellSlots) {
        this.spellSlots = spellSlots;
        this.transcribedSpells = new SpellData[this.spellSlots];
    }

    public SpellData getActiveSpell() {
        if (activeSpellIndex < 0) {
            return SpellData.EMPTY;
        }

        var spellData = transcribedSpells[activeSpellIndex];

        if (spellData == null) {
            return SpellData.EMPTY;
        }

        return transcribedSpells[activeSpellIndex];
    }

    public boolean setActiveSpellIndex(int index, ItemStack stack) {
        if (index > -1 && index < transcribedSpells.length && transcribedSpells[index] != null) {
            this.activeSpellIndex = index;
            handleDirty(stack);
            return true;
        }
        return false;
    }

    public SpellData[] getInscribedSpells() {
        var result = new SpellData[this.spellSlots];
        System.arraycopy(transcribedSpells, 0, result, 0, transcribedSpells.length);
        return result;
    }

    public List<SpellData> getActiveInscribedSpells() {
        return Arrays.stream(this.transcribedSpells).filter(Objects::nonNull).toList();
    }

    private void handleDirty(ItemStack stack) {
        if (stack != null) {
            SpellBookData.setSpellBookData(stack, this);
        }
    }

    public int getSpellSlots() {
        return spellSlots;
    }

    public int getActiveSpellIndex() {
        return activeSpellIndex;
    }

    public int getSpellCount() {
        return spellCount;
    }

    @Nullable
    public SpellData getSpell(int index) {
        if (index >= 0 && index < transcribedSpells.length)
            return transcribedSpells[index];
        else
            return SpellData.EMPTY;
    }

    public boolean addSpell(AbstractSpell spell, int level, int index, ItemStack stack) {
        if (index > -1 && index < transcribedSpells.length &&
                transcribedSpells[index] == null &&
                Arrays.stream(transcribedSpells).noneMatch(s -> s != null && s.getSpell().equals(spell))) {
            transcribedSpells[index] = new SpellData(spell, level);
            spellCount++;
            if (spellCount == 1) {
                //Stack is intentionally null to avoid multiple calls to handleDirty
                setActiveSpellIndex(index, null);
            }
            handleDirty(stack);
            return true;
        }
        return false;
    }

    public boolean addSpell(AbstractSpell spell, int level, ItemStack stack) {
        int index = getNextSpellIndex();
        if (index > -1) {
            return addSpell(spell, level, index, stack);
        }
        return false;
    }

    private int getNextSpellIndex() {
        return ArrayUtils.indexOf(this.transcribedSpells, null);
    }

    public boolean removeSpell(int index, ItemStack stack) {
        if (index > -1 && index < transcribedSpells.length && transcribedSpells[index] != null) {
            transcribedSpells[index] = null;
            spellCount--;

            if (spellCount == 0) {
                activeSpellIndex = -1;
            } else {
                for (int i = 0; i < transcribedSpells.length; i++) {
                    if (transcribedSpells[i] != null) {
                        activeSpellIndex = i;
                        break;
                    }
                }
            }

            handleDirty(stack);
            return true;
        }

        return false;
    }

    public CompoundNBT getNBT() {
        CompoundNBT compound = new CompoundNBT();
        compound.putInt(SPELL_SLOTS, spellSlots);
        compound.putInt(ACTIVE_SPELL_INDEX, activeSpellIndex);

        ListNBT listTagSpells = new ListNBT();
        for (int i = 0; i < transcribedSpells.length; i++) {
            var spellData = transcribedSpells[i];
            if (spellData != null) {
                CompoundNBT ct = new CompoundNBT();
                ct.putString(ID, spellData.getSpell().getSpellId());
                ct.putInt(LEVEL, spellData.getLevel());
                ct.putInt(SLOT, i);
                listTagSpells.add(ct);
            }
        }

        compound.put(SPELLS, listTagSpells);
        return compound;
    }

    public void loadFromNBT(ItemStack stack, CompoundNBT compound) {
        this.spellSlots = compound.getInt(SPELL_SLOTS);
        this.transcribedSpells = new SpellData[spellSlots];
        this.activeSpellIndex = compound.getInt(ACTIVE_SPELL_INDEX);

        ListNBT listTagSpells = (ListNBT) compound.get(SPELLS);

        spellCount = 0;
        if (listTagSpells != null && listTagSpells.size() > 0) {

//            if (((CompoundTag) listTagSpells.get(0)).contains(LEGACY_ID)) {
//                DataFixerHelpers.fixSpellbookData(listTagSpells);
//            }

            listTagSpells.forEach(tag -> {
                CompoundNBT t = (CompoundNBT) tag;
                String id = t.getString(ID);
                int level = t.getInt(LEVEL);
                int index = t.getInt(SLOT);
                transcribedSpells[index] = new SpellData(SpellRegistry.getSpell(id), level);
                spellCount++;
            });
        }
    }

    public static SpellBookData getSpellBookData(ItemStack stack) {
        if (stack == null) {
            return new SpellBookData(0);
        }

        CompoundNBT tag = stack.getTagElement(ISB_SPELLBOOK);

        if (tag != null) {
            return new SpellBookData(stack, tag);
        } else if (stack.getItem() instanceof SpellBook spellBook) {
            var spellBookData = new SpellBookData(spellBook.getSpellSlots());

            if (spellBook instanceof UniqueSpellBook uniqueSpellBook) {
                uniqueSpellBook.getSpells().forEach(sd -> spellBookData.addSpell(sd.getSpell(), sd.getLevel(), null));
            }

            setSpellBookData(stack, spellBookData);
            return spellBookData;
        } else {
            return new SpellBookData(0);
        }
    }

    public static void setSpellBookData(ItemStack stack, SpellBookData spellBookData) {
        if (spellBookData != null) {
            stack.addTagElement(ISB_SPELLBOOK, spellBookData.getNBT());
        }
    }
}
