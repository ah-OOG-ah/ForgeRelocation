package mrtjp.mcframes;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import codechicken.lib.vec.Vector3;
import mrtjp.core.resource.SoundLib;
import mrtjp.mcframes.api.IFramePlacement;

public class ItemBlockFrame extends ItemBlock {

    public static Set<IFramePlacement> placements = new HashSet<>();

    public ItemBlockFrame(Block b) {
        super(b);
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

    @Override
    public boolean onItemUse(ItemStack item, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ) {
        if (ItemBlockFrame.placements.stream()
                .anyMatch(p -> p.onItemUse(item, player, world, x, y, z, side, new Vector3(hitX, hitY, hitZ)))) {
            SoundLib.playBlockPlacement(world, x, y, z, field_150939_a);
            return true;
        } else return super.onItemUse(item, player, world, x, y, z, side, hitX, hitY, hitZ);
    }

    @Override
    public boolean func_150936_a(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack stack) {
        return true;
    }
}
