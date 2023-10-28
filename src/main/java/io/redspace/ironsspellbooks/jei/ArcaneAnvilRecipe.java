package io.redspace.ironsspellbooks.jei;

import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Objects;

public final class ArcaneAnvilRecipe {
    private final List<ItemStack> leftInputs;
    private final List<ItemStack> rightInputs;
    private final List<ItemStack> outputs;

    public ArcaneAnvilRecipe(List<ItemStack> leftInputs, List<ItemStack> rightInputs, List<ItemStack> outputs) {
        this.leftInputs = List.copyOf(leftInputs);
        this.rightInputs = List.copyOf(rightInputs);
        this.outputs = List.copyOf(outputs);
    }

    public boolean isValid() {
        if (leftInputs.isEmpty() || rightInputs.isEmpty() || outputs.isEmpty()) {
            return false;
        }

        return true;
    }

    public List<ItemStack> leftInputs() {
        return leftInputs;
    }

    public List<ItemStack> rightInputs() {
        return rightInputs;
    }

    public List<ItemStack> outputs() {
        return outputs;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ArcaneAnvilRecipe) obj;
        return Objects.equals(this.leftInputs, that.leftInputs) &&
                Objects.equals(this.rightInputs, that.rightInputs) &&
                Objects.equals(this.outputs, that.outputs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftInputs, rightInputs, outputs);
    }

    @Override
    public String toString() {
        return "ArcaneAnvilRecipe[" +
                "leftInputs=" + leftInputs + ", " +
                "rightInputs=" + rightInputs + ", " +
                "outputs=" + outputs + ']';
    }

}
