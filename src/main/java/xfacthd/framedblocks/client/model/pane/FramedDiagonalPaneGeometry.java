package xfacthd.framedblocks.client.model.pane;

import com.google.common.base.Preconditions;
//import fuzs.diagonalwindows.api.world.level.block.DiagonalBlock;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.joml.Vector3f;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;

public class FramedDiagonalPaneGeometry extends FramedPaneGeometry
{
    private final boolean northEast;
    private final boolean southEast;
    private final boolean northWest;
    private final boolean southWest;
    private final boolean noPillar;

    FramedDiagonalPaneGeometry(GeometryFactory.Context ctx)
    {
        super(ctx);

        //boolean hasProps = ctx.state().getBlock() instanceof DiagonalBlock diagonalBlock && diagonalBlock.hasProperties();
        this.northEast = false;//hasProps && ctx.state().getValue(DiagonalBlock.NORTH_EAST);
        this.southEast = false;//hasProps && ctx.state().getValue(DiagonalBlock.SOUTH_EAST);
        this.northWest = false;//hasProps && ctx.state().getValue(DiagonalBlock.NORTH_WEST);
        this.southWest = false;//hasProps && ctx.state().getValue(DiagonalBlock.SOUTH_WEST);
        this.noPillar = (northEast && southWest && !southEast && !northWest) || (southEast && northWest && !northEast && !southWest);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
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

    protected static void createDiagonalTopBottomEdgeQuad(QuadMap quadMap, BakedQuad quad, Direction dir, boolean noPillar)
    {
        Preconditions.checkArgument(!Utils.isY(dir), String.format("Invalid direction: %s!", dir));

        QuadModifier.geometry(quad)
                .apply(Modifiers.cutTopBottom(dir.getOpposite(), noPillar ? 8F/16F : 7F/16F))
                .apply(Modifiers.cutTopBottom(dir.getClockWise().getAxis(), 9F/16F))
                .apply(rotate(dir))
                .export(quadMap.get(null));
    }

    protected static void createDiagonalSideEdgeQuad(QuadMap quadMap, BakedQuad quad)
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
