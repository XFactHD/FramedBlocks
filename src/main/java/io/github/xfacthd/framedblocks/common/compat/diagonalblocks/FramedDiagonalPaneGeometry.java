package io.github.xfacthd.framedblocks.common.compat.diagonalblocks;

import com.google.common.base.Preconditions;
import fuzs.diagonalblocks.api.v2.block.DiagonalBlock;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.client.model.geometry.pane.FramedPaneGeometry;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

class FramedDiagonalPaneGeometry extends FramedPaneGeometry
{
    private final boolean northEast;
    private final boolean southEast;
    private final boolean northWest;
    private final boolean southWest;
    private final boolean noPillar;

    public FramedDiagonalPaneGeometry(GeometryFactory.Context ctx)
    {
        super(ctx);

        this.northEast = ctx.state().getValue(DiagonalBlock.NORTH_EAST);
        this.southEast = ctx.state().getValue(DiagonalBlock.SOUTH_EAST);
        this.northWest = ctx.state().getValue(DiagonalBlock.NORTH_WEST);
        this.southWest = ctx.state().getValue(DiagonalBlock.SOUTH_WEST);
        this.noPillar = (northEast && southWest && !southEast && !northWest) || (southEast && northWest && !northEast && !southWest);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        super.transformQuad(quadMap, quad, blockData, modelData);

        Direction face = quad.direction();
        if (DirUtils.isY(face))
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

            if (DirUtils.isX(face))
            {
                if (northEast)
                {
                    createDiagonalSideQuad(quadMap, quad, Direction.NORTH, noPillar);
                }
                if (southWest)
                {
                    createDiagonalSideQuad(quadMap, quad, Direction.SOUTH, noPillar);
                }
            }

            if (DirUtils.isZ(face))
            {
                if (southEast)
                {
                    createDiagonalSideQuad(quadMap, quad, Direction.EAST, noPillar);
                }
                if (northWest)
                {
                    createDiagonalSideQuad(quadMap, quad, Direction.WEST, noPillar);
                }
            }
        }
    }

    @Override
    protected boolean isPillarVisible()
    {
        return !noPillar;
    }

    protected static void createDiagonalTopBottomEdgeQuad(QuadMapBuilder quadMap, BakedQuad quad, Direction dir, boolean noPillar)
    {
        Preconditions.checkArgument(!DirUtils.isY(dir), String.format("Invalid direction: %s!", dir));

        QuadModifier.of(quad)
                .apply(Modifiers.cut(dir.getOpposite(), noPillar ? 8F/16F : 7F/16F))
                .apply(Modifiers.cut(dir.getClockWise().getAxis(), 9F/16F))
                .apply(rotate(dir))
                .export(quadMap, null);
    }

    protected static void createDiagonalSideEdgeQuad(QuadMapBuilder quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.direction();
        QuadModifier.of(quad)
                .apply(Modifiers.cut(quadDir.getClockWise().getAxis(), 9F/16F))
                .apply(rotate(quadDir))
                .export(quadMap, null);
    }

    private static void createDiagonalSideQuad(QuadMapBuilder quadMap, BakedQuad quad, Direction dir, boolean noPillar)
    {
        QuadModifier.of(quad)
                .apply(Modifiers.cut(dir.getOpposite(), noPillar ? 8F/16F : 7F/16F))
                .apply(Modifiers.setPosition(9F/16F))
                .apply(rotate(dir))
                .export(quadMap, null);
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
