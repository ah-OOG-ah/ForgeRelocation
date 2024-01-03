package mrtjp.relocation.handler;

import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.ImmutablePair;

import mrtjp.relocation.MovementManager2;
import mrtjp.relocation.MovingTileRegistry;
import mrtjp.relocation.api.ITileMover;
import mrtjp.relocation.api.RelocationAPI;
import mrtjp.relocation.api.Relocator;

public class RelocationAPI_Impl extends RelocationAPI {

    public static final RelocationAPI_Impl instance = new RelocationAPI_Impl();

    public boolean isPreInit = true;

    @Override
    public void registerTileMover(String name, String desc, ITileMover handler) {
        assert isPreInit;
        MovingTileRegistry.instance.registerTileMover(name, desc, handler);
    }

    @Override
    public void registerPreferredMover(String key, String value) {
        assert isPreInit;
        MovingTileRegistry.instance.preferredMovers.add(new ImmutablePair<>(key, value));
    }

    @Override
    public void registerMandatoryMover(String key, String value) {
        assert isPreInit;
        MovingTileRegistry.instance.mandatoryMovers.add(new ImmutablePair<>(key, value));
    }

    @Override
    public Relocator getRelocator() {
        return Relocator_Impl.instance;
    }

    @Override
    public boolean isMoving(World world, int x, int y, int z) {
        return MovementManager2.isMoving(world, x, y, z);
    }
}
