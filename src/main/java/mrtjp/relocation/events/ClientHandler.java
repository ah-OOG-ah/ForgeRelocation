package mrtjp.relocation.events;

import net.minecraftforge.client.event.RenderWorldLastEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import mrtjp.relocation.MovementManager2;
import mrtjp.relocation.MovingRenderer;

public class ClientHandler {

    public static final ClientHandler instance = new ClientHandler();

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent e) {
        if (e.phase == TickEvent.Phase.START) MovingRenderer.instance.onPreRenderTick(e.renderTickTime);
        else MovingRenderer.instance.onPostRenderTick();
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent e) {
        MovingRenderer.instance.onRenderWorldEvent();
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) MovementManager2.onTick(true);
    }
}
