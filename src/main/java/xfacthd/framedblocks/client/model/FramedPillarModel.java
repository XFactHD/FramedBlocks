package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.client.BakedQuadTransformer;
import xfacthd.framedblocks.api.util.client.ModelUtils;
import xfacthd.framedblocks.common.data.BlockType;

import java.util.List;
import java.util.Map;

public class FramedPillarModel extends FramedBlockModel
{
    private final Direction.Axis axis;
    private final float capStart;
    private final float capEnd;
    private final float sideCut;

    public FramedPillarModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        axis = state.getValue(BlockStateProperties.AXIS);

        IBlockType type = ((IFramedBlock)state.getBlock()).getBlockType();
        capStart = type == BlockType.FRAMED_POST ? (6F / 16F) : (4F / 16F);
        capEnd = type == BlockType.FRAMED_POST ? (10F / 16F) : (12F / 16F);
        sideCut = type == BlockType.FRAMED_POST ? (10F / 16F) : (12F / 16F);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        BakedQuad copy = ModelUtils.duplicateQuad(quad);
        if (createPillarQuad(copy, axis, capStart, capEnd, sideCut))
        {
            if (quad.getDirection().getAxis() == axis)
            {
                quadMap.get(quad.getDirection()).add(copy);
            }
            else
            {
                quadMap.get(null).add(copy);
            }
        }
    }

    public static boolean createPillarQuad(BakedQuad quad, Direction.Axis axis, float capStart, float capEnd, float sideCut)
    {
        if (quad.getDirection().getAxis() == axis)
        {
            if (axis == Direction.Axis.Y)
            {
                return BakedQuadTransformer.createTopBottomQuad(quad, capStart, capStart, capEnd, capEnd);
            }
            else
            {
                return BakedQuadTransformer.createSideQuad(quad, capStart, capStart, capEnd, capEnd);
            }
        }
        else
        {
            if (axis == Direction.Axis.Y)
            {
                if (BakedQuadTransformer.createVerticalSideQuad(quad, quad.getDirection().getClockWise(), sideCut) &&
                    BakedQuadTransformer.createVerticalSideQuad(quad, quad.getDirection().getCounterClockWise(), sideCut)
                )
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(quad, sideCut);
                    return true;
                }
            }
            else if (quad.getDirection().getAxis() == Direction.Axis.Y)
            {
                if (BakedQuadTransformer.createTopBottomQuad(quad, axisToDir(axis, true).getClockWise(), sideCut) &&
                    BakedQuadTransformer.createTopBottomQuad(quad, axisToDir(axis, false).getClockWise(), sideCut)
                )
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(quad, sideCut);
                    return true;
                }
            }
            else
            {
                if (BakedQuadTransformer.createHorizontalSideQuad(quad, true, sideCut) &&
                    BakedQuadTransformer.createHorizontalSideQuad(quad, false, sideCut)
                )
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(quad, sideCut);
                    return true;
                }
            }
        }
        return false;
    }

    private static Direction axisToDir(Direction.Axis axis, boolean positive)
    {
        return switch (axis)
        {
            case X -> positive ? Direction.EAST : Direction.WEST;
            case Y -> positive ? Direction.UP : Direction.DOWN;
            case Z -> positive ? Direction.SOUTH : Direction.NORTH;
        };
    }
}