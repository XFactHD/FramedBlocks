package xfacthd.framedblocks.client.model;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.client.BakedQuadTransformer;
import xfacthd.framedblocks.api.util.client.ModelUtils;

import java.util.List;
import java.util.Map;

public class FramedPanelModel extends FramedBlockModel
{
    private final Direction dir;

    public FramedPanelModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (quad.getDirection() == dir.getOpposite())
        {
            BakedQuad frontQuad = ModelUtils.duplicateQuad(quad);
            BakedQuadTransformer.setQuadPosInFacingDir(frontQuad, .5F);
            quadMap.get(null).add(frontQuad);
        }
        else if (Utils.isY(quad.getDirection()))
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), .5F))
            {
                quadMap.get(quad.getDirection()).add(topBotQuad);
            }
        }
        else
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            boolean dirPositive = Utils.isPositive(dir);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, dirPositive, .5F))
            {
                quadMap.get(quad.getDirection()).add(sideQuad);
            }
        }
    }
}