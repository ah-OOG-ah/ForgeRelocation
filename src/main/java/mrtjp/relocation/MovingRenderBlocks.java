package mrtjp.relocation;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class MovingRenderBlocks extends RenderBlocks {
    public double eps = 1D / 0x10000;

    public MovingRenderBlocks(IBlockAccess w) {
        super(w);
    }

    @Override
    public boolean renderStandardBlock(Block block, int x, int y, int z) {
        if (MovementManager2.isMoving(Minecraft.getMinecraft().theWorld, x, y, z)) {
            if (renderMinX == 0.0d) renderMinX += eps;
            if (renderMinY == 0.0d) renderMinY += eps;
            if (renderMinZ == 0.0d) renderMinZ += eps;
            if (renderMaxX == 1.0d) renderMaxX -= eps;
            if (renderMaxY == 1.0d) renderMaxY -= eps;
            if (renderMaxZ == 1.0d) renderMaxZ -= eps;
        }
        return super.renderStandardBlock(block, x, y, z);
    }
}
