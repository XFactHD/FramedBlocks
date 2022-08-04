package xfacthd.framedblocks.client.model;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;
import java.util.Map;

public class FramedTrapDoorModel extends FramedBlockModel
{
    private static final float DEPTH = 3F/16F;

    private final Direction dir;
    private final boolean top;
    private final boolean open;

    public FramedTrapDoorModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        top = state.getValue(BlockStateProperties.HALF) == Half.TOP;
        open = state.getValue(BlockStateProperties.OPEN);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (open)
        {
            if (quadDir == dir)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.setPosition(DEPTH))
                        .export(quadMap.get(null));
            }
            else if (Utils.isY(quadDir))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(dir, DEPTH))
                        .export(quadMap.get(quadDir));
            }
            else
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(dir, DEPTH))
                        .export(quadMap.get(quadDir));
            }
        }
        else
        {
            if ((top && quad.getDirection() == Direction.DOWN) || (!top && quad.getDirection() == Direction.UP))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.setPosition(DEPTH))
                        .export(quadMap.get(null));
            }
            else if (!Utils.isY(quad.getDirection()))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(top, DEPTH))
                        .export(quadMap.get(quadDir));
            }
        }
    }
}