package mrtjp.relocation;

import cpw.mods.fml.common.Loader;
import mrtjp.core.block.BlockLib;
import mrtjp.core.world.WorldLib;
import mrtjp.relocation.api.ITileMover;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MovingTileRegistry implements ITileMover {

    public static MovingTileRegistry instance = new MovingTileRegistry();

    public Pattern rKeyVal = Pattern.compile("(\\S+.+\\S+)\\s*->\\s*(\\S+.+\\S+)");
    public Pattern rName = Pattern.compile("(\\S+.+\\S+)");
    public Pattern rNameMetaM = Pattern.compile("(\\S+.+\\S+)m(\\d+)");
    public Pattern rMod = Pattern.compile("mod:(\\S+.+\\S+)");

    public Map<Pair<Block, Integer>, ITileMover> blockMetaMap = new HashMap<>();
    public Map<String, ITileMover> modMap = new HashMap<>();

    public Map<String, String> moverDescMap = new HashMap<>();
    public Map<String, ITileMover> moverNameMap = new HashMap<>();

    public ITileMover defaultMover;
    public List<Pair<String, String>> preferredMovers = new ArrayList<>();
    public List<Pair<String, String>> mandatoryMovers = new ArrayList<>();

    public List<Pair<String, String>> parseKV(List<String> kv) {

        return kv.stream().map(s -> {

            Matcher m = rKeyVal.matcher(s);

            if (m.find()) {
                return new ImmutablePair<String, String>(m.group(1), m.group(2));
            }

            throw new RuntimeException("Illegal [k -> v] pair: " + s);
        }).collect(Collectors.toList());
    }

    public Pair<Block, Integer> parseBlockMeta(String b) {

        Matcher m = rNameMetaM.matcher(b);
        if (m.find()) {
            return new ImmutablePair<>(Block.getBlockFromName(fixName(m.group(1))), Integer.parseInt(m.group(2)));
        }

        m = rName.matcher(b);
        if (m.find()) {
            return new ImmutablePair<>(Block.getBlockFromName(fixName(m.group(1))), -1);
        }

        throw new RuntimeException("Illegal set part: " + b);
    }

    public String fixName(String name) {
        if (name.indexOf(':') == -1) {
            return "minecraft:" + name;
        }
        return name;
    }

    public List<String> parseAndSetMovers(List<String> kvs) {
        List<Pair<String, String>> moverMap = parseKV(kvs);
        for (Pair<String, String> kv : preferredMovers) {
            if (!moverMap.contains(kv.getLeft())) moverMap.add(kv);
        }
        moverMap.addAll(mandatoryMovers);
        for (Pair<String, String> p : moverMap) {
            setMover(p.getLeft(), p.getRight());
        }
        return moverMap.stream().map(p -> p.getLeft() + " -> " + p.getRight()).collect(Collectors.toList());
    }

    public void setMover(String that, String m) {
        if (!moverNameMap.containsKey(m)) return;
        ITileMover h = moverNameMap.get(m);
        Matcher r = rMod.matcher(that);

        if (that.equals("default")) {
            defaultMover = h;
        } else if (r.find() && Loader.isModLoaded(r.group(1))) {
            modMap.put(r.group(1), h);
        } else {
            blockMetaMap.put(parseBlockMeta(that), h);
        }
    }

    public void registerTileMover(String name, String desc, ITileMover m) {
        moverDescMap.put(name, desc);
        moverNameMap.put(name, m);
    }

    private ITileMover getHandler(Block b, int m) {
        return blockMetaMap.computeIfAbsent(
            new ImmutablePair<>(b, m),
            (k) -> blockMetaMap.computeIfAbsent(
                new ImmutablePair<>(b, -1),
                (k1) -> modMap.computeIfAbsent(BlockLib.getModId(b), (k2) -> defaultMover)
            )
        );
    }

    @Override
    public boolean canMove(World w, int x, int y, int z) {
        int meta = w.getBlockMetadata(x, y, z);
        Block b = w.getBlock(x, y, z);
        if (b != null) {
            return getHandler(b, meta).canMove(w, x, y, z);
        }
        return false;
    }

    @Override
    public void move(World w, int x, int y, int z, int side) {
        int meta = w.getBlockMetadata(x, y, z);
        Block b = w.getBlock(x, y, z);
        if (b != null) {
            getHandler(b, meta).move(w, x, y, z, side);
        }
    }

    @Override
    public void postMove(World w, int x, int y, int z) {
        int meta = w.getBlockMetadata(x, y, z);
        Block b = w.getBlock(x, y, z);
        if (b != null) {
            getHandler(b, meta).postMove(w, x, y, z);
        }
    }

    public boolean canRunOverBlock(World w, int x, int y, int z) {
        if (w.blockExists(x, y, z)) {
            return w.isAirBlock(x, y, z) || WorldLib.isBlockSoft(
                w,
                x,
                y,
                z,
                w.getBlock(x, y, z)
            );
        }
        else return false;
    }
}
