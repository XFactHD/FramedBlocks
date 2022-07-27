package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;
import java.util.Map;

public class FramedFloorModel extends FramedBlockModel
{
    public FramedFloorModel(BlockState state, BakedModel baseModel) { super(state, baseModel); }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (quad.getDirection() == Direction.UP)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.setPosition(1F/16F))
                    .export(quadMap.get(null));
        }
        else if (!Utils.isY(quad.getDirection()))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(false, 1F/16F))
                    .export(quadMap.get(quad.getDirection()));
        }
    }
}