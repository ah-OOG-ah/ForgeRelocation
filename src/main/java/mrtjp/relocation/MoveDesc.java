package mrtjp.relocation;

import mrtjp.relocation.api.IMovementDescriptor;

import java.lang.ref.WeakReference;

public class MoveDesc implements IMovementDescriptor {

    public WeakReference<BlockStruct> b;

    MoveDesc(BlockStruct b) {
        this(new WeakReference<>(b));
    }

    MoveDesc(WeakReference<BlockStruct> b) {
        this.b = b;
    }

    @Override
    public boolean isMoving() {
        BlockStruct bs = b.get();
        if (bs != null) {
            return bs.isFinished();
        }
        return false;
    }

    @Override
    public double getProgress() {
        BlockStruct bs = b.get();
        if (bs != null) {
            return bs.progress;
        }
        return -1;
    }

    @Override
    public int getSize() {
        BlockStruct bs = b.get();
        if (bs != null) {
            return bs.allBlocks.size();
        }
        return 0;
    }
}
