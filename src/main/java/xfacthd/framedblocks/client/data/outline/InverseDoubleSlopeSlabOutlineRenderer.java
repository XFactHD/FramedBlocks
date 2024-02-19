package xfacthd.framedblocks.client.data.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.render.OutlineRenderer;

public final class InverseDoubleSlopeSlabOutlineRenderer implements OutlineRenderer
{
    public static final InverseDoubleSlopeSlabOutlineRenderer INSTANCE = new InverseDoubleSlopeSlabOutlineRenderer();

    private InverseDoubleSlopeSlabOutlineRenderer() { }

    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        //Back vertical edges
        OutlineRenderer.drawLine(builder, poseStack, 0, .5, 1, 0, 1, 1);
        OutlineRenderer.drawLine(builder, poseStack, 1, .5, 1, 1, 1, 1);

        //Center horizontal edges
        OutlineRenderer.drawLine(builder, poseStack, 0, .5, 0, 1, .5, 0);
        OutlineRenderer.drawLine(builder, poseStack, 0, .5, 1, 1, .5, 1);

        //Top edge
        OutlineRenderer.drawLine(builder, poseStack, 0, 1, 1, 1, 1, 1);

        //Top slope
        OutlineRenderer.drawLine(builder, poseStack, 0, .5, 0, 0, 1, 1);
        OutlineRenderer.drawLine(builder, poseStack, 1, .5, 0, 1, 1, 1);

        //Bottom edge
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 0, 1, 0, 0);

        //Bottom slope
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 0, 0, .5, 1);
        OutlineRenderer.drawLine(builder, poseStack, 1, 0, 0, 1, .5, 1);

        //Front vertical edges
        OutlineRenderer.drawLine(builder, poseStack, 0, 0, 0, 0, .5, 0);
        OutlineRenderer.drawLine(builder, poseStack, 1, 0, 0, 1, .5, 0);
    }
}
