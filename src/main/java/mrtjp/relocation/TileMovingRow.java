package mrtjp.relocation;

import static mrtjp.relocation.handler.RelocationMod.instance;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import mrtjp.core.block.InstancedBlockTile;

public class TileMovingRow extends InstancedBlockTile {

    public static void setBlockForRow(World w, BlockRow r) {
        w.setBlock(r.pos.x, r.pos.y, r.pos.z, instance.blockMovingRow, 0, 3);
    }

    public static Cuboid6 getBoxFor(World w, BlockRow r, double progress) {

        BlockCoord p = r.pos.copy().offset(r.moveDir ^ 1);
        Block bl = w.getBlock(p.x, p.y, p.z);

        if (bl == instance.blockMovingRow) return Cuboid6.full.copy();

        AxisAlignedBB aabb = bl.getCollisionBoundingBoxFromPool(w, p.x, p.y, p.z);
        if (aabb != null) {
            return new Cuboid6(aabb).sub(new Vector3(r.pos)).add(new Vector3(BlockCoord.sideOffsets[r.moveDir]));
        } else {
            return Cuboid6.full.copy();
        }
    }

    public double prevProg = 0;

    @Override
    public void update() {
        if (!MovementManager2.isMoving(world(), xCoord, yCoord, zCoord)) {
            world().setBlockToAir(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public Block getBlock() {
        return instance.blockMovingRow;
    }

    @Override
    public Cuboid6 getBlockBounds() {
        BlockStruct s = MovementManager2.getEnclosedStructure(world(), xCoord, yCoord, zCoord);
        if (s != null) {
            BlockRow r = s.rows.stream().filter(u -> u.contains(xCoord, yCoord, zCoord)).findFirst().get();
            return TileMovingRow.getBoxFor(world(), r, s.progress);
        } else return Cuboid6.full;
    }

    @Override
    public Cuboid6 getCollisionBounds() {
        return getBlockBounds();
    }

    public void pushEntities(BlockRow r, double progress) {

        AxisAlignedBB box = Cuboid6.full.copy().add(new Vector3(r.preMoveBlocks.get(0)))
                .add(new Vector3(BlockCoord.sideOffsets[r.moveDir]).multiply(progress)).toAABB();

        double dp = ((progress >= 1.0) ? progress + 0.1 : progress) - prevProg;
        Vector3 d = new Vector3(BlockCoord.sideOffsets[r.moveDir]).multiply(dp);
        List<Entity> entities = world().getEntitiesWithinAABBExcludingEntity(null, box);
        if (entities != null) {
            for (Entity e : entities) {
                e.moveEntity(d.x, Math.max(d.y, 0), d.z);
            }
        }

        prevProg = progress;
    }
}
