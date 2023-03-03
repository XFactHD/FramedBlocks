package xfacthd.framedblocks.client.model.slopeslab;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.common.FBContent;

import java.util.List;
import java.util.Map;

public class FramedFlatElevatedSlopeSlabCornerModel extends FramedBlockModel
{
    private final Direction facing;
    private final boolean top;
    private final boolean ySlope;

    public FramedFlatElevatedSlopeSlabCornerModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
        this.top = state.getValue(FramedProperties.TOP);
        this.ySlope = state.getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction face = quad.getDirection();

        if (face == facing.getOpposite() || face == facing.getClockWise())
        {
            if (!ySlope)
            {
                boolean right = face == facing.getClockWise();
                float lenTop = top ? 1F : 0F;
                float lenBot = top ? 0F : 1F;

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(right, lenTop, lenBot))
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
                    .apply(Modifiers.cutTopBottom(facing.getClockWise(), 1, 0))
                    .apply(Modifiers.makeVerticalSlope(facing.getOpposite(), FramedSlopeSlabModel.SLOPE_ANGLE_VERT))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(facing.getOpposite(), 0, 1))
                    .apply(Modifiers.makeVerticalSlope(facing.getClockWise(), FramedSlopeSlabModel.SLOPE_ANGLE_VERT))
                    .export(quadMap.get(null));
        }
        else if (face == facing || face == facing.getCounterClockWise())
        {
            boolean rightFace = face == facing;
            float right = rightFace ? .5F : 1;
            float left =  rightFace ? 1 : .5F;

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(top, right, left))
                    .export(quadMap.get(face));
        }
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedFlatElevatedSlopeSlabCorner.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
