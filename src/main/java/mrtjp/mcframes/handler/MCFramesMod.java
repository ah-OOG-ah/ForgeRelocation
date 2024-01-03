package mrtjp.mcframes.handler;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import mrtjp.mcframes.BlockFrame;
import mrtjp.mcframes.BlockMotor;
import mrtjp.mcframes.api.MCFramesAPI;

@Mod(
        modid = MCFramesMod.modID,
        useMetadata = true,
        guiFactory = "mrtjp.mcframes.handler.GuiConfigFactory",
        acceptedMinecraftVersions = "[1.7.10]",
        dependencies = "required-after:MrTJPCoreMod",
        name = MCFramesMod.modName,
        version = MCFramesMod.version)
public class MCFramesMod {

    static {
        MCFramesAPI.instance = MCFramesAPI_Impl.instance;
    }

    public static final MCFramesMod instance = new MCFramesMod();

    public static final String modID = "MCFrames";
    public static final String modName = "MCFrames";
    public static final String version = "GRADLETOKEN_VERSION";

    public BlockFrame blockFrame;
    public BlockMotor blockMotor;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MCFramesProxy.instance.preinit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MCFramesConfig.instance.loadConfig();
        MCFramesProxy.instance.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        MCFramesProxy.instance.postinit();
    }
}
