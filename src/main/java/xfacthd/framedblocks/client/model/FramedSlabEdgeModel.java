package xfacthd.framedblocks.client.model;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;
import java.util.Map;

public class FramedSlabEdgeModel extends FramedBlockModel
{
    private final Direction dir;
    private final boolean top;

    public FramedSlabEdgeModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.getValue(FramedProperties.FACING_HOR);
        top = state.getValue(FramedProperties.TOP);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, final BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (Utils.isY(quadDir))
        {
            boolean inset = (quadDir == Direction.DOWN) == top;

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                    .applyIf(Modifiers.setPosition(.5F), inset)
                    .export(quadMap.get(inset ? null : quadDir));
        }
        else
        {
            boolean inset = quadDir == dir.getOpposite();
            boolean side = quadDir.getAxis() != dir.getAxis();

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .applyIf(Modifiers.cutSideLeftRight(dir.getOpposite(), .5F), side)
                    .applyIf(Modifiers.setPosition(.5F), inset)
                    .export(quadMap.get(inset ? null : quadDir));
        }
    }
}