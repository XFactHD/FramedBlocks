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

public class FramedWallSignModel extends FramedBlockModel
{
    private final Direction dir;

    public FramedWallSignModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (quad.getDirection() == dir || quad.getDirection() == dir.getOpposite())
        {
            BakedQuad faceQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(faceQuad, 0F, 4.5F/16F, 1F, 12.5F/16F))
            {
                if (quad.getDirection() == dir)
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(faceQuad, 2F/16F);
                    quadMap.get(null).add(faceQuad);
                }
                else
                {
                    quadMap.get(quad.getDirection()).add(faceQuad);
                }
            }
        }
        else if (quad.getDirection() == Direction.UP || quad.getDirection() == Direction.DOWN)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir, 2F/16F))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, quad.getDirection() == Direction.UP ? 12.5F/16F : 11.5F/16F);
                quadMap.get(null).add(topBotQuad);
            }
        }
        else
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            boolean dirPositive = Utils.isPositive(dir);

            float minXZ = dirPositive ? 0F : 14F/16F;
            float maxXZ = dirPositive ? 2F/16F : 1F;

            if (BakedQuadTransformer.createSideQuad(sideQuad, minXZ, 4.5F/16F, maxXZ, 12.5F/16F))
            {
                quadMap.get(quad.getDirection()).add(sideQuad);
            }
        }
    }
}