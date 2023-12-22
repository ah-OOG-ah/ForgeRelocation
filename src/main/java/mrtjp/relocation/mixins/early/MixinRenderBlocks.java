package mrtjp.relocation.mixins.early;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.sugar.Local;

import mrtjp.relocation.MovementManager2;
import mrtjp.relocation.MovingRenderer;

@Mixin(RenderBlocks.class)
public class MixinRenderBlocks {

    @Redirect(
            method = "renderBlockByRenderType",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/Block;getRenderType()I", ordinal = 0))
    private int frelocation$vanishMovingBlocks(Block block, @Local(ordinal = 0) int x, @Local(ordinal = 1) int y,
            @Local(ordinal = 2) int z) {

        if (MovingRenderer.instance.renderHack && MovementManager2.isMoving(Minecraft.getMinecraft().theWorld, x, y, z)) {
            return -1;
        } else {
            return block.getRenderType();
        }
    }
}
