package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.BlockType;

import java.util.List;
import java.util.Map;

public class FramedPillarModel extends FramedBlockModel
{
    private final Direction.Axis axis;
    private final float capStart;
    private final float capEnd;
    private final float sideCut;

    public FramedPillarModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        axis = state.get(BlockStateProperties.AXIS);

        BlockType type = ((IFramedBlock)state.getBlock()).getBlockType();
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
            if (quad.getFace().getAxis() == axis)
            {
                quadMap.get(quad.getFace()).add(copy);
            }
            else
            {
                quadMap.get(null).add(copy);
            }
        }
    }

    public static boolean createPillarQuad(BakedQuad quad, Direction.Axis axis, float capStart, float capEnd, float sideCut)
    {
        if (quad.getFace().getAxis() == axis)
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
                if (BakedQuadTransformer.createVerticalSideQuad(quad, quad.getFace().rotateY(), sideCut) &&
                    BakedQuadTransformer.createVerticalSideQuad(quad, quad.getFace().rotateYCCW(), sideCut)
                )
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(quad, sideCut);
                    return true;
                }
            }
            else if (quad.getFace().getAxis() == Direction.Axis.Y)
            {
                if (BakedQuadTransformer.createTopBottomQuad(quad, axisToDir(axis, true).rotateY(), sideCut) &&
                    BakedQuadTransformer.createTopBottomQuad(quad, axisToDir(axis, false).rotateY(), sideCut)
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
        switch (axis)
        {
            case X: return positive ? Direction.EAST : Direction.WEST;
            case Y: return positive ? Direction.UP : Direction.DOWN;
            case Z: return positive ? Direction.SOUTH : Direction.NORTH;
            default: throw new IllegalArgumentException("Invalid axis!");
        }
    }
}