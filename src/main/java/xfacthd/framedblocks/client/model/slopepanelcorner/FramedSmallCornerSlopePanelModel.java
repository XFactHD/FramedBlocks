package xfacthd.framedblocks.client.model.slopepanelcorner;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.client.model.slopepanel.FramedSlopePanelModel;
import xfacthd.framedblocks.common.FBContent;

public class FramedSmallCornerSlopePanelModel extends FramedBlockModel
{
    private static final Vector3f ORIGIN_BOTTOM = new Vector3f(.5F, 0, .5F);
    private static final Vector3f ORIGIN_TOP = new Vector3f(.5F, 1, .5F);

    private final Direction dir;
    private final boolean top;
    private final boolean ySlope;

    public FramedSmallCornerSlopePanelModel(BlockState state, BakedModel baseModel)
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
                    .apply(Modifiers.cutSideLeftRight(cutDir, top ? .5F : 0F, top ? 0F : .5F))
                    .export(quadMap.get(quadDir));
        }
        else if (!ySlope && (quadDir == dir.getOpposite() || quadDir == dir.getClockWise()))
        {
            Direction cutDir = quadDir == dir.getOpposite() ? dir.getClockWise() : dir.getOpposite();
            float angle = top ? FramedSlopePanelModel.SLOPE_ANGLE : -FramedSlopePanelModel.SLOPE_ANGLE;
            if (quadDir == Direction.NORTH || quadDir == Direction.EAST)
            {
                angle *= -1F;
            }

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(cutDir, top ? .5F : 0F, top ? 0F : .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .apply(Modifiers.rotate(cutDir.getAxis(), top ? ORIGIN_TOP : ORIGIN_BOTTOM, angle, true))
                    .export(quadMap.get(null));
        }
        else if (ySlope && ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN)))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), 0, .5F))
                    .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), FramedSlopePanelModel.SLOPE_ANGLE_VERT))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F, 0))
                    .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), FramedSlopePanelModel.SLOPE_ANGLE_VERT))
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
    protected void applyInHandTransformation(PoseStack poseStack, ItemDisplayContext ctx)
    {
        poseStack.translate(0, .5, -.5);
    }



    public static BlockState itemModelSource()
    {
        return FBContent.BLOCK_FRAMED_SMALL_CORNER_SLOPE_PANEL.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }
}
