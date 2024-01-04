package mrtjp.relocation;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.BlockCoord;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mrtjp.core.math.JMathLib;
import mrtjp.core.math.MathLib;
import mrtjp.relocation.api.IMovementCallback;
import mrtjp.relocation.handler.RelocationConfig;
import mrtjp.relocation.handler.RelocationSPH;
import scala.Tuple2;

public class MovementManager2 {

    public static final Map<Integer, WorldStructs> serverRelocations = new HashMap<>();
    public static final Map<Integer, WorldStructs> clientRelocations = new HashMap<>();

    public static Map<Integer, WorldStructs> relocationMap(boolean isClient) {
        if (isClient) return clientRelocations;
        else return serverRelocations;
    }

    public static WorldStructs getWorldStructs(World w) {

        return relocationMap(w.isRemote).computeIfAbsent(w.provider.dimensionId, (k) -> new WorldStructs());
    }

    public static World getWorld(int dim, boolean isClient) {
        return (isClient) ? getClientWorld(dim) : DimensionManager.getWorld(dim);
    }

    @SideOnly(Side.CLIENT)
    private static World getClientWorld(int dim) {
        WorldClient w = Minecraft.getMinecraft().theWorld;
        if (w == null) return null;
        return (w.provider.dimensionId == dim) ? w : null;
    }

    public static boolean writeDesc(World w, Set<ChunkCoordIntPair> chunks, MCDataOutput out) {
        boolean send = false;
        for (BlockStruct s : getWorldStructs(w).structs.stream()
                .filter(s -> s.getChunks().stream().anyMatch(chunks::contains)).collect(Collectors.toSet())) {
            send = true;
            out.writeShort(s.id);
            s.writeDesc(out);
        }
        if (send) out.writeShort(Short.MAX_VALUE);
        return send;
    }

    public static void readDesc(World w, MCDataInput in) {
        int id = in.readUShort();
        while (id != Short.MAX_VALUE) {
            BlockStruct struct = new BlockStruct();
            struct.id = id;
            struct.readDesc(in);
            addStructToWorld(w, struct);
            id = in.readUShort();
        }
    }

    public static void read(World w, MCDataInput in, int key) {
        switch (key) {
            case 1: {
                BlockStruct struct = new BlockStruct();
                struct.id = in.readUShort();
                struct.readDesc(in);
                addStructToWorld(w, struct);

                for (BlockCoord b : struct.allBlocks) { // rerender all moving blocks
                    w.func_147479_m(b.x, b.y, b.z);
                }
                break;
            }
            case 2: {
                int id = in.readUShort();
                Optional<BlockStruct> Bs = getWorldStructs(w).structs.stream().filter(bs -> bs.id == id).findFirst();
                if (Bs.isPresent()) {
                    clientCycleMove(w, Bs.get());
                } else {
                    throw new RuntimeException("DC: Moving structure with id " + id + " was not found client-side.");
                }
                break;
            }
            default: {
                throw new RuntimeException(
                        "DC: Packet with ID ${key} was not handled. "
                                + "Skipped ${in.asInstanceOf[PacketCustom].getByteBuf.array().length} bytes.");
            }
        }
    }

    public static void sendStruct(World w, BlockStruct struct) {
        final RelocationSPH.MCByteStream out = RelocationSPH.instance.getStream(w, struct.getChunks(), 1);
        out.writeShort(struct.id);
        struct.writeDesc(out);
        RelocationSPH.instance.forceSendData();
    }

    public static void sendCycle(World w, BlockStruct struct) {
        RelocationSPH.instance.getStream(w, struct.getChunks(), 2).writeShort(struct.id);
        RelocationSPH.instance.forceSendData();
    }

    public static boolean isMoving(World w, int x, int y, int z) {
        return getWorldStructs(w).contains(x, y, z);
    }

    public static void addStructToWorld(World w, BlockStruct b) {
        getWorldStructs(w).addStruct(b);
        b.onAdded(w);
    }

    public static BlockStruct getEnclosedStructure(World w, int x, int y, int z) {
        return getWorldStructs(w).structs.stream().filter(s -> s.contains(x, y, z)).findFirst().orElse(null);
    }

