/*
 * Copyright (c) 2015.
 * Created by MrTJP.
 * All rights reserved.
 */
package mrtjp.mcframes.handler

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.{
  FMLInitializationEvent,
  FMLPostInitializationEvent,
  FMLPreInitializationEvent
}
import mrtjp.core.data.{ModConfig, SpecialConfigGui, TModGuiFactory}
import mrtjp.mcframes.api.MCFramesAPI
import mrtjp.mcframes.{BlockFrame, BlockMotor, StickRegistry}
import net.minecraft.client.gui.GuiScreen

class MCFramesConfigGui(parent: GuiScreen)
    extends SpecialConfigGui(parent, "MCFrames", MCFramesConfig.config)
class GuiConfigFactory extends TModGuiFactory {
  override def mainConfigGuiClass() = classOf[MCFramesConfigGui]
}

