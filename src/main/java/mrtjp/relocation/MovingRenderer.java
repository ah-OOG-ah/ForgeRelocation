package mrtjp.relocation;

import static org.lwjgl.opengl.GL11.*;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.world.World;

import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
import mrtjp.core.math.MathLib;

public class MovingRenderer {

    public static final MovingRenderer instance = new MovingRenderer();

    public boolean isRendering = false;
    public boolean renderHack = true;

    private World oldWorld = null;
    private float frame = 0;
    private RenderBlocks renderBlocks = null;

    private void render(int x, int y, int z, Vector3 rpos) {

        Minecraft mc = Minecraft.getMinecraft();

        int oldOcclusion = mc.gameSettings.ambientOcclusion;
        mc.gameSettings.ambientOcclusion = 0;

        Block block = mc.theWorld.getBlock(x, y, z);
        if (block == null) return;

        TextureManager engine = TileEntityRendererDispatcher.instance.field_147553_e;
        if (engine != null) engine.bindTexture(TextureMap.locationBlocksTexture);
        mc.entityRenderer.enableLightmap(frame);

        int light = mc.theWorld.getLightBrightnessForSkyBlocks(x, y, z, block.getLightValue(mc.theWorld, x, y, z));
        int l1 = light % 65536;
        int l2 = light / 65536;

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, l1, l2);
        glColor4f(0, 0, 0, 0);
        RenderHelper.disableStandardItemLighting();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
        glDisable(GL_CULL_FACE);
        glShadeModel((Minecraft.isAmbientOcclusionEnabled()) ? GL_SMOOTH : GL_FLAT);

        for (int pass : Arrays.asList(0, 1)) {
            Tessellator.instance.startDrawingQuads();
            Tessellator.instance.setTranslation(
                    -TileEntityRendererDispatcher.staticPlayerX + MathLib.clamp(-1f, 1f, (float) rpos.x),
                    -TileEntityRendererDispatcher.staticPlayerY + MathLib.clamp(-1f, 1f, (float) rpos.y),
                    -TileEntityRendererDispatcher.staticPlayerZ + MathLib.clamp(-1f, 1f, (float) rpos.z));
            Tessellator.instance.setColorOpaque(1, 1, 1);

            if (block.canRenderInPass(pass)) {
                renderHack = false;
                renderBlocks.renderBlockByRenderType(block, x, y, z);
                renderHack = true;
            }

            Tessellator.instance.setTranslation(0, 0, 0);
            Tessellator.instance.draw();
        }
        RenderHelper.enableStandardItemLighting();
        mc.entityRenderer.disableLightmap(frame);
        mc.gameSettings.ambientOcclusion = oldOcclusion;
    }

    public void onRenderWorldEvent() {
        Minecraft mc = Minecraft.getMinecraft();
        if (oldWorld != mc.theWorld) {
            oldWorld = mc.theWorld;
            renderBlocks = new MovingRenderBlocks(new MovingWorld(mc.theWorld));
            renderBlocks.renderAllFaces = true;
        }

        for (BlockStruct s : MovementManager2.getWorldStructs(mc.theWorld).structs) {
            for (BlockCoord b : s.preMoveBlocks) {
                render(b.x, b.y, b.z, renderPos(s, frame));
            }
        }
    }

    public void onPreRenderTick(float time) {
        isRendering = true;
        frame = time;
    }

    public void onPostRenderTick() {
        isRendering = false;
    }

    public Vector3 renderPos(BlockStruct s, float partial) {
        return new Vector3(BlockCoord.sideOffsets[s.moveDir()]).multiply(s.progress + s.speed * partial);
    }
}
