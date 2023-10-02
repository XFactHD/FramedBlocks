package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.renderer.block.model.BakedQuad;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;

public class FramedCubeGeometry implements Geometry
{
    public FramedCubeGeometry(@SuppressWarnings("unused") GeometryFactory.Context ctx) { }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad) { }

    @Override
    public boolean forceUngeneratedBaseModel()
    {
        return true;
    }
}
