package xfacthd.framedblocks.client.model.stairs;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.StairsType;

public class FramedVerticalStairsGeometry extends Geometry
{
    private final boolean vertical;
    private final boolean top;
    private final boolean bottom;
    private final boolean forward;
    private final boolean counterClockWise;
    private final Direction dir;

    public FramedVerticalStairsGeometry(GeometryFactory.Context ctx)
    {
        StairsType type = ctx.state().getValue(PropertyHolder.STAIRS_TYPE);
        this.vertical = type == StairsType.VERTICAL;
        this.top = type.isTop();
        this.bottom = type.isBottom();
        this.forward = type.isForward();
        this.counterClockWise = type.isCounterClockwise();
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (vertical && (quadDir == dir.getOpposite() || quadDir == dir.getClockWise()))
        {
            Direction cutDir = quadDir == dir.getOpposite() ? dir.getClockWise() : dir.getOpposite();

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideLeftRight(cutDir, .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideLeftRight(cutDir.getOpposite(), .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }

        if ((quadDir == Direction.UP && !top) || (quadDir == Direction.DOWN && !bottom))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(dir, .5F))
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                    .export(quadMap.get(quadDir));
        }

        if (quadDir == dir.getOpposite() && !vertical)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), .5F))
                    .applyIf(Modifiers.cutSideUpDown(bottom, .5F), counterClockWise)
                    .export(quadMap.get(quadDir));

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), .5F))
                    .applyIf(Modifiers.cutSideUpDown(bottom, .5F), forward)
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));

            if (counterClockWise)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), .5F))
                        .apply(Modifiers.cutSideUpDown(!bottom, .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap.get(null));
            }
        }

        if (quadDir == dir.getClockWise() && !vertical)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .5F))
                    .applyIf(Modifiers.cutSideUpDown(bottom, .5F), forward)
                    .export(quadMap.get(quadDir));

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideLeftRight(dir, .5F))
                    .applyIf(Modifiers.cutSideUpDown(bottom, .5F), counterClockWise)
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));

            if (forward)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .5F))
                        .apply(Modifiers.cutSideUpDown(!bottom, .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap.get(null));
            }
        }

        if ((quadDir == Direction.UP && top) || (quadDir == Direction.DOWN && bottom))
        {
            QuadModifier.of(quad)
                    .applyIf(Modifiers.cutTopBottom(dir.getOpposite(), .5F), counterClockWise)
                    .applyIf(Modifiers.cutTopBottom(dir.getClockWise(), .5F), forward)
                    .export(quadMap.get(quadDir));

            if (forward)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                        .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap.get(null));
            }

            if (counterClockWise)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir, .5F))
                        .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap.get(null));
            }
        }

        if (quadDir == dir && forward)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideUpDown(bottom, .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), .5F))
                    .export(quadMap.get(quadDir));
        }

        if (quadDir == dir.getCounterClockWise() && counterClockWise)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideUpDown(bottom, .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .5F))
                    .export(quadMap.get(quadDir));
        }
    }
}
