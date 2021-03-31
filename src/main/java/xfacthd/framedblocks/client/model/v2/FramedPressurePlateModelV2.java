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

public class FramedPressurePlateModelV2 extends FramedBlockModelV2
{
    private final boolean pressed;

    public FramedPressurePlateModelV2(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        pressed = state.get(BlockStateProperties.POWERED);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (quad.getFace() == Direction.UP)
        {
            BakedQuad topQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topQuad, 1F/16F, 1F/16F, 15F/16F, 15F/16F))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(topQuad, pressed ? .5F / 16F : 1F / 16F);
                quadMap.get(null).add(topQuad);
            }
        }
        else if (quad.getFace() != Direction.DOWN)
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(sideQuad, 1F/16F, 0F, 15F/16F, pressed ? .5F/16F : 1F/16F))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 15F/16F);
                quadMap.get(null).add(sideQuad);
            }
        }
    }
}