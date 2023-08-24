package io.redspace.ironsspellbooks.registries;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.*;
import io.redspace.ironsspellbooks.item.armor.*;
import io.redspace.ironsspellbooks.item.curios.*;
import io.redspace.ironsspellbooks.item.spell_books.SimpleAttributeSpellBook;
import io.redspace.ironsspellbooks.item.spell_books.VillagerSpellBook;
import io.redspace.ironsspellbooks.item.weapons.*;
import io.redspace.ironsspellbooks.spells.AbstractSpell;
import io.redspace.ironsspellbooks.spells.SpellRarity;
import io.redspace.ironsspellbooks.spells.SpellType;
import io.redspace.ironsspellbooks.spells.blood.BloodSlashSpell;
import io.redspace.ironsspellbooks.spells.blood.BloodStepSpell;
import io.redspace.ironsspellbooks.spells.blood.RayOfSiphoningSpell;
import io.redspace.ironsspellbooks.spells.blood.WitherSkullSpell;
import io.redspace.ironsspellbooks.spells.evocation.FangStrikeSpell;
import io.redspace.ironsspellbooks.spells.evocation.FangWardSpell;
import io.redspace.ironsspellbooks.spells.evocation.SummonVexSpell;
import io.redspace.ironsspellbooks.spells.fire.BlazeStormSpell;
import io.redspace.ironsspellbooks.util.SpellbookModCreativeTabs;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.RegistryObject;

import java.util.Collection;

