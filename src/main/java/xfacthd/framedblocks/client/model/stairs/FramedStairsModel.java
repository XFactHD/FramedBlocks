package xfacthd.framedblocks.client.model.stairs;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;

public class FramedStairsModel extends FramedBlockModel
{
    private final Direction dir;
    private final boolean top;
    private final StairsShape shape;

    public FramedStairsModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        top = state.getValue(BlockStateProperties.HALF) == Half.TOP;
        shape = state.getValue(BlockStateProperties.STAIRS_SHAPE);
    }

    @Override
    protected void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if ((top && quadDir == Direction.DOWN) || (!top && quadDir == Direction.UP))
        {
            createCenterQuads(quadMap.get(null), quad);
            createTopBottomQuads(quadMap.get(quadDir), quad);
        }
        else if (!Utils.isY(quad.getDirection()))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .export(quadMap.get(quadDir));

            createSideQuads(quadMap, quad, quadDir);
        }
    }

    private void createCenterQuads(List<BakedQuad> quadList, BakedQuad quad)
    {
        if (shape == StairsShape.STRAIGHT || shape == StairsShape.OUTER_LEFT || shape == StairsShape.OUTER_RIGHT)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir, .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadList);
        }

        if (shape != StairsShape.STRAIGHT)
        {
            boolean opposite = shape == StairsShape.OUTER_LEFT || shape == StairsShape.OUTER_RIGHT;
            boolean left = shape == StairsShape.OUTER_LEFT || shape == StairsShape.INNER_LEFT;

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(opposite ? dir.getOpposite() : dir, .5F))
                    .apply(Modifiers.cutTopBottom(left ? dir.getCounterClockWise() : dir.getClockWise(), .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadList);
        }
    }

    private void createTopBottomQuads(List<BakedQuad> quadList, BakedQuad quad)
    {
        if (shape == StairsShape.STRAIGHT || shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                    .export(quadList);
        }

        if (shape != StairsShape.STRAIGHT)
        {
            boolean outer = shape == StairsShape.OUTER_LEFT || shape == StairsShape.OUTER_RIGHT;
            boolean left = shape == StairsShape.OUTER_LEFT || shape == StairsShape.INNER_LEFT;

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(outer ? dir.getOpposite() : dir, .5F))
                    .apply(Modifiers.cutTopBottom(left ? dir.getClockWise() : dir.getCounterClockWise(), .5F))
                    .export(quadList);
        }
    }

    private void createSideQuads(QuadMap quadMap, BakedQuad quad, Direction quadDir)
    {
        boolean inner = shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT;
        boolean left = shape == StairsShape.OUTER_LEFT || shape == StairsShape.INNER_LEFT;

        if (quadDir == dir.getOpposite())
        {
            Direction cutDir = left != inner ? dir.getClockWise() : dir.getCounterClockWise();
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(!top, .5F))
                    .applyIf(Modifiers.cutSideLeftRight(cutDir, .5F), shape != StairsShape.STRAIGHT)
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));

            if (inner)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(!top, .5F))
                        .apply(Modifiers.cutSideLeftRight(left ? dir.getClockWise() : dir.getCounterClockWise(), .5F))
                        .export(quadMap.get(quadDir));
            }
        }
        else if (quadDir == dir && !inner)
        {
            Direction cutDir = left ? dir.getClockWise() : dir.getCounterClockWise();
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(!top, .5F))
                    .apply(Modifiers.cutSideLeftRight(cutDir, .5F))
                    .export(quadMap.get(quadDir));
        }
        else if (quadDir.getAxis() != dir.getAxis())
        {
            boolean outerLeft = shape == StairsShape.OUTER_LEFT && quadDir == dir.getClockWise();
            boolean outerRight = shape == StairsShape.OUTER_RIGHT && quadDir == dir.getCounterClockWise();
            boolean innerLeft = shape == StairsShape.INNER_LEFT && quadDir == dir.getClockWise();
            boolean innerRight = shape == StairsShape.INNER_RIGHT && quadDir == dir.getCounterClockWise();

            if (shape == StairsShape.STRAIGHT || !inner || innerLeft || innerRight)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(!top, .5F))
                        .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .5F))
                        .applyIf(Modifiers.setPosition(.5F), outerLeft || outerRight)
                        .export(quadMap.get(outerLeft || outerRight ? null : quadDir));
            }

            if (innerLeft || innerRight)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(!top, .5F))
                        .apply(Modifiers.cutSideLeftRight(dir, .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap.get(null));
            }
        }
    }
}