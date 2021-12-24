package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.util.client.BakedQuadTransformer;
import xfacthd.framedblocks.api.util.client.ModelUtils;

import java.util.List;
import java.util.Map;

public class FramedDoorModel extends FramedBlockModel
{
    private final Direction dir;
    private final boolean hingeRight;
    private final boolean open;

    public FramedDoorModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        hingeRight = state.getValue(BlockStateProperties.DOOR_HINGE) == DoorHingeSide.RIGHT;
        open = state.getValue(BlockStateProperties.OPEN);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction faceDir = dir;
        if (open) { faceDir = hingeRight ? faceDir.getCounterClockWise() : faceDir.getClockWise(); }
        boolean facePositive = faceDir.getAxisDirection() == Direction.AxisDirection.POSITIVE;

        if (quad.getDirection().getAxis() == Direction.Axis.Y)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, faceDir, 3F/16F))
            {
                quadMap.get(quad.getDirection()).add(topBotQuad);
            }
        }
        else
        {
            if (quad.getDirection() == faceDir)
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
                    quadMap.get(quad.getDirection()).add(sideQuad);
                }
            }
        }
    }
}