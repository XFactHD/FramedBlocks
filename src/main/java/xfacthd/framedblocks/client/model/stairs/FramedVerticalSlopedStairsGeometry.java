package xfacthd.framedblocks.client.model.stairs;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public class FramedVerticalSlopedStairsGeometry extends Geometry
{
    private final Direction facing;
    private final Direction rotDir;
    private final Direction rotDirTwo;
    private final boolean ySlope;

    public FramedVerticalSlopedStairsGeometry(GeometryFactory.Context ctx)
    {
        this.facing = ctx.state().getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = ctx.state().getValue(PropertyHolder.ROTATION);
        this.rotDir = rot.withFacing(facing);
        this.rotDirTwo = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (quadDir == rotDir || quadDir == rotDirTwo)
        {
            if (Utils.isY(quadDir))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(facing.getOpposite(), .5F))
                        .export(quadMap.get(quadDir));
            }
            else
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(facing.getOpposite(), .5F))
                        .export(quadMap.get(quadDir));
            }
        }
        else if (quadDir == facing.getOpposite())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(rotDir, 1F, 0F))
                    .export(quadMap.get(quadDir));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(rotDir.getOpposite(), 1F, 0F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }

        boolean useRotDirQuad = Utils.isY(rotDir) == ySlope;
        Direction slopeQuadDir = useRotDirQuad ? rotDir : rotDirTwo;
        Direction slopeRotDir = useRotDirQuad ? rotDirTwo : rotDir;

        if (quadDir == slopeQuadDir)
        {
            if (Utils.isY(slopeQuadDir))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(facing, .5F))
                        .apply(Modifiers.makeVerticalSlope(slopeRotDir, 45F))
                        .export(quadMap.get(null));
            }
            else
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(facing, .5F))
                        .apply(Modifiers.makeVerticalSlope(slopeRotDir == Direction.UP, 45F))
                        .export(quadMap.get(null));
            }
        }
    }
}
