package io.redspace.ironsspellbooks.datafix;

import com.google.common.collect.Lists;
import net.minecraft.nbt.*;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@ParametersAreNonnullByDefault
public class IronsTagTraverser implements TagVisitor {
    private final AtomicInteger changeCount;

    public IronsTagTraverser() {
        changeCount = new AtomicInteger(0);
    }

    private IronsTagTraverser(AtomicInteger changeCount) {
        this.changeCount = changeCount;
    }

    public boolean changesMade() {
        return changeCount.get() > 0;
    }

    public int totalChanges() {
        return changeCount.get();
    }

    public void visit(@Nullable INBT pTag) {
        if (pTag != null) {
            pTag.accept(this);
        }
    }

    public void visitString(StringNBT pTag) {
    }

    public void visitByte(ByteNBT pTag) {
    }

    public void visitShort(ShortNBT pTag) {
    }

    public void visitInt(IntNBT pTag) {
    }

    public void visitLong(LongNBT pTag) {
    }

    public void visitFloat(FloatNBT pTag) {
    }

    public void visitDouble(DoubleNBT pTag) {
    }

    public void visitByteArray(ByteArrayNBT pTag) {
    }

    public void visitIntArray(IntArrayNBT pTag) {
    }

    public void visitLongArray(LongArrayNBT pTag) {
    }

    public void visitList(ListNBT pTag) {
        for (int i = 0; i < pTag.size(); ++i) {
            new IronsTagTraverser(changeCount).visit(pTag.get(i));
        }
    }

    public void visitCompound(CompoundNBT pTag) {
        if (DataFixerHelpers.doFixUps(pTag)) {
            changeCount.incrementAndGet();
        }

        List<String> list = Lists.newArrayList(pTag.getAllKeys());
        Collections.sort(list);

        for (String s : list) {
            new IronsTagTraverser(changeCount).visit(pTag.get(s));
        }
    }

    public void visitEnd(EndNBT pTag) {
    }
}
