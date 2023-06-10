package xfacthd.framedblocks.client.model.slope;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;

import java.util.List;
import java.util.Map;

public class FramedPrismCornerModel extends FramedBlockModel
{
    private final Direction dir;
    private final boolean top;
    private final boolean offset;
    private final boolean ySlope;

    public FramedPrismCornerModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.dir = state.getValue(FramedProperties.FACING_HOR);
        this.top = state.getValue(FramedProperties.TOP);
        this.offset = state.getValue(FramedProperties.OFFSET);
        this.ySlope = state.getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if ((quadDir == Direction.UP && top) || (quadDir == Direction.DOWN && !top))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), 1, 0))
                    .export(quadMap.get(quadDir));
        }
        else if (quadDir == dir || quadDir == dir.getCounterClockWise())
        {
            Direction cutDir = quadDir == dir ? dir.getClockWise() : dir.getOpposite();
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(cutDir, top ? 1 : 0, top ? 0 : 1))
                    .export(quadMap.get(quadDir));
        }
        else if (!ySlope && quadDir == dir.getOpposite())
        {
            if (offset)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), .5F))
                        .apply(Modifiers.offset(dir.getClockWise(), .5F))
                        .apply(Modifiers.cutPrismTriangle(!top, true))
                        .export(quadMap.get(null));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.cutPrismTriangle(!top, true))
                        .export(quadMap.get(null));
            }
            else
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutPrismTriangle(!top, true))
                        .export(quadMap.get(null));
            }
        }
        else if (ySlope && ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN)))
        {
            if (offset)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                        .apply(Modifiers.offset(dir.getClockWise(), .5F))
                        .apply(Modifiers.cutPrismTriangle(dir, true))
                        .export(quadMap.get(null));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.cutPrismTriangle(dir, true))
                        .export(quadMap.get(null));
            }
            else
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutPrismTriangle(dir, true))
                        .export(quadMap.get(null));
            }
        }
    }



    public static BlockState itemSource()
    {
        return FBContent.BLOCK_FRAMED_PRISM_CORNER.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}