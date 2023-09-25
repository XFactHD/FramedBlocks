package xfacthd.framedblocks.client.model.slopepanelcorner;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.client.model.slopepanel.FramedSlopePanelModel;
import xfacthd.framedblocks.common.FBContent;

public class FramedExtendedInnerCornerSlopePanelModel extends FramedBlockModel
{
    private final Direction dir;
    private final boolean top;
    private final boolean ySlope;

    public FramedExtendedInnerCornerSlopePanelModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.dir = state.getValue(FramedProperties.FACING_HOR);
        this.top = state.getValue(FramedProperties.TOP);
        this.ySlope = state.getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    protected void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (quadDir == dir || quadDir == dir.getCounterClockWise())
        {
            Direction cutDir = quadDir == dir ? dir.getClockWise() : dir.getOpposite();
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(cutDir.getOpposite(), top ? 1F : .5F, top ? .5F : 1F))
                    .export(quadMap.get(quadDir));

            if (!ySlope)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(cutDir, top ? 0F : .5F, top ? .5F : 0F))
                        .apply(Modifiers.makeVerticalSlope(!top, FramedSlopePanelModel.SLOPE_ANGLE))
                        .export(quadMap.get(null));
            }
        }
        else if ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN))
        {
            if (ySlope)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), 0, .5F))
                        .apply(Modifiers.makeVerticalSlope(dir.getCounterClockWise(), FramedSlopePanelModel.SLOPE_ANGLE_VERT))
                        .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                        .export(quadMap.get(null));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F, 0))
                        .apply(Modifiers.makeVerticalSlope(dir, FramedSlopePanelModel.SLOPE_ANGLE_VERT))
                        .apply(Modifiers.offset(dir, .5F))
                        .export(quadMap.get(null));
            }

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir, .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                    .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .5F))
                    .export(quadMap.get(quadDir));
        }
    }

    @Override
    protected void applyInHandTransformation(PoseStack poseStack, ItemDisplayContext ctx)
    {
        //poseStack.mulPose(Quaternions.YP_90);
    }



    public static BlockState itemModelSource()
    {
        return FBContent.BLOCK_FRAMED_EXTENDED_INNER_CORNER_SLOPE_PANEL.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.EAST);
    }
}
