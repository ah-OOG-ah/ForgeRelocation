package mrtjp.mcframes;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mrtjp.core.block.BlockCore;
import mrtjp.mcframes.api.IFrame;
import mrtjp.mcframes.api.MCFramesAPI;

public class BlockFrame extends BlockCore implements IFrame {

    public BlockFrame() {
        super("mcframes.frame", Material.wood);
        setResistance(5.0f);
        setHardness(2.0f);
        setStepSound(Block.soundTypeWood);
        setCreativeTab(CreativeTabs.tabTransport);
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockFrame.class;
    }

    @Override
    public boolean stickOut(World w, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public boolean stickIn(World w, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public boolean isSideSolid(IBlockAccess w, int x, int y, int z, ForgeDirection side) {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
        return MCFramesAPI.instance.raytraceFrame(x, y, z, 0, start, end);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World w, int x, int y, int z) {
        return Cuboid6.full.copy().add(new Vector3(x, y, z)).toAABB();
    }

    @Override
    public int getRenderType() {
        return RenderFrame.instance.renderID;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return RenderFrame.instance.icon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {
        RenderFrame.instance.registerIcons(reg);
    }
}
