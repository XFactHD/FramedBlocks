package xfacthd.framedblocks.client.model.slab;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedCheckeredPanelSegmentGeometry extends Geometry
{
    private final Direction dir;
    private final boolean second;

    public FramedCheckeredPanelSegmentGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.second = ctx.state().getValue(PropertyHolder.SECOND);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (Utils.isY(quadDir))
        {
            boolean up = quadDir == Direction.UP;
            Direction xDir = (second ^ up) ? Direction.WEST : Direction.EAST;

            if (dir == Direction.NORTH || dir == xDir.getOpposite())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(Direction.SOUTH, .5F))
                        .apply(Modifiers.cutTopBottom(xDir, .5F))
                        .export(quadMap.get(quadDir));
            }

            if (dir == Direction.SOUTH || dir == xDir)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(Direction.NORTH, .5F))
                        .apply(Modifiers.cutTopBottom(xDir.getOpposite(), .5F))
                        .export(quadMap.get(quadDir));
            }

            if (dir == Direction.NORTH || dir == xDir)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(Direction.SOUTH, .5F))
                        .apply(Modifiers.cutTopBottom(xDir.getOpposite(), .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap.get(null));
            }

            if (dir == Direction.SOUTH || dir == xDir.getOpposite())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(Direction.NORTH, .5F))
                        .apply(Modifiers.cutTopBottom(xDir, .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap.get(null));
            }
        }
        else
        {
            Direction horDir = Utils.isX(quadDir) ^ second ? quadDir.getCounterClockWise() : quadDir.getClockWise();

            if (quadDir != dir.getOpposite())
            {
                if (quadDir == dir || horDir.getOpposite() == dir)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(false, .5F))
                            .apply(Modifiers.cutSideLeftRight(horDir, .5F))
                            .export(quadMap.get(quadDir));
                }

                if (quadDir == dir || horDir == dir)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(true, .5F))
                            .apply(Modifiers.cutSideLeftRight(horDir.getOpposite(), .5F))
                            .export(quadMap.get(quadDir));
                }
            }

            if (quadDir != dir)
            {
                if (quadDir == dir.getOpposite() || horDir == dir)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(false, .5F))
                            .apply(Modifiers.cutSideLeftRight(horDir.getOpposite(), .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap.get(null));
                }

                if (quadDir == dir.getOpposite() || horDir.getOpposite() == dir)
                {
                    QuadModifier.of(quad)
                            .apply(Modifiers.cutSideUpDown(true, .5F))
                            .apply(Modifiers.cutSideLeftRight(horDir, .5F))
                            .apply(Modifiers.setPosition(.5F))
                            .export(quadMap.get(null));
                }
            }
        }
    }
}
