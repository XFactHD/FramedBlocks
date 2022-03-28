package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.client.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;
import java.util.Map;

public class FramedSlopedPrismModel extends FramedBlockModel
{
    private final Direction facing;
    private final Direction orientation;

    public FramedSlopedPrismModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(BlockStateProperties.FACING);
        this.orientation = state.getValue(PropertyHolder.ORIENTATION);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (isStateInvalid()) { return; }

        Direction quadFace = quad.getDirection();
        if (quadFace == orientation.getOpposite() && !Utils.isY(orientation))
        {
            BakedQuad triangle = ModelUtils.duplicateQuad(quad);
            if (Utils.isY(facing))
            {
                TriangleDirection triDir = facing == Direction.UP ? TriangleDirection.UP : TriangleDirection.DOWN;
                if (BakedQuadTransformer.createSmallTriangleQuad(triangle, triDir))
                {
                    BakedQuadTransformer.createTopBottomSlopeQuad(triangle, facing == Direction.UP);
                    quadMap.get(null).add(triangle);
                }
            }
            else
            {
                TriangleDirection triDir = orientation == facing.getCounterClockWise() ? TriangleDirection.RIGHT : TriangleDirection.LEFT;
                if (BakedQuadTransformer.createSmallTriangleQuad(triangle, triDir))
                {
                    BakedQuadTransformer.createSideSlopeQuad(triangle, triDir == TriangleDirection.RIGHT);
                    quadMap.get(null).add(triangle);
                }
            }
        }
        else if (quadFace == facing && Utils.isY(orientation))
        {
            TriangleDirection triDir = orientation == Direction.UP ? TriangleDirection.UP : TriangleDirection.DOWN;

            BakedQuad triangle = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSmallTriangleQuad(triangle, triDir))
            {
                BakedQuadTransformer.createTopBottomSlopeQuad(triangle, orientation == Direction.DOWN);
                quadMap.get(null).add(triangle);
            }
        }
        else if (quadFace == orientation)
        {
            TriangleDirection triDir;
            if (Utils.isY(facing))
            {
                triDir = facing == Direction.UP ? TriangleDirection.UP : TriangleDirection.DOWN;
            }
            else if (!Utils.isY(orientation))
            {
                triDir = quadFace == facing.getClockWise() ? TriangleDirection.RIGHT : TriangleDirection.LEFT;
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
        else if (Utils.isY(facing) && quadFace.getAxis() != facing.getAxis() && quadFace.getAxis() != orientation.getAxis())
        {
            BakedQuad slope = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(slope, facing == Direction.DOWN, .5F)
            )
            {
                BakedQuad corner = ModelUtils.duplicateQuad(slope);
                if (BakedQuadTransformer.createVerticalSideQuad(corner, orientation, .5F) &&
                    BakedQuadTransformer.createSideTriangleQuad(corner, quadFace == orientation.getClockWise(), facing == Direction.DOWN)
                )
                {
                    BakedQuadTransformer.createTopBottomSlopeQuad(corner, facing == Direction.UP);
                    quadMap.get(null).add(corner);
                }

                if (BakedQuadTransformer.createVerticalSideQuad(slope, orientation.getOpposite(), .5F))
                {
                    BakedQuadTransformer.createTopBottomSlopeQuad(slope, facing == Direction.UP);
                    quadMap.get(null).add(slope);
                }
            }
        }
        else if (Utils.isY(orientation) && quadFace.getAxis() != facing.getAxis() && quadFace.getAxis() != orientation.getAxis())
        {
            BakedQuad slope = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(slope, facing, .5F))
            {
                BakedQuad corner = ModelUtils.duplicateQuad(slope);
                if (BakedQuadTransformer.createHorizontalSideQuad(corner, orientation == Direction.DOWN, .5F) &&
                    BakedQuadTransformer.createSideTriangleQuad(corner, quadFace == facing.getCounterClockWise(), orientation == Direction.UP)
                )
                {
                    BakedQuadTransformer.createSideSlopeQuad(corner, quadFace == facing.getClockWise());
                    quadMap.get(null).add(corner);
                }

                if (BakedQuadTransformer.createHorizontalSideQuad(slope, orientation == Direction.UP, .5F))
                {
                    BakedQuadTransformer.createSideSlopeQuad(slope, quadFace == facing.getClockWise());
                    quadMap.get(null).add(slope);
                }
            }
        }
        else if (!Utils.isY(orientation) && !Utils.isY(facing) && quadFace == facing)
        {
            BakedQuad slope = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(slope, false, .5F))
            {
                BakedQuad corner = ModelUtils.duplicateQuad(slope);
                if (BakedQuadTransformer.createVerticalSideQuad(corner, orientation, .5F) &&
                    BakedQuadTransformer.createSideTriangleQuad(corner, orientation == facing.getCounterClockWise(), false)
                )
                {
                    BakedQuadTransformer.createTopBottomSlopeQuad(corner, false);
                    quadMap.get(null).add(corner);
                }

                if (BakedQuadTransformer.createVerticalSideQuad(slope, orientation.getOpposite(), .5F))
                {
                    BakedQuadTransformer.createTopBottomSlopeQuad(slope, false);
                    quadMap.get(null).add(slope);
                }
            }

            slope = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(slope, true, .5F))
            {
                BakedQuad corner = ModelUtils.duplicateQuad(slope);
                if (BakedQuadTransformer.createVerticalSideQuad(corner, orientation, .5F) &&
                    BakedQuadTransformer.createSideTriangleQuad(corner, orientation == facing.getCounterClockWise(), true)
                )
                {
                    BakedQuadTransformer.createTopBottomSlopeQuad(corner, true);
                    quadMap.get(null).add(corner);
                }

                if (BakedQuadTransformer.createVerticalSideQuad(slope, orientation.getOpposite(), .5F))
                {
                    BakedQuadTransformer.createTopBottomSlopeQuad(slope, true);
                    quadMap.get(null).add(slope);
                }
            }
        }
    }

    private boolean isStateInvalid() { return orientation.getAxis() == facing.getAxis(); }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedSlopedPrism.get().defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.UP)
                .setValue(PropertyHolder.ORIENTATION, Direction.WEST);
    }
}
