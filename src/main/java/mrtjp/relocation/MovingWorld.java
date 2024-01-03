package mrtjp.relocation;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

import codechicken.lib.vec.BlockCoord;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MovingWorld implements IBlockAccess {

    public final World world;

    public MovingWorld(World w) {
        this.world = w;
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        return world.getBlock(x, y, z);
    }

    @Override
    public TileEntity getTileEntity(int x, int y, int z) {
        return world.getTileEntity(x, y, z);
    }

    int computeLightValue(int x, int y, int z, EnumSkyBlock tpe) {

        int max = Integer.MIN_VALUE;
        for (int s = 0; s < 6; ++s) {
            BlockCoord c = new BlockCoord(x, y, z).offset(s);
            max = Math.max(world.getSavedLightValue(tpe, c.x, c.y, c.z), max);
        }
        return max;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int light) {
        if (MovementManager2.isMoving(Minecraft.getMinecraft().theWorld, x, y, z)) {
            int l1 = computeLightValue(x, y, z, EnumSkyBlock.Sky);
            int l2 = computeLightValue(x, y, z, EnumSkyBlock.Block);
            return l1 << 20 | Math.max(l2, light) << 4;
        }
        return world.getLightBrightnessForSkyBlocks(x, y, z, light);
    }

    @Override
    public int getBlockMetadata(int x, int y, int z) {
        return world.getBlockMetadata(x, y, z);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isAirBlock(int x, int y, int z) {
        return world.isAirBlock(x, y, z);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BiomeGenBase getBiomeGenForCoords(int x, int z) {
        return world.getBiomeGenForCoords(x, z);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getHeight() {
        return world.getHeight();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean extendedLevelsInChunkCache() {
        return world.extendedLevelsInChunkCache();
    }

    @Override
    public int isBlockProvidingPowerTo(int x, int y, int z, int side) {
        return world.isBlockProvidingPowerTo(x, y, z, side);
    }

    @Override
    public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
        return world.isSideSolid(x, y, z, side, _default);
    }
}
