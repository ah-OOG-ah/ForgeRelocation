package mrtjp.relocation.handler;

import mrtjp.core.data.SpecialConfigGui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.Configuration;

public class RelocationConfigGui extends SpecialConfigGui {

    public RelocationConfigGui(GuiScreen parent) {
        super(parent, "ForgeRelocation", RelocationConfig.instance.config);
    }
}
