package mrtjp.relocation;

import codechicken.lib.vec.BlockCoord;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Utils {

    public static void rescheduleTicks(
        World world,
        Set<BlockCoord> blocks,
        Set<BlockCoord> allBlocks,
        int dir
    ) {
        if (world instanceof WorldServer) {
            Set<NextTickListEntry> hash = ObfuscationReflectionHelper.getPrivateValue(
                WorldServer.class,
                (WorldServer) world,
                "field_73064_N",
                "pendingTickListEntriesHashSet"
            );

            Set<NextTickListEntry> tree = ObfuscationReflectionHelper.getPrivateValue(
                WorldServer.class,
                (WorldServer) world,
                "field_73065_O",
                "pendingTickListEntriesTreeSet"
            );

            List<NextTickListEntry> list = ObfuscationReflectionHelper.getPrivateValue(
                WorldServer.class,
                (WorldServer) world,
                "field_94579_S",
                "pendingTickListEntriesThisTick"
            );

            boolean isOptifine = world.getClass().getName() == "WorldServerOF";

            Set<Chunk> chunks = allBlocks.stream().map(b -> world.getChunkFromChunkCoords(b.x, b.y))
                .filter(Objects::nonNull).collect(Collectors.toSet());

            Set<NextTickListEntry> scheduledTicks = new HashSet<>(chunks.stream().map(c -> world.getPendingBlockUpdates(c, isOptifine)).filter(Objects::nonNull).reduce((s, s2) -> {
                s.addAll(s2);
                return s;
            }).orElseGet(ArrayList::new));

            if (isOptifine) {
                for (NextTickListEntry tick : scheduledTicks) {
                    tree.remove(tick);
                    hash.remove(tick);
                    list.remove(tick);
                }
            }

            for (NextTickListEntry tick : scheduledTicks) {
                BlockCoord bc = new BlockCoord(tick.xCoord, tick.yCoord, tick.zCoord);
                if (blocks.contains(bc)) {
                    bc.offset(dir);
                    tick.xCoord = bc.x;
                    tick.yCoord = bc.y;
                    tick.zCoord = bc.z;
                }
            }

            for (NextTickListEntry tick : scheduledTicks) {
                if (!hash.contains(tick)) {
                    hash.add(tick);
                    tree.add(tick);
                }
            }
        }
    }

    public static void rerenderBlocks(World world, Set<BlockCoord> blocks) {
        if (world.isRemote) {
            Minecraft mc = Minecraft.getMinecraft();
            List<TileEntity> teList = mc.renderGlobal.tileEntities;

            for (int pass : Arrays.asList(0, 1)) {
                for (BlockCoord c : blocks) {
                    TileEntity te = world.getTileEntity(c.x, c.y, c.z);
                    if (te != null) {
                        switch (pass) {
                            case 0: {
                                teList.remove(te);
                                break;
                            }
                            case 1: {
                                teList.add(te);
                                break;
                            }
                        }
                    }
                    mc.renderGlobal.markBlockForRenderUpdate(c.x, c.y, c.z);
                }
                mc.renderGlobal.updateRenderers(mc.renderViewEntity, false);
            }
        }
    }
}
