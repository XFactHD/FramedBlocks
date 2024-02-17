package xfacthd.framedblocks.client.model.pillar;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;

import java.util.List;
import java.util.Map;

public class FramedThreewayCornerPillarModel extends FramedBlockModel
{
    private final Direction dir;
    private final boolean top;

    public FramedThreewayCornerPillarModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.dir = state.getValue(FramedProperties.FACING_HOR);
        this.top = state.getValue(FramedProperties.TOP);
    }

    @Override
    public void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if ((!top && quadDir == Direction.DOWN) || (top && quadDir == Direction.UP))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                    .apply(Modifiers.cutTopBottom(dir, .5F))
                    .export(quadMap.get(quadDir));
        }
        else if ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir, .5F))
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                    .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(quadDir));
        }
        else if (quadDir == dir)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), .5F))
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .export(quadMap.get(quadDir));
        }
        else if (quadDir == dir.getCounterClockWise())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir, .5F))
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .export(quadMap.get(quadDir));
        }
        else if (quadDir == dir.getOpposite())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), .5F))
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), .5F))
                    .apply(Modifiers.cutSideUpDown(!top, .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), .5F))
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }
        else if (quadDir == dir.getClockWise())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .5F))
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .5F))
                    .apply(Modifiers.cutSideUpDown(!top, .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir, .5F))
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }
    }
}
