package mrtjp.mcframes.handler;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mrtjp.core.block.TileRenderRegistry;
import mrtjp.mcframes.RenderFrame;
import mrtjp.mcframes.RenderMotor;

public class MCFramesProxy_client extends MCFramesProxy_server {

    @SideOnly(Side.CLIENT)
    @Override
    public void preinit() {
        super.preinit();

        TileRenderRegistry.setRenderer(MCFramesMod.instance.blockMotor, 0, RenderMotor.instance);

        RenderFrame.instance.renderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(RenderFrame.instance);
    }
}
