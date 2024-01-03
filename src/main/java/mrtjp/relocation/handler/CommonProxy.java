package mrtjp.relocation.handler;

import net.minecraftforge.common.MinecraftForge;

import codechicken.lib.packet.PacketCustom;
import cpw.mods.fml.common.FMLCommonHandler;
import mrtjp.relocation.BlockMovingRow;
import mrtjp.relocation.CoordPushTileMover;
import mrtjp.relocation.events.ServerHandler;
import mrtjp.relocation.SaveLoadTileMover;
import mrtjp.relocation.StaticTileMover;
import mrtjp.relocation.api.RelocationAPI;

public class CommonProxy {

    private static final RelocationAPI API = RelocationAPI.instance;

    public void preinit() {
        RelocationMod.instance.blockMovingRow = new BlockMovingRow();

        API.registerTileMover(
                "saveload",
                "Saves the tile and then reloads it in the next position. Reliable but CPU intensive.",
                new SaveLoadTileMover());

        API.registerTileMover(
                "coordpush",
                "Physically changes the location of tiles. Works if tiles do not cache their position.",
                new CoordPushTileMover());

        API.registerTileMover(
                "static",
                "Setting this disables movement for the specified block.",
                new StaticTileMover());

        API.registerPreferredMover("default", "saveload");
        API.registerPreferredMover("mod:minecraft", "coordpush");
        API.registerPreferredMover("mod:Relocation", "coordpush");
        API.registerPreferredMover("mod:ComputerCraft", "coordpush");
        API.registerPreferredMover("mod:EnderStorage", "coordpush");
        API.registerPreferredMover("mod:ChickenChunks", "coordpush");
        API.registerPreferredMover("mod:Translocator", "coordpush");
        API.registerPreferredMover("mod:ProjRed|Compatibility", "coordpush");
        API.registerPreferredMover("mod:ProjRed|Core", "coordpush");
        API.registerPreferredMover("mod:ProjRed|Expansion", "coordpush");
        API.registerPreferredMover("mod:ProjRed|Exploration", "coordpush");
        API.registerPreferredMover("mod:ProjRed|Fabrication", "coordpush");
        API.registerPreferredMover("mod:ProjRed|Illumination", "coordpush");
        API.registerPreferredMover("mod:ProjRed|Integration", "coordpush");
        API.registerPreferredMover("mod:ProjRed|Transmission", "coordpush");
        API.registerPreferredMover("mod:ProjRed|Transportation", "coordpush");
    }

    public void init() {}

    public void postinit() {
        PacketCustom.assignHandler(RelocationMod.modID, RelocationSPH.instance);

        FMLCommonHandler.instance().bus().register(ServerHandler.instance);
        MinecraftForge.EVENT_BUS.register(ServerHandler.instance);
    }
}
