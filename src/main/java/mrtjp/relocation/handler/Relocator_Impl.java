package mrtjp.relocation.handler;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;

import net.minecraft.world.World;

import codechicken.lib.vec.BlockCoord;
import mrtjp.relocation.MovementManager2;
import mrtjp.relocation.api.BlockPos;
import mrtjp.relocation.api.IMovementCallback;
import mrtjp.relocation.api.Relocator;

public class Relocator_Impl extends Relocator {

    public static final Relocator_Impl instance = new Relocator_Impl();

    public Queue<RelocationRun> mainStack = new ArrayDeque<>();
    public Queue<RelocationRun> tempStack = new ArrayDeque<>();

    private void assertState() {
        if (mainStack.isEmpty()) throw new IllegalStateException("Relocator stack is empty.");
    }

    @Override
    public void push() {
        RelocationRun r = (tempStack.isEmpty()) ? new RelocationRun() : tempStack.remove();
        mainStack.add(r);
    }

    @Override
    public void pop() {
        assertState();
        RelocationRun r = mainStack.remove();
        r.clear();
        tempStack.add(r);
    }

    @Override
    public void setWorld(World world) {
        assertState();
        RelocationRun top = mainStack.peek();
        if (top.world != null) throw new IllegalStateException("World already set.");
        top.world = world;
    }

    @Override
    public void setDirection(int dir) {
        assertState();
        RelocationRun top = mainStack.peek();
        if (top.dir != -1) throw new IllegalStateException("Direction already set.");
        top.dir = dir;
    }

    @Override
    public void setSpeed(double speed) {
        assertState();
        RelocationRun top = mainStack.peek();
        if (top.speed > 0) throw new IllegalStateException("Speed already set.");
        top.speed = speed;
    }

    @Override
    public void setCallback(IMovementCallback callback) {
        assertState();
        RelocationRun top = mainStack.peek();
        if (top.callback != null) throw new IllegalStateException("Callback already set.");
        top.callback = callback;
    }

    @Override
    public void addBlock(int x, int y, int z) {
        assertState();
        mainStack.peek().blocks.add(new BlockCoord(x, y, z));
    }

    @Override
    public void addBlock(BlockPos bc) {
        addBlock(bc.x, bc.y, bc.z);
    }

    @Override
    public void addBlocks(Set<BlockPos> blocks) {
        for (BlockPos b : blocks) addBlock(b);
    }

    @Override
    public boolean execute() {
        assertState();
        RelocationRun top = mainStack.peek();
        if (top.world == null) throw new IllegalStateException("World must be set before move.");
        if (top.world.isRemote) throw new IllegalStateException("Movements cannot be executed client-side.");
        if (top.dir == -1) throw new IllegalStateException("Direction must be set before move.");
        if (top.speed <= 0) throw new IllegalStateException("Speed must be greater than 0.");
        if (top.speed >= 1) throw new IllegalStateException("Speed must be less than 1.");
        if (top.blocks.isEmpty()) throw new IllegalStateException("No blocks queued for move.");
        return MovementManager2.tryStartMove(top.world, top.blocks, top.dir, top.speed, top.callback);
    }
}
