package mrtjp.mcframes.handler;

import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import codechicken.lib.vec.Vector3;
import mrtjp.core.vec.ModelRayTracer;
import mrtjp.mcframes.BlockFrame;
import mrtjp.mcframes.ItemBlockFrame;
import mrtjp.mcframes.RenderFrame;
import mrtjp.mcframes.StickRegistry;
import mrtjp.mcframes.api.IFrameInteraction;
import mrtjp.mcframes.api.IFramePlacement;
import mrtjp.mcframes.api.MCFramesAPI;
import mrtjp.mcframes.api.StickResolver;

public class MCFramesAPI_Impl extends MCFramesAPI {

    public static final MCFramesAPI_Impl instance = new MCFramesAPI_Impl();

    @Override
    public void registerFramePlacement(IFramePlacement placement) {
        ItemBlockFrame.placements.add(placement);
    }

    @Override
    public BlockFrame getFrameBlock() {
        return MCFramesMod.instance.blockFrame;
    }

    @Override
    public void registerFrameInteraction(IFrameInteraction interaction) {
        StickRegistry.instance.interactionList.add(interaction);
    }

    @Override
    public StickResolver getStickResolver() {
        return StickResolver_Impl.instance;
    }

    @Override
    public void renderFrame(double x, double y, double z, int mask) {
        RenderFrame.instance.render(new Vector3(x, y, z), mask);
    }

    @Override
    public MovingObjectPosition raytraceFrame(double x, double y, double z, int mask, Vec3 start, Vec3 end) {
        return ModelRayTracer.raytraceModel(x, y, z, start, end, RenderFrame.instance.getOrGenerateModel(mask));
    }
}
