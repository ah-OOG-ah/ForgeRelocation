package mrtjp.relocation;

import static java.lang.Math.min;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.BlockCoord;
import mrtjp.core.world.WorldLib;
import mrtjp.relocation.api.IMovementCallback;

public class BlockStruct {

    private static int maxID = 0;

    public static int claimID() {

        // little less than Short.MaxValue (reserved for terminator)
        maxID = (maxID < 32765) ? maxID + 1 : 0;
        return maxID;
    }

    public int id = -1;
    public double speed = 1 / 16D;
    private Set<BlockRow> rows = new HashSet<>();
    public WeakReference<IMovementCallback> callback = new WeakReference<>(null);

    public double progress = 0;

    // Originally, these were lazy. In the interest of not writing spaghetti (and also because I can't figure out how),
    // I'm just going to make them eager
    public final Set<BlockCoord> allBlocks = new HashSet<>();
    public final Set<BlockCoord> preMoveBlocks = new HashSet<>();
    public final Set<BlockCoord> postMoveBlocks = new HashSet<>();

    public Set<BlockRow> getRows() {
        return this.rows;
    }

    public void setRows(Set<BlockRow> rows) {
        this.rows = rows;
        initBlockSets();
    }

    private void initBlockSets() {

        this.allBlocks.clear();
        this.preMoveBlocks.clear();
        this.postMoveBlocks.clear();

        this.allBlocks.addAll(rows.stream().flatMap(br -> br.allBlocks.stream()).collect(Collectors.toSet()));
        this.preMoveBlocks.addAll(rows.stream().flatMap(br -> br.preMoveBlocks.stream()).collect(Collectors.toSet()));
        this.postMoveBlocks.addAll(rows.stream().flatMap(br -> br.postMoveBlocks.stream()).collect(Collectors.toSet()));
    }

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
        rows.clear();
        final int end = in.readUByte();
        for (int i = 0; i < end; ++i) {
            rows.add(new BlockRow(WorldLib.unpackCoords(in.readLong()), in.readUByte(), in.readUShort()));
        }
        initBlockSets();
    }

}
