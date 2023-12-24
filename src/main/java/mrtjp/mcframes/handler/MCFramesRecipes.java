package mrtjp.mcframes.handler;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class MCFramesRecipes {
    public static void initRecipes() {
        // Frame
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                new ItemStack(MCFramesMod.instance.blockFrame, 8),
                "sls",
                "lsl",
                "sls",
                's',
                Items.stick,
                'l',
                "logWood"
            )
        );
    }
}
