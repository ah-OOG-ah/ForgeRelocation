package mrtjp.mcframes.handler;

import net.minecraft.client.gui.GuiScreen;

import mrtjp.core.data.SpecialConfigGui;

public class MCFramesConfigGui extends SpecialConfigGui {

    public MCFramesConfigGui(GuiScreen parent) {
        super(parent, "MCFrames", MCFramesConfig.instance.config);
    }
}
