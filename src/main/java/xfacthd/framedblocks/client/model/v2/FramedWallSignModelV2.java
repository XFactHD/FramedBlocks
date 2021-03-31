package xfacthd.framedblocks.client.model.v2;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;

import java.util.List;
import java.util.Map;

public class FramedWallSignModelV2 extends FramedBlockModelV2
{
    private final Direction dir;

    public FramedWallSignModelV2(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.get(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (quad.getFace() == dir || quad.getFace() == dir.getOpposite())
        {
            BakedQuad faceQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(faceQuad, 0F, 4.5F/16F, 1F, 12.5F/16F))
            {
                if (quad.getFace() == dir)
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(faceQuad, 2F/16F);
                    quadMap.get(null).add(faceQuad);
                }
                else
                {
                    quadMap.get(quad.getFace()).add(faceQuad);
                }
            }
        }
        else if (quad.getFace() == Direction.UP || quad.getFace() == Direction.DOWN)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir, 2F/16F))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, quad.getFace() == Direction.UP ? 12.5F/16F : 11.5F/16F);
                quadMap.get(null).add(topBotQuad);
            }
        }
        else
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            boolean dirPositive = dir.getAxisDirection() == Direction.AxisDirection.POSITIVE;

            float minXZ = dirPositive ? 0F : 14F/16F;
            float maxXZ = dirPositive ? 2F/16F : 1F;

            if (BakedQuadTransformer.createSideQuad(sideQuad, minXZ, 4.5F/16F, maxXZ, 12.5F/16F))
            {
                quadMap.get(quad.getFace()).add(sideQuad);
            }
        }
    }
}