public class ItemRegistry {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, IronsSpellbooks.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    //public static final RegistryObject<Item> SPELL_BOOK = ITEMS.register("spell_book", SpellBook::new);
    /**
     * Spell items
     */
    public static final RegistryObject<Item> WIMPY_SPELL_BOOK = ITEMS.register("wimpy_spell_book", () -> new SpellBook(0, SpellRarity.LEGENDARY, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> LEGENDARY_SPELL_BOOK = ITEMS.register("legendary_spell_book", () -> new SpellBook(12, SpellRarity.LEGENDARY, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> NETHERITE_SPELL_BOOK = ITEMS.register("netherite_spell_book", () -> new SimpleAttributeSpellBook(10, SpellRarity.LEGENDARY, AttributeRegistry.COOLDOWN_REDUCTION.get(), .20));
    public static final RegistryObject<Item> DIAMOND_SPELL_BOOK = ITEMS.register("diamond_spell_book", () -> new SpellBook(8, SpellRarity.EPIC));
    public static final RegistryObject<Item> GOLD_SPELL_BOOK = ITEMS.register("gold_spell_book", () -> new SpellBook(6, SpellRarity.RARE));
    public static final RegistryObject<Item> IRON_SPELL_BOOK = ITEMS.register("iron_spell_book", () -> new SpellBook(5, SpellRarity.UNCOMMON));
    public static final RegistryObject<Item> COPPER_SPELL_BOOK = ITEMS.register("copper_spell_book", () -> new SpellBook(4, SpellRarity.COMMON));
    public static final RegistryObject<Item> EVOKER_SPELL_BOOK = ITEMS.register("evoker_spell_book", () -> new UniqueSpellBook(SpellRarity.COMMON, new AbstractSpell[]{new FangStrikeSpell(6), new FangWardSpell(4), new SummonVexSpell(4)}));
    public static final RegistryObject<Item> ROTTEN_SPELL_BOOK = ITEMS.register("rotten_spell_book", () -> new SpellBook(3, SpellRarity.RARE));
    public static final RegistryObject<Item> BLAZE_SPELL_BOOK = ITEMS.register("blaze_spell_book", () -> new SimpleAttributeSpellBook(5, SpellRarity.EPIC, AttributeRegistry.FIRE_SPELL_POWER.get(), .10));
    public static final RegistryObject<Item> DRAGONSKIN_SPELL_BOOK = ITEMS.register("dragonskin_spell_book", () -> new SimpleAttributeSpellBook(10, SpellRarity.LEGENDARY, AttributeRegistry.ENDER_SPELL_POWER.get(), .10));
    public static final RegistryObject<Item> VILLAGER_SPELL_BOOK = ITEMS.register("villager_spell_book", VillagerSpellBook::new);
    public static final RegistryObject<Item> BLOOD_STAFF = ITEMS.register("blood_staff", () -> new BloodStaffItem(new AbstractSpell[]{new WitherSkullSpell(6), new RayOfSiphoningSpell(6), new BloodStepSpell(3), new BloodSlashSpell(6), new BlazeStormSpell(6)}));
    public static final RegistryObject<Item> MAGEHUNTER = ITEMS.register("magehunter", MagehunterItem::new);
    //public static final RegistryObject<Item> SPELLBREAKER = ITEMS.register("spellbreaker", () -> new SpellbreakerItem(SpellType.COUNTERSPELL_SPELL, 1));
    public static final RegistryObject<Item> TEST_CLAYMORE = ITEMS.register("claymore", TestClaymoreItem::new);
    public static final RegistryObject<Item> KEEPER_FLAMBERGE = ITEMS.register("keeper_flamberge", KeeperFlambergeItem::new);
    //public static final RegistryObject<Item> TRUTHSEEKER = ITEMS.register("truthseeker", TruthseekerItem::new);
    public static final RegistryObject<Item> SCROLL = ITEMS.register("scroll", Scroll::new);

    /**
     * Ink
     */
    public static final RegistryObject<Item> INK_COMMON = ITEMS.register("common_ink", () -> new InkItem(SpellRarity.COMMON));
    public static final RegistryObject<Item> INK_UNCOMMON = ITEMS.register("uncommon_ink", () -> new InkItem(SpellRarity.UNCOMMON));
    public static final RegistryObject<Item> INK_RARE = ITEMS.register("rare_ink", () -> new InkItem(SpellRarity.RARE));
    public static final RegistryObject<Item> INK_EPIC = ITEMS.register("epic_ink", () -> new InkItem(SpellRarity.EPIC));
    public static final RegistryObject<Item> INK_LEGENDARY = ITEMS.register("legendary_ink", () -> new InkItem(SpellRarity.LEGENDARY));

    /**
     * Upgrade Orbs
     */
    public static final RegistryObject<Item> UPGRADE_ORB = ITEMS.register("upgrade_orb", () -> new Item((new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> FIRE_UPGRADE_ORB = ITEMS.register("fire_upgrade_orb", () -> new UpgradeOrbItem(UpgradeType.FIRE_SPELL_POWER, (new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> ICE_UPGRADE_ORB = ITEMS.register("ice_upgrade_orb", () -> new UpgradeOrbItem(UpgradeType.ICE_SPELL_POWER, (new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> LIGHTNING_UPGRADE_ORB = ITEMS.register("lightning_upgrade_orb", () -> new UpgradeOrbItem(UpgradeType.LIGHTNING_SPELL_POWER, (new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> HOLY_UPGRADE_ORB = ITEMS.register("holy_upgrade_orb", () -> new UpgradeOrbItem(UpgradeType.HOLY_SPELL_POWER, (new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> ENDER_UPGRADE_ORB = ITEMS.register("ender_upgrade_orb", () -> new UpgradeOrbItem(UpgradeType.ENDER_SPELL_POWER, (new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> BLOOD_UPGRADE_ORB = ITEMS.register("blood_upgrade_orb", () -> new UpgradeOrbItem(UpgradeType.BLOOD_SPELL_POWER, (new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> EVOCATION_UPGRADE_ORB = ITEMS.register("evocation_upgrade_orb", () -> new UpgradeOrbItem(UpgradeType.EVOCATION_SPELL_POWER, (new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> POISON_UPGRADE_ORB = ITEMS.register("poison_upgrade_orb", () -> new UpgradeOrbItem(UpgradeType.POISON_SPELL_POWER, (new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> MANA_UPGRADE_ORB = ITEMS.register("mana_upgrade_orb", () -> new UpgradeOrbItem(UpgradeType.MANA, (new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> COOLDOWN_UPGRADE_ORB = ITEMS.register("cooldown_upgrade_orb", () -> new UpgradeOrbItem(UpgradeType.COOLDOWN, (new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> PROTECTION_UPGRADE_ORB = ITEMS.register("protection_upgrade_orb", () -> new UpgradeOrbItem(UpgradeType.SPELL_RESISTANCE, (new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB).rarity(Rarity.UNCOMMON)));

    /**
     * Generic Items
     */
    public static final RegistryObject<Item> LIGHTNING_BOTTLE = ITEMS.register("lightning_bottle", () -> new Item((new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> FROZEN_BONE_SHARD = ITEMS.register("frozen_bone", () -> new Item((new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB)));
    public static final RegistryObject<Item> BLOOD_VIAL = ITEMS.register("blood_vial", () -> new Item((new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB)));
    public static final RegistryObject<Item> DIVINE_PEARL = ITEMS.register("divine_pearl", () -> new Item((new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB)));
    public static final RegistryObject<Item> HOGSKIN = ITEMS.register("hogskin", () -> new Item((new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB)));
    public static final RegistryObject<Item> DRAGONSKIN = ITEMS.register("dragonskin", DragonskinItem::new);
    public static final RegistryObject<Item> ARCANE_ESSENCE = ITEMS.register("arcane_essence", () -> new Item((new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB)));
    public static final RegistryObject<Item> MAGIC_CLOTH = ITEMS.register("magic_cloth", () -> new Item((new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB)));
    public static final RegistryObject<Item> BLANK_RUNE = ITEMS.register("blank_rune", () -> new Item((new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB)));
    public static final RegistryObject<Item> FIRE_RUNE = ITEMS.register("fire_rune", () -> new Item((new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB)));
    public static final RegistryObject<Item> ICE_RUNE = ITEMS.register("ice_rune", () -> new Item((new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB)));
    public static final RegistryObject<Item> LIGHTNING_RUNE = ITEMS.register("lightning_rune", () -> new Item((new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB)));
    public static final RegistryObject<Item> ENDER_RUNE = ITEMS.register("ender_rune", () -> new Item((new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB)));
    public static final RegistryObject<Item> HOLY_RUNE = ITEMS.register("holy_rune", () -> new Item((new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB)));
    public static final RegistryObject<Item> BLOOD_RUNE = ITEMS.register("blood_rune", () -> new Item((new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB)));
    public static final RegistryObject<Item> EVOCATION_RUNE = ITEMS.register("evocation_rune", () -> new Item((new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB)));
    public static final RegistryObject<Item> MANA_RUNE = ITEMS.register("arcane_rune", () -> new Item((new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB)));
    public static final RegistryObject<Item> COOLDOWN_RUNE = ITEMS.register("cooldown_rune", () -> new Item((new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB)));
    public static final RegistryObject<Item> PROTECTION_RUNE = ITEMS.register("protection_rune", () -> new Item((new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB)));
    public static final RegistryObject<Item> POISON_RUNE = ITEMS.register("poison_rune", () -> new Item((new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB)));
    public static final RegistryObject<Item> WAYWARD_COMPASS = ITEMS.register("wayward_compass", WaywardCompass::new);
    //    public static final RegistryObject<Item> ANTIQUATED_COMPASS = ITEMS.register("antiquated_compass", AntiquatedCompass::new);
    public static final RegistryObject<Item> RUINED_BOOK = ITEMS.register("ruined_book", () -> new Item((new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> CINDER_ESSENCE = ITEMS.register("cinder_essence", () -> new Item((new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB)));
    public static final RegistryObject<Item> ARCANE_SALVAGE = ITEMS.register("arcane_salvage", ArcaneSalvageItem::new);
    public static final RegistryObject<Item> ARCANE_INGOT = ITEMS.register("arcane_ingot", () -> new Item((new Item.Properties()).tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB)));
    public static final RegistryObject<Item> SHRIVING_STONE = ITEMS.register("shriving_stone", ShrivingStoneItem::new);

    /**
     * Block Items
     */
    public static final RegistryObject<Item> INSCRIPTION_TABLE_BLOCK_ITEM = ITEMS.register("inscription_table", () -> new BlockItem(BlockRegistry.INSCRIPTION_TABLE_BLOCK.get(), new Item.Properties().tab(ItemGroup.TAB_DECORATIONS)));
    public static final RegistryObject<Item> ACANE_ANVIL_BLOCK_ITEM = ITEMS.register("arcane_anvil", () -> new BlockItem(BlockRegistry.ARCANE_ANVIL_BLOCK.get(), new Item.Properties().tab(ItemGroup.TAB_DECORATIONS).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> SCROLL_FORGE_BLOCK = ITEMS.register("scroll_forge", () -> new BlockItem(BlockRegistry.SCROLL_FORGE_BLOCK.get(), new Item.Properties().tab(ItemGroup.TAB_DECORATIONS).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> PEDESTAL_BLOCK_ITEM = ITEMS.register("pedestal", () -> new BlockItem(BlockRegistry.PEDESTAL_BLOCK.get(), new Item.Properties().tab(ItemGroup.TAB_DECORATIONS)));
    public static final RegistryObject<Item> ARMOR_PILE_BLOCK_ITEM = ITEMS.register("armor_pile", () -> new BlockItem(BlockRegistry.ARMOR_PILE_BLOCK.get(), new Item.Properties().tab(ItemGroup.TAB_DECORATIONS)));
    //public static final RegistryObject<Item> BLOOD_SLASH_BLOCK_ITEM = ITEMS.register("blood_slash_block", () -> new BlockItem(BlockRegistry.BLOOD_SLASH_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
    public static final RegistryObject<Item> ARCANE_DEBRIS_BLOCK_ITEM = ITEMS.register("arcane_debris", () -> new BlockItem(BlockRegistry.ARCANE_DEBRIS.get(), new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
    public static final RegistryObject<Item> ALCHEMIST_CAULDRON_BLOCK_ITEM = ITEMS.register("alchemist_cauldron", () -> new BlockItem(BlockRegistry.ALCHEMIST_CAULDRON.get(), new Item.Properties().tab(ItemGroup.TAB_DECORATIONS)));

    /**
     * Armor
     */
//    public static final RegistryObject<Item> WIZARD_HAT = ITEMS.register("wizard_hat", () -> new WizardArmorItem(ArmorMaterials.LEATHER, EquipmentSlot.HEAD, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
//    public static final RegistryObject<Item> WIZARD_ROBE = ITEMS.register("wizard_robe", () -> new WizardArmorItem(ArmorMaterials.LEATHER, EquipmentSlot.CHEST, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
//    public static final RegistryObject<Item> WIZARD_PANTS = ITEMS.register("wizard_pants", () -> new WizardArmorItem(ArmorMaterials.LEATHER, EquipmentSlot.LEGS, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
//    public static final RegistryObject<Item> WIZARD_BOOTS = ITEMS.register("wizard_boots", () -> new WizardArmorItem(ArmorMaterials.LEATHER, EquipmentSlot.FEET, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));

    public static final RegistryObject<Item> WANDERING_MAGICIAN_HELMET = ITEMS.register("wandering_magician_helmet", () -> new WanderingMagicianArmorItem(EquipmentSlotType.HEAD, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> WANDERING_MAGICIAN_CHESTPLATE = ITEMS.register("wandering_magician_chestplate", () -> new WanderingMagicianArmorItem(EquipmentSlotType.CHEST, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> WANDERING_MAGICIAN_LEGGINGS = ITEMS.register("wandering_magician_leggings", () -> new WanderingMagicianArmorItem(EquipmentSlotType.LEGS, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> WANDERING_MAGICIAN_BOOTS = ITEMS.register("wandering_magician_boots", () -> new WanderingMagicianArmorItem(EquipmentSlotType.FEET, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));

    public static final RegistryObject<Item> PUMPKIN_HELMET = ITEMS.register("pumpkin_helmet", () -> new PumpkinArmorItem(EquipmentSlotType.HEAD, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> PUMPKIN_CHESTPLATE = ITEMS.register("pumpkin_chestplate", () -> new PumpkinArmorItem(EquipmentSlotType.CHEST, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> PUMPKIN_LEGGINGS = ITEMS.register("pumpkin_leggings", () -> new PumpkinArmorItem(EquipmentSlotType.LEGS, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> PUMPKIN_BOOTS = ITEMS.register("pumpkin_boots", () -> new PumpkinArmorItem(EquipmentSlotType.FEET, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));

    public static final RegistryObject<Item> PYROMANCER_HELMET = ITEMS.register("pyromancer_helmet", () -> new PyromancerArmorItem(EquipmentSlotType.HEAD, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> PYROMANCER_CHESTPLATE = ITEMS.register("pyromancer_chestplate", () -> new PyromancerArmorItem(EquipmentSlotType.CHEST, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> PYROMANCER_LEGGINGS = ITEMS.register("pyromancer_leggings", () -> new PyromancerArmorItem(EquipmentSlotType.LEGS, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> PYROMANCER_BOOTS = ITEMS.register("pyromancer_boots", () -> new PyromancerArmorItem(EquipmentSlotType.FEET, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));

    public static final RegistryObject<Item> ELECTROMANCER_HELMET = ITEMS.register("electromancer_helmet", () -> new ElectromancerArmorItem(EquipmentSlotType.HEAD, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> ELECTROMANCER_CHESTPLATE = ITEMS.register("electromancer_chestplate", () -> new ElectromancerArmorItem(EquipmentSlotType.CHEST, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> ELECTROMANCER_LEGGINGS = ITEMS.register("electromancer_leggings", () -> new ElectromancerArmorItem(EquipmentSlotType.LEGS, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> ELECTROMANCER_BOOTS = ITEMS.register("electromancer_boots", () -> new ElectromancerArmorItem(EquipmentSlotType.FEET, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));

    public static final RegistryObject<Item> ARCHEVOKER_HELMET = ITEMS.register("archevoker_helmet", () -> new ArchevokerArmorItem(EquipmentSlotType.HEAD, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> ARCHEVOKER_CHESTPLATE = ITEMS.register("archevoker_chestplate", () -> new ArchevokerArmorItem(EquipmentSlotType.CHEST, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> ARCHEVOKER_LEGGINGS = ITEMS.register("archevoker_leggings", () -> new ArchevokerArmorItem(EquipmentSlotType.LEGS, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> ARCHEVOKER_BOOTS = ITEMS.register("archevoker_boots", () -> new ArchevokerArmorItem(EquipmentSlotType.FEET, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));

    public static final RegistryObject<Item> CULTIST_HELMET = ITEMS.register("cultist_helmet", () -> new CultistArmorItem(EquipmentSlotType.HEAD, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> CULTIST_CHESTPLATE = ITEMS.register("cultist_chestplate", () -> new CultistArmorItem(EquipmentSlotType.CHEST, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> CULTIST_LEGGINGS = ITEMS.register("cultist_leggings", () -> new CultistArmorItem(EquipmentSlotType.LEGS, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> CULTIST_BOOTS = ITEMS.register("cultist_boots", () -> new CultistArmorItem(EquipmentSlotType.FEET, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));

    public static final RegistryObject<Item> CRYOMANCER_HELMET = ITEMS.register("cryomancer_helmet", () -> new CryomancerArmorItem(EquipmentSlotType.HEAD, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> CRYOMANCER_CHESTPLATE = ITEMS.register("cryomancer_chestplate", () -> new CryomancerArmorItem(EquipmentSlotType.CHEST, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> CRYOMANCER_LEGGINGS = ITEMS.register("cryomancer_leggings", () -> new CryomancerArmorItem(EquipmentSlotType.LEGS, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> CRYOMANCER_BOOTS = ITEMS.register("cryomancer_boots", () -> new CryomancerArmorItem(EquipmentSlotType.FEET, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));

    public static final RegistryObject<Item> SHADOWWALKER_HELMET = ITEMS.register("shadowwalker_helmet", () -> new ShadowwalkerArmorItem(EquipmentSlotType.HEAD, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> SHADOWWALKER_CHESTPLATE = ITEMS.register("shadowwalker_chestplate", () -> new ShadowwalkerArmorItem(EquipmentSlotType.CHEST, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> SHADOWWALKER_LEGGINGS = ITEMS.register("shadowwalker_leggings", () -> new ShadowwalkerArmorItem(EquipmentSlotType.LEGS, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> SHADOWWALKER_BOOTS = ITEMS.register("shadowwalker_boots", () -> new ShadowwalkerArmorItem(EquipmentSlotType.FEET, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));

    public static final RegistryObject<Item> PRIEST_HELMET = ITEMS.register("priest_helmet", () -> new PriestArmorItem(EquipmentSlotType.HEAD, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> PRIEST_CHESTPLATE = ITEMS.register("priest_chestplate", () -> new PriestArmorItem(EquipmentSlotType.CHEST, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> PRIEST_LEGGINGS = ITEMS.register("priest_leggings", () -> new PriestArmorItem(EquipmentSlotType.LEGS, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> PRIEST_BOOTS = ITEMS.register("priest_boots", () -> new PriestArmorItem(EquipmentSlotType.FEET, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));

    public static final RegistryObject<Item> PLAGUED_HELMET = ITEMS.register("plagued_helmet", () -> new PlaguedArmorItem(EquipmentSlotType.HEAD, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> PLAGUED_CHESTPLATE = ITEMS.register("plagued_chestplate", () -> new PlaguedArmorItem(EquipmentSlotType.CHEST, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> PLAGUED_LEGGINGS = ITEMS.register("plagued_leggings", () -> new PlaguedArmorItem(EquipmentSlotType.LEGS, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));
    public static final RegistryObject<Item> PLAGUED_BOOTS = ITEMS.register("plagued_boots", () -> new PlaguedArmorItem(EquipmentSlotType.FEET, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB)));

    public static final RegistryObject<Item> TARNISHED_CROWN = ITEMS.register("tarnished_helmet", () -> new TarnishedCrownArmorItem(EquipmentSlotType.HEAD, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB).rarity(Rarity.UNCOMMON)));

    /**
     * Curios
     */
    //public static final RegistryObject<CurioBaseItem> ENCHANTED_WARD_AMULET = ITEMS.register("enchanted_ward_amulet", EnchantedWardAmulet::new);
    public static final RegistryObject<CurioBaseItem> MANA_RING = ITEMS.register("mana_ring", () -> new SimpleAttributeCurio(new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB).stacksTo(1), AttributeRegistry.MAX_MANA.get(), new AttributeModifier("mana", 100, AttributeModifier.Operation.ADDITION)));
    public static final RegistryObject<CurioBaseItem> SILVER_RING = ITEMS.register("silver_ring", () -> new SimpleAttributeCurio(new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB).stacksTo(1), AttributeRegistry.MAX_MANA.get(), new AttributeModifier("mana", 25, AttributeModifier.Operation.ADDITION)));
    public static final RegistryObject<CurioBaseItem> COOLDOWN_RING = ITEMS.register("cooldown_ring", () -> new SimpleAttributeCurio(new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB).stacksTo(1), AttributeRegistry.COOLDOWN_REDUCTION.get(), new AttributeModifier("cd", 0.15, AttributeModifier.Operation.MULTIPLY_BASE)));
    public static final RegistryObject<CurioBaseItem> CAST_TIME_RING = ITEMS.register("cast_time_ring", () -> new SimpleAttributeCurio(new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB).stacksTo(1), AttributeRegistry.CAST_TIME_REDUCTION.get(), new AttributeModifier("ct", 0.15, AttributeModifier.Operation.MULTIPLY_BASE)));
    public static final RegistryObject<CurioBaseItem> HEAVY_CHAIN = ITEMS.register("heavy_chain_necklace", () -> new SimpleAttributeCurio(new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB).stacksTo(1), AttributeRegistry.SPELL_RESIST.get(), new AttributeModifier("spell resist", 0.15, AttributeModifier.Operation.MULTIPLY_BASE)));
    public static final RegistryObject<CurioBaseItem> EMERALD_STONEPLATE_RING = ITEMS.register("emerald_stoneplate_ring", () -> new SimpleDescriptiveCurio(new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB).stacksTo(1), "ring"));
    public static final RegistryObject<CurioBaseItem> FIREWARD_RING = ITEMS.register("fireward_ring", FirewardRing::new);
    public static final RegistryObject<CurioBaseItem> FROSTWARD_RING = ITEMS.register("frostward_ring", FrostwardRing::new);
    public static final RegistryObject<CurioBaseItem> POISONWARD_RING = ITEMS.register("poisonward_ring", PoisonwardRing::new);
    public static final RegistryObject<CurioBaseItem> CONJURERS_TALISMAN = ITEMS.register("conjurers_talisman", () -> new SimpleAttributeCurio(new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB).stacksTo(1), AttributeRegistry.SUMMON_DAMAGE.get(), new AttributeModifier("summon", 0.10, AttributeModifier.Operation.MULTIPLY_BASE)));
    public static final RegistryObject<CurioBaseItem> AFFINITY_RING = ITEMS.register("affinity_ring", () -> new AffinityRing(new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB).stacksTo(1)));
    public static final RegistryObject<CurioBaseItem> CONCENTRATION_AMULET = ITEMS.register("concentration_amulet", () -> new SimpleDescriptiveCurio(new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_EQUIPMENT_TAB).stacksTo(1), "necklace"));
    public static final RegistryObject<CurioBaseItem> INVISIBILITY_RING = ITEMS.register("invisibility_ring", InvisibiltyRing::new);

    /**
     * Spawn eggs
     */
    public static final RegistryObject<ForgeSpawnEggItem> KEEPER_SPAWN_EGG = ITEMS.register("keeper_spawn_egg", () -> new ForgeSpawnEggItem(EntityRegistry.KEEPER, 0x352d2d, 0x766a76, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB).stacksTo(64)));
    public static final RegistryObject<ForgeSpawnEggItem> DEAD_KING_CORPSE_SPAWN_EGG = ITEMS.register("dead_king_corpse_spawn_egg", () -> new ForgeSpawnEggItem(EntityRegistry.DEAD_KING_CORPSE, 6842447, 15066584, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB).stacksTo(64)));
    public static final RegistryObject<ForgeSpawnEggItem> ARCHEVOKER_SPAWN_EGG = ITEMS.register("archevoker_spawn_egg", () -> new ForgeSpawnEggItem(EntityRegistry.ARCHEVOKER, 0x0C0C0C, 0xCCA858, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB).stacksTo(64)));
    public static final RegistryObject<ForgeSpawnEggItem> NECROMANCER_SPAWN_EGG = ITEMS.register("necromancer_spawn_egg", () -> new ForgeSpawnEggItem(EntityRegistry.NECROMANCER, 0x3E2B20, 0x515937, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB).stacksTo(64)));
    public static final RegistryObject<ForgeSpawnEggItem> CRYOMANCER_SPAWN_EGG = ITEMS.register("cryomancer_spawn_egg", () -> new ForgeSpawnEggItem(EntityRegistry.CRYOMANCER, 0xFFFFFF, 0x97ffed, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB).stacksTo(64)));
    public static final RegistryObject<ForgeSpawnEggItem> PYROMANCER_SPAWN_EGG = ITEMS.register("pyromancer_spawn_egg", () -> new ForgeSpawnEggItem(EntityRegistry.PYROMANCER, 0x7A1010, 0x262525, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB).stacksTo(64)));
    public static final RegistryObject<ForgeSpawnEggItem> PRIEST_SPAWN_EGG = ITEMS.register("priest_spawn_egg", () -> new ForgeSpawnEggItem(EntityRegistry.PRIEST, 0xFFFFFF, 0xffde58, new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB).stacksTo(64)));

    public static Collection<RegistryObject<Item>> getIronsItems() {
        return ITEMS.getEntries();
    }
}
