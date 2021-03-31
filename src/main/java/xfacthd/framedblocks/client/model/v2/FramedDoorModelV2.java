package xfacthd.framedblocks.client.model.v2;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.properties.*;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;

import java.util.List;
import java.util.Map;

public class FramedDoorModelV2 extends FramedBlockModelV2
{
    private final Direction dir;
    private final boolean top;
    private final boolean hingeRight;
    private final boolean open;

    public FramedDoorModelV2(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.get(BlockStateProperties.HORIZONTAL_FACING);
        top = state.get(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER;
        hingeRight = state.get(BlockStateProperties.DOOR_HINGE) == DoorHingeSide.RIGHT;
        open = state.get(BlockStateProperties.OPEN);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction faceDir = dir;
        if (open) { faceDir = hingeRight ? faceDir.rotateYCCW() : faceDir.rotateY(); }
        boolean facePositive = faceDir.getAxisDirection() == Direction.AxisDirection.POSITIVE;

        if ((top && quad.getFace() == Direction.UP) || (!top && quad.getFace() == Direction.DOWN))
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, faceDir, 3F/16F))
            {
                quadMap.get(quad.getFace()).add(topBotQuad);
            }
        }
        else if (quad.getFace().getAxis() != Direction.Axis.Y)
        {
            if (quad.getFace() == faceDir)
            {
                BakedQuad faceQuad = ModelUtils.duplicateQuad(quad);
                BakedQuadTransformer.setQuadPosInFacingDir(faceQuad, 3F/16F);
                quadMap.get(null).add(faceQuad);
            }
            else
            {
                BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, !facePositive, 3F/16F))
                {
                    quadMap.get(quad.getFace()).add(sideQuad);
                }
            }
        }
    }
}