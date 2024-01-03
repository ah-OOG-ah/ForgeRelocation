package mrtjp.mcframes.handler;

import net.minecraft.client.gui.GuiScreen;

import mrtjp.core.data.TModGuiFactory;

public class GuiConfigFactory implements TModGuiFactory {

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return MCFramesConfigGui.class;
    }
}
