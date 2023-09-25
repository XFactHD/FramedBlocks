package xfacthd.framedblocks.client.model.slopeslab;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;

public class FramedElevatedSlopeSlabModel extends FramedBlockModel
{
    private final Direction facing;
    private final boolean top;
    private final boolean ySlope;

    public FramedElevatedSlopeSlabModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
        this.top = state.getValue(FramedProperties.TOP);
        this.ySlope = state.getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    protected void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction face = quad.getDirection();
        if (face == facing.getOpposite())
        {
            if (!ySlope)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.makeVerticalSlope(!top, FramedSlopeSlabModel.SLOPE_ANGLE))
                        .apply(Modifiers.offset(top ? Direction.DOWN : Direction.UP, .5F))
                        .export(quadMap.get(null));
            }

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(top, .5F))
                    .export(quadMap.get(face));
        }
        else if (ySlope && ((!top && face == Direction.UP) || (top && face == Direction.DOWN)))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.makeVerticalSlope(facing.getOpposite(), FramedSlopeSlabModel.SLOPE_ANGLE_VERT))
                    .export(quadMap.get(null));
        }
        else if (face == facing.getClockWise() || face == facing.getCounterClockWise())
        {
            boolean rightFace = face == facing.getClockWise();
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(top, rightFace ? .5F : 1, rightFace ? 1 : .5F))
                    .export(quadMap.get(face));
        }
    }



    public static BlockState itemSource()
    {
        return FBContent.BLOCK_FRAMED_ELEVATED_SLOPE_SLAB.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
