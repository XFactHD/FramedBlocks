package xfacthd.framedblocks.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;

import java.util.List;
import java.util.Map;

public class FramedPyramidModel extends FramedBlockModel
{
    private final Direction facing;

    public FramedPyramidModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(BlockStateProperties.FACING);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (Utils.isY(facing))
        {
            if (quadDir.getAxis() == facing.getAxis()) { return; }

            boolean up = facing == Direction.UP;
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(false, up ? .5F : 1, up ? 1 : .5F))
                    .apply(Modifiers.cutSideLeftRight(true, up ? .5F : 1, up ? 1 : .5F))
                    .apply(Modifiers.makeVerticalSlope(up, FramedSlopePanelModel.SLOPE_ANGLE))
                    .export(quadMap.get(null));
        }
        else
        {
            if (quadDir.getAxis() == facing.getAxis())
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(facing.getClockWise(), 1, .5F))
                        .apply(Modifiers.cutSideLeftRight(facing.getCounterClockWise(), 1, .5F))
                        .apply(Modifiers.makeVerticalSlope(true, FramedSlopeSlabModel.SLOPE_ANGLE))
                        .apply(Modifiers.offset(Direction.UP, .5F))
                        .export(quadMap.get(null));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(facing.getClockWise(), .5F, 1))
                        .apply(Modifiers.cutSideLeftRight(facing.getCounterClockWise(), .5F, 1))
                        .apply(Modifiers.makeVerticalSlope(false, FramedSlopeSlabModel.SLOPE_ANGLE))
                        .apply(Modifiers.offset(Direction.DOWN, .5F))
                        .export(quadMap.get(null));
            }
            else if (!Utils.isY(quadDir))
            {
                boolean right = quadDir == facing.getClockWise();
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(true, right ? 1 : .5F, right ? .5F : 1))
                        .apply(Modifiers.cutSideUpDown(false, right ? 1 : .5F, right ? .5F : 1))
                        .apply(Modifiers.makeHorizontalSlope(!right, FramedSlopePanelModel.SLOPE_ANGLE))
                        .export(quadMap.get(null));
            }
        }
    }

    @Override
    protected void applyInHandTransformation(PoseStack poseStack, ItemTransforms.TransformType type)
    {
        poseStack.translate(0, .5, 0);
    }



    public static BlockState itemSource() { return FBContent.blockFramedPyramid.get().defaultBlockState(); }
}
