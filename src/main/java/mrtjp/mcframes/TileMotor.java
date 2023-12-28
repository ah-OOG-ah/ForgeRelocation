package mrtjp.mcframes;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import mrtjp.core.block.InstancedBlockTile;
import mrtjp.core.block.TTileOrient;
import mrtjp.mcframes.api.IFrame;
import mrtjp.mcframes.api.MCFramesAPI;
import mrtjp.mcframes.api.StickResolver;
import mrtjp.mcframes.handler.MCFramesMod;
import mrtjp.relocation.api.BlockPos;
import mrtjp.relocation.api.RelocationAPI;
import mrtjp.relocation.api.Relocator;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.Set;

public class TileMotor extends TTileOrient implements IFrame {
    byte orientation = 0;

    @Override
    public void save(NBTTagCompound tag) {
        super.save(tag);
        tag.setByte("orient", orientation);
    }

    @Override
    public void load(NBTTagCompound tag) {
        super.load(tag);
        orientation = tag.getByte("orient");
    }

    @Override
    public void readDesc(MCDataInput in) {
        super.readDesc(in);
        orientation = in.readByte();
    }

    @Override
    public void writeDesc(MCDataOutput out) {
        super.writeDesc(out);
        out.writeByte(orientation);
    }

    @Override
    public void read(MCDataInput in, int key) {
        if (key == 2) {
            orientation = in.readByte();
            markRender();
        } else {
            super.read(in, key);
        }
    }

    public void sendOrientUpdate() {
        if (!world().isRemote)
            streamToSend(writeStream(2).writeByte(orientation)).sendToChunk();
    }

    @Override
    public void onBlockPlaced(
        int side,
        int meta,
        EntityPlayer player,
        ItemStack stack,
        Vector3 hit
    ) {
        super.onBlockPlaced(side, meta, player, stack, hit);
        setSide(side ^ 1);
        setRotation(Rotation.getSidedRotation(player, side ^ 1));
    }

    @Override
    public boolean onBlockActivated(EntityPlayer player, int s) {
        if (super.onBlockActivated(player, s)) return true;

        if (player.isSneaking()) setRotation((rotation() + 1) % 4);
        else setSide((side() + 1) % 6);

        return true;
    }

    @Override
    public void onOrientChanged(int oldOrient) {
        // sus
        //super.onOrientChanged(oldOrient);
        sendOrientUpdate();
    }

    @Override
    public Block getBlock() {
        return MCFramesMod.instance.blockMotor;
    }

    public int getMoveDir() {
        return absoluteDir((rotation() + 2) % 4);
    }

    @Override
    public boolean stickOut(World w, int x, int y, int z, int side) {
        return side == (this.side() ^ 1);
    }

    @Override
    public boolean stickIn(World w, int x, int y, int z, int side) {
        return side != (this.side() ^ 1);
    }

    @Override
    public void update() {
        if (!world().isRemote && world().getBlockPowerInput(xCoord, yCoord, zCoord) > 0) {
            BlockCoord pos = position().offset(side() ^ 1);
            if (world().isAirBlock(pos.x, pos.y, pos.z)) return;

            if (
                !RelocationAPI.instance.isMoving(world(), pos.x, pos.y, pos.z) &&
                    !RelocationAPI.instance.isMoving(world(), xCoord, yCoord, zCoord)
            ) {
                Set<BlockPos> blocks = MCFramesAPI.instance.getStickResolver()
                    .getStructure(world(), pos.x, pos.y, pos.z, new BlockPos(xCoord, yCoord, zCoord));

                Relocator r = RelocationAPI.instance.getRelocator();
                r.push();
                r.setWorld(world());
                r.setDirection(getMoveDir());
                r.setSpeed(1 / 16d);
                r.addBlocks(blocks);
                r.execute();
                r.pop();
            }
        }
    }
}
