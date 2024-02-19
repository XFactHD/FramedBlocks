package xfacthd.framedblocks.client.model.slopeslab;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;
import java.util.Map;

public class FramedCompoundSlopeSlabModel extends FramedBlockModel
{
    private final Direction dir;
    private final boolean ySlope;

    public FramedCompoundSlopeSlabModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.dir = state.getValue(FramedProperties.FACING_HOR);
        this.ySlope = state.getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (quadDir == dir)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(true, .5F))
                    .export(quadMap.get(quadDir));

            if (!ySlope)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.makeVerticalSlope(false, FramedSlopeSlabModel.SLOPE_ANGLE))
                        .apply(Modifiers.offset(Direction.DOWN, .5F))
                        .export(quadMap.get(null));
            }
        }
        else if (quadDir == dir.getOpposite())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(false, .5F))
                    .export(quadMap.get(quadDir));

            if (!ySlope)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.makeVerticalSlope(true, FramedSlopeSlabModel.SLOPE_ANGLE))
                        .apply(Modifiers.offset(Direction.UP, .5F))
                        .export(quadMap.get(null));
            }
        }
        else if (ySlope && Utils.isY(quadDir))
        {
            Direction edge = quadDir == Direction.UP ? dir.getOpposite() : dir;
            QuadModifier.geometry(quad)
                    .apply(Modifiers.makeVerticalSlope(edge, FramedSlopeSlabModel.SLOPE_ANGLE_VERT))
                    .export(quadMap.get(null));
        }
        else if (quadDir.getAxis() == dir.getClockWise().getAxis())
        {
            boolean cw = quadDir == dir.getClockWise();
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(false, cw ? .5F : 1F, cw ? 1F : .5F))
                    .apply(Modifiers.cutSideUpDown(true, cw ? 1F : .5F, cw ? .5F : 1F))
                    .export(quadMap.get(null));
        }
    }
}
