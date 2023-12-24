/*
 * Copyright (c) 2014.
 * Created by MrTJP.
 * All rights reserved.
 */
package mrtjp.mcframes

import codechicken.lib.data.{MCDataInput, MCDataOutput}
import codechicken.lib.render.uv.MultiIconTransformation
import codechicken.lib.vec.{Rotation, Vector3}
import mrtjp.core.block.{InstancedBlock, InstancedBlockTile, TTileOrient}
import mrtjp.core.render.TCubeMapRender
import mrtjp.core.world.WorldLib
import mrtjp.mcframes.api.{IFrame, MCFramesAPI}
import mrtjp.mcframes.handler.MCFramesMod
import mrtjp.relocation.api.{BlockPos, RelocationAPI}
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.IIcon
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.util.ForgeDirection


object RenderMotor extends TCubeMapRender {
  var bottom: IIcon = _
  var side: IIcon = _
  var sidew: IIcon = _
  var sidee: IIcon = _
  var top: IIcon = _

  override def getData(w: IBlockAccess, x: Int, y: Int, z: Int) = {
    val te = WorldLib.getTileEntity(w, x, y, z, classOf[TileMotor])
    var s = 0
    var r = 0
    if (te != null) {
      s = te.side
      r = te.rotation
    }
    (s, r, new MultiIconTransformation(bottom, top, side, side, sidew, sidee))
  }

  override def getInvData =
    (0, 0, new MultiIconTransformation(bottom, top, side, side, sidew, sidee))

  override def getIcon(s: Int, meta: Int) = s match {
    case 0 => bottom
    case 1 => top
    case 2 => side
    case 3 => side
    case 4 => sidew
    case 5 => sidee
  }

  override def registerIcons(reg: IIconRegister) {
    bottom = reg.registerIcon("mcframes:motor/bottom")
    top = reg.registerIcon("mcframes:motor/top")
    side = reg.registerIcon("mcframes:motor/side")
    sidew = reg.registerIcon("mcframes:motor/sidew")
    sidee = reg.registerIcon("mcframes:motor/sidee")
  }
}
