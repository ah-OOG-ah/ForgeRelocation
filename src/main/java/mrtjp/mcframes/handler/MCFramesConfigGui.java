package mrtjp.mcframes.handler;

import mrtjp.core.data.SpecialConfigGui;
import net.minecraft.client.gui.GuiScreen;

public class MCFramesConfigGui extends SpecialConfigGui {

    public MCFramesConfigGui(GuiScreen parent) {
        super(parent, "MCFrames", MCFramesConfig.instance.config);
    }
}
