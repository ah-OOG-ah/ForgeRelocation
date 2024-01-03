package mrtjp.relocation.handler;

import net.minecraft.client.gui.GuiScreen;

import mrtjp.core.data.SpecialConfigGui;

public class RelocationConfigGui extends SpecialConfigGui {

    public RelocationConfigGui(GuiScreen parent) {
        super(parent, "ForgeRelocation", RelocationConfig.instance.config);
    }
}
