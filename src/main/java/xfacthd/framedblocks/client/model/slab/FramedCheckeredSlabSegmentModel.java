package xfacthd.framedblocks.client.model.slab;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;
import java.util.Map;

public class FramedCheckeredSlabSegmentModel extends FramedBlockModel
{
    private final boolean top;
    private final boolean second;

    public FramedCheckeredSlabSegmentModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.top = state.getValue(FramedProperties.TOP);
        this.second = state.getValue(PropertyHolder.SECOND);
    }

    @Override
    public void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (Utils.isY(quadDir))
        {
            boolean up = quadDir == Direction.UP;
            Direction xDir = (second ^ up) ? Direction.WEST : Direction.EAST;

            if (up == top)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(Direction.SOUTH, .5F))
                        .apply(Modifiers.cutTopBottom(xDir, .5F))
                        .export(quadMap.get(quadDir));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(Direction.NORTH, .5F))
                        .apply(Modifiers.cutTopBottom(xDir.getOpposite(), .5F))
                        .export(quadMap.get(quadDir));
            }
            else
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(Direction.SOUTH, .5F))
                        .apply(Modifiers.cutTopBottom(xDir.getOpposite(), .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap.get(null));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(Direction.NORTH, .5F))
                        .apply(Modifiers.cutTopBottom(xDir, .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap.get(null));
            }
        }
        else
        {
            Direction horDir = Utils.isX(quadDir) ^ second ? quadDir.getCounterClockWise() : quadDir.getClockWise();

            if (!top)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(false, .5F))
                        .apply(Modifiers.cutSideLeftRight(horDir, .5F))
                        .export(quadMap.get(quadDir));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(false, .5F))
                        .apply(Modifiers.cutSideLeftRight(horDir.getOpposite(), .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap.get(null));
            }
            else
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(true, .5F))
                        .apply(Modifiers.cutSideLeftRight(horDir.getOpposite(), .5F))
                        .export(quadMap.get(quadDir));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(true, .5F))
                        .apply(Modifiers.cutSideLeftRight(horDir, .5F))
                        .apply(Modifiers.setPosition(.5F))
                        .export(quadMap.get(null));
            }
        }
    }
}
