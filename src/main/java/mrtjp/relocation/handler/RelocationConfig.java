package mrtjp.relocation.handler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import mrtjp.core.data.ModConfig;
import mrtjp.relocation.MovingTileRegistry;

public class RelocationConfig extends ModConfig {

    public static RelocationConfig instance = new RelocationConfig();

    public int moveLimit = 2048;
    public String[] moverMap = new String[] { "default -> saveload" };

    public RelocationConfig() {
        super("ForgeRelocation");
    }

    @Override
    public void initValues() {

        BaseCategory general = new BaseCategory(this, "General", "Basic settings");
        moveLimit = general.put("moveLimit", moveLimit, "Maximum amount of blocks that can be moved at once.");

        BaseCategory movers = new BaseCategory(this, "Tile Movers", buildMoverDesc());
        moverMap = movers.put("mover registry", moverMap, "");
        moverMap = movers.put(
                "mover registry",
                MovingTileRegistry.instance.parseAndSetMovers(Arrays.asList(moverMap)).toArray(new String[0]),
                true);
    }

    public String buildMoverDesc() {

        String s = "Used to configure which registered Tile Mover is used for a block. Key-Value pairs are defined using \n"
                + "the syntax key -> value. \n"
                + "Most blocks are configurable, but some mods may have opted to lock which handlers can be used for its \n"
                + "blocks.\n"
                + "Possible keys: \n"
                + "    'default' - to assign default handler. \n"
                + "    'mod:<modID>' - to assign every block from a mod. \n"
                + "    '<modID>:<blockname>' - to assign block from a mod for every meta. \n"
                + "    '<modID>:<blockname>m<meta>' - to assign block from mod for specific meta. \n"

                + "\nAvailable tile movers:\n";
        Map<String, String> tmp = MovingTileRegistry.instance.moverDescMap;
        for (Map.Entry<String, String> e : tmp.entrySet()) {
            s += "    '" + e.getKey() + "' - " + e.getValue() + "\n";
        }
        if (!MovingTileRegistry.instance.mandatoryMovers.isEmpty()) {
            s += "\nMovers locked via API:\n";
            List<Pair<String, String>> tmp2 = MovingTileRegistry.instance.mandatoryMovers;
            for (Pair<String, String> p : tmp2) {
                s += "    " + p.getKey() + " -> " + p.getValue() + "\n";
            }
        }

        return s;
    }
}
