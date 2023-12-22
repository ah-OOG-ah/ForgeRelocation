package mrtjp.relocation.handler;

import mrtjp.core.data.TModGuiFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class GuiConfigFactory implements TModGuiFactory {

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return RelocationConfigGui.class;
    }
}
