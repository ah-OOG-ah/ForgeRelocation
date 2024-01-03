package mrtjp.relocation;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.world.ChunkCoordIntPair;

public class WorldStructs {

    public Set<BlockStruct> structs = new HashSet<>();

    public boolean isEmpty() {
        return structs.isEmpty();
    }

    public boolean nonEmpty() {
        return !isEmpty();
    }

    public boolean contains(int x, int y, int z) {
        return structs.stream().anyMatch(bs -> bs.contains(x, y, z));
    }

    public void addStruct(BlockStruct b) {
        structs.add(b);
    }

    public void pushAll() {
        for (BlockStruct bs : structs) {
            bs.push();
        }
    }

    public Set<BlockStruct> removeFinished() {
        Set<BlockStruct> finished = structs.stream().filter(BlockStruct::isFinished).collect(Collectors.toSet());
        structs = structs.stream().filter(bs -> !bs.isFinished()).collect(Collectors.toSet());
        return finished;
    }

    public void removeStruct(BlockStruct s) {
        structs = structs.stream().filter(bs -> bs != s).collect(Collectors.toSet());
    }

    public void clear() {
        structs.clear();
    }

    public Set<ChunkCoordIntPair> getChunks() {
        return structs.stream().map(BlockStruct::getChunks).reduce((s1, s2) -> {
            s1.addAll(s2);
            return s1;
        }).orElseGet(HashSet::new);
    }
}
