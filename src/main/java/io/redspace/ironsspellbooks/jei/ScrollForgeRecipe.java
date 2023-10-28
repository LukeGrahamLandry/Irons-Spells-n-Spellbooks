package io.redspace.ironsspellbooks.jei;

import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Objects;

public final class ScrollForgeRecipe {
    private final List<ItemStack> inkInputs;
    private final ItemStack paperInput;
    private final ItemStack focusInput;
    private final List<ItemStack> scrollOutputs;

    public ScrollForgeRecipe(List<ItemStack> inkInputs, ItemStack paperInput, ItemStack focusInput, List<ItemStack> scrollOutputs) {
        this.inkInputs = List.copyOf(inkInputs);
        this.paperInput = paperInput;
        this.focusInput = focusInput;
        this.scrollOutputs = List.copyOf(scrollOutputs);
    }

    public boolean isValid() {
        if (inkInputs.isEmpty() || scrollOutputs.isEmpty() || paperInput.isEmpty() || focusInput.isEmpty()) {
            return false;
        }

        return true;
    }

    public List<ItemStack> inkInputs() {
        return inkInputs;
    }

    public ItemStack paperInput() {
        return paperInput;
    }

    public ItemStack focusInput() {
        return focusInput;
    }

    public List<ItemStack> scrollOutputs() {
        return scrollOutputs;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ScrollForgeRecipe) obj;
        return Objects.equals(this.inkInputs, that.inkInputs) &&
                Objects.equals(this.paperInput, that.paperInput) &&
                Objects.equals(this.focusInput, that.focusInput) &&
                Objects.equals(this.scrollOutputs, that.scrollOutputs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inkInputs, paperInput, focusInput, scrollOutputs);
    }

    @Override
    public String toString() {
        return "ScrollForgeRecipe[" +
                "inkInputs=" + inkInputs + ", " +
                "paperInput=" + paperInput + ", " +
                "focusInput=" + focusInput + ", " +
                "scrollOutputs=" + scrollOutputs + ']';
    }

}
