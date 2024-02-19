package xfacthd.framedblocks.client.model.slopeedge;

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
import xfacthd.framedblocks.common.data.property.SlopeType;

import java.util.List;
import java.util.Map;

public class FramedElevatedSlopeEdgeModel extends FramedBlockModel
{
    private final Direction dir;
    private final SlopeType type;
    private final boolean ySlope;

    public FramedElevatedSlopeEdgeModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.dir = state.getValue(FramedProperties.FACING_HOR);
        this.type = state.getValue(PropertyHolder.SLOPE_TYPE);
        this.ySlope = state.getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (type == SlopeType.HORIZONTAL)
        {
            if (quadDir == dir.getOpposite())
            {
                if (!ySlope)
                {
                    QuadModifier.geometry(quad)
                            .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), .5F))
                            .apply(Modifiers.makeHorizontalSlope(false, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap.get(null));
                }

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), .5F))
                        .export(quadMap.get(quadDir));
            }
            else if (quadDir == dir.getClockWise())
            {
                if (ySlope)
                {
                    QuadModifier.geometry(quad)
                            .apply(Modifiers.cutSideLeftRight(dir, .5F))
                            .apply(Modifiers.makeHorizontalSlope(true, 45))
                            .apply(Modifiers.offset(dir.getClockWise(), .5F))
                            .export(quadMap.get(null));
                }

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), .5F))
                        .export(quadMap.get(quadDir));
            }
            else if (Utils.isY(quadDir))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .5F))
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), 1.5F, .5F))
                        .export(quadMap.get(quadDir));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                        .export(quadMap.get(quadDir));
            }
        }
        else
        {
            boolean top = type == SlopeType.TOP;
            if (quadDir == dir.getOpposite())
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(top, .5F))
                        .export(quadMap.get(quadDir));

                if (!ySlope)
                {
                    QuadModifier.geometry(quad)
                            .apply(Modifiers.cutSideUpDown(!top, .5F))
                            .apply(Modifiers.makeVerticalSlope(!top, 45))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .export(quadMap.get(null));
                }
            }
            else if ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                        .export(quadMap.get(quadDir));

                if (ySlope)
                {
                    QuadModifier.geometry(quad)
                            .apply(Modifiers.cutTopBottom(dir, .5F))
                            .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                            .apply(Modifiers.offset(top ? Direction.DOWN : Direction.UP, .5F))
                            .export(quadMap.get(null));
                }
            }
            else if (quadDir == dir.getClockWise() || quadDir == dir.getCounterClockWise())
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(!top, .5F))
                        .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), top ? 1.5F : .5F, top ? .5F : 1.5F))
                        .export(quadMap.get(quadDir));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(top, .5F))
                        .export(quadMap.get(quadDir));
            }
        }
    }
}
