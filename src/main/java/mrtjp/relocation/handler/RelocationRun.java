package mrtjp.relocation.handler;

import codechicken.lib.vec.BlockCoord;
import mrtjp.relocation.api.IMovementCallback;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class RelocationRun {

    public World world = null;
    public int dir = -1;
    public double speed = 0;
    public IMovementCallback callback = null;
    public final Set<BlockCoord> blocks = new HashSet<>();

    public void clear() {
        world = null;
        dir = -1;
        speed = 0;
        callback = null;
        blocks.clear();
    }
}
