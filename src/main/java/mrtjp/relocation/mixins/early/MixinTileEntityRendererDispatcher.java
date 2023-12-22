package mrtjp.relocation.mixins.early;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import codechicken.lib.vec.Vector3;
import mrtjp.core.math.MathLib;
import mrtjp.relocation.BlockStruct;
import mrtjp.relocation.MovementManager2;
import mrtjp.relocation.MovingRenderer;

@Mixin(TileEntityRendererDispatcher.class)
public class MixinTileEntityRendererDispatcher {

    @Redirect(
            method = "renderTileEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher;renderTileEntityAt(Lnet/minecraft/tileentity/TileEntity;DDDF)V"))
    private void frelocation$renderTEAt(TileEntityRendererDispatcher thiz, TileEntity te, double xDist, double yDist,
            double zDist, float partialTicks) {
        if (MovementManager2.isMoving(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord)) {

            BlockStruct s = MovementManager2.getEnclosedStructure(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
            Vector3 vec = MovingRenderer.instance.renderPos(s, partialTicks);

            TileEntityRendererDispatcher.instance.renderTileEntityAt(
                    te,
                    xDist + MathLib.clamp(-1f, 1f, (float) vec.x),
                    yDist + MathLib.clamp(-1f, 1f, (float) vec.y),
                    zDist + MathLib.clamp(-1f, 1f, (float) vec.z),
                    partialTicks);
        } else {

            TileEntityRendererDispatcher.instance.renderTileEntityAt(te, xDist, yDist, zDist, partialTicks);
        }
    }
}
