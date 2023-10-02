package xfacthd.framedblocks.client.model.slopepanelcorner;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.render.Quaternions;
import xfacthd.framedblocks.client.model.slopepanel.FramedSlopePanelGeometry;
import xfacthd.framedblocks.common.FBContent;

public class FramedSmallInnerCornerSlopePanelGeometry implements Geometry
{
    private final Direction dir;
    private final boolean top;
    private final boolean ySlope;

    public FramedSmallInnerCornerSlopePanelGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (quadDir == dir || quadDir == dir.getCounterClockWise())
        {
            Direction cutDir = quadDir == dir ? dir.getClockWise() : dir.getOpposite();
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(cutDir.getOpposite(), top ? 1F : .5F, top ? .5F : 1F))
                    .apply(Modifiers.cutSideLeftRight(cutDir, .5F))
                    .export(quadMap.get(quadDir));

            if (!ySlope)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(cutDir, top ? 0F : .5F, top ? .5F : 0F))
                        .apply(Modifiers.makeVerticalSlope(!top, FramedSlopePanelGeometry.SLOPE_ANGLE))
                        .export(quadMap.get(null));
            }
        }
        else if (quadDir == dir.getOpposite() || quadDir == dir.getClockWise())
        {
            Direction cutDir = quadDir == dir.getOpposite() ? dir.getClockWise() : dir.getOpposite();
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(cutDir, .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }
        else if (ySlope && ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN)))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), 0, .5F))
                    .apply(Modifiers.makeVerticalSlope(dir.getCounterClockWise(), FramedSlopePanelGeometry.SLOPE_ANGLE_VERT))
                    .apply(Modifiers.offset(dir.getCounterClockWise(), .5F))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F, 0))
                    .apply(Modifiers.makeVerticalSlope(dir, FramedSlopePanelGeometry.SLOPE_ANGLE_VERT))
                    .apply(Modifiers.offset(dir, .5F))
                    .export(quadMap.get(null));
        }
        else if ((!top && quadDir == Direction.DOWN) || (top && quadDir == Direction.UP))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                    .export(quadMap.get(quadDir));
        }
    }

    @Override
    public void applyInHandTransformation(PoseStack poseStack, ItemDisplayContext ctx)
    {
        poseStack.mulPose(Quaternions.YP_90);
    }



    public static BlockState itemModelSource()
    {
        return FBContent.BLOCK_FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.EAST);
    }
}
