package xfacthd.framedblocks.client.model.slopeslab;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedFlatInnerSlopeSlabCornerModel extends FramedBlockModel
{
    private final Direction facing;
    private final boolean top;
    private final boolean topHalf;
    private final boolean ySlope;

    public FramedFlatInnerSlopeSlabCornerModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
        this.top = state.getValue(FramedProperties.TOP);
        this.topHalf = state.getValue(PropertyHolder.TOP_HALF);
        this.ySlope = state.getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    protected void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction face = quad.getDirection();
        boolean offset = top != topHalf;

        if (face == facing.getOpposite() || face == facing.getClockWise())
        {
            if (!ySlope)
            {
                boolean right = face != facing.getClockWise();
                float lenTop = top ? 0F : 1F;
                float lenBot = top ? 1F : 0F;

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(right, lenTop, lenBot))
                        .apply(Modifiers.makeVerticalSlope(!top, FramedSlopeSlabModel.SLOPE_ANGLE))
                        .applyIf(Modifiers.offset(top ? Direction.DOWN : Direction.UP, .5F), offset)
                        .export(quadMap.get(null));
            }

            boolean rightFace = face == facing.getClockWise();
            float lenRight = rightFace ? (offset ? .5F : 0) : (offset ? 1 : .5F);
            float lenLeft =  rightFace ? (offset ? 1 : .5F) : (offset ? .5F : 0);

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(top, lenRight, lenLeft))
                    .applyIf(Modifiers.cutSideUpDown(!top, .5F), offset)
                    .export(quadMap.get(face));
        }
        else if (ySlope && ((!top && face == Direction.UP) || (top && face == Direction.DOWN)))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(facing.getCounterClockWise(), 1, 0))
                    .apply(Modifiers.makeVerticalSlope(facing.getOpposite(), FramedSlopeSlabModel.SLOPE_ANGLE_VERT))
                    .applyIf(Modifiers.offset(top ? Direction.UP : Direction.DOWN, .5F), !offset)
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(facing, 0, 1))
                    .apply(Modifiers.makeVerticalSlope(facing.getClockWise(), FramedSlopeSlabModel.SLOPE_ANGLE_VERT))
                    .applyIf(Modifiers.offset(top ? Direction.UP : Direction.DOWN, .5F), !offset)
                    .export(quadMap.get(null));
        }
        else if (face == facing || face == facing.getCounterClockWise())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(topHalf, .5F))
                    .export(quadMap.get(face));
        }
        else if ((top && !topHalf && face == Direction.UP) || (!top && topHalf && face == Direction.DOWN))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }
    }

    @Override
    protected void applyInHandTransformation(PoseStack poseStack, ItemDisplayContext ctx)
    {
        poseStack.translate(0, .5, 0);
    }



    public static BlockState itemSource()
    {
        return FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
