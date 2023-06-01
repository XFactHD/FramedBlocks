package xfacthd.framedblocks.client.data.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.render.Quaternions;
import xfacthd.framedblocks.api.util.client.OutlineRender;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public final class SlopePanelOutlineRenderer implements OutlineRender
{
    public static final Quaternion[] ROTATIONS = new Quaternion[] {
            Quaternion.ONE,
            Quaternions.ZP_180,
            Quaternions.ZP_90,
            Quaternions.ZN_90
    };

    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        // Bottom edges
        OutlineRender.drawLine(builder, poseStack, 0, 0,  0, 1, 0,  0);
        OutlineRender.drawLine(builder, poseStack, 0, 0, .5, 1, 0, .5);
        OutlineRender.drawLine(builder, poseStack, 0, 0,  0, 0, 0, .5);
        OutlineRender.drawLine(builder, poseStack, 1, 0,  0, 1, 0, .5);

        // Back edges
        OutlineRender.drawLine(builder, poseStack, 0, 0, .5, 0, 1, .5);
        OutlineRender.drawLine(builder, poseStack, 1, 0, .5, 1, 1, .5);

        // Top edge
        OutlineRender.drawLine(builder, poseStack, 0, 1, .5, 1, 1, .5);

        // Slopes
        OutlineRender.drawLine(builder, poseStack, 0, 0,  0, 0, 1, .5);
        OutlineRender.drawLine(builder, poseStack, 1, 0,  0, 1, 1, .5);
    }

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state)
    {
        OutlineRender.super.rotateMatrix(poseStack, state);

        HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
        poseStack.mulPose(ROTATIONS[rotation.ordinal()]);

        if (!state.getValue(PropertyHolder.FRONT))
        {
            poseStack.translate(0, 0, .5);
        }
    }
}
