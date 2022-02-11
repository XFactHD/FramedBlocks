package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.model.FramedBlockModel;
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

    public FramedSlopedPrismModel(BakedModel baseModel)
    {
        this(
                FBContent.blockFramedSlopedPrism.get().defaultBlockState()
                        .setValue(BlockStateProperties.FACING, Direction.UP)
                        .setValue(PropertyHolder.ORIENTATION, Direction.WEST),
                baseModel
        );
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadFace = quad.getDirection();
        if (quadFace == orientation.getOpposite() && orientation.getAxis() != Direction.Axis.Y)
        {
            BakedQuad triangle = ModelUtils.duplicateQuad(quad);
            if (facing.getAxis() == Direction.Axis.Y)
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
        else if (quadFace == facing && orientation.getAxis() == Direction.Axis.Y)
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
            if (facing.getAxis() == Direction.Axis.Y)
            {
                triDir = facing == Direction.UP ? TriangleDirection.UP : TriangleDirection.DOWN;
            }
            else if (orientation.getAxis() != Direction.Axis.Y)
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
        else if (facing.getAxis() == Direction.Axis.Y && quadFace.getAxis() != facing.getAxis() && quadFace.getAxis() != orientation.getAxis())
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
        else if (orientation.getAxis() == Direction.Axis.Y && quadFace.getAxis() != facing.getAxis() && quadFace.getAxis() != orientation.getAxis())
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
        else if (orientation.getAxis() != Direction.Axis.Y && facing.getAxis() != Direction.Axis.Y && quadFace == facing)
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
}
