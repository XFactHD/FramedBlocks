package xfacthd.framedblocks.client.model;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.client.BakedQuadTransformer;
import xfacthd.framedblocks.api.util.client.ModelUtils;

import java.util.List;
import java.util.Map;

public class FramedSignModel extends FramedBlockModel
{
    private static final float Y_OFF = 1.75F/16F;
    private final Direction dir;
    private final float rotDegrees;

    public FramedSignModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        int rotation = state.getValue(BlockStateProperties.ROTATION_16);
        dir = Direction.from2DDataValue(rotation / 4);
        rotDegrees = (float)(rotation % 4) * -22.5F;
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (quad.getDirection() == dir || quad.getDirection() == dir.getOpposite())
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
        else if (Utils.isY(quad.getDirection()))
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir, 9F/16F) &&
                BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), 9F/16F)
            )
            {
                if (quad.getDirection() == Direction.DOWN) { BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, .5F); }
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

        if (!Utils.isY(quad.getDirection()))
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(sideQuad, 7F/16F, 0F, 9F/16F, 9.75F/16F))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 9F/16F);
                BakedQuadTransformer.rotateQuadAroundAxisCentered(sideQuad, Direction.Axis.Y, rotDegrees, false);
                quadMap.get(null).add(sideQuad);
            }
        }
        else if (quad.getDirection() == Direction.DOWN)
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