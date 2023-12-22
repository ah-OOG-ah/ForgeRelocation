package mrtjp.relocation;

import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
import mrtjp.core.math.MathLib;
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

import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class MovingRenderer {

    public static final MovingRenderer instance = new MovingRenderer();

    public boolean isRendering = false;
    public boolean renderHack = true;

    private World oldWorld = null;
    private float frame = 0;
    private RenderBlocks renderBlocks = null;

    private Minecraft mc = Minecraft.getMinecraft();
    private World world = mc.theWorld;
    private Tessellator tes = Tessellator.instance;

    private void render(int x, int y, int z, Vector3 rpos) {
        int oldOcclusion = mc.gameSettings.ambientOcclusion;
        mc.gameSettings.ambientOcclusion = 0;

        Block block = world.getBlock(x, y, z);
        if (block == null) return;

        TextureManager engine = TileEntityRendererDispatcher.instance.field_147553_e;
        if (engine != null) engine.bindTexture(TextureMap.locationBlocksTexture);
        mc.entityRenderer.enableLightmap(frame);

        int light = world.getLightBrightnessForSkyBlocks(
            x,
            y,
            z,
            block.getLightValue(world, x, y, z)
        );
        int l1 = light % 65536;
        int l2 = light / 65536;

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, l1, l2);
        glColor4f(0, 0, 0, 0);
        RenderHelper.disableStandardItemLighting();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
        glDisable(GL_CULL_FACE);
        glShadeModel((Minecraft.isAmbientOcclusionEnabled()) ? GL_SMOOTH : GL_FLAT);

        for (int pass : Arrays.asList(1, 2)) {
            tes.startDrawingQuads();
            tes.setTranslation(
                -TileEntityRendererDispatcher.staticPlayerX + MathLib
                    .clamp(-1f, 1f, (float) rpos.x),
                -TileEntityRendererDispatcher.staticPlayerY + MathLib
                    .clamp(-1f, 1f, (float) rpos.y),
                -TileEntityRendererDispatcher.staticPlayerZ + MathLib.clamp(
                    -1f,
                    1f,
                        (float) rpos.z
                )
            );
            tes.setColorOpaque(1, 1, 1);

            if (block.canRenderInPass(pass)) {
                renderHack = false;
                renderBlocks.renderBlockByRenderType(block, x, y, z);
                renderHack = true;
            }

            tes.setTranslation(0, 0, 0);
            tes.draw();
        }
        RenderHelper.enableStandardItemLighting();
        mc.entityRenderer.disableLightmap(frame);
        mc.gameSettings.ambientOcclusion = oldOcclusion;
    }

    public void onRenderWorldEvent() {
        if (oldWorld != world) {
            oldWorld = world;
            renderBlocks = new MovingRenderBlocks(new MovingWorld(world));
            renderBlocks.renderAllFaces = true;
        }

        for (BlockStruct s : MovementManager2.getWorldStructs(world).structs) {
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
        return new Vector3(BlockCoord.sideOffsets[s.moveDir()])
            .multiply(s.progress + s.speed * partial);
    }
}
