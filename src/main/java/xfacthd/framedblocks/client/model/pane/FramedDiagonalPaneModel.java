package xfacthd.framedblocks.client.model.pane;

import com.google.common.base.Preconditions;
import com.mojang.math.Vector3f;
import fuzs.diagonalwindows.api.world.level.block.DiagonalBlock;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;
import java.util.Map;

public class FramedDiagonalPaneModel extends FramedPaneModel
{
    private final boolean northEast;
    private final boolean southEast;
    private final boolean northWest;
    private final boolean southWest;
    private final boolean noPillar;

    FramedDiagonalPaneModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);

        boolean hasProps = state.getBlock() instanceof DiagonalBlock && ((DiagonalBlock) state.getBlock()).hasProperties();
        this.northEast = hasProps && state.getValue(DiagonalBlock.NORTH_EAST);
        this.southEast = hasProps && state.getValue(DiagonalBlock.SOUTH_EAST);
        this.northWest = hasProps && state.getValue(DiagonalBlock.NORTH_WEST);
        this.southWest = hasProps && state.getValue(DiagonalBlock.SOUTH_WEST);
        this.noPillar = (northEast && southWest && !southEast && !northWest) || (southEast && northWest && !northEast && !southWest);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        super.transformQuad(quadMap, quad);

        Direction face = quad.getDirection();
        if (Utils.isY(face))
        {
            if (northEast)
            {
                createDiagonalTopBottomEdgeQuad(quadMap, quad, Direction.NORTH, noPillar);
            }
            if (southEast)
            {
                createDiagonalTopBottomEdgeQuad(quadMap, quad, Direction.EAST, noPillar);
            }
            if (southWest)
            {
                createDiagonalTopBottomEdgeQuad(quadMap, quad, Direction.SOUTH, noPillar);
            }
            if (northWest)
            {
                createDiagonalTopBottomEdgeQuad(quadMap, quad, Direction.WEST, noPillar);
            }
        }
        else
        {
            if (isDiagonalSideNotInset(face))
            {
                createDiagonalSideEdgeQuad(quadMap, quad);
            }

            if (Utils.isX(face))
            {
                if (northEast)
                {
                    createDiagonalSideQuad(quadMap.get(null), quad, Direction.NORTH, noPillar);
                }
                if (southWest)
                {
                    createDiagonalSideQuad(quadMap.get(null), quad, Direction.SOUTH, noPillar);
                }
            }

            if (Utils.isZ(face))
            {
                if (southEast)
                {
                    createDiagonalSideQuad(quadMap.get(null), quad, Direction.EAST, noPillar);
                }
                if (northWest)
                {
                    createDiagonalSideQuad(quadMap.get(null), quad, Direction.WEST, noPillar);
                }
            }
        }
    }

    @Override
    protected boolean isPillarVisible()
    {
        return !noPillar;
    }

    protected static void createDiagonalTopBottomEdgeQuad(
            Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, Direction dir, boolean noPillar
    )
    {
        Preconditions.checkArgument(!Utils.isY(dir), String.format("Invalid direction: %s!", dir));

        QuadModifier.geometry(quad)
                .apply(Modifiers.cutTopBottom(dir.getOpposite(), noPillar ? 8F/16F : 7F/16F))
                .apply(Modifiers.cutTopBottom(dir.getClockWise().getAxis(), 9F/16F))
                .apply(rotate(dir))
                .export(quadMap.get(null));
    }

    protected static void createDiagonalSideEdgeQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        QuadModifier.geometry(quad)
                .apply(Modifiers.cutSideLeftRight(9F/16F))
                .apply(rotate(quad.getDirection()))
                .export(quadMap.get(null));
    }

    private static void createDiagonalSideQuad(List<BakedQuad> quadList, BakedQuad quad, Direction dir, boolean noPillar)
    {
        QuadModifier.geometry(quad)
                .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), noPillar ? 8F/16F : 7F/16F))
                .apply(Modifiers.setPosition(9F/16F))
                .apply(rotate(dir))
                .export(quadList);
    }

    protected boolean isDiagonalSideNotInset(Direction face)
    {
        return switch (face)
        {
            case NORTH -> northEast;
            case EAST -> southEast;
            case SOUTH -> southWest;
            case WEST -> northWest;
            default -> throw new IllegalArgumentException(String.format("Invalid face: %s!", face));
        };
    }

    private static QuadModifier.Modifier rotate(Direction dir)
    {
        return Modifiers.rotateCentered(Direction.Axis.Y, -45F, true, new Vector3f(dir.getStepX(), 1, dir.getStepZ()));
    }
}
