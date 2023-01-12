package xfacthd.framedblocks.client.model.slopeslab;

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

public class FramedFlatElevatedInnerSlopeSlabCornerModel extends FramedBlockModel
{
    private final Direction facing;
    private final boolean top;

    public FramedFlatElevatedInnerSlopeSlabCornerModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
        this.top = state.getValue(FramedProperties.TOP);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction face = quad.getDirection();

        if (face == facing.getOpposite() || face == facing.getClockWise())
        {
            boolean right = face != facing.getClockWise();
            float lenTop = top ? 0F : 1F;
            float lenBot = top ? 1F : 0F;

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(right, lenTop, lenBot))
                    .apply(Modifiers.makeVerticalSlope(!top, FramedSlopeSlabModel.SLOPE_ANGLE))
                    .apply(Modifiers.offset(top ? Direction.DOWN : Direction.UP, .5F))
                    .export(quadMap.get(null));

            boolean rightFace = face == facing.getOpposite();
            float lenRight = rightFace ? 1 : .5F;
            float lenLeft =  rightFace ? .5F : 1;

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(top, lenRight, lenLeft))
                    .export(quadMap.get(face));
        }
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedFlatElevatedInnerSlopeSlabCorner.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
