package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.client.BakedQuadTransformer;
import xfacthd.framedblocks.api.util.client.ModelUtils;

import java.util.List;
import java.util.Map;

public class FramedHalfPillarModel extends FramedBlockModel
{
    private final Direction face;

    public FramedHalfPillarModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        face = state.getValue(BlockStateProperties.FACING);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        BakedQuad copy = ModelUtils.duplicateQuad(quad);
        if (FramedPillarModel.createPillarQuad(copy, face.getAxis(), 4F / 16F, 12F / 16F, 12F / 16F))
        {
            if (quad.getDirection() == face)
            {
                quadMap.get(face).add(copy);
            }
            else if (quad.getDirection() == face.getOpposite())
            {
                BakedQuadTransformer.setQuadPosInFacingDir(copy, .5F);
                quadMap.get(null).add(copy);
            }
            else if (Utils.isY(face))
            {
                if (BakedQuadTransformer.createHorizontalSideQuad(copy, face == Direction.UP, .5F))
                {
                    quadMap.get(null).add(copy);
                }
            }
            else if (Utils.isY(quad.getDirection()))
            {
                if (BakedQuadTransformer.createTopBottomQuad(copy, face.getOpposite(), .5F))
                {
                    quadMap.get(null).add(copy);
                }
            }
            else
            {
                if (BakedQuadTransformer.createVerticalSideQuad(copy, Utils.isPositive(face), .5F))
                {
                    quadMap.get(null).add(copy);
                }
            }
        }
    }
}