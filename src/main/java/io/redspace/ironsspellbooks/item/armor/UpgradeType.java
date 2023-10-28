package io.redspace.ironsspellbooks.item.armor;

import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface UpgradeType {

    Map<ResourceLocation, UpgradeType> UPGRADE_REGISTRY = new HashMap<>();

    static void registerUpgrade(UpgradeType upgrade) {
        UPGRADE_REGISTRY.put(upgrade.getId(), upgrade);
    }

    static Optional<UpgradeType> getUpgrade(ResourceLocation key) {
        UpgradeType upgradeType = UPGRADE_REGISTRY.get(key);
        return upgradeType == null ? Optional.empty() : Optional.of(upgradeType);
    }

    Attribute getAttribute();

    AttributeModifier.Operation getOperation();

    float getAmountPerUpgrade();

    ResourceLocation getId();
}
