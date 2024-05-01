package xfacthd.framedblocks.client.model.pane;

import com.google.common.base.Preconditions;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;

public class FramedPaneGeometry extends Geometry
{
    protected final boolean north;
    protected final boolean east;
    protected final boolean south;
    protected final boolean west;

    public FramedPaneGeometry(GeometryFactory.Context ctx)
    {
        this.north = ctx.state().getValue(BlockStateProperties.NORTH);
        this.east = ctx.state().getValue(BlockStateProperties.EAST);
        this.south = ctx.state().getValue(BlockStateProperties.SOUTH);
        this.west = ctx.state().getValue(BlockStateProperties.WEST);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction face = quad.getDirection();
        if (Utils.isY(face))
        {
            if (isPillarVisible())
            {
                createTopBottomCenterQuad(quadMap, quad, false);
            }

            if (north) { createTopBottomEdgeQuad(quadMap, quad, Direction.NORTH, false); }
            if (east) { createTopBottomEdgeQuad(quadMap, quad, Direction.EAST, false); }
            if (south) { createTopBottomEdgeQuad(quadMap, quad, Direction.SOUTH, false); }
            if (west) { createTopBottomEdgeQuad(quadMap, quad, Direction.WEST, false); }
        }
        else
        {
            boolean inset = isSideInset(face);
            if (!inset || isPillarVisible())
            {
                createSideEdgeQuad(quadMap, quad, inset, false);
            }

            if (Utils.isX(face))
            {
                if (north) { createSideQuad(quadMap.get(null), quad, Direction.NORTH); }
                if (south) { createSideQuad(quadMap.get(null), quad, Direction.SOUTH); }
            }

            if (Utils.isZ(face))
            {
                if (east) { createSideQuad(quadMap.get(null), quad, Direction.EAST); }
                if (west) { createSideQuad(quadMap.get(null), quad, Direction.WEST); }
            }
        }
    }

    protected boolean isPillarVisible()
    {
        return true;
    }

    protected static void createTopBottomCenterQuad(QuadMap quadMap, BakedQuad quad, boolean mirrored)
    {
        QuadModifier.of(quad)
                .apply(Modifiers.cutTopBottom(7F/16F, 7F/16F, 9F/16F, 9F/16F))
                .applyIf(Modifiers.setPosition(.001F), mirrored)
                .export(quadMap.get(mirrored ? null : quad.getDirection()));
    }

    protected static void createTopBottomEdgeQuad(QuadMap quadMap, BakedQuad quad, Direction dir, boolean mirrored)
    {
        Preconditions.checkArgument(!Utils.isY(dir), String.format("Invalid direction: %s!", dir));

        QuadModifier.of(quad)
                .apply(Modifiers.cutTopBottom(dir.getOpposite(), 7F/16F))
                .apply(Modifiers.cutTopBottom(dir.getClockWise().getAxis(), 9F/16F))
                .applyIf(Modifiers.setPosition(.001F), mirrored)
                .export(quadMap.get(mirrored ? null : quad.getDirection()));
    }

    protected static void createSideEdgeQuad(QuadMap quadMap, BakedQuad quad, boolean inset, boolean mirrored)
    {
        Preconditions.checkArgument(!inset || !mirrored, "Quad can't be mirrored and inset!");

        Direction quadDir = quad.getDirection();
        Direction exportSide = inset ? null : (mirrored ? quadDir.getOpposite() : quadDir);

        QuadModifier.of(quad)
                .apply(Modifiers.cutSideLeftRight(9F/16F))
                .applyIf(Modifiers.setPosition(9F/16F), inset)
                .applyIf(Modifiers.setPosition(.001F), !inset && mirrored)
                .export(quadMap.get(exportSide));
    }

    private static void createSideQuad(List<BakedQuad> quadList, BakedQuad quad, Direction dir)
    {
        QuadModifier.of(quad)
                .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), 7F/16F))
                .apply(Modifiers.setPosition(9F/16F))
                .export(quadList);
    }

    protected boolean isSideInset(Direction face)
    {
        return switch (face)
        {
            case NORTH -> !north;
            case EAST -> !east;
            case SOUTH -> !south;
            case WEST -> !west;
            default -> throw new IllegalArgumentException(String.format("Invalid face: %s!", face));
        };
    }
}