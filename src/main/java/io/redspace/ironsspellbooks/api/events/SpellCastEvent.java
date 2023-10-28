package io.redspace.ironsspellbooks.api.events;


import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * SpellCastEvent is fired whenever a {@link Player} casts a spell.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the spell is not cast.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public class SpellCastEvent extends PlayerEvent {
    private final String spellId;
    private final SchoolType schoolType;
    private final CastSource castSource;
    private final int spellLevel;
    public SpellCastEvent(PlayerEntity player, String spellId, int spellLevel, SchoolType schoolType, CastSource castSource)
    {
        super(player);
        this.spellId = spellId;
        this.spellLevel = spellLevel;
        this.schoolType = schoolType;
        this.castSource = castSource;
    }

    @Override
    public boolean isCancelable() { return true; }
    public String getSpellId() { return this.spellId; }
    public SchoolType getSchoolType() { return this.schoolType; }
    public int getSpellLevel() { return this.spellLevel; }
    public CastSource getCastSource() { return this.castSource; }
}
