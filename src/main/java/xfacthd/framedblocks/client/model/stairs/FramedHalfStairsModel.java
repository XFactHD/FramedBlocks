package xfacthd.framedblocks.client.model.stairs;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedHalfStairsModel extends FramedBlockModel
{
    private final Direction dir;
    private final boolean top;
    private final boolean right;

    public FramedHalfStairsModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.getValue(FramedProperties.FACING_HOR);
        top = state.getValue(FramedProperties.TOP);
        right = state.getValue(PropertyHolder.RIGHT);
    }

    @Override
    protected void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction face = quad.getDirection();
        Direction vertCut = right ? dir.getCounterClockWise() : dir.getClockWise();

        if (face == dir)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(vertCut, .5F))
                    .export(quadMap.get(face));
        }
        else if (face == dir.getOpposite())
        {
            QuadModifier mod = QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(vertCut, .5F));

            mod.derive().apply(Modifiers.cutSideUpDown(!top, .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));

            mod.apply(Modifiers.cutSideUpDown(top, .5F))
                    .export(quadMap.get(face));
        }
        else if (!Utils.isY(face) && face.getAxis() != dir.getAxis())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .5F))
                    .applyIf(Modifiers.setPosition(.5F), face == vertCut)
                    .export(quadMap.get(face == vertCut ? null : face));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir, .5F))
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .applyIf(Modifiers.setPosition(.5F), face == vertCut)
                    .export(quadMap.get(face == vertCut ? null : face));
        }
        else if (Utils.isY(face))
        {
            boolean base = (face == Direction.UP && top) || (face == Direction.DOWN && !top);

            QuadModifier mod = QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(vertCut, .5F));

            if (!base)
            {
                mod.derive().apply(Modifiers.cutTopBottom(dir, .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap.get(null));
            }

            mod.applyIf(Modifiers.cutTopBottom(dir.getOpposite(), .5F), !base)
                    .export(quadMap.get(face));
        }
    }
}
