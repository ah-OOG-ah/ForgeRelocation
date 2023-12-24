package mrtjp.mcframes;

import mrtjp.core.block.InstancedBlock;
import mrtjp.core.block.InstancedBlockTile;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockMotor extends InstancedBlock {

    public BlockMotor() {
        super("mcframes.motor", Material.iron);
        setHardness(5f);
        setResistance(10f);
        setStepSound(Block.soundTypeMetal);
        setCreativeTab(CreativeTabs.tabTransport);
        addSingleTile(TileMotor.class);
    }

    @Override
    public boolean isSideSolid(
        IBlockAccess world,
        int x,
        int y,
        int z,
        ForgeDirection side
    ) {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return true;
    }
}
