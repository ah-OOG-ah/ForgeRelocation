package mrtjp.relocation.handler;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import mrtjp.relocation.BlockMovingRow;
import mrtjp.relocation.api.RelocationAPI;
import mrtjp.relocation.core.FRelocationCore;

@Mod(
        modid = RelocationMod.modID,
        useMetadata = true,
        guiFactory = "mrtjp.relocation.handler.GuiConfigFactory",
        acceptedMinecraftVersions = "[1.7.10]",
        dependencies = "required-after:MrTJPCoreMod",
        name = RelocationMod.modName,
        version = RelocationMod.version)
public final class RelocationMod {

    static {
        RelocationAPI.instance = RelocationAPI_Impl.instance;
    }

    public static final RelocationMod instance = new RelocationMod();
    @SidedProxy(
            clientSide = "mrtjp.relocation.handler.ClientProxy",
            serverSide = "mrtjp.relocation.handler.CommonProxy")
    public static CommonProxy proxy;

    public static final String modID = "ForgeRelocation";
    public static final String modName = "ForgeRelocation";
    public static final String version = "GRADLETOKEN_VERSION";
    public static final Logger log = FRelocationCore.LOGGER;

    public BlockMovingRow blockMovingRow;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preinit();
    }

    @EventHandler
    public static void init(FMLInitializationEvent event) {
        RelocationAPI_Impl.instance.isPreInit = false;
        RelocationConfig.instance.loadConfig();
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postinit();
    }
}
