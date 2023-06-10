package xfacthd.framedblocks.client.model.pane;

import com.google.common.base.Preconditions;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;
import java.util.Map;

public class FramedPaneModel extends FramedBlockModel
{
    protected final boolean north;
    protected final boolean east;
    protected final boolean south;
    protected final boolean west;

    public FramedPaneModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);

        north = state.getValue(BlockStateProperties.NORTH);
        east = state.getValue(BlockStateProperties.EAST);
        south = state.getValue(BlockStateProperties.SOUTH);
        west = state.getValue(BlockStateProperties.WEST);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction face = quad.getDirection();
        if (Utils.isY(face))
        {
            createTopBottomCenterQuad(quadMap, quad, false);

            if (north) { createTopBottomEdgeQuad(quadMap, quad, Direction.NORTH, false); }
            if (east) { createTopBottomEdgeQuad(quadMap, quad, Direction.EAST, false); }
            if (south) { createTopBottomEdgeQuad(quadMap, quad, Direction.SOUTH, false); }
            if (west) { createTopBottomEdgeQuad(quadMap, quad, Direction.WEST, false); }
        }
        else
        {
            createSideEdgeQuad(quadMap, quad, isSideInset(face), false);

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

    protected static void createTopBottomCenterQuad(
            Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, boolean mirrored
    )
    {
        QuadModifier.geometry(quad)
                .apply(Modifiers.cutTopBottom(7F/16F, 7F/16F, 9F/16F, 9F/16F))
                .applyIf(Modifiers.setPosition(.001F), mirrored)
                .export(quadMap.get(mirrored ? null : quad.getDirection()));
    }

    protected static void createTopBottomEdgeQuad(
            Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, Direction dir, boolean mirrored
    )
    {
        Preconditions.checkArgument(!Utils.isY(dir), String.format("Invalid direction: %s!", dir));

        QuadModifier.geometry(quad)
                .apply(Modifiers.cutTopBottom(dir.getOpposite(), 7F/16F))
                .apply(Modifiers.cutTopBottom(dir.getClockWise().getAxis(), 9F/16F))
                .applyIf(Modifiers.setPosition(.001F), mirrored)
                .export(quadMap.get(mirrored ? null : quad.getDirection()));
    }

    protected static void createSideEdgeQuad(
            Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, boolean inset, boolean mirrored
    )
    {
        Preconditions.checkArgument(!inset || !mirrored, "Quad can't be mirrored and inset!");

        Direction quadDir = quad.getDirection();
        Direction exportSide = inset ? null : (mirrored ? quadDir.getOpposite() : quadDir);

        QuadModifier.geometry(quad)
                .apply(Modifiers.cutSideLeftRight(9F/16F))
                .applyIf(Modifiers.setPosition(9F/16F), inset)
                .applyIf(Modifiers.setPosition(.001F), !inset && mirrored)
                .export(quadMap.get(exportSide));
    }

    private static void createSideQuad(List<BakedQuad> quadList, BakedQuad quad, Direction dir)
    {
        QuadModifier.geometry(quad)
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