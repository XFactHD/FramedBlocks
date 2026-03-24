package io.github.xfacthd.framedblocks.client.model.geometry.stairs;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import org.jspecify.annotations.Nullable;

public class FramedStairsGeometry extends Geometry
{
    private final Direction dir;
    private final boolean top;
    private final StairsShape shape;

    public FramedStairsGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(BlockStateProperties.HORIZONTAL_FACING);
        this.top = ctx.state().getValue(BlockStateProperties.HALF) == Half.TOP;
        this.shape = ctx.state().getValue(BlockStateProperties.STAIRS_SHAPE);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if ((top && quadDir == Direction.DOWN) || (!top && quadDir == Direction.UP))
        {
            createCenterQuads(quadMap, quad);
            createTopBottomQuads(quadMap, quadDir, quad);
        }
        else if (!DirUtils.isY(quadDir))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(top ? Direction.DOWN : Direction.UP, .5F))
                    .export(quadMap, quadDir);

            createSideQuads(quadMap, quad, quadDir);
        }
    }

    private void createCenterQuads(QuadMapBuilder quadMap, BakedQuad quad)
    {
        if (shape == StairsShape.STRAIGHT || shape == StairsShape.OUTER_LEFT || shape == StairsShape.OUTER_RIGHT)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir, .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap, null);
        }

        if (shape != StairsShape.STRAIGHT)
        {
            boolean opposite = shape == StairsShape.OUTER_LEFT || shape == StairsShape.OUTER_RIGHT;
            boolean left = shape == StairsShape.OUTER_LEFT || shape == StairsShape.INNER_LEFT;

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(opposite ? dir.getOpposite() : dir, .5F))
                    .apply(Modifiers.cut(left ? dir.getCounterClockWise() : dir.getClockWise(), .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap, null);
        }
    }

    private void createTopBottomQuads(QuadMapBuilder quadMap, Direction quadDir, BakedQuad quad)
    {
        if (shape == StairsShape.STRAIGHT || shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), .5F))
                    .export(quadMap, quadDir);
        }

        if (shape != StairsShape.STRAIGHT)
        {
            boolean outer = shape == StairsShape.OUTER_LEFT || shape == StairsShape.OUTER_RIGHT;
            boolean left = shape == StairsShape.OUTER_LEFT || shape == StairsShape.INNER_LEFT;

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(outer ? dir.getOpposite() : dir, .5F))
                    .apply(Modifiers.cut(left ? dir.getClockWise() : dir.getCounterClockWise(), .5F))
                    .export(quadMap, quadDir);
        }
    }

    private void createSideQuads(QuadMapBuilder quadMap, BakedQuad quad, Direction quadDir)
    {
        boolean inner = shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT;
        boolean left = shape == StairsShape.OUTER_LEFT || shape == StairsShape.INNER_LEFT;
        Direction vertEdge = top ? Direction.UP : Direction.DOWN;

        if (quadDir == dir.getOpposite())
        {
            Direction cutDir = left != inner ? dir.getClockWise() : dir.getCounterClockWise();
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(vertEdge, .5F))
                    .applyIf(Modifiers.cut(cutDir, .5F), shape != StairsShape.STRAIGHT)
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap, null);

            if (inner)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(vertEdge, .5F))
                        .apply(Modifiers.cut(left ? dir.getClockWise() : dir.getCounterClockWise(), .5F))
                        .export(quadMap, quadDir);
            }
        }
        else if (quadDir == dir && !inner)
        {
            Direction cutDir = left ? dir.getClockWise() : dir.getCounterClockWise();
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(vertEdge, .5F))
                    .apply(Modifiers.cut(cutDir, .5F))
                    .export(quadMap, quadDir);
        }
        else if (quadDir.getAxis() != dir.getAxis())
        {
            boolean outerLeft = shape == StairsShape.OUTER_LEFT && quadDir == dir.getClockWise();
            boolean outerRight = shape == StairsShape.OUTER_RIGHT && quadDir == dir.getCounterClockWise();
            boolean innerLeft = shape == StairsShape.INNER_LEFT && quadDir == dir.getClockWise();
            boolean innerRight = shape == StairsShape.INNER_RIGHT && quadDir == dir.getCounterClockWise();

            if (shape == StairsShape.STRAIGHT || !inner || innerLeft || innerRight)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(vertEdge, .5F))
                        .apply(Modifiers.cut(dir.getOpposite(), .5F))
                        .applyIf(Modifiers.setPosition(.5F), outerLeft || outerRight)
                        .export(quadMap, outerLeft || outerRight ? null : quadDir);
            }

            if (innerLeft || innerRight)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(vertEdge, .5F))
                        .apply(Modifiers.cut(dir, .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap, null);
            }
        }
    }
}
