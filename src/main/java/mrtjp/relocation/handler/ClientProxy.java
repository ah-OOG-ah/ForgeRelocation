package mrtjp.relocation.handler;

import net.minecraftforge.common.MinecraftForge;

import codechicken.lib.packet.PacketCustom;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mrtjp.relocation.events.ClientHandler;

public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        super.init();

        PacketCustom.assignHandler(RelocationCPH.instance.channel, RelocationCPH.instance);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void postinit() {
        super.postinit();

        FMLCommonHandler.instance().bus().register(ClientHandler.instance);
        MinecraftForge.EVENT_BUS.register(ClientHandler.instance);
    }
}
