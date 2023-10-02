package xfacthd.framedblocks.client.model.pillar;

import fuzs.diagonalfences.api.world.level.block.DiagonalBlock;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.joml.Vector3f;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

public class FramedDiagonalFenceGeometry extends FramedFenceGeometry
{
    private final boolean northEast;
    private final boolean southEast;
    private final boolean northWest;
    private final boolean southWest;

    FramedDiagonalFenceGeometry(GeometryFactory.Context ctx)
    {
        super(ctx);
        boolean hasProps = ctx.state().getBlock() instanceof DiagonalBlock diagonalBlock && diagonalBlock.hasProperties();
        this.northEast = hasProps && ctx.state().getValue(DiagonalBlock.NORTH_EAST);
        this.southEast = hasProps && ctx.state().getValue(DiagonalBlock.SOUTH_EAST);
        this.northWest = hasProps && ctx.state().getValue(DiagonalBlock.NORTH_WEST);
        this.southWest = hasProps && ctx.state().getValue(DiagonalBlock.SOUTH_WEST);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        super.transformQuad(quadMap, quad);

        createDiagonalFenceBars(quadMap, quad, Direction.NORTH, northEast);
        createDiagonalFenceBars(quadMap, quad, Direction.EAST, southEast);
        createDiagonalFenceBars(quadMap, quad, Direction.SOUTH, southWest);
        createDiagonalFenceBars(quadMap, quad, Direction.WEST, northWest);
    }

    private static void createDiagonalFenceBars(QuadMap quadMap, BakedQuad quad, Direction dir, boolean active)
    {
        if (!active)
        {
            return;
        }

        Direction quadDir = quad.getDirection();

        if (Utils.isY(quadDir))
        {
            QuadModifier mod = QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), 7F/16F))
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), 9F/16F))
                    .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), 9F/16F))
                    .apply(rotate(dir));

            mod.derive().apply(Modifiers.setPosition(quadDir == Direction.UP ? 15F/16F : 4F/16F))
                    .export(quadMap.get(null));

            mod.apply(Modifiers.setPosition(quadDir == Direction.UP ? 9F/16F : 10F/16F))
                    .export(quadMap.get(null));
        }
        else if (quadDir == dir.getClockWise() || quadDir == dir.getCounterClockWise())
        {
            boolean neg = !Utils.isPositive(dir);

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(neg ? 0F : 9F/16F, 6F/16F, neg ? 7F/16F : 1F, 9F/16F))
                    .apply(Modifiers.setPosition(9F/16F))
                    .apply(rotate(dir))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(neg ? 0F : 9F/16F, 12F/16F, neg ? 7F/16F : 1F, 15F/16F))
                    .apply(Modifiers.setPosition(9F/16F))
                    .apply(rotate(dir))
                    .export(quadMap.get(null));
        }
        else if (quadDir == dir)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(7F/16F, 6F/16F, 9F/16F, 9F/16F))
                    .apply(rotate(dir))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(7F/16F, 12F/16F, 9F/16F, 15F/16F))
                    .apply(rotate(dir))
                    .export(quadMap.get(null));
        }
    }

    private static QuadModifier.Modifier rotate(Direction dir)
    {
        return Modifiers.rotateCentered(Direction.Axis.Y, -45F, true, new Vector3f(dir.getStepX(), 1, dir.getStepZ()));
    }
}