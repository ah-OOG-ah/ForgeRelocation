/*
 * Copyright (c) 2015.
 * Created by MrTJP.
 * All rights reserved.
 */
package mrtjp.mcframes.handler

import java.lang.{Character => JC}

import cpw.mods.fml.client.registry.RenderingRegistry
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.{Side, SideOnly}
import mrtjp.core.block.TileRenderRegistry
import mrtjp.mcframes._
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.ShapedOreRecipe

class MCFramesProxy_client extends MCFramesProxy_server {
  @SideOnly(Side.CLIENT)
  override def preinit() {
    super.preinit()

    TileRenderRegistry.setRenderer(MCFramesMod.blockMotor, 0, RenderMotor)

    RenderFrame.renderID = RenderingRegistry.getNextAvailableRenderId
    RenderingRegistry.registerBlockHandler(RenderFrame)
  }
}


