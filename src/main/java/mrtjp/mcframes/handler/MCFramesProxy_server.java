package mrtjp.mcframes.handler;

import mrtjp.mcframes.BlockFrame;
import mrtjp.mcframes.BlockMotor;

public class MCFramesProxy_server {

    public void preinit() {
        MCFramesMod.instance.blockMotor = new BlockMotor();
        MCFramesMod.instance.blockFrame = new BlockFrame();
    }

    public void init() {
        MCFramesRecipes.initRecipes();
    }

    public void postinit() {}
}
