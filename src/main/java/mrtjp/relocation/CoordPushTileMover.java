package mrtjp.relocation;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import codechicken.lib.vec.BlockCoord;
import mrtjp.core.world.WorldLib;
import mrtjp.relocation.api.ITileMover;
import scala.Tuple3;

public class CoordPushTileMover implements ITileMover {

    @Override
    public boolean canMove(World w, int x, int y, int z) {
        return true;
    }

    @Override
    public void move(World w, int x, int y, int z, int side) {

        Tuple3<Block, Object, TileEntity> thing = WorldLib.getBlockInfo(w, x, y, z);
        Block b = thing._1();
        int meta = (int) thing._2();
        TileEntity te = thing._3();

        BlockCoord pos = new BlockCoord(x, y, z).offset(side);
        if (te != null) {
            te.invalidate();
            WorldLib.uncheckedRemoveTileEntity(w, x, y, z);
        }
        WorldLib.uncheckedSetBlock(w, x, y, z, Blocks.air, 0);
        WorldLib.uncheckedSetBlock(w, pos.x, pos.y, pos.z, b, meta);
        if (te != null) {
            te.xCoord = pos.x;
            te.yCoord = pos.y;
            te.zCoord = pos.z;
            te.validate();
            WorldLib.uncheckedSetTileEntity(w, pos.x, pos.y, pos.z, te);
        }
    }

    @Override
    public void postMove(World w, int x, int y, int z) {}
}
