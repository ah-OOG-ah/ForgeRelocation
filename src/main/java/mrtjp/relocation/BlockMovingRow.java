package mrtjp.relocation;

import mrtjp.core.block.InstancedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import scala.runtime.Null$;

public class BlockMovingRow extends InstancedBlock {

    public BlockMovingRow() {
        super("relocation.blockmovingrow", Material.iron);
        setHardness(-1f);
        setStepSound(Block.soundTypeGravel);
        setCreativeTab(null);
        addSingleTile(TileMovingRow.class);
    }

    // Sus, possible mis-translation
    @Override
    public TileEntity createNewTileEntity(World w, int i) {
        return null;
    }
}
