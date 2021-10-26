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

public class FramedHalfPillarModel extends FramedBlockModel
{
    private final Direction face;

    public FramedHalfPillarModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        face = state.get(BlockStateProperties.FACING);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        BakedQuad copy = ModelUtils.duplicateQuad(quad);
        if (FramedPillarModel.createPillarQuad(copy, face.getAxis(), 4F / 16F, 12F / 16F, 12F / 16F))
        {
            if (quad.getFace() == face)
            {
                quadMap.get(face).add(copy);
            }
            else if (quad.getFace() == face.getOpposite())
            {
                BakedQuadTransformer.setQuadPosInFacingDir(copy, .5F);
                quadMap.get(null).add(copy);
            }
            else if (face.getAxis() == Direction.Axis.Y)
            {
                if (BakedQuadTransformer.createHorizontalSideQuad(copy, face == Direction.UP, .5F))
                {
                    quadMap.get(null).add(copy);
                }
            }
            else if (quad.getFace().getAxis() == Direction.Axis.Y)
            {
                if (BakedQuadTransformer.createTopBottomQuad(copy, face.getOpposite(), .5F))
                {
                    quadMap.get(null).add(copy);
                }
            }
            else
            {
                if (BakedQuadTransformer.createVerticalSideQuad(copy, face.getAxisDirection() == Direction.AxisDirection.POSITIVE, .5F))
                {
                    quadMap.get(null).add(copy);
                }
            }
        }
    }
}