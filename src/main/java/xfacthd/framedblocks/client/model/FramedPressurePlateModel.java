package xfacthd.framedblocks.client.model;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;

import java.util.List;
import java.util.Map;

public class FramedPressurePlateModel extends FramedBlockModel
{
    private final boolean pressed;

    public FramedPressurePlateModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        pressed = state.getValue(BlockStateProperties.POWERED);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (quad.getDirection() == Direction.UP)
        {
            BakedQuad topQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topQuad, 1F/16F, 1F/16F, 15F/16F, 15F/16F))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(topQuad, pressed ? .5F / 16F : 1F / 16F);
                quadMap.get(null).add(topQuad);
            }
        }
        else if (quad.getDirection() != Direction.DOWN)
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