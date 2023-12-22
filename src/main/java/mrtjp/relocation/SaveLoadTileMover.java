package mrtjp.relocation;

import codechicken.lib.vec.BlockCoord;
import mrtjp.core.world.WorldLib;
import mrtjp.relocation.api.ITileMover;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import scala.Tuple3;

import java.util.function.Supplier;

public class SaveLoadTileMover implements ITileMover {

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
        NBTTagCompound tag;
        if ((te != null)) {
            tag = new NBTTagCompound();
            te.writeToNBT(tag);
            tag.setInteger("x", pos.x);
            tag.setInteger("y", pos.y);
            tag.setInteger("z", pos.z);
            te.onChunkUnload();
            w.removeTileEntity(x, y, z);
        } else {
            tag = null;
        }
        WorldLib.uncheckedSetBlock(w, x, y, z, Blocks.air, 0);
        WorldLib.uncheckedSetBlock(w, pos.x, pos.y, pos.z, b, meta);
        if (tag != null) {
            TileEntity te2 = TileEntity.createAndLoadEntity(tag);
            if (te2 instanceof TileEntity){
                w.getChunkFromBlockCoords(pos.x, pos.z).addTileEntity(te);
            }
        }
    }

    @Override
    public void postMove(World w, int x, int y, int z) {}
}