    public static boolean tryStartMove(World w, Set<BlockCoord> blocks, int moveDir, double speed,
            IMovementCallback c) {
        if (blocks.size() > RelocationConfig.instance.moveLimit) return false;

        Multimap<Pair<Integer, Integer>, Integer> map = MultimapBuilder.hashKeys().arrayListValues().build();
        for (BlockCoord b : blocks) {
            map.put(JMathLib.normal(b, moveDir), MathLib.basis(b, moveDir));
        }

        int shift = ((moveDir & 1) == 1) ? 1 : -1;
        LinkedHashSet<BlockRow> rows = new LinkedHashSet<>();
        for (Pair<Integer, Integer> normal : map.keySet()) {
            Integer[] line = map.get(normal).toArray(new Integer[0]);
            Integer[] sline = (shift == 1) ? Arrays.stream(line).sorted().toArray(Integer[]::new)
                    : Arrays.stream(line).sorted(Collections.reverseOrder()).toArray(Integer[]::new);
            for (Pair<Integer, Integer> e : JMathLib.splitLine(Arrays.asList(sline), shift)) {

                int basis = e.getLeft();
                int size = e.getRight();

                BlockCoord coord = MathLib
                        .rhrAxis(moveDir, new Tuple2<>(normal.getKey(), normal.getValue()), basis + shift);
                rows.add(new BlockRow(coord, moveDir, size));
            }
        }

        if (rows.stream().anyMatch(
                row -> !MovingTileRegistry.instance.canRunOverBlock(w, row.pos.x, row.pos.y, row.pos.z)))
            return false;

        for (BlockRow r : rows) TileMovingRow.setBlockForRow(w, r);

        final BlockStruct struct = new BlockStruct();
        struct.id = BlockStruct.claimID();
        struct.speed = speed;
        struct.setRows(rows);
        struct.callback = new WeakReference<>(c);
        addStructToWorld(w, struct);
        sendStruct(w, struct);

        return true;
    }

    public static void onTick(boolean isClient) {
        final Map<Integer, WorldStructs> map = relocationMap(isClient);
        for (Map.Entry<Integer, WorldStructs> entry : map.entrySet()) {
            WorldStructs ws = entry.getValue();

            if (ws != null && ws.nonEmpty()) {
                Integer dim = entry.getKey();
                ws.pushAll();
                final World world = getWorld(dim, isClient);
                if (world != null) {
                    for (BlockStruct bs : ws.structs) {
                        for (BlockRow br : bs.getRows()) {
                            br.pushEntities(world, bs.progress);
                        }
                    }
                }
            }
        }

        if (!isClient) {

            // Fair point to scala - this is a lot less ugly there. tbh there's definitely cleaner ways in java
            // but that's a future me problem
            final Map<Integer, Set<BlockStruct>> fin = map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().removeFinished())).entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            for (Map.Entry<Integer, Set<BlockStruct>> entry : fin.entrySet()) {
                Integer dim = entry.getKey();
                Set<BlockStruct> b = entry.getValue();

                World w = getWorld(dim, false);
                if (w != null) {
                    for (BlockStruct s : b) {
                        cycleMove(w, s);
                        sendCycle(w, s);
                    }
                }
            }
        }
    }

    public static void onWorldUnload(World w) {
        getWorldStructs(w).clear();
    }

    public static void clientCycleMove(World w, BlockStruct struct) {
        getWorldStructs(w).removeStruct(struct);
        struct.getRows().forEach(br -> br.pushEntities(w, 1.0));
        cycleMove(w, struct);
    }

    public static void cycleMove(World w, BlockStruct struct) {

        struct.doMove(w);
        struct.postMove(w);
        struct.endMove(w);

        Utils.rescheduleTicks(w, struct.preMoveBlocks, struct.allBlocks, struct.moveDir());

        // TODO: Scala moment
        Set<BlockCoord> changes = new HashSet<>();
        for (BlockRow r : struct.getRows()) {
            r.cacheChanges(w, changes);
        }
        for (BlockCoord bc : changes) w.notifyBlockOfNeighborChange(bc.x, bc.y, bc.z, Blocks.air);

        Utils.rerenderBlocks(w, struct.preMoveBlocks);
    }
}
