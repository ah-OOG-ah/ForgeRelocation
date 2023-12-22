package mrtjp.relocation.handler;

import codechicken.lib.data.MCDataOutputWrapper;
import codechicken.lib.packet.PacketCustom;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import mrtjp.relocation.MovementManager2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RelocationSPH extends RelocationPH implements PacketCustom.IServerPacketHandler {

    public static final RelocationSPH instance = new RelocationSPH();

    public static class MCByteStream extends MCDataOutputWrapper {

        private final ByteArrayOutputStream bout;

        public MCByteStream(ByteArrayOutputStream bout) {
            super(new DataOutputStream(bout));
            this.bout = bout;
        }

        public byte[] getBytes() {
            return bout.toByteArray();
        }
    }

    @Override
    public void handlePacket(
        PacketCustom packetCustom,
        EntityPlayerMP entityPlayerMP,
        INetHandlerPlayServer iNetHandlerPlayServer
    ) {}

    private Map<World, Map<Set<ChunkCoordIntPair>, MCByteStream>> updateMap = new HashMap<>();
    private Multimap<Integer, ChunkCoordIntPair> chunkWatchers = MultimapBuilder.hashKeys().arrayListValues().build();
    private Map<Integer, LinkedList<ChunkCoordIntPair>> newWatchers = new HashMap<>();

    public void onTickEnd() {
        List<EntityPlayerMP> players = getServerPlayers();
        sendData(players);
        sendDesc(players);
    }

    public void onWorldUnload(World world) {
        if (!world.isRemote) {
            updateMap.remove(world);
            if (!chunkWatchers.isEmpty()) {
                List<EntityPlayerMP> players = getServerPlayers();
                for (EntityPlayerMP p : players) {
                    if (p.worldObj.provider.dimensionId == world.provider.dimensionId)
                        chunkWatchers.removeAll(p.getEntityId());
                }
            }
        }
    }

    public void onChunkWatch(EntityPlayer p, ChunkCoordIntPair c) {
        newWatchers.computeIfAbsent(p.getEntityId(), (k) -> new LinkedList<>(Collections.singletonList(c)));
    }

    public void onChunkUnWatch(EntityPlayer p, ChunkCoordIntPair c) {
        LinkedList<ChunkCoordIntPair> nw = newWatchers.get(p.getEntityId());
        if (nw != null && !nw.isEmpty()) {
            nw.remove(c);
        }
        chunkWatchers.remove(p.getEntityId(), c);
    }

    private List<EntityPlayerMP> getServerPlayers() {
        return MinecraftServer.getServer().getConfigurationManager().playerEntityList;
    }

    private void sendData(List<EntityPlayerMP> players) {
        for (EntityPlayerMP p : players) {
            if (chunkWatchers.containsKey(p.getEntityId())) {
                Map<Set<ChunkCoordIntPair>, MCByteStream> pairs = updateMap.get(p.worldObj);
                if (pairs != null && !pairs.isEmpty()) {
                    Collection<ChunkCoordIntPair> chunks = chunkWatchers.get(p.getEntityId());
                    PacketCustom packet = new PacketCustom(channel, 2).compress();
                    boolean send = false;
                    Set<Map.Entry<Set<ChunkCoordIntPair>, MCByteStream>> toIter = pairs.entrySet().stream().filter(pair -> pair.getKey().stream().anyMatch(chunks::contains)).collect(Collectors.toSet());

                    for (Map.Entry<Set<ChunkCoordIntPair>, MCByteStream> pair : toIter) {
                        MCByteStream stream = pair.getValue();
                        send = true;
                        packet.writeByteArray(stream.getBytes());
                        packet.writeByte(255); // terminator
                    }

                    if (send) packet.sendToPlayer(p);
                }
            }
        }
        updateMap.entrySet().forEach(worldMapEntry -> worldMapEntry.getValue().clear());
    }

    private void sendDesc(List<EntityPlayerMP> players) {
        players.stream().filter(epMP -> newWatchers.containsKey(epMP.getEntityId())).forEach(ePMP -> {
            LinkedList<ChunkCoordIntPair> watched = newWatchers.get(ePMP.getEntityId());
            PacketCustom pkt = getDescPacket(ePMP.worldObj, new HashSet<>(watched));
            if (pkt != null) pkt.sendToPlayer(ePMP);
            for (ChunkCoordIntPair c : watched) {
                chunkWatchers.put(ePMP.getEntityId(), c);
            }
        });
        newWatchers.clear();
    }

    private PacketCustom getDescPacket(
        World world,
        Set<ChunkCoordIntPair> chunks
    ) {
        PacketCustom packet = new PacketCustom(channel, 1);
        if (MovementManager2.writeDesc(world, chunks, packet)) return packet; else return null;
    }

    public void forceSendData() {
        sendData(getServerPlayers());
    }

    public void forceSendDesc() {
        sendDesc(getServerPlayers());
    }

    public MCByteStream getStream(World world, Set<ChunkCoordIntPair> chunks, int key) {
        return updateMap
            .computeIfAbsent(
                world, w -> {
                    if (w.isRemote) {
                        throw new IllegalArgumentException(
                            "Cannot use RelocationSPH on a client world"
                        );
                    }
                    return new HashMap<>();
                }
        ).computeIfAbsent(
            chunks, cs -> {
                MCByteStream s = new MCByteStream(new ByteArrayOutputStream());
                s.writeByte(key);
                return s;
            }
        );
    }

}
