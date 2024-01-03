package mrtjp.mcframes;

import static codechicken.lib.render.CCModel.combine;
import static codechicken.lib.render.CCModel.parseObjModels;

import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.TextureUtils;
import codechicken.lib.render.uv.IconTransformation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import mrtjp.core.vec.InvertX$;

public class RenderFrame implements ISimpleBlockRenderingHandler {

    public static final RenderFrame instance = new RenderFrame();

    public int renderID = -1;
    IIcon icon = null;

    private CCModel model = parseModel("frame");
    private ArrayList<CCModel> models = new ArrayList<>(64);

    private CCModel parseModel(String name) {
        CCModel m = null;
        try {
            m = combine(
                    parseObjModels(
                            this.getClass().getResource("/assets/mcframes/obj/" + name + ".obj").openStream(),
                            7,
                            InvertX$.MODULE$).values());
        } catch (IOException e) {
            e.printStackTrace();
        }

        m.apply(new Scale(1.00075, 1.00075, 1.00075));
        m.apply(new Translation(Vector3.center));
        return m;
    }

    @Override
    public int getRenderId() {
        return renderID;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public void renderInventoryBlock(Block b, int meta, int id, RenderBlocks rb) {
        renderInvBlock(rb, meta);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block b, int id, RenderBlocks rb) {
        renderWorldBlock(rb, world, x, y, z, world.getBlockMetadata(x, y, z));
        return true;
    }

    public void renderWorldBlock(RenderBlocks r, IBlockAccess w, int x, int y, int z, int meta) {
        TextureUtils.bindAtlas(0);
        CCRenderState ccrsi = CCRenderState.instance();
        ccrsi.reset();
        ccrsi.lightMatrix.locate(w, x, y, z);
        ccrsi.setBrightness(w, x, y, z);

        if (r.hasOverrideBlockTexture()) {
            getOrGenerateModel(0).render(new Translation(x, y, z), new IconTransformation(r.overrideBlockTexture));
        } else RenderFrame.instance.render(new Vector3(x, y, z), 0);
    }

    public void renderInvBlock(RenderBlocks r, int meta) {
        TextureUtils.bindAtlas(0);
        CCRenderState ccrsi = CCRenderState.instance();
        ccrsi.reset();
        ccrsi.setDynamic();
        ccrsi.pullLightmap();

        ccrsi.startDrawing();
        RenderFrame.instance.render(new Vector3(-0.5, -0.5, -0.5), 0);

        ccrsi.render();
        ccrsi.draw();
    }

    public void registerIcons(IIconRegister reg) {
        icon = reg.registerIcon("mcframes:frame");
    }

    public void render(Vector3 pos, int mask) {
        getOrGenerateModel(mask).render(pos.translation(), new IconTransformation(icon));
    }

    public CCModel getOrGenerateModel(int mask) {
        CCModel m = models.get(mask & 0x3f);
        if (m == null) {
            m = FrameModelGen.generate(model, mask);
            models.add(mask & 0x3f, m);
        }
        return m;
    }
}
