package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.*;
import xfacthd.framedblocks.common.FBContent;

import java.util.List;
import java.util.Map;

public class FramedPrismModel extends FramedBlockModel
{
    private final Direction facing;
    private final Direction.Axis axis;

    public FramedPrismModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.get(BlockStateProperties.FACING);
        this.axis = state.get(BlockStateProperties.AXIS);
    }

    public FramedPrismModel(IBakedModel baseModel)
    {
        this(
                FBContent.blockFramedPrism.get().getDefaultState().with(BlockStateProperties.FACING, Direction.UP),
                baseModel
        );
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadFace = quad.getFace();
        if (facing.getAxis() == Direction.Axis.Y && quadFace.getAxis() != axis && quadFace.getAxis() != facing.getAxis())
        {
            BakedQuad slope = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(slope, facing == Direction.DOWN, .5F))
            {
                BakedQuadTransformer.createTopBottomSlopeQuad(slope, facing == Direction.UP);
                quadMap.get(null).add(slope);
            }
        }
        else if (facing.getAxis() != Direction.Axis.Y && axis != Direction.Axis.Y && quadFace == facing)
        {
            BakedQuad slope = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(slope, false, .5F))
            {
                BakedQuadTransformer.createTopBottomSlopeQuad(slope, false);
                quadMap.get(null).add(slope);
            }

            slope = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(slope, true, .5F))
            {
                BakedQuadTransformer.createTopBottomSlopeQuad(slope, true);
                quadMap.get(null).add(slope);
            }
        }
        else if (facing.getAxis() != Direction.Axis.Y && axis == Direction.Axis.Y && quadFace.getAxis() != axis && quadFace.getAxis() != facing.getAxis())
        {
            BakedQuad slope = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(slope, facing, .5F))
            {
                BakedQuadTransformer.createSideSlopeQuad(slope, quadFace == facing.rotateY());
                quadMap.get(null).add(slope);
            }
        }
        else if (quadFace.getAxis() == axis)
        {
            TriangleDirection triDir;
            if (facing.getAxis() == Direction.Axis.Y)
            {
                triDir = facing == Direction.UP ? TriangleDirection.UP : TriangleDirection.DOWN;
            }
            else if (axis != Direction.Axis.Y)
            {
                triDir = quadFace == facing.rotateY() ? TriangleDirection.RIGHT : TriangleDirection.LEFT;
            }
            else
            {
                BakedQuad triangle = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomSmallTriangleQuad(triangle, facing))
                {
                    quadMap.get(quadFace).add(triangle);
                }
                return;
            }

            BakedQuad triangle = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSmallTriangleQuad(triangle, triDir))
            {
                quadMap.get(quadFace).add(triangle);
            }
        }
    }
}
