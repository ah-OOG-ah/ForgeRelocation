package mrtjp.relocation;

import codechicken.lib.vec.BlockCoord;
import mrtjp.core.math.MathLib;
import mrtjp.core.world.WorldLib;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class BlockRow {

    public final BlockCoord pos;
    public final int moveDir;
    public final int size;
    final List<BlockCoord> allBlocks;
    final List<BlockCoord> preMoveBlocks;
    final List<BlockCoord> postMoveBlocks;

    BlockRow(BlockCoord pos, int moveDir, int size) {
        this.pos = pos;
        this.moveDir = moveDir;
        this.size = size;

        allBlocks = IntStream.rangeClosed(0, size).boxed()
            .map(i -> pos.copy().offset(moveDir ^ 1, i)).collect(Collectors.toList());

        // Not sure about these - they might need to be swapped
        preMoveBlocks = allBlocks.stream().skip(1).collect(Collectors.toList());
        postMoveBlocks = allBlocks.stream().limit(allBlocks.size() - 1).collect(Collectors.toList());
    }

    public boolean contains(int x, int y, int z) {

        if (MathLib.normal(x, y, z, moveDir) == MathLib.normal(pos, moveDir)) {
            int b1 = MathLib.basis(pos, moveDir);
            int b2 = b1 + size * MathLib.shift(moveDir ^ 1);
            return IntStream.rangeClosed(min(b1, b2), max(b1, b2)).anyMatch(value -> value == MathLib.basis(x, y, z, moveDir));
        } else return false;
    }

    public void pushEntities(World w, double progress) {
        TileEntity te = WorldLib.uncheckedGetTileEntity(w, pos.x, pos.y, pos.z);
        if (te instanceof TileMovingRow) {
            ((TileMovingRow) te).pushEntities(this, progress);
        }
    }

    public void doMove(World w) {
        if (pos.y < 0 || pos.y >= 256) return;

        w.removeTileEntity(pos.x, pos.y, pos.z);
        WorldLib.uncheckedSetBlock(
            w,
            pos.x,
            pos.y,
            pos.z,
            Blocks.air,
            0
        ); // Remove movement block

        for (BlockCoord b : preMoveBlocks) {
            MovingTileRegistry.instance.move(w, b.x, b.y, b.z, moveDir);
        }
    }

    public void postMove(World w) {
        for (BlockCoord b : postMoveBlocks) {
            MovingTileRegistry.instance.postMove(w, b.x, b.y, b.z);
        }
    }

    public void endMove(World w) {}

    public void cacheChanges(World w, Set<BlockCoord> changes) {
        for (int i : IntStream.rangeClosed(0, size).toArray()) {
            BlockCoord c = pos.copy().offset(moveDir ^ 1, i);
            changes.add(c);
            for (int ii = 0; ii < 6; ++ii) {
                for (int iii = 0; iii < 6; ++iii) {
                    // might be a mis-translation
                    if (iii != (ii ^ 1)) {
                        changes.add(c.copy().offset(ii).offset(iii));
                    }
                }
            }
        }
    }
}
