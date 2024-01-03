package mrtjp.mcframes;

import static codechicken.lib.render.CCModel.combine;
import static codechicken.lib.render.CCModel.quadModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import codechicken.lib.lighting.LightModel;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.Vertex5;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;

public class FrameModelGen {

    public static final double w = 2.0 / 16.0;
    public static final double l = 16.0 / 16.0;
    public static final double i = 1.0 / 16.0;
    public static final double u = 0.5;
    public static final double v = 0.5;

    public static CCModel generate(CCModel box, int mask) {
        CCModel m = generateSinglePeg();
        m = generateQuartRotated(m);
        m = generateEightRotated(m);
        m = generateBackface(m);

        List<CCModel> b = new ArrayList<>();
        b.add(box);
        for (int s = 0; s < 6; ++s) {
            if ((mask & 1 << s) == 0) b.add(generateSided(m.copy(), s));
        }

        return finishModel(combine(b));
    }

    public static CCModel generateSinglePeg() {
        double dw = w / 2.0;
        double dl = l / 2.0;

        CCModel m = quadModel(4);
        m.verts[0] = new Vertex5(dw, i, -dl, u + dw, v - dl);
        m.verts[1] = new Vertex5(dw, i, dl, u + dw, v + dl);
        m.verts[2] = new Vertex5(-dw, i, dl, u - dw, v + dl);
        m.verts[3] = new Vertex5(-dw, i, -dl, u - dw, v - dl);
        return m.apply(new Translation(u, 0, v));
    }

    public static CCModel generateQuartRotated(CCModel m) {
        return combine(
                Arrays.asList(
                        m,
                        m.copy().apply(
                                Rotation.quarterRotations[1].at(Vector3.center).with(new Translation(0, 0.01, 0)))));
    }

    public static CCModel generateEightRotated(CCModel m) {
        return m.apply(new Rotation(Math.PI / 4, 0, 1, 0).at(Vector3.center));
    }

    public static CCModel generateBackface(CCModel m) {
        return combine(Arrays.asList(m, m.backfacedCopy()));
    }

    public static CCModel generateSided(CCModel m, int side) {
        return m.apply(Rotation.sideRotations[side].at(Vector3.center.copy()));
    }

    public static CCModel finishModel(CCModel m) {
        m.shrinkUVs(0.0005);
        m.computeNormals();
        return m.computeLighting(LightModel.standardLightModel);
    }
}
