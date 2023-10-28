package io.redspace.ironsspellbooks.jei;

import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Objects;

public final class AlchemistCauldronJeiRecipe {
    private final List<ItemStack> inputs;
    private final List<ItemStack> outputs;
    private final List<ItemStack> catalysts;

    public AlchemistCauldronJeiRecipe(List<ItemStack> inputs, List<ItemStack> outputs, List<ItemStack> catalysts) {
        this.inputs = List.copyOf(inputs);
        this.outputs = List.copyOf(outputs);
        this.catalysts = List.copyOf(catalysts);
    }

    public List<ItemStack> inputs() {
        return inputs;
    }

    public List<ItemStack> outputs() {
        return outputs;
    }

    public List<ItemStack> catalysts() {
        return catalysts;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AlchemistCauldronJeiRecipe) obj;
        return Objects.equals(this.inputs, that.inputs) &&
                Objects.equals(this.outputs, that.outputs) &&
                Objects.equals(this.catalysts, that.catalysts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputs, outputs, catalysts);
    }

    @Override
    public String toString() {
        return "AlchemistCauldronJeiRecipe[" +
                "inputs=" + inputs + ", " +
                "outputs=" + outputs + ", " +
                "catalysts=" + catalysts + ']';
    }

}
