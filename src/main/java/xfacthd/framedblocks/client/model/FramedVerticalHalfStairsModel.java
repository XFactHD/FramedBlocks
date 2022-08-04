package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;
import java.util.Map;

public class FramedVerticalHalfStairsModel extends FramedBlockModel
{
    private final Direction dir;
    private final boolean top;

    public FramedVerticalHalfStairsModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.dir = state.getValue(FramedProperties.FACING_HOR);
        this.top = state.getValue(FramedProperties.TOP);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (quadDir == dir.getOpposite() || quadDir == dir.getClockWise())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(quadDir == dir.getOpposite() ? dir.getClockWise() : dir.getOpposite(), .5F))
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(quadDir == dir.getOpposite() ? dir.getCounterClockWise() : dir, .5F))
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }
        else if (quadDir == dir || quadDir == dir.getCounterClockWise())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .export(quadMap.get(quadDir));
        }
        else if (Utils.isY(quadDir))
        {
            boolean inset = (quadDir == Direction.UP) != top;

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                    .applyIf(Modifiers.setPosition(.5F), inset)
                    .export(quadMap.get(inset ? null : quadDir));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir, .5F))
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                    .applyIf(Modifiers.setPosition(.5F), inset)
                    .export(quadMap.get(inset ? null : quadDir));
        }
    }
}
