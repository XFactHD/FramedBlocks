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

public class FramedSlabCornerModel extends FramedBlockModel
{
    private final Direction dir;
    private final boolean top;

    public FramedSlabCornerModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.getValue(FramedProperties.FACING_HOR);
        top = state.getValue(FramedProperties.TOP);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (Utils.isY(quadDir))
        {
            boolean inset = (!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN);

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                    .applyIf(Modifiers.setPosition(.5F), inset)
                    .export(quadMap.get(inset ? null : quadDir));
        }
        else
        {
            Direction cutDir = quadDir.getAxis() == dir.getAxis() ? dir.getClockWise() : dir.getOpposite();
            boolean inset = quadDir == dir.getOpposite() || quadDir == dir.getClockWise();

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(cutDir, .5F))
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .applyIf(Modifiers.setPosition(.5F), inset)
                    .export(quadMap.get(inset ? null : quadDir));
        }
    }
}