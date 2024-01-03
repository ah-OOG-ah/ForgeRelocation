package mrtjp.relocation.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import codechicken.lib.packet.PacketCustom;
import mrtjp.relocation.MovementManager2;

public class RelocationCPH extends RelocationPH implements PacketCustom.IClientPacketHandler {

    public static final RelocationCPH instance = new RelocationCPH();

    public void handlePacket(PacketCustom packet, Minecraft mc, INetHandlerPlayClient netHandler) {
        try {
            if (packet.getType() == 1) {
                handleChunkDesc(packet, mc.theWorld);
            } else {
                handleChunkData(packet, mc.theWorld);
            }
        } catch (RuntimeException e) {
            if (e.getMessage().startsWith("DC: ")) {
                netHandler
                        .handleDisconnect(new S40PacketDisconnect(new ChatComponentText(e.getMessage().substring(4))));
            }
        }
    }

    public void handleChunkData(PacketCustom packet, World world) {
        int i = packet.readUByte();
        while (i != 255) {
            MovementManager2.read(world, packet, i);
            i = packet.readUByte();
        }
    }

    public void handleChunkDesc(PacketCustom packet, World world) {
        MovementManager2.readDesc(world, packet);
    }
}
