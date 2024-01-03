package mrtjp.relocation;

import static java.lang.Math.min;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.BlockCoord;
import mrtjp.core.world.WorldLib;
import mrtjp.relocation.api.IMovementCallback;

public class BlockStruct {

    public static final BlockStruct BLOCK_STRUCT = new BlockStruct();

    private int maxID = 0;

    public int claimID() {

        // little less than Short.MaxValue (reserved for terminator)
        maxID = (maxID < 32765) ? maxID + 1 : 0;
        return maxID;
    }

    int id = -1;
    double speed = 1 / 16D;
    // Note: this needs to preserve ordering! hence the LinkedHashSet
    LinkedHashSet<BlockRow> rows = new LinkedHashSet<>();
    WeakReference<IMovementCallback> callback = new WeakReference<>(null);

    double progress = 0;

    Set<BlockCoord> allBlocks = new HashSet<>(rows.stream().map(br -> br.allBlocks).reduce((l, r) -> {
        l.addAll(r);
        return l;
    }).orElseGet(ArrayList::new));
    Set<BlockCoord> preMoveBlocks = new HashSet<>(rows.stream().map(br -> br.preMoveBlocks).reduce((l, r) -> {
        l.addAll(r);
        return l;
    }).orElseGet(ArrayList::new));
    Set<BlockCoord> postMoveBlocks = new HashSet<>(rows.stream().map(br -> br.postMoveBlocks).reduce((l, r) -> {
        l.addAll(r);
        return l;
    }).orElseGet(ArrayList::new));

    public int moveDir() {
        return rows.iterator().next().moveDir;
    }

    public boolean contains(int x, int y, int z) {
        return rows.stream().anyMatch(r -> r.contains(x, y, z));
    }

    public void push() {
        progress = min(1, progress + speed);
    }

    public boolean isFinished() {
        return progress >= 1;
    }

    public Set<ChunkCoordIntPair> getChunks() {
        Set<ChunkCoordIntPair> c = new HashSet<>();
        for (BlockCoord b : allBlocks) {
            c.add(new ChunkCoordIntPair(b.x >> 4, b.z >> 4));
        }
        return c;
    }

    public void onAdded(World w) {
        if (!w.isRemote) {
            IMovementCallback c = callback.get();
            if (c != null) {
                c.setDescriptor(new MoveDesc(this));
                c.onMovementStarted();
            }
        }
    }

    public void doMove(World w) {
        for (BlockRow r : rows) r.doMove(w);
    }

    public void postMove(World w) {
        for (BlockRow r : rows) r.postMove(w);
    }

    public void endMove(World w) {
        for (BlockRow r : rows) r.endMove(w);
        if (!w.isRemote) {
            IMovementCallback c = callback.get();
            if (c != null) {
                c.onMovementFinished();
            }
        }
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof BlockStruct) {
            return ((BlockStruct) obj).id == this.id;
        }

        return false;
    }

    public void writeDesc(MCDataOutput out) {
        out.writeFloat((float) progress);
        out.writeFloat((float) speed);
        out.writeByte(rows.size());
        for (BlockRow r : rows) {
            out.writeLong(WorldLib.packCoords(r.pos));
            out.writeByte(r.moveDir);
            out.writeShort(r.size);
        }
    }

    public void readDesc(MCDataInput in) {

        progress = in.readFloat();
        speed = in.readFloat();
        LinkedHashSet<BlockRow> rb = new LinkedHashSet<>();
        for (int i = 0; i <= in.readUByte(); ++i) {
            rb.add(new BlockRow(WorldLib.unpackCoords(in.readLong()), in.readUByte(), in.readUShort()));
        }
        rows = rb;
    }

}
