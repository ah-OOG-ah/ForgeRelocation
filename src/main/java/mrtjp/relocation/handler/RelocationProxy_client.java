package mrtjp.relocation.handler;

import codechicken.lib.packet.PacketCustom;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mrtjp.relocation.RelocationClientEventHandler;
import net.minecraftforge.common.MinecraftForge;

public class RelocationProxy_client extends RelocationProxy_server {

    @SideOnly(Side.CLIENT)
    @Override
    public void postinit() {
        super.postinit();
        PacketCustom.assignHandler(RelocationCPH.instance.channel, RelocationCPH.instance);

        FMLCommonHandler.instance().bus().register(RelocationClientEventHandler.instance);
        MinecraftForge.EVENT_BUS.register(RelocationClientEventHandler.instance);
    }
}
