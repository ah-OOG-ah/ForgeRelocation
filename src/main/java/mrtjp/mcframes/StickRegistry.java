package mrtjp.mcframes;


import codechicken.lib.vec.BlockCoord;
import com.google.common.collect.Sets;
import mrtjp.core.world.JWorldLib;
import mrtjp.core.world.WorldLib;
import mrtjp.mcframes.api.IFrame;
import mrtjp.mcframes.api.IFrameInteraction;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.minecraft.block.BlockPistonMoving.getTileEntity;

public class StickRegistry {

    public static final StickRegistry instance = new StickRegistry();

    Pattern rKeyVal = Pattern.compile("([\\w:]+)\\s*->\\s*(.+)");
    Pattern rName = Pattern.compile("(.+)");
    Pattern rNameMetaM = Pattern.compile("(.+)m(\\d+)");
    Pattern rMod = Pattern.compile("mod:(\\w+)");

    // Default value is an empty set
    Map<Pair<Block, Integer>, Set<Pair<Block, Integer>>> latchMap = new HashMap<>();
    public List<IFrameInteraction> interactionList = new ArrayList<>();

    public List<Pair<String, String>> parseKV(List<String> kv) {
        return kv.stream().map(s -> {
            Matcher m = rKeyVal.matcher(s);
            if (!m.find()) throw new RuntimeException("Illegal [k -> v] pair: " + s);
            return new ImmutablePair<>(m.group(1), m.group(2));
        }).collect(Collectors.toList());
    }

    public Pair<Block, Integer> parseBlockMeta(String b) {
        Matcher m = rNameMetaM.matcher(b);
        if (m.find())
            return new ImmutablePair<>(Block.getBlockFromName(fixName(m.group(1))), Integer.parseInt(m.group(2)));

        m = rName.matcher(b);
        if (m.find())
            return new ImmutablePair<>(Block.getBlockFromName(fixName(m.group(1))), -1);

        throw new RuntimeException("Illegal set part: " + b);
    }

    public String fixName(String name) {
        int i = name.indexOf(':');
        if (i == -1) {
            return "minecraft:" + name;
        }
        return name;
    }

    public List<String> parseAndAddLatchSets(List<String> kv) {
        parseKV(kv).forEach(b ->
            addLatchSet(parseBlockMeta(b.getLeft()), parseBlockMeta(b.getRight())));

        List<String> ret = new ArrayList<>();
        latchMap.forEach((block, pairs) -> {
            Block b = block.getLeft();
            int i = block.getRight();

            String e1 = Block.blockRegistry.getNameForObject(b) + ((i != -1) ? i : "");
            ret.addAll(pairs.stream().map(k -> {
                Block b2 = k.getLeft();
                int i2 = k.getRight();
                String e2 = Block.blockRegistry.getNameForObject(b2) + ((i2 != -1) ? i2 : "");
                return e1 + " -> " + e2;
            }).collect(Collectors.toList()));
        });

        return ret;
    }

    public void addLatchSet(Pair<Block, Integer> b1, Pair<Block, Integer> b2) {
        Set<Pair<Block, Integer>> tmp = latchMap.getOrDefault(b1, new HashSet<>());
        tmp.add(b2);
        latchMap.put(b1, tmp);
    }

    private IFrame getFrame(World w, BlockCoord pos) {
        Block b = WorldLib.getBlock(w, pos);
        if (b instanceof  IFrame) return (IFrame) b;
        IFrame te = WorldLib.getTileEntity(w, pos, IFrame.class);
        if (te != null) return te;
        return interactionList.stream().filter(v -> v.canInteract(w, pos.x, pos.y, pos.z)).findAny().orElse(null);
    }

    public boolean resolveStick(World w, BlockCoord pos, int side) {

        IFrame f1 = getFrame(w, pos);
        if (f1 != null && f1.stickOut(w, pos.x, pos.y, pos.z, side)) {
            BlockCoord p2 = pos.copy().offset(side);
            IFrame f2 = getFrame(w, p2);
            return f2 == null || f2.stickIn(w, p2.x, p2.y, p2.z, side ^ 1);
        }

        return latchSet(w, pos.x, pos.y, pos.z, side);
    }

    public boolean latchSet(World w, int x, int y, int z, int side) {
        BlockCoord pos = new BlockCoord(x, y, z).offset(side);
        Pair<Block, Integer> b1 = JWorldLib.getBlockMetaPair(w, x, y, z);
        Pair<Block, Integer> b2 = JWorldLib.getBlockMetaPair(w, pos.x, pos.y, pos.z);

        Set<Pair<Block, Integer>> set = latchMap.getOrDefault(b1, latchMap.get(new ImmutablePair<>(b1.getLeft(), -1)));
        return set.contains(b2) || set.contains(new ImmutablePair<>(b2.getLeft(), -1));
    }
}
