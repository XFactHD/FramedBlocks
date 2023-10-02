package xfacthd.framedblocks.client.model.pillar;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

public class FramedHalfPillarGeometry implements Geometry
{
    private final Direction face;

    public FramedHalfPillarGeometry(GeometryFactory.Context ctx)
    {
        this.face = ctx.state().getValue(BlockStateProperties.FACING);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        QuadModifier mod = FramedPillarGeometry.createPillarQuad(quad, face.getAxis(), 4F / 16F, 12F / 16F, 12F / 16F);
        if (mod.hasFailed())
        {
            return;
        }

        Direction quadDir = quad.getDirection();
        if (quadDir == face)
        {
            mod.export(quadMap.get(face));
        }
        else if (quadDir == face.getOpposite())
        {
            mod.apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }
        else if (Utils.isY(face))
        {
            mod.apply(Modifiers.cutSideUpDown(face == Direction.UP, .5F))
                    .export(quadMap.get(null));
        }
        else if (Utils.isY(quadDir))
        {
            mod.apply(Modifiers.cutTopBottom(face.getOpposite(), .5F))
                    .export(quadMap.get(null));
        }
        else
        {
            mod.apply(Modifiers.cutSideLeftRight(face.getOpposite(), .5F))
                    .export(quadMap.get(null));
        }
    }
}