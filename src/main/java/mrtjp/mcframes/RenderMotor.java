package mrtjp.mcframes;

import codechicken.lib.render.uv.MultiIconTransformation;
import codechicken.lib.render.uv.UVTransformation;
import mrtjp.core.render.TCubeMapRender;
import mrtjp.core.world.WorldLib;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

public class RenderMotor implements TCubeMapRender {

    public static RenderMotor instance = new RenderMotor();

    public IIcon bottom;
    public IIcon side;
    public IIcon sidew;
    public IIcon sidee;
    public IIcon top;

    @Override
    public Triple<Integer, Integer, UVTransformation> getData(IBlockAccess w, int x, int y, int z) {

        TileMotor te = WorldLib.getTileEntity(w, x, y, z, TileMotor.class);
        int s = 0;
        int r = 0;
        if (te != null) {
            s = te.side();
            r = te.rotation();
        }
        return new ImmutableTriple<>(s, r, new MultiIconTransformation(bottom, top, side, side, sidew, sidee));
    }

    @Override
    public Triple<Integer, Integer, UVTransformation> getInvData() {
        return new ImmutableTriple<>(0, 0, new MultiIconTransformation(bottom, top, side, side, sidew, sidee));
    }

    @Override
    public IIcon getIcon(int s, int meta) {
        switch (s) {
            case 0: return bottom;
            case 1: return top;
            case 2:
            case 3:
                return side;
            case 4: return sidew;
            case 5: return sidee;
            default: return null;
        }
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        bottom = reg.registerIcon("mcframes:motor/bottom");
        top = reg.registerIcon("mcframes:motor/top");
        side = reg.registerIcon("mcframes:motor/side");
        sidew = reg.registerIcon("mcframes:motor/sidew");
        sidee = reg.registerIcon("mcframes:motor/sidee");
    }
}
