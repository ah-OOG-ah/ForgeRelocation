package mrtjp.mcframes.handler;

import java.util.Arrays;
import java.util.stream.Collectors;

import mrtjp.core.data.ModConfig;
import mrtjp.mcframes.StickRegistry;

public class MCFramesConfig extends ModConfig {

    public static final MCFramesConfig instance = new MCFramesConfig();

    public MCFramesConfig() {
        super("MCFrames");
    }

    public static String[] setMap = new String[] { "minecraft:bed -> minecraft:bed",
            "minecraft:wooden_door -> minecraft:wooden_door", "minecraft:iron_door -> minecraft:iron_door" };

    @Override
    public void initValues() {
        BaseCategory sets = new BaseCategory(this, "Latched Sets", buildLatchSetsDesc);
        setMap = sets.put("latch registry", setMap, "");
        setMap = sets.put(
                "latch registry",
                StickRegistry.instance.parseAndAddLatchSets(Arrays.stream(setMap).collect(Collectors.toList()))
                        .toArray(new String[0]),
                true);
    }

    public static String buildLatchSetsDesc = "Used to define which pairs of blocks will be stuck together. \n"
            + "Latched sets will always move in pairs, even if only one of them are actually connected to a block. \n"
            + "'block1 -> block2' means that if block1 is moved, any block2 connected to it will also move. \n"
            + "However, moving block2 does not move block1. To do that, you must also register block2 -> block1. \n"
            + "Sets are defined using the syntax of key -> value. \n"
            + "Possible keys and values:\n"
            + "    '<modID>:<blockname>' - to assign block from a mod for every meta. \n"
            + "    '<modID>:<blockname>m<meta>' - to assign block from mod for specific meta. \n";
}
