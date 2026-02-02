package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedTubeGeometry extends Geometry
{
    private final Direction.Axis axis;
    private final float thickness;

    public FramedTubeGeometry(GeometryFactory.Context ctx)
    {
        this.axis = ctx.state().getValue(BlockStateProperties.AXIS);
        this.thickness = ctx.state().getValue(PropertyHolder.THICK) ? 3F : 2F;
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (axis == Direction.Axis.Y)
        {
            if (quadDir.getAxis() == axis)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(Direction.NORTH, thickness / 16F))
                        .export(quadMap.get(quadDir));
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(Direction.SOUTH, thickness / 16F))
                        .export(quadMap.get(quadDir));
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(Direction.EAST, thickness / 16F))
                        .apply(Modifiers.cutTopBottom(Direction.Axis.Z, (16F - thickness) / 16F))
                        .export(quadMap.get(quadDir));
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(Direction.WEST, thickness / 16))
                        .apply(Modifiers.cutTopBottom(Direction.Axis.Z, (16F - thickness) / 16F))
                        .export(quadMap.get(quadDir));
            }
            else
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideLeftRight((16F - thickness) / 16F))
                        .apply(Modifiers.setPosition(thickness / 16F))
                        .export(quadMap.get(null));
            }
        }
        else
        {
            if (quadDir.getAxis() == axis)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(false, thickness / 16F))
                        .export(quadMap.get(quadDir));
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(true, thickness / 16F))
                        .export(quadMap.get(quadDir));
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideLeftRight(quadDir.getClockWise(), thickness / 16F))
                        .apply(Modifiers.cutSideUpDown((16F - thickness) / 16F))
                        .export(quadMap.get(quadDir));
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideLeftRight(quadDir.getCounterClockWise(), thickness / 16F))
                        .apply(Modifiers.cutSideUpDown((16F - thickness) / 16F))
                        .export(quadMap.get(quadDir));
            }
            else if (Utils.isY(quadDir))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(Utils.getPerpendicularAxis(axis, Direction.Axis.Y), (16F - thickness) / 16F))
                        .apply(Modifiers.setPosition(thickness / 16F))
                        .export(quadMap.get(null));
            }
            else
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown((16F - thickness) / 16F))
                        .apply(Modifiers.setPosition(thickness / 16F))
                        .export(quadMap.get(null));
            }
        }
    }

    @Override
    public boolean transformAllQuads()
    {
        return true;
    }
}
