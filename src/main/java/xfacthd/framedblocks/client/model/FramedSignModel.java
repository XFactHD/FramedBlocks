package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;

import java.util.List;
import java.util.Map;

public class FramedSignModel extends FramedBlockModel
{
    private static final float Y_OFF = 1.75F/16F;
    private final Direction dir;
    private final float rotDegrees;

    public FramedSignModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        int rotation = state.get(BlockStateProperties.ROTATION_0_15);
        dir = Direction.byHorizontalIndex(rotation / 4);
        rotDegrees = (float)(rotation % 4) * -22.5F;
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (quad.getFace() == dir || quad.getFace() == dir.getOpposite())
        {
            BakedQuad faceQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createHorizontalSideQuad(faceQuad, true, .5F))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(faceQuad, 9F/16F);
                BakedQuadTransformer.offsetQuadInDir(faceQuad, Direction.UP, Y_OFF);
                BakedQuadTransformer.rotateQuadAroundAxisCentered(faceQuad, Direction.Axis.Y, rotDegrees, false);
                quadMap.get(null).add(faceQuad);
            }
        }
        else if (quad.getFace().getAxis() == Direction.Axis.Y)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir, 9F/16F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), 9F/16F)
            )
            {
                if (quad.getFace() == Direction.DOWN) { BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, .5F); }
                BakedQuadTransformer.offsetQuadInDir(topBotQuad, Direction.UP, Y_OFF);
                BakedQuadTransformer.rotateQuadAroundAxisCentered(topBotQuad, Direction.Axis.Y, rotDegrees, false);
                quadMap.get(null).add(topBotQuad);
            }
        }
        else
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(sideQuad, 7F/16F, .5F, 9F/16F, 1F))
            {
                BakedQuadTransformer.offsetQuadInDir(sideQuad, Direction.UP, Y_OFF);
                BakedQuadTransformer.rotateQuadAroundAxisCentered(sideQuad, Direction.Axis.Y, rotDegrees, false);
                quadMap.get(null).add(sideQuad);
            }
        }

        if (quad.getFace().getAxis() != Direction.Axis.Y)
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(sideQuad, 7F/16F, 0F, 9F/16F, 9.75F/16F))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 9F/16F);
                BakedQuadTransformer.rotateQuadAroundAxisCentered(sideQuad, Direction.Axis.Y, rotDegrees, false);
                quadMap.get(null).add(sideQuad);
            }
        }
        else if (quad.getFace() == Direction.DOWN)
        {
            BakedQuad botQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(botQuad, 7F/16F, 7F/16F, 9F/16F, 9F/16F))
            {
                BakedQuadTransformer.rotateQuadAroundAxisCentered(botQuad, Direction.Axis.Y, rotDegrees, false);
                quadMap.get(Direction.DOWN).add(botQuad);
            }
        }
    }
}