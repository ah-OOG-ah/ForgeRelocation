package mrtjp.mcframes.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import codechicken.lib.vec.BlockCoord;
import mrtjp.core.world.WorldLib;
import mrtjp.mcframes.StickRegistry;
import mrtjp.mcframes.api.StickResolver;
import mrtjp.relocation.api.BlockPos;
import mrtjp.relocation.api.RelocationAPI;

public class StickResolver_Impl extends StickResolver {

    public static final StickResolver_Impl instance = new StickResolver_Impl();

    private World world = null;
    private BlockCoord start = null;
    private Set<BlockCoord> excl = null;

    @Override
    public Set<BlockPos> getStructure(World w, int x, int y, int z, BlockPos... ex) {
        world = w;
        start = new BlockCoord(x, y, z);
        excl = Arrays.stream(ex).map(b -> new BlockCoord(b.x, b.y, b.z)).collect(Collectors.toSet());
        ArrayList<BlockCoord> tmp = new ArrayList<>();
        tmp.add(start);
        Set<BlockCoord> result = iterate(tmp);
        world = null;
        start = null;
        excl = null;
        return result.stream().map(b -> new BlockPos(b.x, b.y, b.z)).collect(Collectors.toSet());
    }

    private Set<BlockCoord> iterate(List<BlockCoord> open) {
        return iterate(open, new HashSet<>());
    }

    private Set<BlockCoord> iterate(List<BlockCoord> open, Set<BlockCoord> closed) {

        if (open.isEmpty()) {
            return closed;
        }

        BlockCoord next = open.get(0);

        Block b = WorldLib.getBlock(world, next);
        if (b instanceof Block) {
            List<BlockCoord> toCheck = new ArrayList<>();
            for (int s = 0; s < 6; ++s) {
                if (StickRegistry.instance.resolveStick(world, next, s)) {
                    BlockCoord to = next.copy().offset(s);
                    if (!excl.contains(to) && !closed.contains(to) && !open.contains(to))
                        if (!world.isAirBlock(to.x, to.y, to.z)
                                && !RelocationAPI.instance.isMoving(world, to.x, to.y, to.z)
                        /* && MovingTileRegistry.canMove(world, to.x, to.y, to.z) */)
                            // Dont ignore non-movables, have them halt movement.
                            toCheck.add(to);
                }
            }
            open.remove(next);
            open.addAll(toCheck);
            closed.add(next);
            return iterate(open, closed);
        }

        open.remove(next);
        closed.add(next);
        return iterate(open, closed);
    }
}
