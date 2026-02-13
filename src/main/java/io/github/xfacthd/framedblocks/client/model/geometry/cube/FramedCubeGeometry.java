package io.github.xfacthd.framedblocks.client.model.geometry.cube;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.jspecify.annotations.Nullable;

public class FramedCubeGeometry extends Geometry
{
    public FramedCubeGeometry(@SuppressWarnings("unused") GeometryFactory.Context ctx) { }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData) { }

    @Override
    public boolean forceUngeneratedBaseModel()
    {
        return true;
    }
}
