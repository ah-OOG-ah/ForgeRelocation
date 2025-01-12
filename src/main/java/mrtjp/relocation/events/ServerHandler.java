package mrtjp.relocation.events;

import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import mrtjp.relocation.MovementManager2;
import mrtjp.relocation.handler.RelocationSPH;

public class ServerHandler {

    public static final ServerHandler instance = new ServerHandler();

    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload event) {
        RelocationSPH.instance.onWorldUnload(event.world);
        MovementManager2.onWorldUnload(event.world);
    }

    @SubscribeEvent
    public void chunkWatch(ChunkWatchEvent.Watch event) {
        RelocationSPH.instance.onChunkWatch(event.player, event.chunk);
    }

    @SubscribeEvent
    public void chunkUnwatch(ChunkWatchEvent.UnWatch event) {
        RelocationSPH.instance.onChunkUnWatch(event.player, event.chunk);
    }

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            RelocationSPH.instance.onTickEnd();
            MovementManager2.onTick(false);
        }
    }
}
