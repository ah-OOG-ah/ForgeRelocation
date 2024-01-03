package mrtjp.relocation;

import net.minecraft.world.World;

import mrtjp.relocation.api.ITileMover;

public class StaticTileMover implements ITileMover {

    @Override
    public boolean canMove(World w, int x, int y, int z) {
        return false;
    }

    @Override
    public void move(World w, int x, int y, int z, int dir) {

    }

    @Override
    public void postMove(World w, int x, int y, int z) {

    }
}
